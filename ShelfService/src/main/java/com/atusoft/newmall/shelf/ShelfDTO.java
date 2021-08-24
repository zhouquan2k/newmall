package com.atusoft.newmall.shelf;

import java.math.BigDecimal;
import java.util.Map;

public class ShelfDTO {
	
	String shelfId;

	String productId;
	
	public enum PromoterLevel {
		Silver,Gold,Diamond,None
	}
	
	static class ShelfItem {
		
		String productId;
		String skuId;
		
		BigDecimal originPrice;
		Map<PromoterLevel,BigDecimal> promoterPrices;	

	}
}
