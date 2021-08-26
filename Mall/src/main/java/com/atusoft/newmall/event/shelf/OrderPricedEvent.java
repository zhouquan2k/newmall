package com.atusoft.newmall.event.shelf;

import java.util.List;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;

import lombok.Data;

@Data
public class OrderPricedEvent extends BaseEvent {

	OrderDTO order;
	
	protected OrderPricedEvent() {
		super(null);
	}
	public OrderPricedEvent(OrderDTO order) {
		super(order);
		this.order=order;
	}
}
