package com.atusoft.infrastructure;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

	Class<? extends BaseEvent> value() default BaseEvent.class;  //event name
}
