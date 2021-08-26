package com.atusoft.redis;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;

public class RedisUtil  extends AbstractVerticle {
	
	@Autowired
	Vertx vertx;
	
	//RedisConnection conn;
	RedisAPI redis;

	@PostConstruct
	public void init() {
		this.vertx.deployVerticle(this);
		
		
		Redis r=Redis.createClient(
				  vertx,
				  // The client handles REDIS URLs. The select database as per spec is the
				  // numerical path of the URL and the password is the password field of
				  // the URL authority
				  "redis://localhost:6379/1");  
				
		r.connect()
				 .onSuccess(conn -> {
				    // use the connection
					//that.conn=conn;
					 this.redis = RedisAPI.api(r);	
				 
				  }).onFailure(e->{
					  e.printStackTrace();
				  });
		
	}
	
	public RedisAPI getRedis() {
		return this.redis;
	}
	
	
	
}
