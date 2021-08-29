package com.atusoft.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ActiveProfiles;

import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.PersistUtil;
import com.atusoft.messaging.MessageContext;
import com.atusoft.redis.RedisUtil;
import com.atusoft.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class BaseTest {
	
	/*
	@TestConfiguration
	@Profile("test")
	@Scope("prototype")
    static class MyTestConfiguration {
 
        //tests specific beans
		 @Bean
		 @Primary
		 Infrastructure testInfrastructure(){
		     return new Infrastructure4Test();
		 }
		    
		 @Bean
		 @Primary
		 PersistUtil testPersistUtil(){
		    return new TestPersistUtil();
		 }
    }
    */
	
	@MockBean
	MessageContext messageContext;
	
	@MockBean
	RedisUtil redisUtil;
	
	
	@Autowired 
	protected JsonUtil jsonUtil;
	
	@Autowired 
	protected Infrastructure4Test infrastructure;
}
	
