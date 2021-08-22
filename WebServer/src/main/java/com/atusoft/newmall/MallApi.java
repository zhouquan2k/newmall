package com.atusoft.newmall;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.atusoft.infrastructure.RestApi;
import com.atusoft.infrastructure.RestApi.ApiEntry;
import static  com.atusoft.infrastructure.RestApi.Method.*;

@Configuration 
public class MallApi {

	@Bean
	RestApi apiFactory() {
		return new RestApi(new ApiEntry[] {
			new ApiEntry(POST,"/order","Order.CreateOrder"),
			
			new ApiEntry(POST,"/security","Security.Login"),
			
		});
	}
}
