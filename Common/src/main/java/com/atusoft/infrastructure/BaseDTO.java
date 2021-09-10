package com.atusoft.infrastructure;

import lombok.Data;

//TODO refactor?
@Data
public class BaseDTO {
	
	protected String _token;
	
	protected String _serial; //business serial
}
