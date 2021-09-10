package com.atusoft.newmall.dto.order;

import java.math.BigDecimal;
import java.util.List;

import com.atusoft.infrastructure.BaseDTO;
import com.atusoft.newmall.dto.user.CouponDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
public class DeductionOptions extends BaseDTO {
	  
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Deduction {
		boolean deduction;
		BigDecimal balance;
		
		BigDecimal deducted; //output
		
		public Deduction(boolean deduction) {
			this.deduction=deduction;
		}
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	public static class CouponDeduction extends Deduction {
		String couponId;//null means no deduction
		List<CouponDTO> coupons;
	}
	

	
	Deduction integralDeduction;
	Deduction brokerageDeduction;
	CouponDeduction couponDeduction;
	

}
