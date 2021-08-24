package com.atusoft.newmall.event.user;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;

import lombok.Data;

@Data
public class OrderDeductionBalancedEvent extends BaseEvent{

	OrderDTO order;
	
	protected OrderDeductionBalancedEvent() {
		
	}
	public OrderDeductionBalancedEvent(OrderDTO order) {
		this.order=order;
	}
}
