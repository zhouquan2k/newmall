package com.atusoft.newmall.event.order;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper=true)
public class OrderPreviewEvent extends BaseEvent {

	OrderDTO order;
	
	protected OrderPreviewEvent() {
		
	}
	
	public OrderPreviewEvent(OrderDTO order) {
		this.order=order;
	}
}
