package com.atusoft.newmall.order;

import java.math.BigDecimal;
import java.util.List;

import com.atusoft.infrastructure.BaseDTO;

import lombok.Data;


@Data
//using base class or as another param
public class OrderDTO extends BaseDTO {

	String orderId; // for output
	//User related
	
	String userId; // for output
	String addressId;
	
	//product related
	
	List<PurchaseItem> purchaseItem;
		
	public static class PurchaseItem {
		String productId;
		String skuId;
		int count;
		
		// for output
		String warehouseId;
		String shelfId;
		BigDecimal unitPrice;	
	}
	
	String cartId;

		
	//Pay related
	public enum PayMethod {
		WeChatPay,AliPay,
		Balance,
	}
	
	PayMethod payMethod;
	
	boolean doBrokerageDeduction;
	boolean doIntegralDeduction;

	public class CouponDeduction {
		String couponId;
		BigDecimal value; //calculated
	}
		
	//deliver related
	
	String deliveryPlanId;
	
	BigDecimal deliveryPrice; // for output
	BigDecimal totalPrice; //calculated = sum price of purchase item 
	BigDecimal payPrice; //calculated = totalPrice - deduction
	BigDecimal deductionPrice; //calculated = BrokerageDeduction+IntegralDeduction+CouponDeduction
	
}
