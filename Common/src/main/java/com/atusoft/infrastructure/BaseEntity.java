package com.atusoft.infrastructure;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.vertx.core.Future;

public abstract  class   BaseEntity {
	
	public static  <T extends BaseEntity> BaseEntity create(Class<T> cls,BaseDTO o) {
		return infrastructure.newEntity(cls, o);
	}
	
	public static <T extends BaseEntity> Future<Optional<T>> load(Class<T> cls,String key) {
		return infrastructure.getEntity(cls, key);
	}
	


	@JsonIgnore
	static protected Infrastructure infrastructure;
	
	public static void setInfrastructure(Infrastructure inf) {
		infrastructure=inf;
	}
	
	public abstract String getId();
	
	
	//TODO assure transaction with persistence and event
	public Future<?> save(BaseEvent event) {
		return save(event,0);
	}
	
	public Future<?> save(BaseEvent event,int expiration) {
		Future<?> ret=infrastructure.persistEntity(this.getId(), this, expiration);
		return ret.compose(o->{
			if (event!=null) infrastructure.publishEvent(event);
			return Future.succeededFuture(ret.result()); 
		});
	}

}
