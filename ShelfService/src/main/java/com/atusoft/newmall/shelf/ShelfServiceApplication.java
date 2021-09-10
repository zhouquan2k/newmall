package com.atusoft.newmall.shelf;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})

public class ShelfServiceApplication {
	
	@Autowired
	ShelfService shelfService;
	
	@Bean Map<String,Object> allServices() {
		return Map.of("Shelf",shelfService);
	}

	public static void main(String[] args) {
		SpringApplication.run(ShelfServiceApplication.class, args);
	}

}
