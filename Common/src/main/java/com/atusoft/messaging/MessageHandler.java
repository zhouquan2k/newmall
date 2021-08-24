package com.atusoft.messaging;

@FunctionalInterface
public interface MessageHandler {
	void handle(Message message);
}
