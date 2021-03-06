package com.atusoft.newmall.event.shelf;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper=true)
public class OrderPricedEvent extends BaseEvent {

	OrderDTO order;
	
	protected OrderPricedEvent() {
	}
	public OrderPricedEvent(OrderDTO order) {
		super(order);
		this.order=order;
	}
}
