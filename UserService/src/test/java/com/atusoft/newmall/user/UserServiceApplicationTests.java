package com.atusoft.newmall.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.newmall.dto.user.AccountDTO;
import com.atusoft.newmall.dto.user.PromoterLevel;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.newmall.event.order.OrderCancelledEvent;
import com.atusoft.newmall.event.order.OrderSubmitedEvent;
import com.atusoft.newmall.event.user.UserLoginEvent;
import com.atusoft.test.BaseTest;

class UserServiceApplicationTests extends BaseTest {

	@Autowired
	UserService userService;
	
	static String submitEventId;
	static BigDecimal brokerageBalance;
	
	
	@Test
	void contextLoads() {
	}
	
	@Test
	@Order(1)
	public void testSaveUser() {
		UserDTO user=new UserDTO();
		user.setUserId("27");
		user.setNickname("zhouquan");
		user.setPromoterLevel(PromoterLevel.Silver);
		userService.SaveUser(user);
	}
	
	@Test
	@Order(1)
	public void testSaveAccount() {
		AccountDTO dto=new AccountDTO();
		dto.setUserId("27");
		dto.setBrokerage(new BigDecimal(100));
		dto.setBalance(new BigDecimal(1000));
		userService.SaveAccount(dto);
		
	}
	
	final String orderJson="{order:{orderId:\"order_1\",purchaseItems:[{skuId:\"sku_1\",shelfId:\"shelf_1\",count:2}],brokerageDeduction:{deduction:true,deducted:5},deductionPrice:5,userId:\"27\"},_token:\"token_1\"}";

	
	@Test
	@Order(1)
	public void testLogin() {
		
		String json="{userId:\"27\",user:{userId:\"27\",username:\"zhouquan\",promoterLevel:\"Silver\"},_token:\"token_1\"}";
		System.out.println(json);
		UserLoginEvent event=this.jsonUtil.fromJson(json,UserLoginEvent.class);
		userService.onUserLoginEvent(event);
	}
	
	@Test
	@Order(2)
	public void testBrokerageDeduction() {		
		OrderSubmitedEvent event=this.jsonUtil.fromJson(orderJson,OrderSubmitedEvent.class);
		submitEventId=event.getEventId();
		String userId=infrastructure.getCurrentUser(event).result().getUserId();
		User user=infrastructure.getEntity(User.class,userId).result();
		//BigDecimal initBalance=user.getAccount().getBalance();
		BigDecimal initBrokerageBalance=user.getAccount().getBrokerage();
		this.userService.onOrderSubmitedEvent(event);
		User user2=infrastructure.getEntity(User.class,userId).result();
		assertEquals(initBrokerageBalance.subtract(event.getOrder().getDeductionPrice()),user2.getAccount().getBrokerage());
		brokerageBalance=user2.getAccount().getBrokerage();
	}
	
	@Test
	@Order(3)
	public void testCancelOrder() {		
		String json=orderJson;
		OrderCancelledEvent event=this.jsonUtil.fromJson(json,OrderCancelledEvent.class);
		event.setCauseEventId(submitEventId);
		userService.onOrderCancelledEvent(event);
		User user=infrastructure.getEntity(User.class,event.getOrder().getUserId()).result();
		assertEquals(brokerageBalance.add(event.getOrder().getDeductionPrice()),user.getAccount().getBrokerage());
		infrastructure.dump();
	}
	

}
