package com.facilio.db.builder;

import com.facilio.bmsconsole.modules.FacilioField;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

public interface SelectBuilderIfc<E> extends WhereBuilderIfc<SelectBuilderIfc<E>> {
	
	public SelectBuilderIfc<E> select(Collection<FacilioField> fields);
	
	public SelectBuilderIfc<E> table(String tableName);
	
	public SelectBuilderIfc<E> useExternalConnection (Connection conn);
	
	public JoinBuilderIfc<? extends SelectBuilderIfc<E>> innerJoin(String tableName);
	
	public JoinBuilderIfc<? extends SelectBuilderIfc<E>> leftJoin(String tableName);
	
	public JoinBuilderIfc<? extends SelectBuilderIfc<E>> rightJoin(String tableName);
	
	public JoinBuilderIfc<? extends SelectBuilderIfc<E>> fullJoin(String tableName);
	
	public SelectBuilderIfc<E> groupBy(String groupBy);
	
	public SelectBuilderIfc<E> having(String having); 
	
	public SelectBuilderIfc<E> orderBy(String orderBy);
	
	public SelectBuilderIfc<E> limit(int limit);
	
	public SelectBuilderIfc<E> offset(int offset);
	
	public List<E> get() throws Exception;
	
	public SelectBuilderIfc<E> forUpdate();
	
}
