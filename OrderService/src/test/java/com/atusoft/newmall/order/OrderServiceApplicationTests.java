package com.atusoft.newmall.order;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;
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
	public void testPreview() throws InterruptedException {
		OrderDTO dto=this.jsonUtil.fromJson("{purchaseItems:[ {productId:\"1\",skuId:\"1.1\"} ]}",OrderDTO.class);
		orderService.PreviewOrder(dto); 
		
		Thread.sleep(50);
		orderService.onOrderDeductionBalancedEvent(new OrderDeductionBalancedEvent(dto));
		Thread.sleep(20);
		orderService.onOrderPricedEvent(new OrderPricedEvent(dto));
		
	}

	
	@AfterAll
	static public void exit() throws InterruptedException {
		Thread.sleep(60000);
	}
}
