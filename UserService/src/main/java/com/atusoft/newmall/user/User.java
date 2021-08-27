package com.atusoft.newmall.user;

import com.atusoft.infrastructure.BaseEntity;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.user.AccountDTO;
import com.atusoft.newmall.dto.user.UserDTO;


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
	
	public void save() {
		this.infrastructure.persistEntity(this.user.getUserId(), this, 0);
	}
	
	//account
	public void getOrderBalance(OrderDTO order) {
		order.setBalance(this.account.getBalance());
		if (order.getBrokerageDeduction()!=null) 
			order.getBrokerageDeduction().setBalance(this.account.getBrokerage());
		if (order.getIntegralDeduction()!=null) 
			order.getIntegralDeduction().setBalance(this.account.getIntegral());
		if (order.getCouponDeduction()!=null)
			order.getCouponDeduction().setCoupons(this.account.getCoupons());
		
	}
}
