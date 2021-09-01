package com.atusoft.newmall.event.user;

import java.math.BigDecimal;

import com.atusoft.infrastructure.BaseEvent;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper=true)
public class AccountChangedEvent extends BaseEvent {

	protected AccountChangedEvent() {
		
	}
	
	public enum ChangeType {
		Brokerage,Integral,Balance
	}
	
	ChangeType changeType;
	String userId;
	BigDecimal change;
	
	public AccountChangedEvent(BaseEvent cause,String userId,ChangeType changeType,BigDecimal change) {
		super(cause);
		this.userId=this.sourceId=userId;
		this.change=change;
		this.changeType=changeType;
	}
}
