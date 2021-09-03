package com.atusoft.newmall.user;

import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.infrastructure.EventHandler;
import com.atusoft.newmall.BaseService;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.user.AccountDTO;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.newmall.event.order.OrderCancelledEvent;
import com.atusoft.newmall.event.order.OrderCreatedEvent;
import com.atusoft.newmall.event.order.OrderSubmitedEvent;
import com.atusoft.newmall.event.user.AccountChangedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;
import com.atusoft.newmall.event.user.UserLoginEvent;
import com.atusoft.util.Util;

import io.vertx.core.Future;

@Component("service")
public class UserService extends BaseService {
	
	//TODO move to securityserver?
	@CommandHandler
	public void Login(String username,String password) {
		//TODO authentication
		Util.onSuccess(infrastructure.getEntity(User.class, "27"),user->{
			UserLoginEvent event=new UserLoginEvent(user.getUser());
			event.set_token("token_1");
			this.infrastructure.publishEvent(event);
			return Future.succeededFuture();
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
		user.save(null); //User changed
	}
	
	@EventHandler
	public void onOrderCreatedEvent(OrderCreatedEvent event) {
		//get balance info 
		//get brokerage info
		final OrderDTO order=event.getOrder();
		this.infrastructure.getCurrentUser(event).onSuccess(user->{
			
			this.infrastructure.getEntity(User.class,user.getUserId()).onSuccess(eUser->{
				AccountDTO account=eUser.getAccount();
				order.setBalance(account.getBalance());
				if (order.getBrokerageDeduction()!=null) 
					order.getBrokerageDeduction().setBalance(account.getBrokerage());
				if (order.getIntegralDeduction()!=null) 
					order.getIntegralDeduction().setBalance(account.getIntegral());
				if (order.getCouponDeduction()!=null)
					order.getCouponDeduction().setCoupons(account.getCoupons());

				this.infrastructure.publishEvent(new OrderDeductionBalancedEvent(order));
			});
		});
	}
	
	@EventHandler
	public void onOrderSubmitedEvent(OrderSubmitedEvent event) {
		this.infrastructure.getCurrentUser(event).onSuccess(user->{
			this.infrastructure.getEntity(User.class,user.getUserId()).onSuccess(eUser->{
				if (event.getOrder().getBrokerageDeduction()!=null&&event.getOrder().getBrokerageDeduction().isDeduction())
					eUser.deductBrokerage(event,event.getOrder().getBrokerageDeduction().getDeducted());
			});
		});
	}
	
	@EventHandler
	public void onOrderCancelledEvent(OrderCancelledEvent event) {
		this.rollback(event, e->{
			String userId=e.getSourceId();
			this.infrastructure.getEntity(User.class,userId).onSuccess(user->{
				if (e instanceof AccountChangedEvent) //ignore OrderExceptionEvent
					user.cancelOrder(event,(AccountChangedEvent)e);
			});
		});
	}
	
}
