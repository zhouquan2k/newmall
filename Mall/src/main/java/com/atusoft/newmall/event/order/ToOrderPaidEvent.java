package com.atusoft.newmall.event.order;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO.PayMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper=true)
@AllArgsConstructor
public class ToOrderPaidEvent extends BaseEvent {

	String orderId;
	PayMethod payMethod;
	boolean success;
	
}
