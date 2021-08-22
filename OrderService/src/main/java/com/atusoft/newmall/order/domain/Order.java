package com.atusoft.newmall.order.domain;

import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.User;
import com.atusoft.newmall.order.OrderCreatedEvent;
import com.atusoft.newmall.order.OrderDTO;

public class Order   {
	
	@Autowired
	Infrastructure infrastructure;
	
	
	/*
	public enum PromoterLevel {
		Silver,Gold,Diamond,None
	}

	@Data
	static class Price {
		
		Map<PromoterLevel,BigDecimal> promoterPrices;		
	}
	*/
	
	final OrderDTO order;
	
		
	public Order(OrderDTO order){
		this.order=order;
		//TODO copy from it instead of reference it.
	}
	
	
	public OrderDTO preview() {
		User user=this.infrastructure.getCurrentUser(this.order);
		
		//constraints:
		//1.address required
		
		
		//TODO do price calculation
		//计算抵扣，活动不能抵扣
		this.order.setOrderId(this.infrastructure.getUUID());
		this.infrastructure.persistEntity(this.order,60*10); // persist object to json stream
		
		//create OrderCreatedEvent to check stock, not decrease
		this.infrastructure.publishEvent(new OrderCreatedEvent(this.order));
		return this.order;
	}
	
	public void confirm() {
		this.infrastructure.publishEvent(new OrderCreatedEvent(this.order));
	}
}
