package com.facilio.db.builder;

import java.sql.Connection;

public interface DeleteBuilderIfc<E> extends WhereBuilderIfc<DeleteBuilderIfc<E>> {
	public DeleteBuilderIfc<E> table(String tableName);
	
	public DeleteBuilderIfc<E> useExternalConnection (Connection conn);
	
	public int delete() throws Exception;
	
	public JoinBuilderIfc<? extends DeleteBuilderIfc<E>> innerJoin(String tableName, boolean delete);
	
	public JoinBuilderIfc<? extends DeleteBuilderIfc<E>> leftJoin(String tableName, boolean delete);
	
	public JoinBuilderIfc<? extends DeleteBuilderIfc<E>> rightJoin(String tableName, boolean delete);
	
	public JoinBuilderIfc<? extends DeleteBuilderIfc<E>> fullJoin(String tableName, boolean delete);
}
