package com.atusoft.newmall.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.atusoft.infrastructure.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


//using base class or as another param
//TODO REFACTOR  BaseDTO contains _token


@Data
@EqualsAndHashCode(callSuper=false)
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO extends BaseDTO 
{

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
	
	CartDTO cart;
	
		
	//Pay related
	public enum PayMethod {
		WeChatPay,AliPay,
		Balance,
	}
	
	PayMethod payMethod;
	
	@Builder.Default
	DeductionOptions deductionOptions=new DeductionOptions();
	
		
	//deliver related
	
	String deliveryPlanId;
	
	BigDecimal balance; // for output
	
	BigDecimal deliveryPrice; // for output
	BigDecimal totalPrice; //calculated = sum price of purchase item 
	BigDecimal payPrice; //calculated = totalPrice - deduction
	BigDecimal deductionPrice; //calculated = BrokerageDeduction+IntegralDeduction+CouponDeduction
	
	LocalDateTime submitTime;
}
