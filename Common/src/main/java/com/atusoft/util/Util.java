package com.atusoft.util;

import java.lang.reflect.Method;
import java.util.UUID;

import io.vertx.core.CompositeFuture;

public class Util {

	//reflection
	//TODO should tune, make a method map
	//find method by name, by param count if provided
	public static Method getMethod(Class<?> cls, String methodName, Object[] params)
	{
		try
		{
			Method method = null;
			Method[] methods = cls.getMethods();
			for (int i = 0; i < methods.length; i++)
			{
				String name = methods[i].getName();
				int paramCnt = methods[i].getParameterTypes().length;
				if (methodName.equals(name) && (params==null||paramCnt == params.length))
					return methods[i];
			}
			return method;
		}
		catch (Throwable e)
		{
			throw new RuntimeException("Util.getMethod",e);
		}
	}
	
	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-","").toLowerCase();
	}
	
	public static <T> T getFutureResult(CompositeFuture cf,Class<T> cls)  {
		for (Object o:cf.list()) {
			if (cls.isAssignableFrom(o.getClass())) return (T)o;
		}
		throw new RuntimeException("no result of type :"+cls.getName());
	}
}
