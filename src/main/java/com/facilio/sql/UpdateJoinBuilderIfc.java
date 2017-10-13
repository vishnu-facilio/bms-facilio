package com.facilio.sql;

public interface UpdateJoinBuilderIfc<E> {
	public UpdateBuilderIfc<E> on(String condition);
}
