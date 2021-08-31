package com.atusoft.newmall;

import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.infrastructure.EventHandler;
import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.newmall.event.user.UserLoginEvent;
import com.atusoft.util.SecurityUtil;

import io.vertx.core.Handler;


public class BaseService {

	@Autowired
	SecurityUtil securityUtil;
	
	@Autowired
	protected Infrastructure infrastructure;
	
	
	@EventHandler 
	public void onUserLoginEvent(UserLoginEvent event) {
		//persist user 
		securityUtil.loginByEvent(event.get_token(),event.getUser());
	}
	
	protected void rollback(BaseEvent event, Handler<BaseEvent> handler) {
		this.infrastructure.getEventsByCause(event.getCauseEventId()).onSuccess( l->{
			for (BaseEvent e:l) handler.handle(e);
		});
	}
}
