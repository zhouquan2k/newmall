package com.atusoft.util;


/**
 * 常用的java对象到json字符串的相互转换
 * 
 * @author zhouquan
 *
 */
public interface JsonUtil {

	/**
	 * java对象->json字符串
	 * @param src
	 * @return
	 */
	String toJson(Object src);
	
	/**
	 * json字符串->java对象
	 * @param src
	 * @return
	 */
	Object fromJson(String src,Class<?> cls);
	
	Object fromJson(String src,String className);
	
	/**
	 * 直接获取底层转换对象，以完成更高级功能
	 * @return
	 */
	Object getObjectMapper();
}
