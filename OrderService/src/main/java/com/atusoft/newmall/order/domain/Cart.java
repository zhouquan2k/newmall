package com.atusoft.newmall.order.domain;

import com.atusoft.infrastructure.BaseEntity;
import com.atusoft.newmall.dto.order.CartDTO;
import com.atusoft.newmall.dto.order.PurchaseItem;

import io.vertx.core.Future;

public class Cart extends BaseEntity {
	
	CartDTO cart;
	
	Cart() {
	}
	Cart(CartDTO cart) {
		this.cart=cart;
	}
	
	public static Cart create(CartDTO dto) {
		if (dto.getCartId()==null) {
			//if (dto.isTempCart())
				dto.setCartId(infrastructure.getUUID());
			//else 
			//	dto.setCartId(infrastructure.getCurrentUser(dto).result().getUserId());
		}
		Cart cart=new Cart(dto);
		return cart;
	}
	
	
	public CartDTO getCart() {
		return this.cart;
	}
	
	@SuppressWarnings("unchecked")
	public Future<Cart> addPurchaseItem(PurchaseItem item) {
		this.cart.getPurchaseItems().add(item);
		return (Future<Cart>)this.save(null,this.cart.isTempCart()?60*10:0);
	}
	
	
	

	@Override
	public String getId() {
		return this.cart.getCartId();
	}
	
	public void setId(String cartId) {
		this.cart.setCartId(cartId);
	}
}
