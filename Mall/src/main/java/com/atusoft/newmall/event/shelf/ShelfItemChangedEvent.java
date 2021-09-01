package com.atusoft.newmall.event.shelf;

import com.atusoft.infrastructure.BaseEvent;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper=true)
public class ShelfItemChangedEvent extends BaseEvent {

	String shelfId;
	String skuId;
	int changeCount;
	
	protected ShelfItemChangedEvent(){
		
	}
	
	public ShelfItemChangedEvent(BaseEvent cause,String shelfId,String skuId,int changeCount) {
		super(cause);
		this.shelfId=this.sourceId=shelfId;
		this.skuId=skuId;
		this.changeCount=changeCount;
	}
	
	
}
