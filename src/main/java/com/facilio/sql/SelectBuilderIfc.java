package com.facilio.sql;

import java.util.List;

import com.facilio.bmsconsole.modules.FacilioField;

public interface SelectBuilderIfc<E> extends WhereBuilderIfc<SelectBuilderIfc<E>> {
	
	public SelectBuilderIfc<E> select(List<FacilioField> fields);
	
	public SelectBuilderIfc<E> table(String tableName);
	
	public SelectJoinBuilderIfc<E> innerJoin(String tableName);
	
	public SelectJoinBuilderIfc<E> leftJoin(String tableName);
	
	public SelectJoinBuilderIfc<E> rightJoin(String tableName);
	
	public SelectJoinBuilderIfc<E> fullJoin(String tableName);
	
	public SelectBuilderIfc<E> groupBy(String groupBy);
	
	public SelectBuilderIfc<E> having(String having); 
	
	public SelectBuilderIfc<E> orderBy(String orderBy);
	
	public SelectBuilderIfc<E> limit(int limit);
	
	List<E> get() throws Exception;
	
}
