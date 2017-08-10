package com.facilio.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;

public class GenericInsertRecordBuilder {
	private List<FacilioField> fields;
	private String tableName;
	private List<Map<String, Object>> values = new ArrayList<>();
	private Connection conn = null;
	
	public GenericInsertRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}
	
	public GenericInsertRecordBuilder fields(List<FacilioField> fields) {
		this.fields = fields;
		return this;
	}
	
	public GenericInsertRecordBuilder addRecord(Map<String, Object> value) {
		this.values.add(value);
		return this;
	}
	
	public GenericInsertRecordBuilder addRecords(List<Map<String, Object>> values) {
		this.values.addAll(values);
		return this;
	}
	
	public GenericInsertRecordBuilder connection(Connection conn) {
		this.conn = conn;
		return this;
	}
	
	public void save() throws SQLException, RuntimeException {
		
		if(values == null || values.isEmpty()) {
			return;
		}
		
		checkForNull();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = constructInsertStatement();
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			int paramIndex = 1;
			
			for(Map<String, Object> value : values) {
				pstmt.clearParameters();
				for(FacilioField field : fields) {
					FieldUtil.castOrParseValueAsPerType(pstmt, paramIndex, field.getDataType(), value.get(field.getName()));
					paramIndex++;
				}
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			rs = pstmt.getGeneratedKeys();
			List<Long> ids = new ArrayList<>();
			paramIndex = 0;
			while(rs.next()) {
				long id = rs.getLong(1);
				ids.add(id);
				Map<String, Object> props = values.get(paramIndex++);
				if(props != null) {
					props.put("id", id);
				}
			}
			System.out.println("Added records with IDs : "+ids);
		}
		catch(SQLException | RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}
	
	private String constructInsertStatement() {
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(tableName);
		sql.append(" (");
		
		boolean isFirst = true;
		for(FacilioField field : fields) {
			if(isFirst) {
				isFirst = false;
			}
			else {
				sql.append(", ");
			}
			sql.append(field.getColumnName());
		}
		
		sql.append(") VALUES (");
		
		isFirst = true;
		for(FacilioField field : fields) {
			if(isFirst) {
				isFirst = false;
			}
			else {
				sql.append(", ");
			}
			sql.append("?");
		}
		
		sql.append(")");
		
		return sql.toString();
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
