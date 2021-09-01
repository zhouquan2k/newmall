package com.atusoft.newmall.event.order;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper=true)
public class OrderSubmitedEvent extends BaseEvent {
	OrderDTO order;
	
	protected OrderSubmitedEvent() {
	}
	public OrderSubmitedEvent(OrderDTO order) {
		super(order);
		this.order=order;
	}
}
