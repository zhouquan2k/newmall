package com.atusoft.test;

import java.util.List;
import java.util.Optional;
import java.util.Vector;

import com.atusoft.infrastructure.BaseEvent;
import com.atusoft.infrastructure.Infrastructure;
import com.atusoft.infrastructure.impl.InfrastructureImpl;
import com.atusoft.util.SecurityUtil;

public class Infrastructure4Test extends InfrastructureImpl implements Infrastructure {
	
	List<BaseEvent> events=new Vector<BaseEvent>();
	
	
	public void setSecurityUtil(SecurityUtil securityUtil) {
		this.securityUtil=securityUtil;
	}

	@Override
	public void publishEvent(BaseEvent event) {
		if (event.getCauseEventId()!=null)
			this.persistUtil.persistEvent(event.getCauseEventId(), event);
		this.events.add(event);
	}

	
	@SuppressWarnings("unchecked")
	public <T extends BaseEvent> T assureEvent(Class<T> cls) {
		Optional<BaseEvent> ret=events.stream().filter(event->cls.isAssignableFrom(event.getClass())).findAny();
		return (T)ret.get();
	}
	

}
