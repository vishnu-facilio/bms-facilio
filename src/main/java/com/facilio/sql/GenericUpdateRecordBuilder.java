package com.facilio.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;

public class GenericUpdateRecordBuilder implements UpdateBuilderIfc<Map<String, Object>> {
	private List<FacilioField> fields;
	private Map<String, FacilioField> fieldMap;
	private String tableName;
	private Map<String, Object> value;
	private WhereBuilder where = new WhereBuilder();
	private Connection conn = null;
	
	public GenericUpdateRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}
	
	public GenericUpdateRecordBuilder fields(List<FacilioField> fields) {
		this.fields = fields;
		return this;
	}
	
	public GenericUpdateRecordBuilder connection(Connection conn) {
		this.conn = conn;
		return this;
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
		this.value = value;
		if(value != null && value.size() > 0) {
			PreparedStatement pstmt = null;
			
			try {
				fieldMap = convertFieldsToMap(fields);
				String sql = constructUpdateStatement();
				if(sql != null && !sql.isEmpty()) {
					pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					
					int paramIndex = 1;
					for(Map.Entry<String, Object> entry : value.entrySet()) {
						FacilioField field = fieldMap.get(entry.getKey());
						if(field != null) {
							FieldUtil.castOrParseValueAsPerType(pstmt, paramIndex++, field.getDataType(), value.get(field.getName()));
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
				e.printStackTrace();
				throw e;
			}
			finally {
				if(pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return 0;
	}
	
	private String constructUpdateStatement() {
		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append(tableName)
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
				sql.append(field.getExtendedModule().getTableName())
					.append(".")
					.append(field.getColumnName())
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
		
		if(conn == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		
		if(fields == null || fields.size() < 1) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
		}
		
	}
	
}
