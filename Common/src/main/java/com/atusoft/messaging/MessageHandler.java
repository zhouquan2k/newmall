package com.atusoft.messaging;

@FunctionalInterface
public interface MessageHandler {
	void handler(MessageContext context,Object message);
}
