package com.atusoft.newmall.user;

import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.newmall.BaseService;
import com.atusoft.newmall.event.user.UserLoginEvent;

public class SecurityService extends BaseService {

	@CommandHandler
	public void login(String username,String password) {
		//TODO
		this.infrastructure.publishEvent(new UserLoginEvent(null));
	}
}
