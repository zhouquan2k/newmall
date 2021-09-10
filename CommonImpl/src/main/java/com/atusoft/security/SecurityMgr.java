package com.atusoft.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.framwork.PersistUtil;
import com.atusoft.framwork.UserObject;
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
	public Future<Optional<User>> getCurrentUser(String token) {
		String key="user_token:"+token;
		return this.persistUtil.getEntity(null,key).map( r->{
			User ret=null;
			if (r.isPresent()) ret=((UserObject)r.get()).getUser();
			return Optional.ofNullable(ret);
		});
	}
}
