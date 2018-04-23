package com.facilio.sql;

public interface DeleteBuilderIfc<E> extends WhereBuilderIfc<DeleteBuilderIfc<E>> {
	public DeleteBuilderIfc<E> table(String tableName);
	
	public int delete() throws Exception;
	
	public JoinBuilderIfc<? extends DeleteBuilderIfc<E>> innerJoin(String tableName, boolean delete);
	
	public JoinBuilderIfc<? extends DeleteBuilderIfc<E>> leftJoin(String tableName, boolean delete);
	
	public JoinBuilderIfc<? extends DeleteBuilderIfc<E>> rightJoin(String tableName, boolean delete);
	
	public JoinBuilderIfc<? extends DeleteBuilderIfc<E>> fullJoin(String tableName, boolean delete);
}
