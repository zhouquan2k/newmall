package com.atusoft.redis;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.framwork.PersistUtil;
import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.util.JsonUtil;

import io.vertx.core.Future;
import io.vertx.redis.client.Response;

public class RedisPersistUtilImpl implements PersistUtil {
	
	@Autowired 
	RedisUtil redisUtil;
	
	@Autowired
	JsonUtil jsonUtil;
	

	@SuppressWarnings("unchecked")
	@Override
	public <T> Future<T> getEntity(Class<T> cls, String key) {
		final String lKey=(cls!=null)?cls.getSimpleName()+":"+key:key;
		return this.redisUtil.getRedis().get(key).map(response->{
			return (T)PersistUtil.str2Obj(jsonUtil, lKey, cls);
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Future<T> persistEntity(String key, T entity, int timeoutInSeconds) {
		Future<Response> ret=null;
		if (key.indexOf(':')<0) key=entity.getClass().getSimpleName()+":"+key;
		List<String> params=new Vector<String>(Arrays.asList(key,PersistUtil.obj2str(jsonUtil, entity)));
		if (timeoutInSeconds>0) {
			params.add("EX");
			params.add(""+timeoutInSeconds);
		}
		ret=this.redisUtil.getRedis().set(params).onFailure(e->{
			e.printStackTrace();
		});
		return ret.map(r->(T)r);
	}

	@Override
	public void persistEvent(String key, BaseEvent event) {
		//TODO think about expiration, to keep less events
		this.redisUtil.getRedis().rpush(Arrays.asList(event.getCauseEventId(),PersistUtil.obj2str(jsonUtil,event)));

	}

	@Override
	public Future<List<BaseEvent>> getEvents(String key) {
		return this.redisUtil.getRedis().lrange(key, "0", "-1").compose(r->{
			List<BaseEvent> l=r.stream().map(rr->(BaseEvent)PersistUtil.str2Obj(jsonUtil, rr.toString(),null))
					.collect(Collectors.toList());
			return Future.succeededFuture(l);
		});
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}

	/*
	@Override
	public Object getLowApi() {
		return this.redisUtil.getRedis();
	}
	*/

}
