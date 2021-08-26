package com.atusoft.newmall.order;

import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.infrastructure.EventHandler;
import com.atusoft.newmall.BaseService;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;
import com.atusoft.newmall.order.domain.Order;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;


@Component("service")
@Slf4j
public class OrderService extends BaseService {
	
	//using dto's base class to get context , or using another param to inject context
	@CommandHandler
	public Future<OrderDTO> PreviewOrder(OrderDTO cmd) {
		
		// throw new RuntimeException("TODO");
		//Order order=new Order(cmd); //or new Order(cmd,ctx) ...
		//TODO using factory: Order.create(cmd)
		Order order=this.infrastructure.newEntity(Order.class,cmd);
		return order.review();
	}
	
	@EventHandler
	public void onOrderPricedEvent(OrderPricedEvent event) {
		log.debug("recved priced event");
		Promise<OrderPricedEvent> p=(Promise<OrderPricedEvent>)this.infrastructure.getPendingFuture("order:"+event.getOrder().getOrderId()+":price");
		if (p!=null) p.complete(event);
	}
	
	@EventHandler
	public void onOrderDeductionBalancedEvent(OrderDeductionBalancedEvent event) {
		log.debug("recved deduction event");
		Promise<OrderDeductionBalancedEvent> p=(Promise<OrderDeductionBalancedEvent>)this.infrastructure.getPendingFuture("order:"+event.getOrder().getOrderId()+":deduction");
		if (p!=null) p.complete(event);
	}
	
	
	
	@CommandHandler
	public void ConfirmOrder(String orderId) {
		
		infrastructure.getEntity(Order.class,orderId).onComplete(result->{
			result.result().confirm();
			//order.confirm();
		});
	}
}
