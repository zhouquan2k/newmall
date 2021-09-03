package com.atusoft.framwork;

import lombok.Data;

@Data
public class ApiResponseMessage {
	int code;
	String body;
	protected ApiResponseMessage() {
		
	}
	
	public ApiResponseMessage (int code,String body) {
		this.code=code;
		this.body=body;
	}
}
