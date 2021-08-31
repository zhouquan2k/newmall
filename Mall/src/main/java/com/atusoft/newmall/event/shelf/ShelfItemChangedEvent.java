package com.atusoft.newmall.event.shelf;

import com.atusoft.infrastructure.BaseEvent;

import lombok.Getter;

@Getter
public class ShelfItemChangedEvent extends BaseEvent {

	String shelfId;
	String skuId;
	int changeCount;
	
	protected ShelfItemChangedEvent(){
		
	}
	
	public ShelfItemChangedEvent(String causeEventId,String shelfId,String skuId,int changeCount) {
		this.causeEventId=causeEventId;
		this.shelfId=this.sourceId=shelfId;
		this.skuId=skuId;
		this.changeCount=changeCount;
	}
	
	
}
