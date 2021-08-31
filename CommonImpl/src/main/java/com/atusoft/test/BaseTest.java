package com.atusoft.test;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.atusoft.messaging.MessageContext;
import com.atusoft.redis.RedisUtil;
import com.atusoft.util.JsonUtil;



@SpringBootTest
@ActiveProfiles("test")
@Import(MyTestConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseTest {
	
		
	@MockBean
	MessageContext messageContext;
	
	@MockBean
	RedisUtil redisUtil;
	
	
	@Autowired 
	protected JsonUtil jsonUtil;
	
	@Autowired 
	protected Infrastructure4Test _infrastructure;
	
	protected static Infrastructure4Test infrastructure;
	
	@PostConstruct
	void _init() {
		if (infrastructure==null) infrastructure=_infrastructure;
	}
}
	
