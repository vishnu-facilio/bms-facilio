package com.facilio.sql;

public interface JoinBuilderIfc<E> {
	public SelectBuilderIfc<E> on(String condition);
}
