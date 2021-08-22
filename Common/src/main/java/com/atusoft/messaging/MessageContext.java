package com.atusoft.messaging;

public interface MessageContext {
	void send(String topic,Object msg);
	void request(String topic,Object request,MessageHandler handler);
	void publish(String topic,Object event);
	void response(Object response);
	
	void setHandler(String[] topic,MessageHandler handler);
}
