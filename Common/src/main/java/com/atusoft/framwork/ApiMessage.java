package com.atusoft.framwork;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

//TODO move to webserver related impl package?
// not directly used by Service/Domain, used by framework

@Data
public class ApiMessage {

	private String commandName;
	private String body;
	private Map<String,String> params; //including query param,path params,other headers info
	
	public ApiMessage() {
		
	}
	public ApiMessage(String commandName) {
		this.commandName=commandName;
	}
	
	public void setParam(String name,String value) {
		if (params==null) params=new HashMap<String,String>();
		params.put(name, value);
	}
	
	public String getParam(String name) {
		return params.get(name);
	}
}
