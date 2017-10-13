package com.facilio.sql;

import java.util.List;

import com.facilio.bmsconsole.modules.FacilioField;

public interface UpdateBuilderIfc<E> extends WhereBuilderIfc<UpdateBuilderIfc<E>> {
	public UpdateBuilderIfc<E> table(String tableName);
	
	public UpdateBuilderIfc<E> fields(List<FacilioField> fields);
	
	public int update(E value) throws Exception;
	
	public UpdateJoinBuilderIfc<E> innerJoin(String tableName);
	
	public UpdateJoinBuilderIfc<E> leftJoin(String tableName);
	
	public UpdateJoinBuilderIfc<E> rightJoin(String tableName);
	
	public UpdateJoinBuilderIfc<E> fullJoin(String tableName);
}
