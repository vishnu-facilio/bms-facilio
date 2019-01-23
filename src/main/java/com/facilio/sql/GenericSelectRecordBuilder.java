package com.facilio.sql;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;

public class GenericSelectRecordBuilder implements SelectBuilderIfc<Map<String, Object>> {
	private static final Logger LOGGER = LogManager.getLogger(GenericSelectRecordBuilder.class.getName());
	private static final int QUERY_TIME_THRESHOLD = 5000;

	private List<FacilioField> selectFields;
	private String tableName;
	private StringBuilder joinBuilder = new StringBuilder();
	private WhereBuilder where = new WhereBuilder();
	private String groupBy;
	private String having;
	private String orderBy;
	private int limit = -1;
	private int offset = -1;
	private boolean forUpdate = false;
	private Connection conn = null;

	public GenericSelectRecordBuilder() {
		
	}
	
	public GenericSelectRecordBuilder(GenericSelectRecordBuilder selectBuilder) { //Do not call after calling getProps
		// TODO Auto-generated constructor stub
		this.tableName = selectBuilder.tableName;
		this.groupBy = selectBuilder.groupBy;
		this.having = selectBuilder.having;
		this.orderBy = selectBuilder.orderBy;
		this.limit = selectBuilder.limit;
		this.offset = selectBuilder.offset;
		this.conn = selectBuilder.conn;
		
		this.joinBuilder = new StringBuilder(selectBuilder.joinBuilder);
		if (selectBuilder.selectFields != null) {
			this.selectFields = new ArrayList<>(selectBuilder.selectFields);
		}
		if (selectBuilder.where != null) {
			this.where = new WhereBuilder(selectBuilder.where);
		}
	}
	
	@Override
	public GenericSelectRecordBuilder select(List<FacilioField> fields) {
		this.selectFields = fields;
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}

	@Override
	public GenericSelectRecordBuilder useExternalConnection (Connection conn) {
		this.conn = conn;
		return this;
	}

	@Override
	public GenericJoinBuilder innerJoin(String tableName) {
		joinBuilder.append(" INNER JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public GenericJoinBuilder leftJoin(String tableName) {
		joinBuilder.append(" LEFT JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public GenericJoinBuilder rightJoin(String tableName) {
		joinBuilder.append(" RIGHT JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public GenericJoinBuilder fullJoin(String tableName) {
		joinBuilder.append(" FULL JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public GenericSelectRecordBuilder andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	@Override
	public GenericSelectRecordBuilder andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	@Override
	public GenericSelectRecordBuilder orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	@Override
	public GenericSelectRecordBuilder andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	@Override
	public GenericSelectRecordBuilder orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder groupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder having(String having) {
		this.having = having;
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder offset(int offset) {
		this.offset = offset;
		return this;
	}
	
	
	public List<FacilioField> getSelectFields() {
		return selectFields;
	}

	public String getTableName() {
		return tableName;
	}

	public StringBuilder getJoinBuilder() {
		return joinBuilder;
	}

	public WhereBuilder getWhere() {
		return where;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public String getHaving() {
		return having;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public int getLimit() {
		return limit;
	}

	public int getOffset() {
		return offset;
	}
	
	public boolean isForUpdate() {
		return forUpdate;
	}

	public Connection getConn() {
		return conn;
	}

	@Override
	public GenericSelectRecordBuilder forUpdate() {
		this.forUpdate = true;
		return this;
	}

	private DBSelectRecordBuilder getDBSelectRecordBuilder() throws Exception {
		String dbClass = AwsUtil.getDBClass();
		return (DBSelectRecordBuilder) Class.forName(dbClass + ".SelectRecordBuilder").getConstructor(GenericSelectRecordBuilder.class).newInstance(this);
	}

	@Override
	public List<Map<String, Object>> get() throws Exception {
		DBSelectRecordBuilder selectRecordBuilder = getDBSelectRecordBuilder();
		return selectRecordBuilder.get();
	}
	
	public String constructSelectStatement() {
		try {
			DBSelectRecordBuilder selectRecordBuilder = getDBSelectRecordBuilder();
			sql = selectRecordBuilder.constructSelectStatement();
			return sql;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String sql;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return sql;
	}
	
	public static class GenericJoinBuilder implements JoinBuilderIfc<GenericSelectRecordBuilder> {

		private GenericSelectRecordBuilder parentBuilder;
		public GenericJoinBuilder(GenericSelectRecordBuilder parentBuilder) {
			// TODO Auto-generated constructor stub
			this.parentBuilder = parentBuilder;
		}
		
		@Override
		public GenericSelectRecordBuilder on(String condition) {
			// TODO Auto-generated method stub
			parentBuilder.joinBuilder.append("ON ")
										.append(condition)
										.append(" ");
			return parentBuilder;
		}

	}
}
