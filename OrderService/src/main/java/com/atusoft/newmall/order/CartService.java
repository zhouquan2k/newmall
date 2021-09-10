package com.atusoft.newmall.order;

import org.springframework.stereotype.Component;

import com.atusoft.infrastructure.CommandHandler;
import com.atusoft.newmall.BaseService;
import com.atusoft.newmall.dto.order.CartDTO;
import com.atusoft.newmall.dto.order.PurchaseItem;
import com.atusoft.newmall.order.domain.Cart;

import io.vertx.core.Future;

@Component
public class CartService extends BaseService {
	@CommandHandler
	//return temp cart 
	public Future<CartDTO> singlePurchase(PurchaseItem item) {
		//1.create a temp cart
		//User user=this.infrastructure.getCurrentUser(item).result();
		
		return infrastructure.getCurrentUser(item).compose(user->{
			Cart cart=Cart.create(CartDTO.builder().build());
			//cart.getCart().setCartId(user.getUserId());
			//2. cart.addPurchaseItem(item);
			return cart.addPurchaseItem(item).map(c->c.getCart());
		});
	}
	
	
	@CommandHandler 
	public Future<CartDTO> addToCart(PurchaseItem item) {
		//1.load user's default cart
		return infrastructure.getCurrentUser(item).compose(user->{
			return infrastructure.getEntity(Cart.class, user.orElseThrow().getUserId());
			//return Future.succeededFuture(new CartDTO());
		}).compose(cart->{
			return cart.orElseThrow().addPurchaseItem(item);
		})
		.map(cart->cart.getCart());		
	}
	
}
