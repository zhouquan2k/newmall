package com.atusoft.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.framwork.PersistUtil;
import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.util.JsonUtil;

import io.vertx.core.Future;

public class TestPersistUtil implements PersistUtil {
	
	@Autowired
	JsonUtil jsonUtil;
	
	Map<String,String> entities=new HashMap<String,String>();
	Map<String,List<BaseEvent>> events=new HashMap<String,List<BaseEvent>>();
	
	@SuppressWarnings("unchecked")
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

	@Override
	public void persistEvent(String key, BaseEvent event) {
		List<BaseEvent> l=events.get(key);
		if (l==null) {
			l=new Vector<BaseEvent>();
			events.put(key, l);
		}
		l.add(event);
	}

	@Override
	public Future<List<BaseEvent>> getEvents(String key) {
		return Future.succeededFuture(this.events.get(key));
	}

	@Override
	public void dump() {
		events.entrySet().stream().forEach(e->{
			System.out.println(String.format("[%s]->\r\n",e.getKey()));
			e.getValue().stream().forEach( i->{
				System.out.println(String.format("- %s\r\n",i));
			});
		});
		System.out.println(events);
		
		
		System.out.println(entities);
	}

	/*
	@Override
	public Object getLowApi() {
		// TODO Auto-generated method stub
		return null;
	}
	*/


}
