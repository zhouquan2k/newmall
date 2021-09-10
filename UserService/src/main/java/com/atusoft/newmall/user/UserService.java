package com.atusoft.newmall.user;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.infrastructure.EventHandler;
import com.atusoft.newmall.BaseService;
import com.atusoft.newmall.dto.order.OrderDTO.PayMethod;
import com.atusoft.newmall.dto.user.AccountDTO;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.newmall.event.order.OrderPreviewEvent;
import com.atusoft.newmall.event.order.OrderCancelledEvent;
import com.atusoft.newmall.event.order.OrderExceptionEvent;
import com.atusoft.newmall.event.order.OrderSubmitedEvent;
import com.atusoft.newmall.event.user.AccountChangedEvent;
import com.atusoft.newmall.event.user.DeductionBalancedEvent;
import com.atusoft.newmall.event.user.UserLoginEvent;
import com.atusoft.util.BusiException;
import com.atusoft.util.Util;

import io.vertx.core.Future;

@Component("service")
public class UserService extends BaseService {
	
	//TODO move to securityserver?
	@CommandHandler
	public Future<?> Login(String username,String password) {
		//TODO authentication
		String userId="27";
		return Util.onSuccess(infrastructure.getEntity(User.class,userId ),user->{
			if (user==null) throw new BusiException("UserNotExist","user not exist:"+userId,"User");
			UserLoginEvent event=new UserLoginEvent(user.orElseThrow().getUser());
			event.set_token("token_1");
			this.infrastructure.publishEvent(event);
			return Future.succeededFuture();
		});
		
	}
	
	@CommandHandler
	public Future<?> SaveAccount(AccountDTO account) {
		return Util.onSuccess(this.infrastructure.getEntity(User.class,account.getUserId()),user->{
			user.orElseThrow().saveAccount(account);
			return Future.succeededFuture();
		});
	}
	
	@CommandHandler
	public Future<?> SaveUser(UserDTO dUser) {
		User user=this.infrastructure.newEntity(User.class, dUser);
		user.save(null); //User changed
		return Future.succeededFuture();
	}
	
	@EventHandler
	public void onOrderPreviewEvent(OrderPreviewEvent event) {
		//get balance info 
		//get brokerage info
		this.infrastructure.getCurrentUser(event).onSuccess(user->{
			
			this.infrastructure.getEntity(User.class,user.orElseThrow().getUserId()).onSuccess(eUser->{
				AccountDTO account=eUser.orElseThrow().getAccount();
				this.infrastructure.publishEvent(new DeductionBalancedEvent(event.getOrder().getOrderId(),account));
			});
		});
	}
	
	@EventHandler
	public void onOrderSubmitedEvent(OrderSubmitedEvent event) {
		this.infrastructure.getCurrentUser(event).onSuccess(user->{
			this.infrastructure.getEntity(User.class,user.orElseThrow().getUserId()).onSuccess(eUser->{
				try
				{
					eUser.orElseThrow().onOrderSubmit(event);
				}
				catch (Throwable e) {
					e.printStackTrace();
					infrastructure.publishEvent(new OrderExceptionEvent(event,event.getOrder().getOrderId(),e));
				}
			});
		});
	}
	
	@EventHandler
	public void onOrderCancelledEvent(OrderCancelledEvent event) {
		this.rollback(event, e->{
			String userId=e.getSourceId();
			this.infrastructure.getEntity(User.class,userId).onSuccess(user->{
				if (e instanceof AccountChangedEvent) //ignore OrderExceptionEvent
					user.orElseThrow().cancelOrder(event,(AccountChangedEvent)e);
			});
		});
	}
	
}
