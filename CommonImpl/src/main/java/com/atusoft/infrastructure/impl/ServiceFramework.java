package com.atusoft.infrastructure.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.atusoft.framwork.ApiMessage;
import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.infrastructure.Context;
import com.atusoft.infrastructure.EventHandler;
import com.atusoft.infrastructure.User;
import com.atusoft.messaging.Message;
import com.atusoft.messaging.MessageContext;
import com.atusoft.messaging.MessageHandler;
import com.atusoft.util.JsonUtil;

import io.vertx.core.Future;

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
	
	Map<String,Method> methodMap=new HashMap<String,Method>();
	
	@PostConstruct
	public void init() throws InterruptedException { 
		
		//construt a method map
		List<String> topics=new ArrayList<String>();
		context.setHandler(new String[]{"Command\\."+serviceName+"\\..*"},this);
		
		for (Method method:service.getClass().getMethods()) {
			CommandHandler c=method.getAnnotation(CommandHandler.class);
			if (c!=null) {
				String name=c.value().isBlank()?method.getName():c.value();
				methodMap.put("Command:"+this.serviceName+"."+name,method);
			}
			EventHandler e=method.getAnnotation(EventHandler.class);
			if (e!=null) {
				Class <?> pc=method.getParameterTypes()[0];
				Class<?> ec=(e.value()==BaseEvent.class)?pc:e.value();
				topics.add("Event."+ec.getName());
				methodMap.put("Event:"+ec.getName(), method);
			}
		}
		
		context.setEventHandler(topics.toArray(new String[topics.size()]), this);
	}

	@Override
	public void handle(Message message) {
		// TODO Auto-generated method stub
		// deserialize message to DTO according to service method signature
		// dispatch to service method
		// get current user info from token
		
		
		switch (message.getType()) {
		case Command:
			handleCommand(message);
			break;
		case Event:
			handleEvent(message);
			break;
		default:
			break;
		}
	}
	
	
	private void handleEvent(Message message) {
		BaseEvent event=(BaseEvent)message.getContent();
		Method method=this.methodMap.get("Event:"+event.getClass().getName());
		Object[] params=new Object[1];
		params[0]=event;
		try {
			method.invoke(service, params);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}	
	}
	
	
	private void handleCommand(Message message) {
		ApiMessage command = (ApiMessage)message.getContent();
		//Method method=Util.getMethod(service.getClass(), command.getCommandName().substring(command.getCommandName().indexOf('.')+1), null);
		Method method=this.methodMap.get("Command:"+command.getCommandName());
		if (method==null) return;
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
			if (ret instanceof Future) {
				((Future<Object>) ret).onComplete( r-> {
					message.getContext().response(r.result());
				});
			}
			else message.getContext().response(ret);
		}  
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			message.getContext().response(e);
		}	
		
	}
}
