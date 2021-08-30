package com.atusoft.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.vertx.core.Future;

public abstract class BaseEntity {

	@JsonIgnore
	protected Infrastructure infrastructure;
	
	public void setInfrastructure(Infrastructure infrastructure) {
		this.infrastructure=infrastructure;
	}
	
	public abstract String getId();
	
	
	public Future<?> save() {
		return this.infrastructure.persistEntity(this.getId(), this, 0);
	}
	
	public Future<?> save(int expiration) {
		return this.infrastructure.persistEntity(this.getId(), this, expiration);
	}

}
