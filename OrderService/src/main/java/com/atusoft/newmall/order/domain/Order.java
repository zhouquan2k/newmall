package com.atusoft.newmall.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import com.atusoft.infrastructure.BaseEntity;
import com.atusoft.infrastructure.User;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.order.OrderDTO.Status;
import com.atusoft.newmall.event.order.OrderCreatedEvent;
import com.atusoft.newmall.event.order.OrderSubmitedEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;
import com.atusoft.util.Util;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Order  extends BaseEntity {
	
	final OrderDTO order;
	
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public Order(@JsonProperty("order") OrderDTO order){
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
		
		Util.onSuccess(fUser,user->{
			this.order.setUserId(user.getUserId());
			this.infrastructure.persistEntity(this.order.getOrderId(),this, 60*10);
			this.infrastructure.publishEvent(new OrderCreatedEvent(this.order));
			return Future.succeededFuture();
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
			
			
			OrderDeductionBalancedEvent de=Util.getFutureResult(cf, OrderDeductionBalancedEvent.class);
			BigDecimal deduction=BigDecimal.ZERO;
			if (order.getBrokerageDeduction()!=null&&order.getBrokerageDeduction().isDeduction()) {	
				order.getBrokerageDeduction().setDeducted(de.getOrder().getBrokerageDeduction().getBalance().min(total));
				total=total.subtract(order.getBrokerageDeduction().getDeducted());
				deduction=deduction.add(order.getBrokerageDeduction().getDeducted());
			}
			this.order.setBalance(de.getOrder().getBalance());
			//TODO
			
			this.order.setPayPrice(total);
			this.order.setDeductionPrice(deduction);
			this.save(10*60);
			
			//do calculations
			log.debug("order calculated."+total);
			return order;
		});
	}
	
	public OrderDTO getOrder() {
		return this.order;
	}
	
	@Override
	public String getId() {
		return this.order.getOrderId();
	}
	

	public void submit() {
		this.order.setSubmitTime(LocalDateTime.now());
		this.order.setStatus(Status.Submited);
		this.save();
		this.infrastructure.publishEvent(new OrderSubmitedEvent(this.order));
	}
}
