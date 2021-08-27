package com.atusoft.newmall.dto.user;

import java.math.BigDecimal;


import lombok.Data;

@Data
public class CouponDTO {
	String couponId;
	String name;
	BigDecimal value;
}
