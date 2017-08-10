package com.facilio.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;

public class GenericSelectRecordBuilder {
	private List<FacilioField> selectFields;
	private String tableName;
	private Connection conn = null;
	private String whereCondition;
	private Object[] whereValues;
	private String orderBy;
	private int limit;
	
	public GenericSelectRecordBuilder select(List<FacilioField> fields) {
		this.selectFields = fields;
		return this;
	}
	
	public GenericSelectRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}
	
	public GenericSelectRecordBuilder connection(Connection conn) {
		this.conn = conn;
		return this;
	}
	
	public GenericSelectRecordBuilder where(String where, Object... values) {
		
		int count = StringUtils.countMatches(where, "?");
		if(values.length < count) {
			throw new IllegalArgumentException("No. of where values doesn't match the number of ?");
		}
		
		this.whereCondition = where;
		this.whereValues = values;
		return this;
	}
	
	public GenericSelectRecordBuilder orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	public GenericSelectRecordBuilder limit(int limit) {
		this.limit = limit;
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
	
	public List<Map<String, Object>> get() throws Exception {
		checkForNull(false);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			String sql = constructSelectStatement();
			pstmt = conn.prepareStatement(sql);
			
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
					record.put(field.getName(), FieldUtil.getValueAsPerType(field, rs));
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
			sql.append(field.getModuleTableName())
				.append(".")
				.append(field.getColumnName());
		}
		
		sql.append(" FROM ")
			.append(tableName);
		
		isFirst = true;
		if(whereCondition != null && !whereCondition.isEmpty()) {
			if(isFirst) {
				isFirst = false;
				sql.append(" WHERE ");
			}
			else {
				sql.append(" AND ");
			}
			sql.append(whereCondition);
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
}
