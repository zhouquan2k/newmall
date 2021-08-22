package com.atusoft.newmall.order;

import com.atusoft.infrastructure.BaseEvent;

public class OrderCreatedEvent extends BaseEvent {
	OrderDTO order;
	
	public OrderCreatedEvent(OrderDTO order) {
		this.order=order;
	}

}
