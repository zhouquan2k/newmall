package com.atusoft.newmall;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.atusoft.framwork.PersistUtil;
import com.atusoft.infrastructure.User;
import com.atusoft.newmall.dto.user.PromoterLevel;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.util.SecurityUtil;

import io.vertx.core.Future;

public class SecurityMgr4Test implements SecurityUtil {

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
		//String key="user_token:"+token;
		return Future.succeededFuture(Optional.of(new User() {

			@Override
			public String getUserId() {
				return "27";
			}

			@Override
			public String getUserName() {
				return "zhouquan-test";
			}

			@Override
			public boolean hasPermission(String[] permissions) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Object getUserObject() {
				UserDTO user=new UserDTO();
				user.setUserId(getUserId());
				user.setNickname(getUserName());
				user.setPromoterLevel(PromoterLevel.Silver);
				return user;
			}
			
		}));
	}
}

