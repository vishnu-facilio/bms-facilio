package com.facilio.sql;

public interface JoinBuilderIfc<E> {
	public E on(String condition);
}
