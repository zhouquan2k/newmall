package com.atusoft.test;

import java.util.HashMap;
import java.util.Map;

import com.atusoft.infrastructure.PersistUtil;

import io.vertx.core.Future;

public class TestPersistUtil implements PersistUtil {
	
	Map<String,Object> entities=new HashMap<String,Object>();
	
	@Override
	public <T> Future<T> getEntity(Class<T> cls, String key) {
		return (Future<T>)Future.succeededFuture(entities.get(key));
	}

	@Override
	public <T> Future<T> persistEntity(String key, T entity, int timeoutInSeconds) {
		this.entities.put(key, entity);
		return Future.succeededFuture(entity);
	}


}
