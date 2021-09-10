package com.atusoft.newmall.order;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})
//@ComponentScan({"com.atusoft.newmall"})
public class OrderServiceApplication {
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	CartService cartService;
	
	@Bean Map<String,Object> allServices() {
		return Map.of("Order",orderService,"Cart",cartService);
	}

	public static void main(String[] args) {
		try {
			SpringApplication.run(OrderServiceApplication.class, args);	
		}
		catch (Throwable e) {
			e.printStackTrace(); 
		}
	}

}
