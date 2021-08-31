package com.atusoft.newmall.event.order;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.util.BusiException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;

@Getter
public class OrderExceptionEvent extends BaseEvent{
	
	public enum Cause {
		ShelfOutOfStock,Unknown
	}
	Cause cause;
	String description;
	@JsonIgnore
	Throwable exception;
	
	String orderId;
	
	protected OrderExceptionEvent() {
	}
	
	
	public OrderExceptionEvent(BaseEvent originEvent,String orderId,Cause cause,String description) {
		super(originEvent);
		this.orderId=orderId;
		this.cause=cause;
		this.description=description;
	}
	
	public OrderExceptionEvent(BaseEvent originEvent,String orderId,BusiException e) {
		super(originEvent);
		this.orderId=orderId;
		this.cause=Cause.valueOf(e.getBusiCode());
		this.description=e.getMessage();
		this.exception=e;
	}
}
