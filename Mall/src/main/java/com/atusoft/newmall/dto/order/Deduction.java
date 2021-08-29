package com.atusoft.newmall.dto.order;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Deduction {
	boolean deduction;
	BigDecimal balance;
	
	BigDecimal deducted; //output
}