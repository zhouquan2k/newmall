package com.atusoft.util;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BusiException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String busiCode;
	String location;
	
	protected BusiException() {
		
	}
	
	public BusiException(String busiCode,String message,String location) {
		super(message);
		this.busiCode=busiCode;
		this.location=location;
	}
	
	
}
