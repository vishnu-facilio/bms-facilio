package com.facilio.bmsconsole.modules;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class FieldUtil {
	
	public static void castOrParseValueAsPerType(PreparedStatement pstmt, int paramIndex, FieldType type, Object value) throws SQLException {
		
		switch(type) {
			
			case STRING:
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					if(value instanceof String) {
						pstmt.setString(paramIndex, (String) value);
					}
					else {
						pstmt.setString(paramIndex, value.toString());
					}
				}
				else {
					pstmt.setNull(paramIndex, Types.VARCHAR);
				}
				break;
			case NUMBER:
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					int val;
					if(value instanceof Integer) {
						val = (int) value;
					}
					else {
						val = Integer.parseInt(value.toString());
					}
					if(val != 0) {
						pstmt.setInt(paramIndex, val);
					}
					else {
						pstmt.setNull(paramIndex, Types.INTEGER);
					}
				}
				else {
					pstmt.setNull(paramIndex, Types.INTEGER);
				}
				break;
			case DECIMAL:
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					double val;
					if(value instanceof Double) {
						val = (double) value;
					}
					else {
						val = Double.parseDouble(value.toString());
					}
					if(val != 0) {
						pstmt.setDouble(paramIndex, val);
					}
					else {
						pstmt.setNull(paramIndex, Types.DOUBLE);
					}
				}
				else {
					pstmt.setNull(paramIndex, Types.DOUBLE);
				}
				break;
			case BOOLEAN:
				if(value != null) {
					if(value instanceof Boolean) {
						pstmt.setBoolean(paramIndex, (boolean) value);
					}
					else {
						pstmt.setBoolean(paramIndex, Boolean.parseBoolean(value.toString()));
					}
				}
				else {
					pstmt.setNull(paramIndex, Types.BOOLEAN);
				}
				break;
			case ID:
			case LONG_INTEGER:	
			case DATE:
			case DATE_TIME:
			case USER:
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					long val;
					if(value instanceof Long) {
						val = (long) value;
					}
					else {
						val = Long.parseLong(value.toString());
					}
					if(val != 0) {
						pstmt.setLong(paramIndex, val);
					}
					else {
						pstmt.setNull(paramIndex, Types.BIGINT);
					}
				}
				else {
					pstmt.setNull(paramIndex, Types.BIGINT);
				}
		}
	}
	
	public static Object getValueAsPerType(FacilioField cf, ResultSet rs) throws SQLException {
		switch(cf.getDataType()) {
			case STRING:
				return rs.getString(cf.getName());
			case NUMBER:
				return rs.getInt(cf.getName());
			case DECIMAL:
				return rs.getDouble(cf.getName());
			case BOOLEAN:
				return rs.getBoolean(cf.getName());
			case ID:
			case LONG_INTEGER:	
			case DATE:
			case DATE_TIME:
			case USER:
				return rs.getLong(cf.getName());
			default:
				return rs.getString(cf.getName());
		}
	}
}
