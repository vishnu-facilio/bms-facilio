package com.facilio.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
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
import com.facilio.transaction.FacilioConnectionPool;

public class GenericUpdateRecordBuilder implements UpdateBuilderIfc<Map<String, Object>> {
	private static final Logger LOGGER = LogManager.getLogger(GenericUpdateRecordBuilder.class.getName());
	private List<FacilioField> fields;
	private Map<String, FacilioField> fieldMap;
	private String tableName;
	private Map<String, Object> value;
	private StringBuilder joinBuilder = new StringBuilder();
	private WhereBuilder where = new WhereBuilder();
	
	public GenericUpdateRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}
	
	public GenericUpdateRecordBuilder fields(List<FacilioField> fields) {
		this.fields = fields;
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
	
	public GenericUpdateRecordBuilder andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}
	
	@Override
	public GenericUpdateRecordBuilder orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	@Override
	public GenericUpdateRecordBuilder andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	@Override
	public GenericUpdateRecordBuilder orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	@Override
	public GenericUpdateRecordBuilder andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	@Override
	public GenericUpdateRecordBuilder orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}
	
	@Override
	public int update(Map<String, Object> value) throws SQLException {
		checkForNull();
		if (value == null) {
			return 0;
		}
		value.remove("id");
		this.value = value;
		if(!value.isEmpty()) {
			
			try {
				//TODO...delete the existing files
				FieldUtil.addFiles(fields, Collections.singletonList(value));
			} catch (Exception e) {
				LOGGER.log(Level.ERROR, "Insertion failed while updating files", e);
				throw new RuntimeException("Insertion failed while updating files");
			}
			
			PreparedStatement pstmt = null;
			
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				fieldMap = convertFieldsToMap(fields);
				String sql = constructUpdateStatement();
				if(sql != null && !sql.isEmpty()) {
					//System.out.println("################ sql: "+sql);
					pstmt = conn.prepareStatement(sql);
					
					int paramIndex = 1;
					for(Map.Entry<String, Object> entry : value.entrySet()) {
						FacilioField field = fieldMap.get(entry.getKey());
						if(field != null) {
							FieldUtil.castOrParseValueAsPerType(pstmt, paramIndex++, field, value.get(field.getName()));
						}
					}
					
					Object[] whereValues = where.getValues();
					if(whereValues != null) {
						for(int i=0; i<whereValues.length; i++) {
							Object whereValue = whereValues[i];
							pstmt.setObject(paramIndex++, whereValue);
						}
					}
					
					int rowCount = pstmt.executeUpdate();
					System.out.println("Updated "+rowCount+" records.");
					return rowCount;
				}
			}
			catch(SQLException e) {
				LOGGER.log(Level.ERROR, "Update failed ", e);
				throw e;
			}
			finally {
				if(pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						LOGGER.log(Level.ERROR, "Exception while closing resource ", e);
					}
				}
			}
		}
		return 0;
	}
	
	private String constructUpdateStatement() {
		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append(tableName)
			.append(joinBuilder.toString())
			.append(" SET ");
		boolean isFirst = true;
		for(String propKey : value.keySet()) {
			FacilioField field = fieldMap.get(propKey);
			if(field != null) {
				if(isFirst) {
					isFirst = false;
				}
				else {
					sql.append(", ");
				}
				sql.append(field.getCompleteColumnName())
					.append(" = ?");
			}
		}
		
		if(isFirst) {
			return null; //Nothing to update
		}
		
		if(where.getWhereClause() != null && !where.getWhereClause().trim().isEmpty()) {
			sql.append(" WHERE ")
				.append(where.getWhereClause());
		}
		
		return sql.toString();
	}
	
	private Map<String, FacilioField> convertFieldsToMap(List<FacilioField> fields) {
		if(fields != null) {
			Map<String, FacilioField> fieldMap = new HashMap<>();
			for(FacilioField field : fields) {
				fieldMap.put(field.getName(), field);
			}
			return fieldMap;
		}
		return null;
	}
	
	private void checkForNull() {
		
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}
		
		if(fields == null || fields.size() < 1) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
		}
		
	}
	
	public static class GenericJoinBuilder implements JoinBuilderIfc<GenericUpdateRecordBuilder> {

		private GenericUpdateRecordBuilder parentBuilder;
		private GenericJoinBuilder(GenericUpdateRecordBuilder parentBuilder) {
			// TODO Auto-generated constructor stub
			this.parentBuilder = parentBuilder;
		}
		
		@Override
		public GenericUpdateRecordBuilder on(String condition) {
			// TODO Auto-generated method stub
			parentBuilder.joinBuilder.append("ON ")
										.append(condition)
										.append(" ");
			return parentBuilder;
		}
		
	}
	
}
