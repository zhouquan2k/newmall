package com.atusoft.infrastructure.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.atusoft.framwork.ApiMessage;
import com.atusoft.infrastructure.BaseDTO;
import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.infrastructure.Context;
import com.atusoft.infrastructure.EventHandler;
import com.atusoft.infrastructure.User;
import com.atusoft.messaging.Message;
import com.atusoft.messaging.MessageContext;
import com.atusoft.messaging.MessageHandler;
import com.atusoft.util.JsonUtil;
import com.atusoft.util.SecurityUtil;

import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ServiceFramework implements MessageHandler {
	
	
	@Resource(name="service") 
	Object service;

	@Autowired
	MessageContext context;
	
	@Autowired
	JsonUtil jsonUtil;
	
	@Value("${app.service-name}")
	String serviceName;
	
	@Autowired 
	SecurityUtil securityUtil;
	
	
	class ContextImpl implements Context {

		ApiMessage command;
		SecurityUtil securityUtil;
		ContextImpl(ApiMessage command,SecurityUtil securityUtil) {
			this.command=command;
			this.securityUtil=securityUtil;
		}
		@Override
		public User getCurrentUser() {
			
			String token=command.getParam("_token");
			return this.securityUtil.getCurrentUser(token).result();
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
		
		context.setEventHandler(this.serviceName,topics.toArray(new String[topics.size()]), this);
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
			log.debug("processing event:"+event);
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
		if (method==null) {
			String msg="invalid command:"+command.getCommandName();
			log.warn(msg);
			message.getContext().response(msg);
			return;
		}
		Object[] params=new Object[method.getParameterCount()];
		if (method.getParameterCount()>0) {
			Class<?>[] paramTypes=method.getParameterTypes();
			Parameter[] parameters=method.getParameters();
			for (int i=0;i<paramTypes.length;i++) {
				Class<?> c=method.getParameterTypes()[i];
				if (BaseDTO.class.isAssignableFrom(c)) {
					try {
						BaseDTO dto=(BaseDTO)jsonUtil.fromJson(command.getBody(),c);
						String token=command.getParam("_token");
						if (token!=null&&token.length()>0) dto.set_token(token);
						params[i]=dto;
					}
					catch (Throwable e)
					{
						e.printStackTrace();
						context.response(e);
						return;
					}
				}
				else if (Context.class.isAssignableFrom(c)) {
					params[i]=new ContextImpl(command,this.securityUtil);
				}
				else {
					if (command.getParam(parameters[i].getName())!=null) {
						params[i]=convertFromString(command.getParam(parameters[i].getName()),
								parameters[i].getType());
					}
				}
			}
		}
		
		try {
			log.debug("// processing command:"+command);
			Object ret=method.invoke(service, params);
			if (ret instanceof Future) {
				((Future<?>) ret).onSuccess( r-> {
					log.debug("\\\\ command response async:"+r);
					message.getContext().response(r);
				}).onFailure(e->{
					log.debug("\\\\ *** command response async :"+e);
					e.printStackTrace();
					message.getContext().response(e);
				});
			}
			else {
				log.debug("\\\\ command response :"+ret);
				message.getContext().response(ret);
			}
		}  
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			message.getContext().response(e);
		}	
		
	}
	
	@SuppressWarnings("unchecked")
	private <T> T convertFromString(String src,Class<T> cls) {
		if (cls.isPrimitive()) {
			if (cls.equals(java.lang.Integer.TYPE))
				return (T)(Object)Integer.parseInt(src);
		}
		else if (cls.equals(String.class))
			return (T)src;
		return null;
		//TODO
	}
}
