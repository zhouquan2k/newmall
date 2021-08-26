package com.atusoft.newmall.shelf;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.atusoft.newmall.event.order.OrderCreatedEvent;
import com.atusoft.newmall.event.user.UserLoginEvent;
import com.atusoft.util.JsonUtil;

@SpringBootTest
class ShelfServiceApplicationTests {

	@Autowired
	ShelfService service;
	
	@Autowired 
	JsonUtil jsonUtil;
	
	
	@Test
	void contextLoads() {
	}
	
	@Test
	public void testLogin() {
		String json="{userId:\"27\",user:{userId:\"27\",username:\"zhouquan\",promoterLevel:\"Silver\"},_token:\"token_1\"}";
		System.out.println(json);
		UserLoginEvent event=this.jsonUtil.fromJson(json,UserLoginEvent.class);
		service.onUserLoginEvent(event);
	}
	
	@Test
	public void testSaveShelf() {
		String json="{shelfId:\"shelf_1\",productId:\"product_1\",sku2Shelf:{\"sku_1\":"
				+"{originPrice:20,promoterPrices:{\"None\":18,\"Silver\":15,\"Gold\":12} } }}";
		System.out.println(json);
		ShelfDTO dto=this.jsonUtil.fromJson(json,ShelfDTO.class);
		service.SaveShelf(dto);
		
	}
	
	@Test
	public void testShelfPrice() {
		String json="{order:{orderId:\"order_1\",purchaseItems:[{skuId:\"sku_1\",shelfId:\"shelf_1\",count:2}]},_token:\"token_1\"}";
		System.out.println(json);
		OrderCreatedEvent event=this.jsonUtil.fromJson(json,OrderCreatedEvent.class);
		service.onOrderCreatedEvent(event);
	}
	
	@AfterAll
	static public void exit() throws InterruptedException {
		Thread.sleep(60000);
	}

}
