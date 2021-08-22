package com.atusoft.newmall.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.newmall.order.domain.Order;


@Component("service")
public class OrderService {
	
	@Autowired
	Infrastructure infrastructure;
	
	//using dto's base class to get context , or using another param to inject context
	public OrderDTO PreviewOrder(OrderDTO cmd) {
		
		// throw new RuntimeException("TODO");
		//Order order=new Order(cmd); //or new Order(cmd,ctx) ...
		Order order=this.infrastructure.newEntity(Order.class,cmd);
		return order.preview();
	}
	
	public void ConfirmOrder(String orderId) {
		
		Order order=infrastructure.getEntity(Order.class,orderId);
		order.confirm();
	}
}
