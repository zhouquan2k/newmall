package com.atusoft.infrastructure;

import java.util.Calendar;

import lombok.Data;

@Data
public class BaseEvent {

	long timestamp=Calendar.getInstance().getTimeInMillis();
	
	String _token;
	
	protected BaseEvent(BaseDTO dto) {
		if (dto!=null) this._token=dto.get_token();
	}
}
