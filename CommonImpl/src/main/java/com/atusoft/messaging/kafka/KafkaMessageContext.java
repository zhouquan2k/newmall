package com.atusoft.messaging.kafka;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.messaging.Message;
import com.atusoft.messaging.MessageContext;
import com.atusoft.messaging.MessageHandler;
import com.atusoft.util.JsonUtil;
import com.atusoft.util.Util;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class KafkaMessageContext extends AbstractVerticle implements MessageContext {
	
	protected KafkaProducer<String,Object> producer;
	protected KafkaProducer<String,Request> requestProducer;
	protected String nodeId=Util.getUUID();
	
	@Value("${kafka.servers}")
	final String servers="192.168.1.181:9092";
	
	static JsonUtil theJsonUtil; //used by serializer/deserializer
	
	@Autowired
	public KafkaMessageContext(JsonUtil jsonUtil,Vertx vertx) {
		this.jsonUtil=jsonUtil;
		this.vertx=vertx;
		if (theJsonUtil==null) theJsonUtil=jsonUtil;
	}
	
	Vertx vertx;
	JsonUtil jsonUtil;
	

	static class Request {
		String nodeId;
		String requestId;
		Object content;
		
		public Request(String nodeId,Object content) {
			this.nodeId=nodeId;
			this.requestId=""+System.nanoTime(); //TODO
			this.content=content;
		}
		
		public Request(String nodeId,String requestId,Object content) {
			this.nodeId=nodeId;
			this.requestId=requestId;
			this.content=content;
		}
		
		@Override
		public String toString() {
			return this.nodeId+","+this.requestId+"\r\n"+this.content;
		}
	}
	
	public static class RequestSerializer implements Serializer<Request> {
		
		@Override
		public byte[] serialize(String topic, Request data) {
			try
			{
				String r=data.nodeId+","+data.requestId+","+
						((data.content!=null)?data.content.getClass().getName():"")
						+":"+theJsonUtil.toJson(data.content); 
				log.debug("sending packet:"+r);
				return r.getBytes();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				throw e;
			}
		}
	}
	public static class RequestDeserializer implements Deserializer<Request> {

		
		@Override
		public Request deserialize(String topic, byte[] data) { 
			try
			{
				String msg=new String(data);
				String header=msg.substring(0,msg.indexOf(':'));
				String[] parts=header.split(",");
				String content=msg.substring(msg.indexOf(':')+1);
				Object obj=null;
				if (parts.length>=3&&parts[2].length()>0) {
					obj=theJsonUtil.fromJson(content,parts[2]);
					Request r=new Request(parts[0],parts[1],obj);
					log.debug("recving packet:"+msg);
					return r;
				}
				else if (parts.length==2) {
					Request r=new Request(parts[0],parts[1],null);
					return r;
				}
				return null;
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				//throw e;
				return null;
			}
		}
	
	}
	
	public static class JsonSerializer implements Serializer<Object> {
		
		@Override
		public byte[] serialize(String topic, Object data) {
			try
			{
				String r=((data!=null)?data.getClass().getName():"")
						+":"+theJsonUtil.toJson(data); 
				log.debug("sending packet:"+r);
				return r.getBytes();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	public static class JsonDeserializer implements Deserializer<Object> {
		@Override
		public Object deserialize(String topic, byte[] data) { 
			try
			{
				String msg=new String(data);
				String className=msg.substring(0,msg.indexOf(':'));
				Object obj=theJsonUtil.fromJson(msg.substring(msg.indexOf(':')+1),className);
				log.debug("recving packet:"+obj);
				return obj;
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				//throw e;
				return null;
			}
		}
	}
	
	Map<String,MessageHandler> pendingHandlers=new HashMap<String,MessageHandler>();
	Map<String,Promise<Message>> pendingFutures=new HashMap<String,Promise<Message>>();
	
	public void setNodeId(String nodeId) {
		this.nodeId=nodeId;
	}
	
		
	@PostConstruct
	public void init() {
		if (theJsonUtil==null) theJsonUtil=jsonUtil;
	    vertx.deployVerticle(this);
	    	
		{
			Map<String, String> config = new HashMap<>();
			config.put("bootstrap.servers", servers);
			config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			config.put("value.serializer", "com.atusoft.messaging.kafka.KafkaMessageContext$JsonSerializer");
			config.put("acks", "1");
			producer = KafkaProducer.create(vertx, config);
		}
		{	
			Map<String, String> config = new HashMap<>();
			config.put("bootstrap.servers", servers);
			config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			config.put("value.serializer", "com.atusoft.messaging.kafka.KafkaMessageContext$RequestSerializer");
			config.put("acks", "1");
			requestProducer= KafkaProducer.create(vertx, config);
		}
		{
			Map<String, String> config = new HashMap<>();
			config.put("bootstrap.servers", servers);
			config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			//config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			config.put("value.deserializer", "com.atusoft.messaging.kafka.KafkaMessageContext$RequestDeserializer");
			config.put("group.id", "single");
			config.put("auto.offset.reset", "earliest");
			config.put("enable.auto.commit", "true");
			
			KafkaConsumer<String, Request> consumer = KafkaConsumer.create(vertx, config);
			final KafkaMessageContext context=this;
			// process respond
			consumer.handler(record->{
				Request req=record.value();
				
				if (req==null) {
					log.warn("NODE {} received invalid response",context.nodeId);
					return;
				}
					
				log.debug("NODE {} received response {}",context.nodeId,req); 
				Message msg=new MessageImpl(Message.Type.Command,context,req.content);
				MessageHandler handler=this.pendingHandlers.remove(req.requestId);	
				
				if (handler!=null)
					handler.handle(msg);
				else {
					Promise<Message> p=this.pendingFutures.remove(req.requestId);
					if (p!=null)  p.complete(msg);
					else
						log.warn("invalid response recved:"+req.requestId+"\r\n"+req);
				}
				
					
					
			});
			consumer.subscribe(this.nodeId);
		}
	}
	
	@Override
	public void send(String topic, Object msg) {
		
		KafkaProducerRecord<String, Object> record = KafkaProducerRecord.create(topic, msg);
		producer.send(record).onSuccess(recordMetadata ->
		    System.out.println(
		      "Message " + record.value() + " written on topic=" + recordMetadata.getTopic() +
		      ", partition=" + recordMetadata.getPartition() +
		      ", offset=" + recordMetadata.getOffset()
		    )
		);
	}

	@Override
	public void request(String topic, Object request, MessageHandler handler) {
		Request r=new Request(this.nodeId,request);
		KafkaProducerRecord<String, Request> record = KafkaProducerRecord.create(topic, r);
		this.pendingHandlers.put(r.requestId, handler);
		log.debug("NODE {} sending to topic {} request: {} ",this.nodeId,topic,r); 
		requestProducer.send(record).onSuccess(recordMetadata ->
		    System.out.println(
		      "Message " + r.requestId + " written on topic=" + recordMetadata.getTopic() +
		      ", partition=" + recordMetadata.getPartition() +
		      ", offset=" + recordMetadata.getOffset()
		    )
		);
		
	}

	@Override
	public void publish(String topic, Object event) {
		KafkaProducerRecord<String, Object> record = KafkaProducerRecord.create(topic, event);
		producer.send(record).onSuccess(recordMetadata ->
		    System.out.println(
		      "Message " + record.value() + " written on topic=" + recordMetadata.getTopic() +
		      ", partition=" + recordMetadata.getPartition() +
		      ", offset=" + recordMetadata.getOffset()
		    )
		);
	}

	@Override
	public void response(Object response) {
		
		assert false;
	}

	@Override
	//TODO refactor to setCommandHandler
	public void setHandler(String[] topic, MessageHandler handler) {
		
		
		Map<String, String> config = new HashMap<>();
		config.put("bootstrap.servers", servers);
		config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		//config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		config.put("value.deserializer", "com.atusoft.messaging.kafka.KafkaMessageContext$RequestDeserializer");
		config.put("group.id", "single");
		config.put("auto.offset.reset", "earliest");
		config.put("enable.auto.commit", "true");
		
		KafkaConsumer<String, Object> consumer = KafkaConsumer.create(vertx, config);
		final KafkaMessageContext context=this;
		consumer.handler(record->{
			if (record.value() instanceof Request) {
				Request req=(Request)record.value();
				//if (req==null)
				//	log.warn("received a invalid request,ignore...");
				//else {
					log.debug("NODE {} received request {}",context.nodeId,req); 
					RequestContext rctx=new RequestContext(context,req);
					Message msg=new MessageImpl(Message.Type.Command,rctx,req!=null?req.content:null);
					handler.handle(msg);
				//}
			}
		});
		
		if (topic.length==1&&topic[0].indexOf('*')>=0)
			consumer.subscribe(Pattern.compile(topic[0])).onSuccess(v ->
	    		System.out.println("subscribed:"+consumer.subscription())
			).onFailure(cause ->
				System.out.println("Could not subscribe " + cause.getMessage())
			);
		else
			consumer.subscribe(new HashSet<String>(Arrays.asList(topic)));
			
	}


	@Override
	public Future<Message> request(String topic, Object request) {
		Promise<Message> p=Promise.promise();
		Request r=new Request(this.nodeId,request);
		KafkaProducerRecord<String, Request> record = KafkaProducerRecord.create(topic, r);
		this.pendingFutures.put(r.requestId, p);
		log.debug("NODE {} sending to topic {} request: {} ",this.nodeId,topic,r); 
		requestProducer.send(record).onSuccess(recordMetadata ->
		    System.out.println(
		      "Message " + r.requestId + " written on topic=" + recordMetadata.getTopic() +
		      ", partition=" + recordMetadata.getPartition() +
		      ", offset=" + recordMetadata.getOffset()
		    )
		).onFailure(e->{
			p.fail(e);
		});
		return p.future(); 
	}


	@Override
	public void setEventHandler(String group,String[] topic, MessageHandler handler) {
		
		Map<String, String> config = new HashMap<>();
		config.put("bootstrap.servers", servers);
		config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		//config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		config.put("value.deserializer", "com.atusoft.messaging.kafka.KafkaMessageContext$JsonDeserializer");
		config.put("group.id", group);
		config.put("auto.offset.reset", "earliest");
		config.put("enable.auto.commit", "true");
		
		KafkaConsumer<String, Object> consumer = KafkaConsumer.create(vertx, config);
		final KafkaMessageContext context=this;
		consumer.handler(record->{
			if (record.value() instanceof BaseEvent) {
				BaseEvent event=(BaseEvent)record.value();
				Message msg=new MessageImpl(Message.Type.Event,context,event);
				handler.handle(msg);
			}
		});
		
		if (topic.length==1&&topic[0].indexOf('*')>=0)
			consumer.subscribe(Pattern.compile(topic[0])).onSuccess(v ->
	    		System.out.println("subscribed:"+consumer.subscription())
			).onFailure(cause ->
				System.out.println("Could not subscribe " + cause.getMessage())
			);
		else
			consumer.subscribe(new HashSet<String>(Arrays.asList(topic)));

	}

}
