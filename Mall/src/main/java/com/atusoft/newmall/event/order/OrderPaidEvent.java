package com.atusoft.newmall.event.order;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;

public class OrderPaidEvent extends BaseEvent {

	OrderDTO order;
	
	protected OrderPaidEvent() {
		
	}
	
	public OrderPaidEvent(OrderDTO order) {
		this.order=order;
	}
}
