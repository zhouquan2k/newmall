package com.atusoft.newmall.user;

import java.math.BigDecimal;

import com.atusoft.infrastructure.BaseEntity;
import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.order.OrderDTO.PayMethod;
import com.atusoft.newmall.dto.user.AccountDTO;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.newmall.event.order.OrderCancelledEvent;
import com.atusoft.newmall.event.order.OrderSubmitedEvent;
import com.atusoft.newmall.event.order.ToOrderPaidEvent;
import com.atusoft.newmall.event.user.AccountChangedEvent;
import com.atusoft.newmall.event.user.AccountChangedEvent.ChangeType;
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
		this.save(null);//TODO AccountChangedEvent
	}
	
	public void onOrderSubmit(OrderSubmitedEvent event) {
		OrderDTO order=event.getOrder();
		
		if (order.getDeductionOptions().getBrokerageDeduction()!=null
				&&order.getDeductionOptions().getBrokerageDeduction().isDeduction())
			this.deductBrokerage(event,order.getDeductionOptions().getBrokerageDeduction().getDeducted());
		if (order.getPayMethod()==PayMethod.Balance||order.getPayPrice().equals(BigDecimal.ZERO)) {
			if (order.getPayPrice().compareTo(this.account.getBalance())>0) {
				//can't pay by balance
				//throw new BusiException("BalancePayFail","BalancePayFail: balance not enough","User");
				infrastructure.publishEvent(new ToOrderPaidEvent(order.getOrderId(),PayMethod.Balance,false));
			}
			this.account.setBalance(this.account.getBalance().subtract(order.getPayPrice()));
			this.save(new ToOrderPaidEvent(order.getOrderId(),PayMethod.Balance,true));
		}
	}
	
	
	
	void deductBrokerage(BaseEvent cause,BigDecimal deduction) {
		if (this.account.getBrokerage().compareTo(deduction)<0) 
			throw new BusiException("BrokerageDeductionFail","BrokerageDeductionFail","User");
		this.account.setBrokerage(account.getBrokerage().subtract(deduction));
		AccountChangedEvent event=new AccountChangedEvent(cause,this.getUser().getUserId(),ChangeType.Brokerage,deduction.negate());
		this.save(event); 
	}
	
	public void cancelOrder(OrderCancelledEvent cause,AccountChangedEvent event) {
		this.account.setBrokerage(account.getBrokerage().subtract(event.getChange()));
		AccountChangedEvent aEvent=new AccountChangedEvent(cause,this.getUser().getUserId(),ChangeType.Brokerage,event.getChange().negate());
		this.save(aEvent); 

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
