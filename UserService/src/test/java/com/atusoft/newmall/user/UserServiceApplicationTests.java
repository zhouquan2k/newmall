package com.atusoft.newmall.user;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.atusoft.newmall.dto.user.AccountDTO;
import com.atusoft.newmall.dto.user.PromoterLevel;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.util.JsonUtil;

@SpringBootTest
class UserServiceApplicationTests {

	@Autowired
	UserService userService;
	
	@Autowired
	JsonUtil jsonUtil;
	
	@Test
	void contextLoads() {
	}
	
	@Test
	public void testSaveUser() {
		UserDTO user=new UserDTO();
		user.setUserId("27");
		user.setNickname("zhouquan");
		user.setPromoterLevel(PromoterLevel.Silver);
		userService.SaveUser(user);
	}
	
	@Test
	public void testSaveAccount() {
		AccountDTO dto=new AccountDTO();
		dto.setUserId("27");
		dto.setBrokerage(new BigDecimal(100));
		dto.setBalance(new BigDecimal(1000));
		userService.SaveAccount(dto);
		
	}
	
	@AfterAll
	static public void exit() throws InterruptedException {
		Thread.sleep(60000);
	}

}
