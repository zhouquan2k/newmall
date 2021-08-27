package com.atusoft.newmall.dto.order;

import java.math.BigDecimal;
import java.util.List;

import com.atusoft.infrastructure.BaseDTO;
import com.atusoft.newmall.dto.user.CouponDTO;
import lombok.Data;


//using base class or as another param
//TODO OrderEx: for output

@Data
public class OrderDTO extends BaseDTO {

	String orderId; // for output
	//User related
	
	String userId; // for output
	String addressId;
	
	//product related
	
	List<PurchaseItem> purchaseItems;
		
	@Data
	public static class PurchaseItem {
		String productId;
		String skuId;
		String shelfId;
		int count;
		
		// for output
		String warehouseId;
		BigDecimal unitPrice;	
	}
	
	String cartId;

		
	//Pay related
	public enum PayMethod {
		WeChatPay,AliPay,
		Balance,
	}
	
	PayMethod payMethod;
	
	
	@Data
	public static class CouponDeduction extends Deduction {
		String couponId;//null means no deduction
		List<CouponDTO> coupons;
	}
	
	Deduction integralDeduction;
	Deduction brokerageDeduction;
	CouponDeduction couponDeduction;
		
	//deliver related
	
	String deliveryPlanId;
	
	BigDecimal balance; // for output
	
	BigDecimal deliveryPrice; // for output
	BigDecimal totalPrice; //calculated = sum price of purchase item 
	BigDecimal payPrice; //calculated = totalPrice - deduction
	BigDecimal deductionPrice; //calculated = BrokerageDeduction+IntegralDeduction+CouponDeduction
	
}
