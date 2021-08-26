package com.atusoft.util;

import com.atusoft.infrastructure.User;

import io.vertx.core.Future;

public interface SecurityUtil {
	void loginByEvent(String token,Object user);
	
	Future<User> getCurrentUser(String token);
}
