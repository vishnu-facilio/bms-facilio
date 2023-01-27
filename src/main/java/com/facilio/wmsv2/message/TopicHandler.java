package com.facilio.wmsv2.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TopicHandler {

    enum DELIVER_TO { ALL, USER, ORG, SESSION, APP }

    String[] topic();

    DELIVER_TO deliverTo() default DELIVER_TO.ORG;

    int priority();

    Group group() default Group.DEFAULT;

    int recordTimeout() default 5; //5 secs

}
