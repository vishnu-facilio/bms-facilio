package com.facilio.db.builder;


import com.facilio.modules.fields.FacilioField;

import java.sql.Connection;
import java.util.List;

public interface UpdateBuilderIfc<E> extends WhereBuilderIfc<UpdateBuilderIfc<E>> {
	public UpdateBuilderIfc<E> table(String tableName);
	
	public UpdateBuilderIfc<E> useExternalConnection (Connection conn);
	
	public UpdateBuilderIfc<E> fields(List<FacilioField> fields);
	
	public int update(E value) throws Exception;
	
	public JoinBuilderIfc<? extends UpdateBuilderIfc<E>> innerJoin(String tableName);
	
	public JoinBuilderIfc<? extends UpdateBuilderIfc<E>> leftJoin(String tableName);
	
	public JoinBuilderIfc<? extends UpdateBuilderIfc<E>> rightJoin(String tableName);
	
	public JoinBuilderIfc<? extends UpdateBuilderIfc<E>> fullJoin(String tableName);
}
