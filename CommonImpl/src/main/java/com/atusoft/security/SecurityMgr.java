package com.atusoft.security;

import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.framwork.UserObject;
import com.atusoft.infrastructure.PersistUtil;
import com.atusoft.infrastructure.User;
import com.atusoft.util.SecurityUtil;

import io.vertx.core.Future;

public class SecurityMgr implements SecurityUtil {

	@Autowired
	PersistUtil  persistUtil;
	
	
	@Override
	public void loginByEvent(String token,Object user) {		
		String key="user_token:"+token;
		this.persistUtil.getEntity(null,key).onSuccess( r->{
			if (r==null) this.persistUtil.persistEntity(key,user,0); 
		});
	}
	
	@Override
	public Future<User> getCurrentUser(String token) {
		String key="user_token:"+token;
		return this.persistUtil.getEntity(null,key).map( r->{
			UserObject uo=(UserObject)r;
			return uo==null?null:uo.getUser();
			//TODO
		});
	}
}
