package com.atusoft.infrastructure;

public interface Context {

	User getCurrentUser();
	//<T> T getCurrentUser(Class<T> cls);
}
