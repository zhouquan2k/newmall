package com.atusoft.messaging;

public interface Message {
	public static enum Type {
		Command,Query,Event
	}
	Type getType();
	MessageContext getContext();
	Object getContent();
}
