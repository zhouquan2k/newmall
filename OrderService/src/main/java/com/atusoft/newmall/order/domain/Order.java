package com.atusoft.newmall.order.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.User;
import com.atusoft.newmall.acl.ShelfDTO;
import com.atusoft.newmall.acl.ShelfService;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.event.order.OrderCreatedEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Order   {
	
	@Autowired
	Infrastructure infrastructure;
	
	final OrderDTO order;
	
	@Autowired
	ShelfService shelfService;
		
	public Order(OrderDTO order){
		this.order=order;
		//TODO copy from it instead of reference it.
	}
	
	
	public Future<OrderDTO> review() {
		User user=this.infrastructure.getCurrentUser(this.order);
		
		//constraints:
		//1.address required
		
		
		this.order.setOrderId(this.infrastructure.getUUID());
		
		this.infrastructure.persistEntity(this.order, 60*10);
		this.infrastructure.publishEvent(new OrderCreatedEvent(this.order));
		
		
		Promise<OrderPricedEvent> pPriced=Promise.promise();
		this.infrastructure.addPendingFuture("order:"+this.order.getOrderId()+":price", pPriced);
		Promise<OrderDeductionBalancedEvent> pDeduction=Promise.promise();
		this.infrastructure.addPendingFuture("order:"+this.order.getOrderId()+":deduction", pDeduction);
		return CompositeFuture.all(pPriced.future(),  pDeduction.future()).map(r->{
		
			CompositeFuture cf=r.result();
			List<Object> results=cf.list();
			OrderDTO order=null;
			for (Object o:results) {
				if (o instanceof OrderPricedEvent) {
					OrderPricedEvent pe=(OrderPricedEvent)o;
					order=pe.getOrder();
				}
			}
			
			//do calculations
			log.debug("order calculated.");
			return order;
		});
			
		//TODO do price calculation
		//计算抵扣，活动不能抵扣
		//1.using price from shelf
		
		
		/*
		Future<List<ShelfDTO>> fPrices=this.shelfService.getPrices(this.order.getPurchaseItems());
		fPrices.onSuccess((list)->{
			for (ShelfDTO shelf:list) {
				log.info(this.infrastructure.toJson(shelf));
			}
		});
		*/
	}
	
	
	
	public void confirm() {
		this.infrastructure.publishEvent(new OrderCreatedEvent(this.order));
	}
}
