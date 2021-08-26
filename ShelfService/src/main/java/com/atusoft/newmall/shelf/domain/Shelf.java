package com.atusoft.newmall.shelf.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.User;
import com.atusoft.newmall.dto.order.OrderDTO.PurchaseItem;
import com.atusoft.newmall.dto.user.UserDTO;
import com.atusoft.newmall.shelf.ShelfDTO;
import com.atusoft.newmall.shelf.ShelfDTO.ShelfItem;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Shelf {
	
	@Autowired
	@JsonIgnore
	Infrastructure infrastructure;

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
	
	public void save() {
		this.infrastructure.persistEntity(this.shelf.getShelfId(), this, 0);
	}
}
