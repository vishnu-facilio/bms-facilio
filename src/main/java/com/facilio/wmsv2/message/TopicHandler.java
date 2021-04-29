package com.facilio.wmsv2.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TopicHandler {
    enum DELIVER_TO { ALL, USER, ORG, SESSION }

    String[] topic();
    DELIVER_TO deliverTo() default DELIVER_TO.ORG;
    int priority();
    boolean sendToAllWorkers() default true;
}
