package com.atusoft.infrastructure.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.atusoft.util.BusiException;
import com.atusoft.util.JsonUtil;
import com.atusoft.util.SecurityUtil;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ServiceFramework implements MessageHandler {
	
	@Autowired
	Vertx vertx;
	
	@Resource(name="allServices")
	Map<String,Object> allServices;

	@Autowired
	MessageContext context;
	
	@Autowired
	JsonUtil jsonUtil;
	
	
	@Autowired 
	SecurityUtil securityUtil;
	
	
	static class Invokation {
		Object service;
		Method method;
		Invokation(Object service,Method method){
			this.service=service;
			this.method=method;
		}
	}
	
	
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
			return this.securityUtil.getCurrentUser(token).result().orElseThrow();
		}
		
	}
	
	Map<String,Invokation> methodMap=new HashMap<String,Invokation>();
	Map<String,List<Invokation>> eventMethodMap=new HashMap<String,List<Invokation>>();
	
	@PostConstruct
	public void init() throws InterruptedException { 
		
		//init all services
		this.allServices.forEach((key,value)->{
			this.initService(key, value);
		});
	}

	private void initService(String serviceName,Object service) {
		//construt a method map
		List<String> topics=new ArrayList<String>();
		context.setHandler(new String[]{"Command\\."+serviceName+"\\..*"},this);
		
		for (Method method:service.getClass().getMethods()) {
			CommandHandler c=method.getAnnotation(CommandHandler.class);
			if (c!=null) {
				String name=c.value().isBlank()?method.getName():c.value();
				methodMap.put("Command:"+serviceName+"."+name,new Invokation(service,method));
			}
			EventHandler e=method.getAnnotation(EventHandler.class);
			if (e!=null) {
				Class <?> pc=method.getParameterTypes()[0];
				Class<?> ec=(e.value()==BaseEvent.class)?pc:e.value();
				topics.add("Event."+ec.getName());
				List<Invokation> l=this.eventMethodMap.get(ec.getName());
				if (l==null) {
					l=new Vector<Invokation>();
					this.eventMethodMap.put(ec.getName(), l);
				}
				l.add(new Invokation(service,method));
			}
		}
		
		context.setEventHandler(serviceName,topics.toArray(new String[topics.size()]), this);

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
		List<Invokation> methods=this.eventMethodMap.get(event.getClass().getName());
		if (methods==null) {
			log.warn("invalid event: "+event.getClass().getName());
			return;
		}
		methods.stream().forEach(inv->{
			this.vertx.executeBlocking(p->{
				Object[] params=new Object[1];
				params[0]=event;
				log.debug("-- // processing event:"+event);
				try {
					@SuppressWarnings("unchecked")
					Future<Object> fut=(Future<Object>)inv.method.invoke(inv.service, params);
					if (fut!=null) fut.onFailure(e->{
						e.printStackTrace();
					}).onComplete(r->{
						log.debug("-- \\\\processed event:"+event);
					});
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}	
			});
		});
	}
	
	
	private void handleCommand(Message message) {
		ApiMessage command = (ApiMessage)message.getContent();
		//Method method=Util.getMethod(service.getClass(), command.getCommandName().substring(command.getCommandName().indexOf('.')+1), null);
		Invokation inv=this.methodMap.get("Command:"+command.getCommandName());
		if (inv==null) {
			String msg="invalid command:"+command.getCommandName();
			log.warn(msg);
			message.getContext().response(new BusiException("InvailidCommand",msg,"ServiceFrame"));
			return;
		}
		Object[] params=new Object[inv.method.getParameterCount()];
		if (inv.method.getParameterCount()>0) {
			Class<?>[] paramTypes=inv.method.getParameterTypes();
			Parameter[] parameters=inv.method.getParameters();
			for (int i=0;i<paramTypes.length;i++) {
				Class<?> c=inv.method.getParameterTypes()[i];
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
			Object ret=inv.method.invoke(inv.service, params);
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> T convertFromString(String src,Class<T> cls) {
		if (cls.isPrimitive()) {
			if (cls.equals(java.lang.Integer.TYPE))
				return (T)(Object)Integer.parseInt(src);
		}
		else if (cls.isEnum())
			return (T)Enum.valueOf((Class<Enum>) cls, src);
		else if (cls.equals(String.class))
			return (T)src;
		return null;
		//TODO
	}
}
