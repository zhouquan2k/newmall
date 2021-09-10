package com.atusoft.newmall.event.user;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.user.AccountDTO;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeductionBalancedEvent extends BaseEvent{

	AccountDTO account;
	
	protected DeductionBalancedEvent() {
	}
	public DeductionBalancedEvent(String sourceId,AccountDTO account) {
		super(account);
		this.sourceId=sourceId;
		this.account=account;
	}
}
