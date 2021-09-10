package com.atusoft.util;

import java.util.Optional;

import com.atusoft.infrastructure.User;

import io.vertx.core.Future;

public interface SecurityUtil {
	void loginByEvent(String token,Object user);
	
	Future<Optional<User>> getCurrentUser(String token);
}
