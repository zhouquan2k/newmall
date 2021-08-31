package com.atusoft.infrastructure;

import java.util.Calendar;

import com.atusoft.util.Util;

import lombok.Data;

@Data
public class BaseEvent {
	
	String eventId=Util.getUUID();
	long timestamp=Calendar.getInstance().getTimeInMillis();
	
	protected String businessId; // from client
	protected String causeEventId; //event who cause current event.
	protected String sourceId; //current event resource id
	String _token; //end user
	
	
	protected BaseEvent() {
		
	}
	protected BaseEvent(BaseDTO dto) {
		if (dto!=null) this._token=dto.get_token();
	}
	protected BaseEvent(BaseEvent event) {
		if (event!=null) {
			this._token=event.get_token();
			this.causeEventId=event.getEventId();
		}
	}
}
