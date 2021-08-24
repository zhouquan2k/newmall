package com.atusoft.infrastructure.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.atusoft.infrastructure.BaseDTO;
import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.User;
import com.atusoft.messaging.Message;
import com.atusoft.messaging.MessageContext;
import com.atusoft.util.JsonUtil;
import com.atusoft.util.Util;

import io.vertx.core.Future;
import io.vertx.core.Promise;

class InfrastructureImpl implements Infrastructure  {

	@Autowired
	MessageContext messageContext;
	
	@Autowired 
	JsonUtil jsonUtil;
	
	@Value("${app.service-name}")
	String serviceName;
	
	@Autowired
	ApplicationContext appCtx;
	
	Map<String,Promise<?>> allPendingFutures=new HashMap<String,Promise<?>>();
	
	@Override
	public User getCurrentUser(BaseDTO dto) {
		//TODO secutityMgr.getCurrentUser(dto.getToken());
		return null;
	}
	
	@Override
	public <T> T getEntity(Class<T> cls, String key) {
		//TODO load entity from nosql repository
		return null;
	}
	

	@Override
	public <T> T newEntity(Class<T> cls, BaseDTO dto) {
		return appCtx.getBean(cls, dto);
	}


	@Override
	public <T> T persistEntity(T entity, int timeoutInSeconds) {
		// TODO Auto-generated method stub
		return null;
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
	public void addPendingFuture(String key,Promise<?> promise) {
		this.allPendingFutures.put(key,promise);
		
	}

	@Override
	public Promise<?> getPendingFuture(String key) {
		return this.allPendingFutures.remove(key);
	}

}
