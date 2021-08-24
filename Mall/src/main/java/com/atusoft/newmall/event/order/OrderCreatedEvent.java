package com.atusoft.newmall.event.order;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;

import lombok.Data;

@Data
public class OrderCreatedEvent extends BaseEvent {
	OrderDTO order;
	
	protected OrderCreatedEvent() {
		
	}
	public OrderCreatedEvent(OrderDTO order) {
		this.order=order;
	}

}
