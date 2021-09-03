package com.atusoft.newmall.dto.user;

import java.math.BigDecimal;
import java.util.List;

import com.atusoft.infrastructure.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AccountDTO extends BaseDTO {
	
	String userId;

	BigDecimal balance;
	
	BigDecimal brokerage;
	BigDecimal integral;
		
	List<CouponDTO> coupons;
}
