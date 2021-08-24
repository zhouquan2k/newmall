package com.atusoft.infrastructure;

import io.vertx.core.Future;
import io.vertx.core.Promise;

public interface Infrastructure {

	<T> T newEntity(Class<T> cls,BaseDTO dto);
	void publishEvent(BaseEvent event);
	
	//TODO to support asynchronize,using Future
	<T> T getEntity(Class<T> cls,String key); 
	<T> T persistEntity(T entity,int timeoutInSeconds); //timeoutInSeconds<=0 means forever
	User getCurrentUser(BaseDTO dto);
	
	//Future<Object> request(String name,Object request);
	<T> Future<T> request(String name, Object request,Class<T> cls);
	
	String getUUID();
	String toJson(Object obj);
	
	void addPendingFuture(String key,Promise<?> promise);
	Promise<?> getPendingFuture(String key);
}
