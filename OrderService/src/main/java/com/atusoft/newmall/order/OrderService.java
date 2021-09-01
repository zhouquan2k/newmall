package com.atusoft.newmall.order;

import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.infrastructure.EventHandler;
import com.atusoft.newmall.BaseService;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.event.order.OrderExceptionEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;
import com.atusoft.newmall.order.domain.Order;
import com.atusoft.util.Util;

import io.vertx.core.Future;
import io.vertx.core.Promise;


@Component("service")
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
		Promise<BaseEvent> p=this.infrastructure.getPendingFuture("order:"+event.getOrder().getOrderId()+":price");
		if (p!=null) p.complete(event);
	}
	
	@EventHandler
	public void onOrderDeductionBalancedEvent(OrderDeductionBalancedEvent event) {
		Promise<BaseEvent> p=this.infrastructure.getPendingFuture("order:"+event.getOrder().getOrderId()+":deduction");
		if (p!=null) p.complete(event);
	}
	
	@EventHandler
	public void onOrderExceptionEvent(OrderExceptionEvent event) {
		
		Util.onSuccess(this.infrastructure.getEntity(Order.class,event.getOrderId()),order->{
			Util.mustNotNull(order);
			order.cancel(event);
			return Future.succeededFuture();
		});
	}
	
	
	
	@CommandHandler
	public Future<?> SubmitOrder(String orderId) {
		
		return Util.onSuccess(this.infrastructure.getEntity(Order.class,orderId),order->{
			Util.mustNotNull(order);
			order.submit();
			return Future.succeededFuture();
		});
	}
}
