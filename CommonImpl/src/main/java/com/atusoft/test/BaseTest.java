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

import io.vertx.core.Future;



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
	
	protected <T> T getResult(Future<T> f) throws Throwable {
		int count=0;
		while (!f.isComplete()) {
			Thread.sleep(50);
			count++;
			if (count>100) throw new RuntimeException("wait for result timeout");
		}
		if (f.failed())  throw f.cause();
		return f.result();
	}
}
	
