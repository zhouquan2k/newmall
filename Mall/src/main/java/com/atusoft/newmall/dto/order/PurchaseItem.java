package com.atusoft.newmall.dto.order;

import java.math.BigDecimal;

import com.atusoft.infrastructure.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public  class PurchaseItem extends BaseDTO{
	String productId;
	String skuId;
	String shelfId;
	int count;
	
	// for output
	String warehouseId;
	BigDecimal unitPrice;
	int stock;
}
