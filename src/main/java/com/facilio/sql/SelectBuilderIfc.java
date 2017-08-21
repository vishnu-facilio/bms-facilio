package com.facilio.sql;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.modules.FacilioField;

public interface SelectBuilderIfc<E> extends WhereBuilderIfc<SelectBuilderIfc<E>> {
	
	public SelectBuilderIfc<E> select(List<FacilioField> fields);
	
	public SelectBuilderIfc<E> table(String tableName);
	
	public SelectBuilderIfc<E> orderBy(String orderBy);
	
	public SelectBuilderIfc<E> limit(int limit);
	
	public JoinBuilderIfc<E> innerJoin(String tableName);
	
	public JoinBuilderIfc<E> leftJoin(String tableName);
	
	public JoinBuilderIfc<E> rightJoin(String tableName);
	
	public JoinBuilderIfc<E> fullJoin(String tableName);
	
	public SelectBuilderIfc<E> connection(Connection conn);

	List<E> get() throws Exception;
	
}
