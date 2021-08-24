package com.atusoft.newmall.event.shelf;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;

import lombok.Data;

@Data
public class OrderPricedEvent extends BaseEvent {

	OrderDTO order;
	
	protected OrderPricedEvent() {
		
	}
	public OrderPricedEvent(OrderDTO order) {
		this.order=order;
	}
}
