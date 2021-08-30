package com.atusoft.newmall.event.order;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.util.BusiException;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class OrderExceptionEvent extends BaseEvent{
	
	public enum Cause {
		ShelfOutOfStock,Unknown
	}
	Cause cause;
	String description;
	@JsonIgnore
	Throwable exception;
	
	protected OrderExceptionEvent() {
	}
	
	
	public OrderExceptionEvent(BaseEvent originEvent,Cause cause,String description) {
		super(originEvent);
		this.cause=cause;
		this.description=description;
	}
	
	public OrderExceptionEvent(BaseEvent originEvent,BusiException e) {
		super(originEvent);
		this.cause=Cause.valueOf(e.getBusiCode());
		this.description=e.getMessage();
		this.exception=e;
	}
}
