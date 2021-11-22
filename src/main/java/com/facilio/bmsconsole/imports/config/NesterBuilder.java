package com.facilio.bmsconsole.imports.config;

public abstract class NesterBuilder<T> {

    protected T parent;

    public NesterBuilder(T parent) {
        this.parent = parent;
    }

    public abstract T done();
}
