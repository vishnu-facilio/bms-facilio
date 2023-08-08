package com.facilio.flows.config.annotations;

import com.facilio.blockfactory.enums.BlockType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Block {
    BlockType value();
}
