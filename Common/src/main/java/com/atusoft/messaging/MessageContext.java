package com.atusoft.messaging;

import io.vertx.core.Future;

public interface MessageContext {
	void send(String topic,Object msg);
	//deprecated
	void request(String topic,Object request,MessageHandler handler);
	Future<Message> request(String topic,Object request);
	void publish(String topic,Object event);
	void response(Object response);
	
	void setHandler(String[] topic,MessageHandler handler);
	void setEventHandler(String[] topic,MessageHandler handler);
}
