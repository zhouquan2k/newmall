package com.atusoft.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.vertx.core.Future;

public abstract  class  BaseEntity {

	@JsonIgnore
	protected Infrastructure infrastructure;
	
	public void setInfrastructure(Infrastructure infrastructure) {
		this.infrastructure=infrastructure;
	}
	
	public abstract String getId();
	
	
	//TODO assure transaction with persistence and event
	public Future<?> save(BaseEvent event) {
		return save(event,0);
	}
	
	public Future<?> save(BaseEvent event,int expiration) {
		Future<?> ret=this.infrastructure.persistEntity(this.getId(), this, expiration);
		return ret.compose(o->{
			if (event!=null) this.infrastructure.publishEvent(event);
			return Future.succeededFuture(ret.result()); 
		});
	}

}
