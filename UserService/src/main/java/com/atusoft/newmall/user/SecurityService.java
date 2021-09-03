package com.atusoft.newmall.user;

import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.newmall.BaseService;
import com.atusoft.newmall.event.user.UserLoginEvent;
import com.atusoft.util.Util;

import io.vertx.core.Future;

//TODO double service?
public class SecurityService extends BaseService {

	@CommandHandler
	public void login(String username,String password) {
		//TODO authentication
		Util.onSuccess(infrastructure.getEntity(User.class, "27"),user->{
			this.infrastructure.publishEvent(new UserLoginEvent(user.getUser()));
			return Future.succeededFuture();
		});
		
		
	}
}
