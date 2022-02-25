package com.facilio.v3.annotation;

import com.facilio.modules.FacilioModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleType {
    FacilioModule.ModuleType type();
}
