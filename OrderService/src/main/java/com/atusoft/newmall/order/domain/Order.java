package com.atusoft.newmall.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import com.atusoft.infrastructure.BaseEntity;
import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.CartDTO;
import com.atusoft.newmall.dto.order.DeductionOptions;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.order.OrderDTO.PayMethod;
import com.atusoft.newmall.dto.order.OrderDTO.Status;
import com.atusoft.newmall.dto.order.PurchaseItem;
import com.atusoft.newmall.event.order.OrderPreviewEvent;
import com.atusoft.newmall.event.order.OrderCancelledEvent;
import com.atusoft.newmall.event.order.OrderPaidEvent;
import com.atusoft.newmall.event.order.OrderSubmitedEvent;
import com.atusoft.newmall.event.order.ToOrderPaidEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.DeductionBalancedEvent;
import com.atusoft.util.BusiException;
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
		//this.order=order;
		this.order=order.toBuilder().build(); //copy from it instead of reference it.
	}
	
	public static Order create(CartDTO cart,DeductionOptions deductionOptions) {
		OrderDTO dto=OrderDTO.builder().build();
		dto.setStatus(Status.Preview);
		dto.setOrderId(infrastructure.getUUID());
		dto.setCart(cart.toBuilder().build()); // copy, not reference
		if (deductionOptions!=null) dto.setDeductionOptions(deductionOptions);
		Order order=new Order(dto);
		return order;
	}
	
	
	public OrderDTO getOrder() {
		//copy one new
		return this.order.toBuilder().build();
	}
	
	@Override
	public String getId() {
		return this.order.getOrderId();
	}
	
	
	public Future<OrderDTO> preview() {
		
		//this.order.setOrderId(this.infrastructure.getUUID());
		//Future<User> fUser=this.infrastructure.getCurrentUser(this.order);
		//constraints:
		//1.address required
		
		Promise<?> pPriced=infrastructure.addPendingFuture("order:"+this.getId()+":price");
		Promise<?> pDeduction=infrastructure.addPendingFuture("order:"+this.getId()+":deduction");
		
		this.save(new OrderPreviewEvent(this.order),60*10);
		/*
		fUser=Util.onSuccess(fUser,user->{
			if (user==null) throw new BusiException("Authorization","not logged in",null);
			this.order.setUserId(user.getUserId());
			this.order.setStatus(Status.Preview);
			this.save(new OrderCreatedEvent(this.order),60*10);
			return Future.succeededFuture();
		});
		*/
		
		return CompositeFuture.all(pPriced.future(),  pDeduction.future()).map(r->{
		
			//TODO do price calculation
			//计算抵扣，活动不能抵扣
			//1.using price from shelf
			CompositeFuture cf=r.result();
			
			OrderPricedEvent pe=Util.getFutureResult(cf, OrderPricedEvent.class);
			
			
			for (PurchaseItem item:pe.getOrder().getCart().getPurchaseItems()) {
				//shelf out of stock check 
				if (item.getCount()>item.getStock()) 
					this.order.setStatus(Status.Cancelled); 
			}
			
			this.order.getCart().setPurchaseItems(pe.getOrder().getCart().getPurchaseItems());
			Stream<BigDecimal> s=this.order.getCart().getPurchaseItems().stream().
				map( pi -> pi.getUnitPrice().multiply(new BigDecimal(""+pi.getCount())) );
			BigDecimal total=s.reduce(BigDecimal::add).get();
			order.setTotalPrice(total);
			
			DeductionBalancedEvent de=Util.getFutureResult(cf, DeductionBalancedEvent.class);
			BigDecimal deduction=BigDecimal.ZERO;
			if (this.order.getDeductionOptions().getBrokerageDeduction()!=null
					&&this.order.getDeductionOptions().getBrokerageDeduction().isDeduction()) {
				
				BigDecimal deducted=de.getAccount().getBrokerage().min(total);
				order.getDeductionOptions().getBrokerageDeduction().setDeducted(deducted);
				total=total.subtract(deducted);
				deduction=deduction.add(deducted);
			}
			order.setBalance(de.getAccount().getBalance());
			//TODO
			
			order.setPayPrice(total);
			order.setDeductionPrice(deduction);
			
			//do calculations
			log.debug("order calculated."+total);
			this.save(null,60*10);
			
			return order;
		});
	}

	public Future<OrderDTO> submit(PayMethod payMethod) {
		if (this.order.getStatus()!=Status.Preview) throw new BusiException("InvalidStatus","invalid order status:"+this.order.getStatus(),"Order");
		
		this.order.setSubmitTime(LocalDateTime.now());
		this.order.setPayMethod(payMethod);
		this.order.setStatus(Status.Submited);
		
		return this.save(new OrderSubmitedEvent(this.order)).map(o->((Order)o).getOrder());
		
	}
	
	
	public void onPaid(ToOrderPaidEvent paid) {
		if (paid.isSuccess()) {
			this.order.setStatus(Status.Paid);
			this.save(new OrderPaidEvent(this.order));
		}
	}
	
	public void cancel(BaseEvent event) {
		this.order.setStatus(Status.Cancelled);
		this.save(new OrderCancelledEvent(event.getCauseEventId(),this.order));
		
	}
	
	
}
