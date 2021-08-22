package com.atusoft.infrastructure.impl;

import com.atusoft.infrastructure.BaseDTO;
import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.User;

import com.atusoft.util.Util;

class InfrastructureImpl implements Infrastructure  {

	
	@Override
	public User getCurrentUser(BaseDTO dto) {
		//TODO secutityMgr.getCurrentUser(dto.getToken());
		return null;
	}
	
	@Override
	public <T> T getEntity(Class<T> cls, String key) {
		//TODO load entity from nosql repository
		return null;
	}
	

	@Override
	public <T> T newEntity(Class<T> cls, BaseDTO dto) {
		//TODO using Spring.getBean() to construct an entity.
		return null;
	}


	@Override
	public <T> T persistEntity(T entity, int timeoutInSeconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void publishEvent(BaseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUUID() {
		return Util.getUUID();
	}

}
