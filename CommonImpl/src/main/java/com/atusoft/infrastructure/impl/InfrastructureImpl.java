package com.atusoft.infrastructure.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.atusoft.infrastructure.BaseDTO;
import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.User;
import com.atusoft.messaging.Message;
import com.atusoft.messaging.MessageContext;
import com.atusoft.redis.RedisUtil;
import com.atusoft.util.JsonUtil;
import com.atusoft.util.SecurityUtil;
import com.atusoft.util.Util;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.redis.client.Response;

class InfrastructureImpl implements Infrastructure  {

	@Autowired
	MessageContext messageContext;
	
	@Autowired 
	JsonUtil jsonUtil;
	
	@Value("${app.service-name}")
	String serviceName;
	
	@Autowired
	ApplicationContext appCtx;
	
	@Autowired
	SecurityUtil securityUtil;
	
	@Autowired 
	RedisUtil redisUtil;
	
	Map<String,Promise<?>> allPendingFutures=new HashMap<String,Promise<?>>();
	
	
	@PostConstruct
	public void init() {
		
	}
	
	
	@Override
	public Future<User> getCurrentUser(BaseDTO dto) {
		return this.securityUtil.getCurrentUser(dto.get_token());
	}
	
	@Override
	public Future<User> getCurrentUser(BaseEvent event) {
		return this.securityUtil.getCurrentUser(event.get_token());
	}
	
	@Override
	public <T> Future<T> getEntity(Class<T> cls, String key) {
		return this.redisUtil.getRedis().get(key).map(response->{
			if (response==null) return null;
			String str=response.toString();
			String className=str.substring(0,str.indexOf(':'));
			String content=str.substring(str.indexOf(':')+1);
			if (cls==Object.class)
				return (T)this.jsonUtil.fromJson(content,className);
			else
				return this.jsonUtil.fromJson(content,cls);
		});
	}
	

	@Override
	public <T> T newEntity(Class<T> cls, BaseDTO dto) {
		return appCtx.getBean(cls, dto);
	}


	@Override
	public <T> Future<T> persistEntity(String key,T entity, int timeoutInSeconds) {
		Future<Response> ret=null;
		List<String> params=new Vector<String>(Arrays.asList(key,entity.getClass().getName()+":"+this.jsonUtil.toJson(entity)));
		if (timeoutInSeconds>0) {
			params.add("EX");
			params.add(""+timeoutInSeconds);
		}
		ret=this.redisUtil.getRedis().set(params).onFailure(e->{
			e.printStackTrace();
		});
		return ret.map(r->(T)r);
	}

	@Override
	public void publishEvent(BaseEvent event) {
		this.messageContext.publish("Event."+event.getClass().getName(),event);
	}

	@Override
	public String getUUID() {
		return Util.getUUID();
	}

	@Override
	public <T> Future<T> request(String name, Object request,Class<T> cls) {
		Future<Message> f=this.messageContext.request("Command."+name, request);
		return f.map(msg-> ((T)msg.getContent()));	
	}

	@Override
	public String toJson(Object obj) {
		return this.jsonUtil.toJson(obj);
	}

	@Override
	public Promise<?> addPendingFuture(String key) {
		Promise<?> promise=Promise.promise();
		this.allPendingFutures.put(key,promise);
		return promise;
		
	}

	@Override
	public Promise<?> getPendingFuture(String key) {
		return this.allPendingFutures.remove(key);
	}


	

}
