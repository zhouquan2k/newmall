package com.atusoft.infrastructure;

import io.vertx.core.Future;
import io.vertx.core.Promise;

public interface Infrastructure {
	
	void publishEvent(BaseEvent event);
	
	
	//<T> T newEntity(Class<T> cls,BaseDTO dto);
	<T extends BaseEntity> T newEntity(Class<T> cls,BaseDTO dto);
	//<T> Future<T> getEntity(Class<T> cls,String key);  //TODO  cache
	<T> Future<T> getEntity(Class<T> cls,String key);  //TODO  cache
	<T> Future<T> persistEntity(String key,T entity,int timeoutInSeconds); //timeoutInSeconds<=0 means forever
	Future<User> getCurrentUser(BaseDTO dto);
	Future<User> getCurrentUser(BaseEvent event);
	
	
	
	//Future<Object> request(String name,Object request);
	<T> Future<T> request(String name, Object request,Class<T> cls);
	
	String getUUID();
	String toJson(Object obj);
	
	Promise<?> addPendingFuture(String key);
	Promise<?> getPendingFuture(String key);
}
