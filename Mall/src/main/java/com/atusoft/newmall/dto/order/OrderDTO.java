package com.atusoft.newmall.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.atusoft.infrastructure.BaseDTO;
import com.atusoft.newmall.dto.user.CouponDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;


//using base class or as another param
//TODO OrderEx: for output

@Data
@EqualsAndHashCode(callSuper=false)
public class OrderDTO extends BaseDTO {

	String orderId; // for output
	//User related
	
	String userId; // for output
	String addressId;
	
	public enum Status {
		Preview,Submited,Cancelled,Paid,Delivering,Delivered,Accepted,Evaluated,
	}
	public enum RefundStatus {
		Applying,Refunded,Rejected,None
	}
	
	Status status;
	RefundStatus refundStatus;
	
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
	@EqualsAndHashCode(callSuper=false)
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
	
	LocalDateTime submitTime;
}
