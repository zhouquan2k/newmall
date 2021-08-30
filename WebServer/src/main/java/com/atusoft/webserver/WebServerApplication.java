package com.atusoft.webserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;


@SpringBootApplication
@ImportResource({"classpath*:webserver-applicationContext.xml"})
//@ComponentScan({"com.atusoft"})
public class WebServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebServerApplication.class, args);
	}

}


/*
@Configuration
class AppConfig {
	@Bean
	public MessageContext messageContext() {
		return new KafkaMessageContext();
	}
}
*/
