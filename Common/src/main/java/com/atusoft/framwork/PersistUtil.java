package com.atusoft.framwork;

import java.util.List;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.util.JsonUtil;

import io.vertx.core.Future;

public interface PersistUtil {
	<T> Future<T> getEntity(Class<T> cls,String key);  //TODO  cache
	<T> Future<T> persistEntity(String key,T entity,int timeoutInSeconds); //timeoutInSeconds<=0 means forever

	void persistEvent(String key,BaseEvent event);
	Future<List<BaseEvent>> getEvents(String key);
	
	void dump(); //for debug only
	
	//Object getLowApi();
		
	
	//refactor to default?
	static String obj2str(JsonUtil jsonUtil,Object src) {
		return src.getClass().getName()+":"+jsonUtil.toJson(src);
	}
	static Object str2Obj(JsonUtil jsonUtil,String str,Class<?> cls) {
		if (str==null) return null;
		String className=str.substring(0,str.indexOf(':'));
		String content=str.substring(str.indexOf(':')+1);
		Object ret=null;
		if (cls==null)
			ret=jsonUtil.fromJson(content,className);
		else
			ret=jsonUtil.fromJson(content,cls);
		return ret;

	}
}
