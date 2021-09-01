
package com.atusoft.newmall.event.order;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper=true)
public class OrderCreatedEvent extends BaseEvent {
	OrderDTO order;
	
	protected OrderCreatedEvent() {
	}
	public OrderCreatedEvent(OrderDTO order) {
		super(order);
		this.order=order;
	}

}
