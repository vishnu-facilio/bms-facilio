package com.facilio.db.builder;

public interface JoinBuilderIfc<E> {
	public E on(String condition);
}
