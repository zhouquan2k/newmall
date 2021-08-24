package com.atusoft.messaging.kafka;

import com.atusoft.messaging.Message;
import com.atusoft.messaging.MessageContext;

public class MessageImpl implements Message {

	@Override
	public MessageContext getContext() {
		return context;
	}

	@Override
	public Object getContent() {
		return content;
	}
	
	MessageContext context;
	Object content;
	Type type;
	
	MessageImpl(Type type,MessageContext ctx,Object content){
		this.context=ctx;
		this.content=content;
		this.type=type;
	}

	@Override
	public Type getType() {
		return this.type;
	}

}
