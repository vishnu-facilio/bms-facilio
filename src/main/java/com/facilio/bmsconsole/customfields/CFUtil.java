package com.facilio.bmsconsole.customfields;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class CFUtil {
	
	public static String getModuleName(String tableName, long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT NAME FROM "+tableName+" WHERE ORGID=?");
			pstmt.setLong(1, orgId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getString("NAME");
			}
			else {
				return null;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static long getObjId(String tableName,long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT OBJID FROM "+tableName+" WHERE ORGID=?");
			pstmt.setLong(1, orgId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getLong("OBJID");
			}
			else {
				return -1;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static int getColumnNum(FacilioCustomField field) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			pstmt = conn.prepareStatement("SELECT MAX(COLUMNNUM) AS MAX_COLUMN_NUM FROM "+field.getTableName()+" WHERE ORGID = ? AND OBJID = ? AND DATATYPE = ?");
			pstmt.setLong(1, field.getOrgId());
			pstmt.setLong(2, field.getObjId());
			pstmt.setLong(3, field.getDataType().getTypeAsInt());
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt("MAX_COLUMN_NUM")+1;
			}
			else {
				return 1;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static long addCustomField(FacilioCustomField cf) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO "+cf.getTableName()+" (ORGID, OBJID, FIELDNAME, COLUMNNUM, DATATYPE) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, cf.getOrgId());
			pstmt.setLong(2, cf.getObjId());
			pstmt.setString(3, cf.getFieldName());
			pstmt.setInt(4, cf.getColumnNum());
			pstmt.setInt(5, cf.getDataType().getTypeAsInt());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add ticket");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long fieldId = rs.getLong(1);
				System.out.println("Added Custom Field with ID : "+fieldId);
				return fieldId;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<FacilioCustomField> getCustomFields(String objecTableName, String fieldTableName, long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT FIELDID, ")
				.append(fieldTableName)
				.append(".ORGID, ")
				.append(fieldTableName)
				.append(".OBJID, FIELDNAME, DATATYPE, COLUMNNUM FROM ")
				.append(fieldTableName)
				.append(" INNER JOIN ")
				.append(objecTableName)
				.append(" ON ")
				.append(fieldTableName)
				.append(".OBJID = ")
				.append(objecTableName)
				.append(".OBJID WHERE ")
				.append(objecTableName)
				.append(".ORGID = ?");
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, orgId);
			
			rs = pstmt.executeQuery();
			List<FacilioCustomField> fields = new ArrayList<>();
			
			while(rs.next()) {
				FacilioCustomField field = new FacilioCustomField();
				
				field.setFieldId(rs.getLong("FIELDID"));
				field.setOrgId(rs.getLong("ORGID"));
				field.setObjId(rs.getLong("OBJID"));
				field.setFieldName(rs.getString("FIELDNAME"));
				field.setColumnNum(rs.getInt("COLUMNNUM"));
				field.setDataType(CFType.getCFType(rs.getInt("DATATYPE")));
				
				fields.add(field);
			}
			
			return fields;
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	//ORGID and OBJID are added by default
	public static String constuctInsertStatement(String objectTableName, String dataTableName, String[] defaultFields, List<FacilioCustomField> customFields, long orgId) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ")
			.append(dataTableName)
			.append(" (ORGID, OBJID");
		
		int defaultFieldsLength = 0, customFieldsLength = 0;
		
		if(defaultFields != null) {
			defaultFieldsLength = defaultFields.length;
			appendDefaultFields(sql, defaultFields);
		}
		
		if(customFields != null) {
			customFieldsLength = customFields.size();
			appendCustomFieldsForInsert(sql, customFields);
		}
		
		if(defaultFieldsLength == 0 && customFieldsLength == 0) {
			throw new IllegalArgumentException("Both Default Fields and Custom Fields cannot be null.");
		}
		
		sql.append(") VALUES (")
			.append(orgId)
			.append(", (SELECT OBJID FROM ")
			.append(objectTableName)
			.append(" WHERE ORGID = ")
			.append(orgId)
			.append(")");
			
		for(int i = 0; i < (defaultFieldsLength+customFieldsLength); i++) {
			sql.append(", ?");
		}
		
		sql.append(")");
		
		return sql.toString();
	}
	
	public static String constructSelectStatement(String objectTableName, String dataTableName, String[] defaultFields, List<FacilioCustomField> customFields, String[] whereFields) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ")
			.append(dataTableName)
			.append(".")
			.append("ORGID, ")
			.append(dataTableName)
			.append(".")
			.append("OBJID");
		
		int defaultFieldsLength = 0, customFieldsLength = 0;
		
		if(defaultFields != null) {
			defaultFieldsLength = defaultFields.length;
			appendDefaultFields(sql, defaultFields);
		}
		
		if(customFields != null) {
			customFieldsLength = customFields.size();
			appendCustomFieldsForSelect(sql, customFields);
		}
		
		if(defaultFieldsLength == 0 && customFieldsLength == 0) {
			throw new IllegalArgumentException("Both Default Fields and Custom Fields cannot be null.");
		}
		
		sql.append(" FROM ")
			.append(dataTableName)
			.append(" INNER JOIN ")
			.append(objectTableName)
			.append(" ON ")
			.append(dataTableName)
			.append(".OBJID = ")
			.append(objectTableName)
			.append(".OBJID");
		
		if(whereFields != null) {
			appendWhereFields(sql, whereFields, dataTableName);
		}
		
		return sql.toString();
	}
	
	private static void appendDefaultFields(StringBuilder sql, String[] defaultFields) {
		for(String field : defaultFields) {
			sql.append(", ")
				.append(field);
		}
	}
	
	private static void appendCustomFieldsForInsert(StringBuilder sql, List<FacilioCustomField> customFields) {
		for(FacilioCustomField field : customFields) {
			sql.append(", ")
				.append(field.getDataType().getTypeAsString())
				.append("_CF")
				.append(field.getColumnNum());
		}
	}
	
	private static void appendCustomFieldsForSelect(StringBuilder sql, List<FacilioCustomField> customFields) {
		for(FacilioCustomField field : customFields) {
			sql.append(", ")
				.append(field.getDataType().getTypeAsString())
				.append("_CF")
				.append(field.getColumnNum())
				.append(" AS ")
				.append(field.getFieldName());
		}
	}
	
	private static void appendWhereFields(StringBuilder sql, String[] whereFields, String dataTableName) {
		for(int i = 0; i < whereFields.length; i++) {
			if(i == 0) {
				sql.append(" WHERE ");
			}
			else {
				sql.append(" AND ");
			}
			sql.append(dataTableName)
				.append(".")
				.append(whereFields[i])
				.append(" = ?");
		}
	}
	
	public static void appendCustomFieldValues(List<FacilioCustomField> customFields, int defaultFieldsLength, Map<Object, Object> customProp, PreparedStatement pstmt) throws SQLException {
		for(int i=0; i<customFields.size(); i++) {
			FacilioCustomField field = customFields.get(i);
			int paramIndex = defaultFieldsLength+(i+1);
			String value = (String) customProp.get(field.getFieldName());
			parseValueAsPerType(pstmt, paramIndex, field.getDataType(), value);
		}
	}
	
	public static void parseValueAsPerType(PreparedStatement pstmt, int paramIndex, CFType type, String value) throws SQLException {
		
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
			case DATE: //Leaving as String for now
				if(value != null) {
					pstmt.setString(paramIndex, String.valueOf(value));
				}
				else {
					pstmt.setNull(paramIndex, Types.VARCHAR);
				}
				break;
			case DATE_TIME: //Leaving as String for now
				if(value != null) {
					pstmt.setString(paramIndex, String.valueOf(value));
				}
				else {
					pstmt.setNull(paramIndex, Types.VARCHAR);
				}
				break;
		}
	}
	
	public static Object getValueAsPerType(FacilioCustomField cf, ResultSet rs) throws SQLException {
		switch(cf.getDataType()) {
			case STRING:
				return rs.getString(cf.getFieldName());
			case NUMBER:
				return rs.getLong(cf.getFieldName());
			case DECIMAL:
				return rs.getDouble(cf.getFieldName());
			case BOOLEAN:
				return rs.getBoolean(cf.getFieldName());
			case DATE:
				return rs.getString(cf.getFieldName()); //String for now
			case DATE_TIME:
				return rs.getString(cf.getFieldName());//String for now
			default:
				return rs.getString(cf.getFieldName());
		}
	}
}
