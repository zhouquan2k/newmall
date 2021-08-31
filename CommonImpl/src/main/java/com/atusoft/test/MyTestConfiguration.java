package com.atusoft.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.atusoft.framwork.PersistUtil;
import com.atusoft.infrastructure.Infrastructure;

@TestConfiguration
@Profile("test")
public class MyTestConfiguration {

	// tests specific beans
	@Bean
	@Primary
	Infrastructure testInfrastructure() {
		return new Infrastructure4Test();
	}

	@Bean
	@Primary
	PersistUtil testPersistUtil() {
		return new TestPersistUtil();
	}
}
