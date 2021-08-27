package com.atusoft.newmall.order;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;
import com.atusoft.newmall.event.user.UserLoginEvent;
import com.atusoft.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class OrderServiceApplicationTests {
	
	@Autowired
	OrderService orderService;
	
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
		orderService.onUserLoginEvent(event);
	}
	
	@Test 
	public void testPreview() throws InterruptedException {
		
		
		
		OrderDTO dto=this.jsonUtil.fromJson("{purchaseItems:[ {productId:\"1\",skuId:\"1.1\"} ],_token:\"token_1\"}",OrderDTO.class);
		orderService.PreviewOrder(dto); 
		
		//assert event/response/repository
		
		
		
		
		Thread.sleep(50);
		orderService.onOrderDeductionBalancedEvent(new OrderDeductionBalancedEvent(dto));
		Thread.sleep(20);
		orderService.onOrderPricedEvent(new OrderPricedEvent(dto));
		
		//response is in the Future
				
		
	}

	
	@AfterAll
	static public void exit() throws InterruptedException {
		Thread.sleep(60000);
	}
}
