package com.atusoft.newmall.shelf.domain;

import com.atusoft.newmall.shelf.ShelfDTO;

public class Shelf {

	final ShelfDTO shelf;
	
	
	public Shelf(ShelfDTO shelf){
		this.shelf=shelf;
		//TODO copy from it instead of reference it.
	}
	
	public ShelfDTO getShelf() {
		return this.shelf;
	}
}
