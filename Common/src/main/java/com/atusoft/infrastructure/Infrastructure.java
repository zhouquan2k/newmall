package com.atusoft.infrastructure;

public interface Infrastructure {

	<T> T newEntity(Class<T> cls,BaseDTO dto);
	void publishEvent(BaseEvent event);
	
	//TODO to support asynchronize
	<T> T getEntity(Class<T> cls,String key); 
	<T> T persistEntity(T entity,int timeoutInSeconds); //timeoutInSeconds<=0 means forever
	User getCurrentUser(BaseDTO dto);
	
	String getUUID();
}
