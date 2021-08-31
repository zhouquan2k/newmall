package com.atusoft.newmall.shelf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.order.OrderDTO.PurchaseItem;
import com.atusoft.newmall.event.order.OrderCancelledEvent;
import com.atusoft.newmall.event.order.OrderCreatedEvent;
import com.atusoft.newmall.event.order.OrderExceptionEvent;
import com.atusoft.newmall.event.order.OrderSubmitedEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.UserLoginEvent;
import com.atusoft.newmall.shelf.domain.Shelf;
import com.atusoft.test.BaseTest;
import com.atusoft.util.BusiException;


class ShelfServiceApplicationTests extends BaseTest {

	@Autowired       
	ShelfService service;
	
	static int shelfStock=0;
	static String submitEventId;
	
	final String orderJson="{order:{orderId:\"order_1\",purchaseItems:[{skuId:\"sku_1\",shelfId:\"shelf_1\",count:2}]},_token:\"token_1\"}";
	final String orderJson2="{order:{orderId:\"order_2\",purchaseItems:[{skuId:\"sku_1\",shelfId:\"shelf_1\",count:2}]},_token:\"token_1\"}";
	@Test
	void contextLoads() {
	}
	
	@Test
	@Order(1)
	public void testLogin() {
		String json="{userId:\"27\",user:{userId:\"27\",username:\"zhouquan\",promoterLevel:\"Silver\"},_token:\"token_1\"}";
		System.out.println(json);
		UserLoginEvent event=this.jsonUtil.fromJson(json,UserLoginEvent.class);
		service.onUserLoginEvent(event);
		OrderDTO dto=this.jsonUtil.fromJson("{\"userId\":27,purchaseItems:[{skuId:\"sku_1\",shelfId:\"shelf_1\",count:2}],brokerageDeduction:{deduction: false},_token:\"token_1\"}", OrderDTO.class);
		assertEquals(infrastructure.getCurrentUser(dto).result().getUserId(),"27");
	}
	
	@Test
	@Order(2)
	public void testSaveShelf() {
		String json="{shelfId:\"shelf_1\",productId:\"product_1\",sku2Shelf:{\"sku_1\":"
				+"{originPrice:20,promoterPrices:{\"None\":18,\"Silver\":15,\"Gold\":12},stock:10 } }}";
		System.out.println(json);
		ShelfDTO dto=this.jsonUtil.fromJson(json,ShelfDTO.class);
		dto=service.SaveShelf(dto).result();
		assertEquals(dto.getShelfId(),"shelf_1");
		Shelf shelf=infrastructure.getEntity(Shelf.class,dto.getShelfId()).result();
		assertEquals(shelf.getShelf().getShelfId(),"shelf_1");
		shelfStock=shelf.getShelf().getSku2Shelf().get("sku_1").getStock();
		assertEquals(shelfStock,10);
	}
	
	@Test
	@Order(3)
	public void testShelfPrice() {
		String json=orderJson;
		OrderCreatedEvent event=this.jsonUtil.fromJson(json,OrderCreatedEvent.class);
		service.onOrderCreatedEvent(event);
		OrderPricedEvent pEvent=infrastructure.assureEvent(OrderPricedEvent.class);
		assertTrue(pEvent.getOrder().getPurchaseItems().get(0).getUnitPrice()!=null);
		
	}
	
	@Test
	@Order(4)
	public void testOrderSubmited() {
		String json=orderJson;
		OrderSubmitedEvent event=this.jsonUtil.fromJson(json,OrderSubmitedEvent.class);
		submitEventId=event.getEventId();
		service.onOrderSubmitedEvent(event);
		PurchaseItem pi=event.getOrder().getPurchaseItems().get(0);
		Shelf shelf=infrastructure.getEntity(Shelf.class,pi.getShelfId()).result();
		assertEquals(shelfStock-pi.getCount(),shelf.getShelf().getSku2Shelf().get(pi.getSkuId()).stock);
		System.out.println(shelf.getShelf());
		shelfStock=shelf.getShelf().getSku2Shelf().get(pi.getSkuId()).getStock();
	}
	
	@Test
	@Order(5)
	public void testOrderCancelledEvent() {
		String json=orderJson;
		OrderCancelledEvent event=this.jsonUtil.fromJson(json,OrderCancelledEvent.class);
		event.setCauseEventId(submitEventId);
		service.onOrderCancelledEvent(event);
		PurchaseItem pi=event.getOrder().getPurchaseItems().get(0);
		Shelf shelf=infrastructure.getEntity(Shelf.class,pi.getShelfId()).result();
		assertEquals(shelfStock+pi.getCount(),shelf.getShelf().getSku2Shelf().get(pi.getSkuId()).stock);
		shelfStock=shelf.getShelf().getSku2Shelf().get(pi.getSkuId()).stock;
		
	}
	
	@Test
	@Order(6)
	public void testOrderSubmitedWithOutOfShelf() {
		String json=orderJson2;
		OrderSubmitedEvent event=this.jsonUtil.fromJson(json,OrderSubmitedEvent.class);
		submitEventId=event.getEventId();
		event.getOrder().getPurchaseItems().get(0).setCount(20);
		service.onOrderSubmitedEvent(event);
		OrderExceptionEvent eEvent=infrastructure.assureEvent(OrderExceptionEvent.class);
		assertEquals(eEvent.getCause(),OrderExceptionEvent.Cause.ShelfOutOfStock);
		assertEquals(((BusiException)eEvent.getException()).getLocation(),"Shelf");
		
		PurchaseItem pi=event.getOrder().getPurchaseItems().get(0);
		Shelf shelf=infrastructure.getEntity(Shelf.class,pi.getShelfId()).result();
		assertEquals(shelfStock,shelf.getShelf().getSku2Shelf().get(pi.getSkuId()).stock);
		System.out.println(shelf.getShelf());
	}
	
	@Test
	@Order(7)
	public void testOrderCancelledEventWhenExceptionAlreadyThrown() {
		String json=orderJson;
		OrderCancelledEvent event=this.jsonUtil.fromJson(json,OrderCancelledEvent.class);
		event.setCauseEventId(submitEventId);
		service.onOrderCancelledEvent(event);
		PurchaseItem pi=event.getOrder().getPurchaseItems().get(0);
		Shelf shelf=infrastructure.getEntity(Shelf.class,pi.getShelfId()).result();
		infrastructure.dump();
		assertEquals(shelfStock,shelf.getShelf().getSku2Shelf().get(pi.getSkuId()).stock);
		
		

	}
	
	
}
