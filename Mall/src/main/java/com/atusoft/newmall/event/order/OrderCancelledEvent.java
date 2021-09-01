package com.atusoft.newmall.event.order;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper=true)
public class OrderCancelledEvent extends BaseEvent {
	OrderDTO order;
	
	protected OrderCancelledEvent() {
	}
	public OrderCancelledEvent(String causeEventId,OrderDTO order) {
		super(order);
		this.causeEventId=causeEventId;
		this.order=order;
	}
	
	public OrderDTO getOrder() {
		return this.order;
	}
}
