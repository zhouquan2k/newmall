package com.atusoft.newmall.shelf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.EventHandler;
import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.newmall.shelf.domain.Shelf;
import com.atusoft.newmall.event.order.OrderCreatedEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;

import io.vertx.core.Future;

@Component("service")
public class ShelfService {

	@Autowired
	Infrastructure infrastructure;
	
	@EventHandler
	public void onOrderCreatedEvent(OrderCreatedEvent event) {
		//TODO
		this.infrastructure.publishEvent(new OrderPricedEvent(event.getOrder()));
		this.infrastructure.publishEvent(new OrderDeductionBalancedEvent(event.getOrder()));
	}
	
}
