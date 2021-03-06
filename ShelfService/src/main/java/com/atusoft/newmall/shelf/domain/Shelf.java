package com.atusoft.newmall.shelf.domain;

import com.atusoft.infrastructure.BaseEntity;
import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.infrastructure.User;
import com.atusoft.newmall.dto.order.PurchaseItem;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.newmall.event.shelf.ShelfItemChangedEvent;
import com.atusoft.newmall.shelf.ShelfDTO;
import com.atusoft.newmall.shelf.ShelfDTO.ShelfItem;
import com.atusoft.util.BusiException;

import io.vertx.core.Future;

public class Shelf extends BaseEntity {
	

	ShelfDTO shelf;
	
	protected Shelf() {
		
	}
	
	public Shelf(ShelfDTO shelf){
		this.shelf=shelf;
		//TODO copy from it instead of reference it.
		
	}
	
	public ShelfDTO getShelf() {
		return this.shelf;
	}
	
	//make sure if shelf stock is enough , and give price
	public PurchaseItem preview(PurchaseItem item,User user) {
		UserDTO userDto=(UserDTO)user.getUserObject();
		assert(userDto!=null);
		ShelfItem shelfItem=this.shelf.getSku2Shelf().get(item.getSkuId());
		//if (item.getCount()>shelfItem.getStock())
		item.setStock(shelfItem.getStock());
			//throw new BusiException("OutOfStock","OutOfStock:"+shelfItem.getSkuId(),"Shelf");
		item.setUnitPrice(shelfItem.getPromoterPrices().get(userDto.getPromoterLevel()));
		return item;
	}
	
	
	@SuppressWarnings("unchecked")
	public Future<ShelfDTO> purchase(BaseEvent cause,PurchaseItem item) {
		ShelfItem shelfItem=this.shelf.getSku2Shelf().get(item.getSkuId());
		if (shelfItem.getStock()<item.getCount())
			throw new BusiException("ShelfOutOfStock","ShelfOutOfStock","Shelf");
		shelfItem.setStock(shelfItem.getStock()-item.getCount());
		return (Future<ShelfDTO>)this.save(new ShelfItemChangedEvent(cause,item.getShelfId(),item.getSkuId(),-item.getCount()));
	}
	
	public void cancelOrder(ShelfItemChangedEvent e) {
		ShelfItem shelfItem=this.shelf.getSku2Shelf().get(e.getSkuId());
		shelfItem.setStock(shelfItem.getStock()-e.getChangeCount());
		this.save(new ShelfItemChangedEvent(null,this.getShelf().getShelfId(),shelfItem.getSkuId(),e.getChangeCount()));
	}

	@Override
	public String getId() {
		return this.shelf.getShelfId();
	}
}
