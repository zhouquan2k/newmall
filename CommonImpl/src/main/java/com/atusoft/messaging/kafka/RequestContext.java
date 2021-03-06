package com.atusoft.messaging.kafka;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map; 

import com.atusoft.framwork.ApiResponseMessage;
import com.atusoft.messaging.MessageContext;
import com.atusoft.util.BusiException;

import io.vertx.kafka.client.producer.KafkaProducerRecord;

class RequestContext  extends KafkaMessageContext implements MessageContext {
	
	Request request;
	
	RequestContext(KafkaMessageContext context,Request req) {
		super(context.jsonUtil,context.vertx);
		this.nodeId= context.nodeId;
		this.producer=context.producer;
		this.requestProducer=context.requestProducer;
		
		this.request=req;
		
		assert !this.nodeId.isBlank();
		
	}
	
	@Override
	public void response(Object response) {
		
		//String content="";
		int retcode=200;
		if (response!=null&&response instanceof Throwable) {
			Throwable ex=(Throwable)response;
			if (ex instanceof InvocationTargetException) ex=((InvocationTargetException) ex).getTargetException();
			//content=ex.getClass().getSimpleName()+" : "+ex.getMessage();
			retcode=500;
			Map<String,Object> resp=new HashMap<String,Object>();
			resp.putAll(Map.of("exception",ex.getClass().getSimpleName()+" : "+ex.getMessage()));
			if (ex instanceof BusiException) {
				BusiException be=(BusiException)ex;
				resp.putAll(Map.of("busiCode",be.getBusiCode()));
			}
			response=resp;		
				
		}
		
		String content=response==null?"":this.jsonUtil.toJson(response);
		
		
		Request r=new Request(this.request.nodeId,this.request.requestId,new ApiResponseMessage(retcode,content));
		KafkaProducerRecord<String, Request> record = KafkaProducerRecord.create(r.nodeId, r);
		
		requestProducer.send(record).onSuccess(recordMetadata ->
		    System.out.println(
		      "Response " + r.requestId + " written on topic=" + recordMetadata.getTopic() +
		      ", partition=" + recordMetadata.getPartition() +
		      ", offset=" + recordMetadata.getOffset()
		    )
		);
		
	}
}
