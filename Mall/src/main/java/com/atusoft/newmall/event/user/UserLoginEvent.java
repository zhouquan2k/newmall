package com.atusoft.newmall.event.user;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.user.UserDTO;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserLoginEvent extends BaseEvent {
	String userId;
	
	UserDTO user;
	
	protected UserLoginEvent() {
	}
	
	public UserLoginEvent(UserDTO user) {
		super(user);
		this.user=user;
		this.userId=user.getUserId();
	}
	/*
	public Object getUserObject() {
		return user;
	}
	*/
}
