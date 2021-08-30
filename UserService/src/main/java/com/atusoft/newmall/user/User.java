package com.atusoft.newmall.user;

import java.math.BigDecimal;

import com.atusoft.infrastructure.BaseEntity;
import com.atusoft.newmall.dto.user.AccountDTO;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.util.BusiException;


public class User extends BaseEntity {
		
	UserDTO user;
	AccountDTO account;
	
	protected User() {
		
	}
	
	public User(UserDTO user) {
		this.user=user;
	}

	public void saveAccount(AccountDTO account) {
		this.account=account; //TODO copy;
		this.save();
	}
		
	//account
	//TODO can't reuse in domain, move to service?, not depending on order
	/*
	public void getOrderBalance(OrderDTO order) {
				
	}
	*/
	
	public void deductBrokerage(BigDecimal deduction) {
		if (this.account.getBrokerage().compareTo(deduction)<0) 
			throw new BusiException("BrokerageDeductionFail","BrokerageDeductionFail","User");
		this.account.setBrokerage(account.getBrokerage().subtract(deduction));
		this.save();
	}
	
	UserDTO getUser() {
		return user;
	}
	
	AccountDTO getAccount() {
		return account;
	}

	@Override
	public String getId() {
		return this.user.getUserId();
	}
}
