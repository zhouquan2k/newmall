package com.atusoft.infrastructure.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.atusoft.framwork.ApiMessage;
import com.atusoft.infrastructure.Context;
import com.atusoft.infrastructure.User;
import com.atusoft.messaging.MessageContext;
import com.atusoft.messaging.MessageHandler;
import com.atusoft.util.JsonUtil;
import com.atusoft.util.Util;

class ServiceFramework implements MessageHandler {
	
	
	@Resource(name="service") 
	Object service;

	@Autowired
	MessageContext context;
	
	@Autowired
	JsonUtil jsonUtil;
	
	@Value("${app.service-name}")
	String serviceName;
	
	
	class ContextImpl implements Context {

		ApiMessage command;
		ContextImpl(ApiMessage command) {
			this.command=command;
		}
		@Override
		public User getCurrentUser() {
			// TODO Auto-generated method stub
			String token=command.getParam("_token");
			//securityMgr.getCurrentUser(token) //load from nosql
			return null;
		}
		
	}
	
	@PostConstruct
	public void init() throws InterruptedException { 
		context.setHandler(new String[] {"Order\\..*"}, this);
	}

	@Override
	public void handler(MessageContext context, Object message) {
		// TODO Auto-generated method stub
		// deserialize message to DTO according to service method signature
		// dispatch to service method
		// get current user info from token
		
		ApiMessage command=(ApiMessage)message;
		//TODO should cache a method map commandName->method,DTO parameter type 
		Method method=Util.getMethod(service.getClass(), command.getCommandName().substring(command.getCommandName().indexOf('.')+1), null);
		Object[] params=new Object[method.getParameterCount()];
		if (method.getParameterCount()>0) {
			Class<?> dtoClass=method.getParameterTypes()[0];
			try {
				Object dto=jsonUtil.fromJson(command.getBody(),dtoClass);
				params[0]=dto;
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				context.response(e);
				return;
			}
			for (int i=1;i<method.getParameterTypes().length;i++) {
				Class<?> c=method.getParameterTypes()[i];
				if (c.isAssignableFrom(Context.class)) {
					params[i]=new ContextImpl(command);
				}
			}
		}
		
		try {
			Object ret=method.invoke(service, params);
			context.response(ret);
		}  
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			context.response(e);
		}	
		
	}
}
