package com.atusoft.newmall.event.order;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;

public class OrderCancelledEvent extends BaseEvent {
	OrderDTO order;
	
	protected OrderCancelledEvent() {
	}
	public OrderCancelledEvent(OrderDTO order) {
		super(order);
		this.order=order;
	}
}
