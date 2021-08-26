package com.atusoft.security;

import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.framwork.UserObject;
import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.User;
import com.atusoft.util.SecurityUtil;

import io.vertx.core.Future;

public class SecurityMgr implements SecurityUtil {

	@Autowired
	Infrastructure infrastructure;
	
	@Override
	public void loginByEvent(String token,Object user) {		
		String key="user_token:"+token;
		this.infrastructure.getEntity(Object.class,key).onSuccess( r->{
			if (r==null) infrastructure.persistEntity(key,user,0); 
		});
	}
	
	@Override
	public Future<User> getCurrentUser(String token) {
		String key="user_token:"+token;
		return this.infrastructure.getEntity(Object.class,key).map( r->{
			UserObject uo=(UserObject)r;
			return uo==null?null:uo.getUser();
			//TODO
		});
	}
}
