package com.atusoft.newmall.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ActiveProfiles;

import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.PersistUtil;
import com.atusoft.newmall.dto.order.Deduction;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.order.OrderDTO.PurchaseItem;
import com.atusoft.newmall.dto.user.PromoterLevel;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.newmall.event.order.OrderCreatedEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;
import com.atusoft.newmall.event.user.UserLoginEvent;
import com.atusoft.test.BaseTest;
import com.atusoft.test.Infrastructure4Test;
import com.atusoft.test.MyTestConfiguration;
import com.atusoft.test.TestPersistUtil;

import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;;

@SpringBootTest
@ActiveProfiles("test")
@Import(MyTestConfiguration.class)
@Slf4j
class OrderServiceApplicationTests extends BaseTest {
	
	
		
	@Autowired
	OrderService orderService;
	
	
	@BeforeEach
	public void init() {
	}
	

	@Test
	void contextLoads() {
		
	}
	
	@Test
	public void testLogin() {
		String json="{userId:\"27\",user:{userId:\"27\",username:\"zhouquan\",promoterLevel:\"Silver\"},_token:\"token_1\"}";
		System.out.println(json);
		UserLoginEvent event=this.jsonUtil.fromJson(json,UserLoginEvent.class);
		orderService.onUserLoginEvent(event);
		OrderDTO dto=this.jsonUtil.fromJson("{\"userId\":27,purchaseItems:[{skuId:\"sku_1\",shelfId:\"shelf_1\",count:2}],brokerageDeduction:{deduction: false},_token:\"token_1\"}", OrderDTO.class);
		assertEquals(this.infrastructure.getCurrentUser(dto).result().getUserId(),"27");
	}
	
	@Test 
	public void testPreview() throws InterruptedException {
		
		UserDTO user=new UserDTO();
		user.setNickname("zhouquan");
		user.setPromoterLevel(PromoterLevel.Silver);
		user.setUserId("27");
		this.infrastructure.persistEntity("user_token:token_1", user, 0);
		
		OrderDTO dto=this.jsonUtil.fromJson("{\"userId\":27,purchaseItems:[{skuId:\"sku_1\",shelfId:\"shelf_1\",count:2}],brokerageDeduction:{deduction: false},_token:\"token_1\"}", OrderDTO.class);
		Future<OrderDTO> future=orderService.PreviewOrder(dto); 
		Thread.sleep(50);
		OrderCreatedEvent event=infrastructure.assureEvent(OrderCreatedEvent.class);
		assertTrue(event!=null);
		String orderId=event.getOrder().getOrderId();
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
				
		
	}

}
