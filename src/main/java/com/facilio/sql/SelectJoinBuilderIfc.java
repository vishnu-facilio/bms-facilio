package com.facilio.sql;

public interface SelectJoinBuilderIfc<E> {
	public SelectBuilderIfc<E> on(String condition);
}
