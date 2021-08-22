package com.atusoft.infrastructure;

public interface User {
	String getUserId();
	String getUserName();
	
	boolean hasPermission(String[] permissions);
	
	<T> T getUserObject(Class<T> cls);//TODO?
}
