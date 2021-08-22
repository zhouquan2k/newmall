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
		responseContext.setHandler(new String[] {command}, (context,request)->{
			String str=(String)request;
			String req=str.substring(str.indexOf('>')+1);
			context.response("<order-response-detail-json>"+req);
		});
		
		
		for (int i=0;i<10;i++) {
			final int index=i;
			requestContext.request(command, "<order-request-detail-json>"+index, (context,response)->{
				System.out.println("response recved:"+index+","+response);
				
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
		responseContext.setHandler(new String[] {command}, (context,request)->{
			CommandMsg msg=(CommandMsg)request;
			int index=Integer.parseInt(msg.params.get("index"));
			CommandMsg response=new CommandMsg(msg.commandName);
			response.params=Map.of("index",""+index);
			response.body="<order-response-detail-json>"+index;
			context.response(response);
		});
		
		
		for (int i=0;i<10;i++) {
			final int index=i;
			CommandMsg msg=new CommandMsg(command);
			msg.params=Map.of("index",""+i);
			msg.body="<order-request-detail-json>";
			requestContext.request(command, msg, (context,response)->{
				System.out.println("response recved:"+index+","+response);	
				CommandMsg cmsg=(CommandMsg)response;
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
		
		
		String command="OrderCreateOrder";
		responseContext.setHandler(new String[]{"OrderCreateOrder"}, (context,request)->{
			CommandMsg msg=(CommandMsg)request;
			int index=Integer.parseInt(msg.params.get("index"));
			CommandMsg response=new CommandMsg(msg.commandName);
			response.params=Map.of("index",""+index);
			response.body="<order-response-detail-json>"+index;
			context.response(response);
		});
		
		
		for (int i=0;i<10;i++) {
			final int index=i;
			CommandMsg msg=new CommandMsg(command);
			msg.params=Map.of("index",""+i);
			msg.body="<order-request-detail-json>";
			requestContext.request(command, msg, (context,response)->{
				System.out.println("response recved:"+index+","+response);	
				CommandMsg cmsg=(CommandMsg)response;
				assert cmsg.params.get("index").equals(""+index);
			});
		}
		
	}
	
	@AfterAll
	static public void exit() throws InterruptedException {
		Thread.sleep(60000);
	}
}
