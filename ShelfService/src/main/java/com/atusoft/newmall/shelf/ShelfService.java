package com.atusoft.newmall.shelf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.infrastructure.EventHandler;
import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.newmall.shelf.domain.Shelf;
import com.atusoft.newmall.BaseService;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.order.OrderDTO.PurchaseItem;
import com.atusoft.newmall.event.order.OrderCreatedEvent;
import com.atusoft.newmall.event.order.OrderSubmitedEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.user.OrderDeductionBalancedEvent;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

@Component("service")
public class ShelfService extends BaseService {

	@Autowired
	Infrastructure infrastructure;
	
	@EventHandler
	public void onOrderCreatedEvent(OrderCreatedEvent event) {
		this.infrastructure.getCurrentUser(event).onSuccess(user->{
			final List<PurchaseItem> allItems=new ArrayList<PurchaseItem>();
			OrderDTO order=event.getOrder();
			List<Future> all=new ArrayList<Future>();
			for (PurchaseItem item:event.getOrder().getPurchaseItems()) {
				Future<?> f=this.infrastructure.getEntity(Shelf.class,item.getShelfId()).map(shelf->{
					PurchaseItem pItem=shelf.getPrice(item,user);
					allItems.add(pItem);
					return null;
				});
				all.add(f);
			}
			
			CompositeFuture.all(all).onSuccess(a->{
				order.setPurchaseItems(allItems);
				this.infrastructure.publishEvent(new OrderPricedEvent(order));
			});
			
		});
		
	}
	
	
	@CommandHandler
	public Future<ShelfDTO> SaveShelf(ShelfDTO shelf) {
		Shelf entity=this.infrastructure.newEntity(Shelf.class, shelf);
		return entity.save();
	}
	
	@EventHandler
	public void onOrderSubmitedEvent(OrderSubmitedEvent event) {
		
	}
	
}
