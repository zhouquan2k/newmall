package com.atusoft.infrastructure.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.atusoft.framwork.PersistUtil;
import com.atusoft.infrastructure.BaseDTO;
import com.atusoft.infrastructure.BaseEntity;
import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.User;
import com.atusoft.messaging.Message;
import com.atusoft.messaging.MessageContext;
import com.atusoft.util.JsonUtil;
import com.atusoft.util.SecurityUtil;
import com.atusoft.util.Util;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InfrastructureImpl implements Infrastructure  {

	@Autowired
	protected PersistUtil persistUtil;
	
	@Autowired
	MessageContext messageContext;
	
	@Autowired 
	JsonUtil jsonUtil;
	
	@Value("${app.service-name}")
	String serviceName;
	
	@Autowired
	ApplicationContext appCtx;
	
	@Autowired
	protected SecurityUtil securityUtil;
	
	
	Map<String,Promise<BaseEvent>> allPendingFutures=new HashMap<String,Promise<BaseEvent>>();
	
	
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
		
		return this.persistUtil.getEntity(cls, key).compose(t->{
			if (t!=null&&BaseEntity.class.isAssignableFrom(cls))
				((BaseEntity)t).setInfrastructure(this);
			return Future.succeededFuture(t);		
		});
	}
	

	@Override
	public <T extends BaseEntity> T newEntity(Class<T> cls, BaseDTO dto) {
		try {
			T ret=cls.getConstructor(dto.getClass()).newInstance(dto);
			ret.setInfrastructure(this);
			return ret;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public <T> Future<T> persistEntity(String key,T entity, int timeoutInSeconds) {
		final Infrastructure that=this;
		return this.persistUtil.persistEntity(key,entity,timeoutInSeconds).map(ret->{
			if (ret instanceof BaseEntity) ((BaseEntity)ret).setInfrastructure(that);
			return ret;
		});
	}

	@Override
	public void publishEvent(BaseEvent event) {
		log.debug("publishing event:"+event);
		//persist event to do rollback
		//TODO assure sync
		/*
		RedisAPI redis=(RedisAPI)this.persistUtil.getLowApi();
		redis.rpush(Arrays.asList(event.getCauseEventId(),PersistUtil.obj2str(jsonUtil,event)));
		*/
		if (event.getCauseEventId()!=null&&!event.getCauseEventId().isBlank()) 
			this.persistUtil.persistEvent(event.getCauseEventId(), event);
		else //TODO to remove
			this.persistUtil.persistEvent("debug_events", event);
		this.messageContext.publish("Event."+event.getClass().getName(),event);
	}

	@Override
	public String getUUID() {
		return Util.getUUID();
	}

	@SuppressWarnings("unchecked")
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
	public Promise<BaseEvent> addPendingFuture(String key) {
		Promise<BaseEvent> promise=Promise.promise();
		this.allPendingFutures.put(key,promise);
		return promise;
		
	}

	@Override
	public Promise<BaseEvent> getPendingFuture(String key) {
		return this.allPendingFutures.remove(key);
	}


	@Override
	public Future<List<BaseEvent>> getEventsByCause(String causeEventId) {
		return this.persistUtil.getEvents(causeEventId);
	}


	public void dump() {
		this.persistUtil.dump();
	}

}
