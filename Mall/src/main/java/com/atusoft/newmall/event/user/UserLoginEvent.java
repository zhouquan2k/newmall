package com.atusoft.newmall.event.user;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.user.UserDTO;

import lombok.Getter;

@Getter
public class UserLoginEvent extends BaseEvent {
	String userId;
	
	UserDTO user;
	
	protected UserLoginEvent() {
	}
	
	public UserLoginEvent(UserDTO user) {
		super(user);
		this.user=user;
	}
	/*
	public Object getUserObject() {
		return user;
	}
	*/
}
