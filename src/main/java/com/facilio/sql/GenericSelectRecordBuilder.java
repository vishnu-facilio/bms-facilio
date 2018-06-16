package com.facilio.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.unitconversion.UnitsUtil;

public class GenericSelectRecordBuilder implements SelectBuilderIfc<Map<String, Object>> {
	private static final Logger LOGGER = LogManager.getLogger(GenericSelectRecordBuilder.class.getName());

	private List<FacilioField> selectFields;
	private String tableName;
	private StringBuilder joinBuilder = new StringBuilder();
	private WhereBuilder where = new WhereBuilder();
	private String groupBy;
	private String having;
	private String orderBy;
	private int limit = -1;
	private int offset = -1;
	
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
	public GenericSelectRecordBuilder offset(int offset) {
		this.offset = offset;
		return this;
	}
	
	
	
	private void checkForNull(boolean checkBean) {
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
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
		Connection conn = FacilioConnectionPool.INSTANCE.getConnection();
		try {
			
			String sql = constructSelectStatement();
//			System.out.println("########### sql : "+ sql);
			pstmt = conn.prepareStatement(sql);
			
			Object[] whereValues = where.getValues();
			if(whereValues != null) {
				for(int i=0; i<whereValues.length; i++) {
					Object value = whereValues[i];
					pstmt.setObject(i+1, value);
				}
			}
			
			rs = pstmt.executeQuery();
			this.sql = pstmt.toString();
			List<Map<String, Object>> records = new ArrayList<>();
			while(rs.next()) {
				Map<String, Object> record = new HashMap<>();
				for(FacilioField field : selectFields) {
					Object val = FieldUtil.getObjectFromRS(field, rs);
					if(field != null &&  field instanceof NumberField) {
						NumberField numberField =  (NumberField)field;
						if(numberField.getMetric() > 0) {
							val = UnitsUtil.convertToOrgDisplayUnitFromSi(val, numberField.getMetric());
						}
					}
					if(val != null) {
						record.put(field.getName(), val);
					}
				}
				if(!record.isEmpty()) {
					records.add(record);
				}
			}
			return records;
		}
		catch(SQLException e) {
			LOGGER.log(Level.ERROR, "Exception " ,e);
			throw e;
		}
		finally {
			DBUtil.closeAll(conn,pstmt, rs);
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
			sql.append(field.getCompleteColumnName())
				.append(" AS `")
				.append(field.getName())
				.append("`");
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
		
		if(limit != -1) {
			sql.append(" LIMIT ")
				.append(limit);
			
			if(offset != -1) {
				sql.append(" OFFSET ")
					.append(offset);
			}
		}
		
		return sql.toString();
	}
	
	private String sql;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return sql;
	}
	
	public static class GenericJoinBuilder implements JoinBuilderIfc<GenericSelectRecordBuilder> {

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
