package com.facilio.bmsconsole.fields;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.facilio.fw.OrgInfo;

public class FieldUtil {
	
	//ORGID and MODULEID are added by default
	public static String constuctInsertStatement(String moduleName, String dataTableName, List<FacilioField> fields) {
		
		if(fields == null || fields.size() <= 0) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
		}
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ")
			.append(dataTableName)
			.append(" (ORGID, MODULEID");
		
		for(FacilioField field : fields) {
			sql.append(", ")
				.append(field.getColumnName());
		}
		
		sql.append(") VALUES (")
			.append(orgId)
			.append(", (SELECT MODULEID FROM Modules WHERE ORGID = ")
			.append(orgId)
			.append(" AND NAME = '")
			.append(moduleName)
			.append("')");
			
		for(int i = 0; i < fields.size(); i++) {
			sql.append(", ?");
		}
		
		sql.append(")");
		
		return sql.toString();
	}
	
	public static String constructSelectStatement(String dataTableName, List<FacilioField> fields, String[] whereFields) {
		
		if(fields == null || fields.size() <= 0) {
			throw new IllegalArgumentException("Fields cannot be null or empty.");
		}
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ORGID, MODULEID");
		
		for(FacilioField field : fields) {
			sql.append(", ")
				.append(field.getColumnName())
				.append(" AS '")
				.append(field.getName())
				.append("'");
		}
		
		sql.append(" FROM ")
			.append(dataTableName)
			.append(" WHERE ORGID = ")
			.append(orgId);
		
		if(whereFields != null) {
			for(int i = 0; i < whereFields.length; i++) {
				sql.append(" AND ")
					.append(dataTableName)
					.append(".")
					.append(whereFields[i])
					.append(" = ?");
			}
		}
		
		return sql.toString();
	}
	
	public static void appendCustomFieldValues(List<FacilioField> customFields, int defaultFieldsLength, Map<Object, Object> customProp, PreparedStatement pstmt) throws SQLException {
		for(int i=defaultFieldsLength; i<customFields.size(); i++) {
			FacilioField field = customFields.get(i);
			int paramIndex = i+1;
			String value = (String) customProp.get(field.getName());
			parseValueAsPerType(pstmt, paramIndex, field.getDataType(), value);
		}
	}
	
	public static void parseValueAsPerType(PreparedStatement pstmt, int paramIndex, FieldType type, String value) throws SQLException {
		
		switch(type) {
			
			case STRING:
				if(value != null) {
					pstmt.setString(paramIndex, value);
				}
				else {
					pstmt.setNull(paramIndex, Types.VARCHAR);
				}
				break;
			case NUMBER:
				if(value != null) {
					pstmt.setInt(paramIndex, Integer.parseInt(value));
				}
				else {
					pstmt.setNull(paramIndex, Types.INTEGER);
				}
				break;
			case LONG_INTEGER:
				if(value != null) {
					pstmt.setLong(paramIndex, Long.parseLong(value));
				}
				else {
					pstmt.setNull(paramIndex, Types.BIGINT);
				}
			case DECIMAL:
				if(value != null) {
					pstmt.setDouble(paramIndex, Double.parseDouble(value));
				}
				else {
					pstmt.setNull(paramIndex, Types.DOUBLE);
				}
				break;
			case BOOLEAN:
				if(value != null) {
					pstmt.setBoolean(paramIndex, Boolean.parseBoolean(value));
				}
				else {
					pstmt.setNull(paramIndex, Types.BOOLEAN);
				}
				break;
			case DATE:
				if(value != null) {
					pstmt.setLong(paramIndex, Long.parseLong(value));
				}
				else {
					pstmt.setNull(paramIndex, Types.BIGINT);
				}
				break;
			case DATE_TIME:
				if(value != null) {
					pstmt.setLong(paramIndex, Long.parseLong(value));
				}
				else {
					pstmt.setNull(paramIndex, Types.BIGINT);
				}
				break;
		}
	}
	
	public static Object getValueAsPerType(FacilioField cf, ResultSet rs) throws SQLException {
		switch(cf.getDataType()) {
			case STRING:
				return rs.getString(cf.getName());
			case NUMBER:
				return rs.getInt(cf.getName());
			case LONG_INTEGER:
				return rs.getLong(cf.getName());
			case DECIMAL:
				return rs.getDouble(cf.getName());
			case BOOLEAN:
				return rs.getBoolean(cf.getName());
			case DATE:
				return rs.getLong(cf.getName());
			case DATE_TIME:
				return rs.getLong(cf.getName());
			default:
				return rs.getString(cf.getName());
		}
	}
}
