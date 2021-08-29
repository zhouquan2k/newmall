package com.atusoft.newmall.shelf.domain;

import com.atusoft.infrastructure.BaseEntity;
import com.atusoft.infrastructure.User;
import com.atusoft.newmall.dto.order.OrderDTO.PurchaseItem;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.newmall.shelf.ShelfDTO;
import com.atusoft.newmall.shelf.ShelfDTO.ShelfItem;

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
	
	public PurchaseItem getPrice(PurchaseItem item,User user) {
		UserDTO userDto=(UserDTO)user.getUserObject();
		ShelfItem shelfItem=this.shelf.getSku2Shelf().get(item.getSkuId());
		item.setUnitPrice(shelfItem.getPromoterPrices().get(userDto.getPromoterLevel()));
		return item;
	}
	
	public Future<ShelfDTO> save() {
		return this.infrastructure.persistEntity(this.shelf.getShelfId(), this, 0).map(r->{
			return r.getShelf();
		});
	}
}
