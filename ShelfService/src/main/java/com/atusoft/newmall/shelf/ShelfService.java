package com.atusoft.newmall.shelf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.infrastructure.EventHandler;
import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.newmall.BaseService;
import com.atusoft.newmall.dto.order.CartDTO;
import com.atusoft.newmall.dto.order.OrderDTO;
import com.atusoft.newmall.dto.order.PurchaseItem;
import com.atusoft.newmall.event.order.OrderPreviewEvent;
import com.atusoft.newmall.event.order.OrderCancelledEvent;
import com.atusoft.newmall.event.order.OrderExceptionEvent;
import com.atusoft.newmall.event.order.OrderSubmitedEvent;
import com.atusoft.newmall.event.shelf.OrderPricedEvent;
import com.atusoft.newmall.event.shelf.ShelfItemChangedEvent;
import com.atusoft.newmall.shelf.domain.Shelf;
import com.atusoft.util.BusiException;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

@Component("service")
public class ShelfService extends BaseService {

	@Autowired
	Infrastructure infrastructure;
	
	@SuppressWarnings("rawtypes")
	@EventHandler
	public void onOrderPreviewEvent(OrderPreviewEvent event) {
		this.infrastructure.getCurrentUser(event).onSuccess(user->{
			final List<PurchaseItem> allItems=new ArrayList<PurchaseItem>();
			OrderDTO order=event.getOrder();
			CartDTO cart=order.getCart();
			List<Future> all=new ArrayList<Future>();
			for (PurchaseItem item:cart.getPurchaseItems()) {
				Future<Void> f=this.infrastructure.getEntity(Shelf.class,item.getShelfId()).compose(shelf->{
					PurchaseItem pItem=shelf.orElseThrow().preview(item,user.orElseThrow());
					allItems.add(pItem);
					return Future.succeededFuture();
				});
				all.add(f);
			}
			
			CompositeFuture.all(all).onSuccess(a->{
				cart.setPurchaseItems(allItems);
				this.infrastructure.publishEvent(new OrderPricedEvent(order));
			}).onFailure(e->{
				e.printStackTrace();
			});
			
		});
		
	}
	
	@EventHandler 
	public void onOrderCancelledEvent(OrderCancelledEvent event) {
		this.rollback(event, e->{
			String shelfId=e.getSourceId();
			this.infrastructure.getEntity(Shelf.class,shelfId).onSuccess(shelf->{
				if (e instanceof ShelfItemChangedEvent) //ignore OrderExceptionEvent
					shelf.orElseThrow().cancelOrder((ShelfItemChangedEvent)e);
			});
		});
		
	}
	
	
	@CommandHandler
	public Future<ShelfDTO> SaveShelf(ShelfDTO shelf) {
		Shelf entity=this.infrastructure.newEntity(Shelf.class, shelf);
		return (Future<ShelfDTO>)entity.save(null).map(s->((Shelf)s).getShelf());
	}
	
	@SuppressWarnings("rawtypes")
	@EventHandler
	public void onOrderSubmitedEvent(OrderSubmitedEvent event) {
		OrderDTO order=event.getOrder();
		List<Future> all=new ArrayList<Future>();
		for (PurchaseItem item:event.getOrder().getCart().getPurchaseItems()) {
			Future<?> f=this.infrastructure.getEntity(Shelf.class,item.getShelfId()).map(shelf->{
				return shelf.orElseThrow().purchase(event,item);
			});
			all.add(f);
		
		}
		CompositeFuture.all(all).onFailure(e->{
			OrderExceptionEvent eEvent;
			if (e instanceof BusiException)
				eEvent=new OrderExceptionEvent(event,order.getOrderId(),(BusiException)e);
			else 
				eEvent=new OrderExceptionEvent(event,order.getOrderId(),OrderExceptionEvent.Cause.Unknown,"ShelfService");
			
			this.infrastructure.publishEvent(eEvent);
				
		});
	}
	
}
