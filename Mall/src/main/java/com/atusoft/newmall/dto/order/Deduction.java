package com.atusoft.newmall.dto.order;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Deduction {
	boolean deduction;
	BigDecimal balance;
	
	BigDecimal deducted; //output
}