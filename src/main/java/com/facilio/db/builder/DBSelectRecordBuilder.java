package com.facilio.db.builder;

import com.facilio.modules.FacilioField;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

public abstract class DBSelectRecordBuilder {
	
	protected Collection<FacilioField> selectFields;
	protected String tableName;
	protected StringBuilder joinBuilder = new StringBuilder();
	protected WhereBuilder where = new WhereBuilder();
	protected String groupBy;
	protected String having;
	protected String orderBy;
	protected int limit = -1;
	protected int offset = -1;
	protected String sql;
	protected boolean forUpdate = false;
	protected Connection conn = null;
	
	protected DBSelectRecordBuilder(GenericSelectRecordBuilder selectBuilder) {
		// TODO Auto-generated constructor stub
		this.tableName = selectBuilder.getTableName();
		this.groupBy = selectBuilder.getGroupBy();
		this.having = selectBuilder.getHaving();
		this.orderBy = selectBuilder.getOrderBy();
		this.limit = selectBuilder.getLimit();
		this.offset = selectBuilder.getOffset();
		
		this.joinBuilder = new StringBuilder(selectBuilder.getJoinBuilder());
		if (selectBuilder.getSelectFields() != null) {
			this.selectFields = new ArrayList<>(selectBuilder.getSelectFields());
		}
		this.selectFields = selectBuilder.getSelectFields();
		if (selectBuilder.getWhere() != null) {
			this.where = new WhereBuilder(selectBuilder.getWhere());
		}
		
		this.forUpdate = selectBuilder.isForUpdate();
		this.conn = selectBuilder.getConn();
	}
	
	public abstract String constructSelectStatement();
	
}
