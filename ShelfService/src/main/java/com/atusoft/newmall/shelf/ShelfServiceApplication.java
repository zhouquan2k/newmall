package com.atusoft.newmall.shelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})

public class ShelfServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShelfServiceApplication.class, args);
	}

}
