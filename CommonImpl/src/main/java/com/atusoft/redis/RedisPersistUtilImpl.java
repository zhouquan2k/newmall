package com.atusoft.redis;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.infrastructure.BaseEntity;
import com.atusoft.infrastructure.PersistUtil;
import com.atusoft.util.JsonUtil;

import io.vertx.core.Future;
import io.vertx.redis.client.Response;

public class RedisPersistUtilImpl implements PersistUtil {
	
	@Autowired 
	RedisUtil redisUtil;
	
	@Autowired
	JsonUtil jsonUtil;
	

	@Override
	public <T> Future<T> getEntity(Class<T> cls, String key) {
		if (cls!=null) key=cls.getSimpleName()+":"+key;
		return this.redisUtil.getRedis().get(key).map(response->{
			if (response==null) return null;
			String str=response.toString();
			String className=str.substring(0,str.indexOf(':'));
			String content=str.substring(str.indexOf(':')+1);
			T ret=null;
			if (cls==null)
				ret=(T)this.jsonUtil.fromJson(content,className);
			else
				ret=this.jsonUtil.fromJson(content,cls);
			return ret;
		});
	}

	@Override
	public <T> Future<T> persistEntity(String key, T entity, int timeoutInSeconds) {
		Future<Response> ret=null;
		if (key.indexOf(':')<0) key=entity.getClass().getSimpleName()+":"+key;
		List<String> params=new Vector<String>(Arrays.asList(key,entity.getClass().getName()+":"+this.jsonUtil.toJson(entity)));
		if (timeoutInSeconds>0) {
			params.add("EX");
			params.add(""+timeoutInSeconds);
		}
		ret=this.redisUtil.getRedis().set(params).onFailure(e->{
			e.printStackTrace();
		});
		return ret.map(r->(T)r);
	}

}
