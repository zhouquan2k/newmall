package com.atusoft.newmall.dto.user;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class AccountDTO {
	
	String userId;

	BigDecimal balance;
	
	BigDecimal brokerage;
	BigDecimal integral;
		
	List<CouponDTO> coupons;
}
