package com.atusoft.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseEntity {

	@JsonIgnore
	protected Infrastructure infrastructure;
	
	public void setInfrastructure(Infrastructure infrastructure) {
		this.infrastructure=infrastructure;
	}
}
