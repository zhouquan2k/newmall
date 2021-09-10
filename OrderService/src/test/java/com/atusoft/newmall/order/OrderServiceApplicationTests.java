package com.atusoft.newmall.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.CartDTO;
import com.atusoft.newmall.dto.order.DeductionOptions;
import com.atusoft.newmall.dto.order.DeductionOptions.Deduction;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.order.OrderDTO.PayMethod;
import com.atusoft.newmall.dto.order.PurchaseItem;
import com.atusoft.newmall.dto.user.AccountDTO;
import com.atusoft.newmall.event.order.OrderPreviewEvent;
import com.atusoft.newmall.event.order.OrderCancelledEvent;
import com.atusoft.newmall.event.order.OrderExceptionEvent;
import com.atusoft.newmall.event.order.OrderExceptionEvent.Cause;
import com.atusoft.newmall.event.order.OrderPaidEvent;
import com.atusoft.newmall.event.order.OrderSubmitedEvent;
import com.atusoft.newmall.event.order.ToOrderPaidEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.DeductionBalancedEvent;
import com.atusoft.newmall.event.user.UserLoginEvent;
import com.atusoft.newmall.order.domain.Order;
import com.atusoft.test.BaseTest;

import io.vertx.core.Future;

class OrderServiceApplicationTests extends BaseTest {
		
	@Autowired
	OrderService orderService;
	
	@Autowired
	CartService cartService;
	
	static String cartId;
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
		assertEquals(infrastructure.getCurrentUser(dto).result().orElseThrow().getUserId(),"27");
	}
	
	
	@Test
	@org.junit.jupiter.api.Order(2)
	public void testSinglePurchase() throws Throwable {
		
		//String json="[{skuId:\"sku_1\",shelfId:\"shelf_1\",count:2}],token:\"token_1\"}"
		PurchaseItem item=PurchaseItem.builder().skuId("sku_1").shelfId("shelf_1").count(2).build();
		item.set_token("token_1");
		
		//...
		CartDTO cart=getResult(cartService.singlePurchase(item));
		assertEquals(cart.getPurchaseItems().size(),1);
		cartId=cart.getCartId();
		assertTrue(cartId.length()>10);
	}
	
	@Test 
	@org.junit.jupiter.api.Order(3)
	public void testPreview() throws Throwable {
		
		DeductionOptions deductionOptions=new DeductionOptions();
		deductionOptions.setBrokerageDeduction(new Deduction(true,BigDecimal.ZERO,BigDecimal.ZERO));
		Future<OrderDTO> fOrder=orderService.previewOrder(cartId, deductionOptions); 
		OrderPreviewEvent event=infrastructure.assureEvent(OrderPreviewEvent.class);
		assertTrue(event!=null);
		orderId=event.getOrder().getOrderId();
		System.out.println("previewing order:"+orderId);
		assertTrue(orderId!=null&&orderId.length()>10);
		assertEquals(event.getOrder().getCart().getCartId(),cartId);
		
		
	
		//assert event/response/repository
		
		//...
		Thread.sleep(20);
		AccountDTO account=new AccountDTO();
		account.setBalance(new BigDecimal("1000"));
		account.setBrokerage(new BigDecimal("100"));
		orderService.onDeductionBalancedEvent(new DeductionBalancedEvent(event.getOrder().getOrderId(),account));
		Thread.sleep(20);
		
		OrderDTO order=event.getOrder();
		PurchaseItem pi=order.getCart().getPurchaseItems().get(0);
		pi.setUnitPrice(new BigDecimal("50"));
		pi.setStock(100);
		orderService.onOrderPricedEvent(new OrderPricedEvent(order));
		
		//response is in the Future
		OrderDTO result=getResult(fOrder);
		System.out.println(result);
		assertEquals(new BigDecimal("100"),result.getTotalPrice());
		assertEquals(new BigDecimal("100"),result.getDeductionPrice());
		assertEquals(new BigDecimal("0"),result.getPayPrice());
		
		
		order=infrastructure.getEntity(Order.class, orderId).result().orElseThrow().getOrder();
		assertEquals(new BigDecimal("100"),order.getTotalPrice());
		assertEquals(new BigDecimal("100"),order.getDeductionPrice());
		assertEquals(new BigDecimal("0"),order.getPayPrice());
		
	}
	
	@Test
	@org.junit.jupiter.api.Order(4)
	public void testSubmit() throws Throwable {
		//Future<?> ret=
		getResult(orderService.submitOrder(orderId,PayMethod.WeChatPay));
		OrderSubmitedEvent event=infrastructure.assureEvent(OrderSubmitedEvent.class);
		assertTrue(event!=null);
		lastEvent=event;
		Order order=infrastructure.getEntity(Order.class, orderId).result().orElseThrow();
		assertEquals(order.getOrder().getStatus(),OrderDTO.Status.Submited);
	}
	
	@Test
	@org.junit.jupiter.api.Order(5)
	public void testCancelEvent() throws InterruptedException {
		//Future<?> ret=
		OrderExceptionEvent exception=new OrderExceptionEvent(lastEvent,orderId,Cause.ShelfOutOfStock,"Shelf OutOfStock");
		orderService.onOrderExceptionEvent(exception);
		
		OrderCancelledEvent event=infrastructure.assureEvent(OrderCancelledEvent.class);
		assertTrue(event!=null);
		Order order=infrastructure.getEntity(Order.class, orderId).result().orElseThrow();
		assertEquals(order.getOrder().getStatus(),OrderDTO.Status.Cancelled);
		
	}
	
	
	@Test 
	@org.junit.jupiter.api.Order(6)
	public void testPayByBalance() throws Throwable {
		testPreview();
		
		getResult(orderService.submitOrder(orderId,PayMethod.Balance));
		OrderSubmitedEvent event=infrastructure.assureEvent(OrderSubmitedEvent.class);
		assertTrue(event!=null);
		lastEvent=event;
		Order order=infrastructure.getEntity(Order.class, orderId).result().orElseThrow();
		assertEquals(order.getOrder().getStatus(),OrderDTO.Status.Submited);
		
		//pay not success
		orderService.onToOrderPaidEvent(new ToOrderPaidEvent(orderId,PayMethod.Balance,false));
		
		Thread.sleep(50);
		order=infrastructure.getEntity(Order.class, orderId).result().orElseThrow();
		assertEquals(order.getOrder().getStatus(),OrderDTO.Status.Submited);
		
		//pay success
		orderService.onToOrderPaidEvent(new ToOrderPaidEvent(orderId,PayMethod.Balance,true));
		
		order=infrastructure.getEntity(Order.class, orderId).result().orElseThrow();
		assertEquals(order.getOrder().getStatus(),OrderDTO.Status.Paid);
		infrastructure.assureEvent(OrderPaidEvent.class);
	}

}
