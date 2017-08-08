package com.facilio.bmsconsole.modules;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import com.facilio.sql.DBUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateRecordBuilder<E extends ModuleBaseWithCustomFields> {
	
	private String moduleName;
	private String dataTableName;
	private Connection conn;
	
	public UpdateRecordBuilder () {
		// TODO Auto-generated constructor stub
	}
	
	public UpdateRecordBuilder<E> moduleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	
	public UpdateRecordBuilder<E> dataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
		return this;
	}
	
	public UpdateRecordBuilder<E> connection(Connection conn) {
		this.conn = conn;
		return this;
	}
	
	public void update(E bean) throws Exception 
	{
		checkForNull();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			Map<String, Object> moduleProps = getAsProperties(bean);
			String sql = constuctUpdateStatement(moduleProps);
			
			pstmt = conn.prepareStatement(sql);
			appendFieldValues(moduleProps, pstmt);
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to update Module");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			e.printStackTrace();
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
	}
	
	private void checkForNull() {
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name cannot be empty");
		}
		
		if(dataTableName == null || dataTableName.isEmpty()) {
			throw new IllegalArgumentException("Data Table Name cannot be empty");
		}
		
		if(conn == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
	}
	
	private String constuctUpdateStatement(Map<String, Object> properties) 
	{
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ")
			.append(dataTableName)
			.append(" SET ");
		
		Iterator<String> fields = properties.keySet().iterator();
		int paramIndex = 1;
		while(fields.hasNext()) 
		{
			String columnName = fields.next();
			sql.append(columnName);
			sql.append(" = ?");
			if(properties.size() > paramIndex)
			{
				sql.append(", ");
			}
			paramIndex++;
		}
		sql.append("WHERE ID = ?");
		return sql.toString();
	}
	
	private void appendFieldValues(Map<String, Object> properties, PreparedStatement pstmt) throws SQLException 
	{
		Iterator<String> fields = properties.keySet().iterator();
		int paramIndex = 1;
		while(fields.hasNext()) 
		{
			String columnName = fields.next();
			FieldUtil.castOrParseValueAsPerType(pstmt, paramIndex, FieldType.STRING, properties.get(columnName));
			paramIndex++;
		}
		FieldUtil.castOrParseValueAsPerType(pstmt, paramIndex, FieldType.NUMBER, properties.get("id"));
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getAsProperties(E bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException 
	{
		Map<String, Object> properties = null;
		if(bean != null) 
		{
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_DEFAULT);
			properties = mapper.convertValue(bean, Map.class);
			
			Map<String, String> customProps = (Map<String, String>) properties.remove("customProps");
			if(customProps != null)
			{
				properties.putAll(customProps);
			}
		}
		return properties;
	}
}
