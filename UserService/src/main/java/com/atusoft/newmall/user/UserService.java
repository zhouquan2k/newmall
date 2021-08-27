package com.atusoft.newmall.user;

import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.infrastructure.EventHandler;
import com.atusoft.newmall.BaseService;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.user.AccountDTO;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.newmall.event.order.OrderCreatedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;

@Component("service")
public class UserService extends BaseService {

	@EventHandler
	public void onOrderCreatedEvent(OrderCreatedEvent event) {
		//get balance info 
		//get brokerage info
		final OrderDTO order=event.getOrder();
		this.infrastructure.getCurrentUser(event).onSuccess(user->{
			
			this.infrastructure.getEntity(User.class,user.getUserId()).onSuccess(eUser->{
				eUser.getOrderBalance(order);
				this.infrastructure.publishEvent(new OrderDeductionBalancedEvent(order));
			});
		});
	}
	
	@CommandHandler
	public void SaveAccount(AccountDTO account) {
		this.infrastructure.getEntity(User.class,account.getUserId()).onSuccess(user->{
			user.saveAccount(account);
		});
	}
	
	@CommandHandler
	public void SaveUser(UserDTO dUser) {
		User user=this.infrastructure.newEntity(User.class, dUser);
		user.save();
	}
	
}
