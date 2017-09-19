package com.facilio.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;

public class GenericSelectRecordBuilder implements SelectBuilderIfc<Map<String, Object>> {
	private List<FacilioField> selectFields;
	private String tableName;
	private StringBuilder joinBuilder = new StringBuilder();
	private WhereBuilder where = new WhereBuilder();
	private String groupBy;
	private String having;
	private String orderBy;
	private int limit;
	private Connection conn = null;
	
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
	public GenericSelectRecordBuilder connection(Connection conn) {
		this.conn = conn;
		return this;
	}
	
	private void checkForNull(boolean checkBean) {
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}
		
		if(conn == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		
		if(selectFields == null || selectFields.size() <= 0) {
			throw new IllegalArgumentException("Select Fields cannot be null or empty");
		}
	}

	@Override
	public List<Map<String, Object>> get() throws Exception {
		checkForNull(false);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			String sql = constructSelectStatement();
			pstmt = conn.prepareStatement(sql);
			
			Object[] whereValues = where.getValues();
			if(whereValues != null) {
				for(int i=0; i<whereValues.length; i++) {
					Object value = whereValues[i];
					pstmt.setObject(i+1, value);
				}
			}
			
			rs = pstmt.executeQuery();
			List<Map<String, Object>> records = new ArrayList<>();
			while(rs.next()) {
				Map<String, Object> record = new HashMap<>();
				for(FacilioField field : selectFields) {
					String key = null;
					if(field.getModuleTableName() != null && !field.getModuleTableName().isEmpty()) {
						key = field.getModuleTableName()+"."+field.getColumnName();
					}
					else {
						key = field.getColumnName();
					}
					Object val = rs.getObject(key);
					if(val != null) {
						record.put(field.getName(), val);
					}
					//record.put(field.getName(), FieldUtil.getValueAsPerType(field, rs));
				}
				
				records.add(record);
			}
			return records;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}
	
	private String constructSelectStatement() {
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		
		boolean isFirst = true;
		for(FacilioField field : selectFields) {
			if(isFirst) {
				isFirst = false;
			}
			else {
				sql.append(", ");
			}
			if(field.getModuleTableName() != null && !field.getModuleTableName().isEmpty()) {
				sql.append(field.getModuleTableName())
					.append(".");
			}
			sql.append(field.getColumnName());
		}
		
		sql.append(" FROM ")
			.append(tableName);
		
		sql.append(joinBuilder.toString());
		
		if(where.getWhereClause() != null && !where.getWhereClause().isEmpty()) {
			sql.append(" WHERE ")
				.append(where.getWhereClause());
		}
		
		if(groupBy != null && !groupBy.isEmpty()) {
			sql.append(" GROUP BY ")
				.append(groupBy);
		}
		
		if(having != null && !having.isEmpty()) {
			sql.append(" HAVING ")
				.append(having);
		}
		
		if(orderBy != null && !orderBy.isEmpty()) {
			sql.append(" ORDER BY ")
				.append(orderBy);
		}
		
		if(limit != 0) {
			sql.append(" LIMIT ")
				.append(limit);
		}
		
		return sql.toString();
	}
	
	public static class GenericJoinBuilder implements JoinBuilderIfc<Map<String, Object>> {

		private GenericSelectRecordBuilder parentBuilder;
		private GenericJoinBuilder(GenericSelectRecordBuilder parentBuilder) {
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
