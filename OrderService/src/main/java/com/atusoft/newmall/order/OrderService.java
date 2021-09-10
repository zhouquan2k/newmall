package com.atusoft.newmall.order;

import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.infrastructure.EventHandler;
import com.atusoft.newmall.BaseService;
import com.atusoft.newmall.dto.order.DeductionOptions;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.order.OrderDTO.PayMethod;
import com.atusoft.newmall.dto.order.OrderDTO.Status;
import com.atusoft.newmall.event.order.OrderExceptionEvent;
import com.atusoft.newmall.event.order.ToOrderPaidEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.DeductionBalancedEvent;
import com.atusoft.newmall.order.domain.Cart;
import com.atusoft.newmall.order.domain.Order;
import com.atusoft.util.Util;

import io.vertx.core.Future;
import io.vertx.core.Promise;


@Component
public class OrderService extends BaseService {
	
	//using dto's base class to get context , or using another param to inject context
	
	@CommandHandler
	public Future<OrderDTO> previewOrder(String cartId,DeductionOptions deductionOptions) {
		
		//create a new temp order
		//return this.infrastructure.getEntity(Cart.class, cartId).compose(cart->{
		return  Cart.load(Cart.class, cartId).compose(cart->{
			Order order=Order.create(cart.orElseThrow().getCart(),deductionOptions);
			return order.preview();		
		});	
	}
	
	//TODO need refator to query?
	@CommandHandler
	public Future<OrderDTO> getOrder(String orderId) {
		return Order.load(Order.class,orderId).map(order->order.orElseThrow().getOrder());
	}
	
	
	
	@EventHandler
	public Future<?> onOrderPricedEvent(OrderPricedEvent event) {
		Promise<BaseEvent> p=this.infrastructure.getPendingFuture("order:"+event.getOrder().getOrderId()+":price");
		if (p!=null) p.complete(event);
		return Future.succeededFuture();
	}
	
	@EventHandler
	public Future<?> onDeductionBalancedEvent(DeductionBalancedEvent event) {
		Promise<BaseEvent> p=this.infrastructure.getPendingFuture("order:"+event.getSourceId()+":deduction");
		if (p!=null) p.complete(event);
		return Future.succeededFuture();
	}
	
	@EventHandler
	public Future<?> onOrderExceptionEvent(OrderExceptionEvent event) {
		
		return Order.load(Order.class,event.getOrderId()).compose(order->{
			if (order.orElseThrow().getOrder().getStatus()==Status.Submited)
				order.orElseThrow().cancel(event);
			return Future.succeededFuture();
			
		});
	}
	
	@EventHandler
	public Future<?> onToOrderPaidEvent(ToOrderPaidEvent event){
		return Order.load(Order.class,event.getOrderId()).compose(order->{
			order.orElseThrow().onPaid(event);
			return Future.succeededFuture();
		});
	}
	
	
	
	@CommandHandler
	public Future<OrderDTO> submitOrder(String orderId,PayMethod payMethod) {
		
		return Util.onSuccess(this.infrastructure.getEntity(Order.class,orderId),order->{
			return order.orElseThrow().submit(payMethod);
		});
	}
}
