package com.atusoft.messaging.kafka;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import com.atusoft.json.JsonUtil;



public class TestKafkaMessageContext {
	
	static JsonUtil jsonUtil=new JsonUtil();

	@Test
	public void testRequestResponse() {
		
		
		
		KafkaMessageContext requestContext=new KafkaMessageContext();
		requestContext.setNodeId("request_node1");
		requestContext.setJsonUtil(jsonUtil);
		requestContext.init();
		
		
		
		KafkaMessageContext responseContext=new KafkaMessageContext();
		responseContext.setNodeId("response_node1");
		requestContext.setJsonUtil(jsonUtil);
		responseContext.init();
		
		String command="Order";
		responseContext.setHandler(new String[] {command}, (message)->{
			String str=(String)message.getContent();
			String req=str.substring(str.indexOf('>')+1);
			message.getContext().response("<order-response-detail-json>"+req);
		});
		
		
		for (int i=0;i<10;i++) {
			final int index=i;
			requestContext.request(command, "<order-request-detail-json>"+index, (msg)->{
				System.out.println("response recved:"+index+","+msg.getContent());
				
			});
		}
		
	}
	
	static class CommandMsg {
		String commandName;
		Map<String,String> params;
		String body;
		public CommandMsg() {}
		public CommandMsg(String name) {
			this.commandName=name;
		}
		public String toString() {
			return jsonUtil.toJson(this);
		}
	}
	
	@Test
	public void testRRWithObject()  {
		JsonUtil jsonUtil=new JsonUtil();
		
		KafkaMessageContext requestContext=new KafkaMessageContext();
		requestContext.setNodeId("request_node2");
		requestContext.setJsonUtil(jsonUtil);
		requestContext.init();
		
		
		KafkaMessageContext responseContext=new KafkaMessageContext();
		responseContext.setNodeId("response_node2");
		requestContext.setJsonUtil(jsonUtil);
		responseContext.init();
		
		
		String command="Order2";
		responseContext.setHandler(new String[] {command}, (message)->{
			CommandMsg msg=(CommandMsg)message.getContent();
			int index=Integer.parseInt(msg.params.get("index"));
			CommandMsg response=new CommandMsg(msg.commandName);
			response.params=Map.of("index",""+index);
			response.body="<order-response-detail-json>"+index;
			message.getContext().response(response);
		});
		
		
		for (int i=0;i<10;i++) {
			final int index=i;
			CommandMsg msg=new CommandMsg(command);
			msg.params=Map.of("index",""+i);
			msg.body="<order-request-detail-json>";
			requestContext.request(command, msg, (message)->{
				System.out.println("response recved:"+index+","+message.getContent());	
				CommandMsg cmsg=(CommandMsg)message.getContent();
				assert cmsg.params.get("index").equals(""+index);
			});
		}
		
		
	}
	
	
	@Test
	public void testRRWithTemplate()  {
		JsonUtil jsonUtil=new JsonUtil();
		
		KafkaMessageContext requestContext=new KafkaMessageContext();
		requestContext.setNodeId("request_node3");
		requestContext.setJsonUtil(jsonUtil);
		requestContext.init();
		
		
		KafkaMessageContext responseContext=new KafkaMessageContext();
		responseContext.setNodeId("response_node3");
		requestContext.setJsonUtil(jsonUtil);
		responseContext.init();
		
		
		String command="Order.CreateOrder";
		responseContext.setHandler(new String[]{"Order\\..*"}, (message)->{
			CommandMsg msg=(CommandMsg)message.getContent();
			int index=Integer.parseInt(msg.params.get("index"));
			CommandMsg response=new CommandMsg(msg.commandName);
			response.params=Map.of("index",""+index);
			response.body="<order-response-detail-json>"+index;
			message.getContext().response(response);
		});
		
		
		for (int i=0;i<10;i++) {
			final int index=i;
			CommandMsg msg=new CommandMsg(command);
			msg.params=Map.of("index",""+i);
			msg.body="<order-request-detail-json>";
			requestContext.request(command, msg, (message)->{
				System.out.println("response recved:"+index+","+message.getContent());	
				CommandMsg cmsg=(CommandMsg)message.getContent();
				assert cmsg.params.get("index").equals(""+index);
			});
		}
	}
	
	@Test
	public void testRRwithFuture()  {
		JsonUtil jsonUtil=new JsonUtil();
		
		KafkaMessageContext requestContext=new KafkaMessageContext();
		requestContext.setNodeId("request_node4");
		requestContext.setJsonUtil(jsonUtil);
		requestContext.init();
		
		
		KafkaMessageContext responseContext=new KafkaMessageContext();
		responseContext.setNodeId("response_node4");
		requestContext.setJsonUtil(jsonUtil);
		responseContext.init();
		
		
		String command="Order4";
		responseContext.setHandler(new String[]{"Order4"}, (message)->{
			CommandMsg msg=(CommandMsg)message.getContent();
			int index=Integer.parseInt(msg.params.get("index"));
			CommandMsg response=new CommandMsg(msg.commandName);
			response.params=Map.of("index",""+index);
			response.body="<order-response-detail-json>"+index;
			message.getContext().response(response);
		});
		
		
		for (int i=0;i<10;i++) {
			final int index=i;
			CommandMsg msg=new CommandMsg(command);
			msg.params=Map.of("index",""+i);
			msg.body="<order-request-detail-json>";
			requestContext.request(command, msg).onSuccess((message)->{
				System.out.println("response recved:"+index+","+message.getContent());	
				CommandMsg cmsg=(CommandMsg)message.getContent();
				assert cmsg.params.get("index").equals(""+index);
			});
		}
	}
	
	@AfterAll
	static public void exit() throws InterruptedException {
		Thread.sleep(60000);
	}
}
