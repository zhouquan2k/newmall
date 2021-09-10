package com.atusoft.newmall.dto.order;

import java.util.List;
import java.util.Vector;

import com.atusoft.infrastructure.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO extends BaseDTO {
	
	String cartId;
	
	@Builder.Default
	boolean isTempCart=true;
	
	@Builder.Default
	List<PurchaseItem> purchaseItems=new Vector<PurchaseItem>();
	
	//DeductionOptions deductionOptions;
}
