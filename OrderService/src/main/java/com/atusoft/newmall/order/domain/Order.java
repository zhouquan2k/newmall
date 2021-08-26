package com.atusoft.newmall.order.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.User;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.order.OrderDTO.PurchaseItem;
import com.atusoft.newmall.event.order.OrderCreatedEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.util.Util;

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
	
		
	public Order(OrderDTO order){
		this.order=order;
		//TODO copy from it instead of reference it.
	}
	
	
	public Future<OrderDTO> review() {
		this.order.setOrderId(this.infrastructure.getUUID());
		Future<User> fUser=this.infrastructure.getCurrentUser(this.order);
		//constraints:
		//1.address required
		
		Promise<?> pPriced=this.infrastructure.addPendingFuture("order:"+this.order.getOrderId()+":price");
		Promise<?> pDeduction=this.infrastructure.addPendingFuture("order:"+this.order.getOrderId()+":deduction");
		
		fUser.onSuccess(user->{
			this.order.setUserId(user.getUserId());
			this.infrastructure.persistEntity(this.order.getOrderId(),order, 60*10);
			this.infrastructure.publishEvent(new OrderCreatedEvent(this.order));
		});
		
	
		return CompositeFuture.all(pPriced.future(),  pDeduction.future()).map(r->{
		
			//TODO do price calculation
			//计算抵扣，活动不能抵扣
			//1.using price from shelf
			CompositeFuture cf=r.result();
			List<Object> results=cf.list();
			
			OrderPricedEvent pe=Util.getFutureResult(cf, OrderPricedEvent.class);
			this.order.setPurchaseItems(pe.getOrder().getPurchaseItems());
			Stream<BigDecimal> s=this.order.getPurchaseItems().stream().
				map( pi -> pi.getUnitPrice().multiply(new BigDecimal(""+pi.getCount())) );
			BigDecimal total=s.reduce(BigDecimal::add).get();
			this.order.setTotalPrice(total);
			//TODO
			this.order.setPayPrice(total);
			
			//do calculations
			log.debug("order calculated."+total);
			return order;
		});
			
		
		
		
		/*
		 RPC call other service
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
