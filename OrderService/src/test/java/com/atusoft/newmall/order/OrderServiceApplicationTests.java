package com.atusoft.newmall.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.Deduction;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.order.OrderDTO.PurchaseItem;
import com.atusoft.newmall.event.order.OrderCancelledEvent;
import com.atusoft.newmall.event.order.OrderCreatedEvent;
import com.atusoft.newmall.event.order.OrderExceptionEvent;
import com.atusoft.newmall.event.order.OrderExceptionEvent.Cause;
import com.atusoft.newmall.event.order.OrderSubmitedEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;
import com.atusoft.newmall.event.user.UserLoginEvent;
import com.atusoft.newmall.order.domain.Order;
import com.atusoft.test.BaseTest;

import io.vertx.core.Future;

class OrderServiceApplicationTests extends BaseTest {
		
	@Autowired
	OrderService orderService;
	
	static String orderId;
	static BaseEvent lastEvent;
	
	
	@BeforeEach
	public void init() {
	}
	

	@Test
	void contextLoads() {
		
	}
	
	@Test
	@org.junit.jupiter.api.Order(1)
	public void testLogin() {
		
		String json="{userId:\"27\",user:{userId:\"27\",username:\"zhouquan\",promoterLevel:\"Silver\"},_token:\"token_1\"}";
		System.out.println(json);
		UserLoginEvent event=this.jsonUtil.fromJson(json,UserLoginEvent.class);
		orderService.onUserLoginEvent(event);
		OrderDTO dto=this.jsonUtil.fromJson("{\"userId\":27,purchaseItems:[{skuId:\"sku_1\",shelfId:\"shelf_1\",count:2}],brokerageDeduction:{deduction: false},_token:\"token_1\"}", OrderDTO.class);
		assertEquals(infrastructure.getCurrentUser(dto).result().getUserId(),"27");
	}
	
	@Test 
	@org.junit.jupiter.api.Order(2)
	public void testPreview() throws InterruptedException {
		
		/*
		UserDTO user=new UserDTO();
		user.setNickname("zhouquan");
		user.setPromoterLevel(PromoterLevel.Silver);
		user.setUserId("27");
		this.infrastructure.persistEntity("user_token:token_1", user, 0);
		*/
		
		OrderDTO dto=this.jsonUtil.fromJson("{\"userId\":27,purchaseItems:[{skuId:\"sku_1\",shelfId:\"shelf_1\",count:2}],brokerageDeduction:{deduction: false},_token:\"token_1\"}", OrderDTO.class);
		Future<OrderDTO> future=orderService.PreviewOrder(dto); 
		Thread.sleep(20);
		OrderCreatedEvent event=infrastructure.assureEvent(OrderCreatedEvent.class);
		assertTrue(event!=null);
		orderId=event.getOrder().getOrderId();
		dto.setOrderId(orderId);
		
		//assert event/response/repository
		Thread.sleep(20);
		dto.setBalance(new BigDecimal("1000"));
		dto.setBrokerageDeduction(new Deduction(true,new BigDecimal("100"),null));
		orderService.onOrderDeductionBalancedEvent(new OrderDeductionBalancedEvent(dto));
		Thread.sleep(20);
		PurchaseItem pi=dto.getPurchaseItems().get(0);
		pi.setUnitPrice(new BigDecimal("50"));
		orderService.onOrderPricedEvent(new OrderPricedEvent(dto));
		
		//response is in the Future
		OrderDTO result=future.result();
		assertTrue(result.getTotalPrice().compareTo(new BigDecimal("100"))==0);
		assertTrue(result.getDeductionPrice().compareTo(new BigDecimal("100"))==0);
		assertTrue(result.getPayPrice().compareTo(new BigDecimal("0"))==0);
		System.out.println(result);
		
		Order order=infrastructure.getEntity(Order.class, orderId).result();
		result=order.getOrder();
		assertTrue(result.getTotalPrice().compareTo(new BigDecimal("100"))==0);
		assertTrue(result.getDeductionPrice().compareTo(new BigDecimal("100"))==0);
		assertTrue(result.getPayPrice().compareTo(new BigDecimal("0"))==0);
		
	}
	
	@Test
	@org.junit.jupiter.api.Order(3)
	public void testPurchase() throws InterruptedException {
		//Future<?> ret=
		orderService.SubmitOrder(orderId);
		OrderSubmitedEvent event=infrastructure.assureEvent(OrderSubmitedEvent.class);
		assertTrue(event!=null);
		lastEvent=event;
		Order order=infrastructure.getEntity(Order.class, orderId).result();
		assertEquals(order.getOrder().getStatus(),OrderDTO.Status.Submited);
	}
	
	@Test
	@org.junit.jupiter.api.Order(4)
	public void testCancelEvent() throws InterruptedException {
		//Future<?> ret=
		OrderExceptionEvent exception=new OrderExceptionEvent(lastEvent,orderId,Cause.ShelfOutOfStock,"Shelf OutOfStock");
		orderService.onOrderExceptionEvent(exception);
		
		OrderCancelledEvent event=infrastructure.assureEvent(OrderCancelledEvent.class);
		assertTrue(event!=null);
		Order order=infrastructure.getEntity(Order.class, orderId).result();
		assertEquals(order.getOrder().getStatus(),OrderDTO.Status.Cancelled);
		
	}
	

}
