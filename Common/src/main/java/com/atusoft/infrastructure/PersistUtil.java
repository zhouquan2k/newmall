package com.atusoft.infrastructure;

import io.vertx.core.Future;

public interface PersistUtil {
	<T> Future<T> getEntity(Class<T> cls,String key);  //TODO  cache
	<T> Future<T> persistEntity(String key,T entity,int timeoutInSeconds); //timeoutInSeconds<=0 means forever
}
