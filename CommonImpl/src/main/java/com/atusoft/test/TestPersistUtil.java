package com.atusoft.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.infrastructure.PersistUtil;
import com.atusoft.util.JsonUtil;

import io.vertx.core.Future;

public class TestPersistUtil implements PersistUtil {
	
	@Autowired
	JsonUtil jsonUtil;
	
	Map<String,String> entities=new HashMap<String,String>();
	
	@Override
	public <T> Future<T> getEntity(Class<T> cls, String key) {
		String str=this.entities.get(key);
		if (str==null) return (Future<T>)Future.succeededFuture(null);
		String className=str.substring(0,str.indexOf(':'));
		String content=str.substring(str.indexOf(':')+1);
		T ret=(cls==null)?(T)jsonUtil.fromJson(content,className):jsonUtil.fromJson(content, cls);
		return (Future<T>)Future.succeededFuture(ret);
	}

	@Override
	public <T> Future<T> persistEntity(String key, T entity, int timeoutInSeconds) {
		this.entities.put(key, entity.getClass().getName()+":"+jsonUtil.toJson(entity));
		return Future.succeededFuture(entity);
	}


}
