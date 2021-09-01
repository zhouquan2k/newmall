package com.atusoft.newmall.shelf;

import java.math.BigDecimal;
import java.util.Map;

import com.atusoft.infrastructure.BaseDTO;
import com.atusoft.newmall.dto.user.PromoterLevel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ShelfDTO extends BaseDTO {
	
	String shelfId;

	String productId;
	
	Map<String,ShelfItem> sku2Shelf;
	
	
	@Data
	public static class ShelfItem {
		
		String productId;
		String skuId;
		
		BigDecimal originPrice;
		Map<PromoterLevel,BigDecimal> promoterPrices;	
		
		int stock;
	}
}
