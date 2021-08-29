package com.atusoft.newmall.event.order;

import com.atusoft.infrastructure.BaseEvent;

public class OrderExceptionEvent extends BaseEvent{
	
	public enum Cause {
		ShelfOutOfStock,
	}
	Cause cause;
	String description;
	
	protected OrderExceptionEvent() {
	}
	
	
	public OrderExceptionEvent(BaseEvent originEvent,Cause cause,String description) {
		super(originEvent);
		this.cause=cause;
		this.description=description;
	}
}
