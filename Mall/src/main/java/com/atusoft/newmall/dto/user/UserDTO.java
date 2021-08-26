package com.atusoft.newmall.dto.user;

import com.atusoft.framwork.UserObject;
import com.atusoft.infrastructure.BaseDTO;
import com.atusoft.infrastructure.User;

import lombok.Data;

@Data
public class UserDTO extends BaseDTO implements UserObject {

	String userId;
	PromoterLevel promoterLevel;
	String nickname;
	
	@Override
	public User getUser() {
		final UserDTO that=this;
		return new User() {

			@Override
			public String getUserId() {
				return userId;
			}

			@Override
			public String getUserName() {
				return nickname;
			}

			@Override
			public boolean hasPermission(String[] permissions) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Object getUserObject() {
				return that;
			}
		};
	}
	
	
	
	
}
