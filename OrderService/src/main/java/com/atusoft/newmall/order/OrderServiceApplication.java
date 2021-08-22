package com.atusoft.newmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})
@ComponentScan({"com.atusoft.newmall"})
public class OrderServiceApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(OrderServiceApplication.class, args);	
		}
		catch (Throwable e) {
			e.printStackTrace(); 
		}
	}

}
