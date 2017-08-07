package com.facilio.bmsconsole.modules;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InsertRecordBuilder<E extends ModuleBaseWithCustomFields> {
	
	private String moduleName;
	private String dataTableName;
	private Connection conn;
	private List<FacilioField> fields;
	
	public InsertRecordBuilder () {
		// TODO Auto-generated constructor stub
	}
	
	public InsertRecordBuilder<E> moduleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	
	public InsertRecordBuilder<E> dataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
		return this;
	}
	
	public InsertRecordBuilder<E> fields(List<FacilioField> fields) {
		this.fields = fields;
		return this;
	}
	
	public InsertRecordBuilder<E> connection(Connection conn) {
		this.conn = conn;
		return this;
	}
	
	public long insert(E bean) throws Exception {
		checkForNull();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = constuctInsertStatement();
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			Map<String, Object> moduleProps = getAsProperties(bean);
			appendFieldValues(moduleProps, pstmt);
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add Module");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				long id = 0;
				if(rs.next())
				{
					id = rs.getLong(1);
					System.out.println("Added "+bean.getClass().getName()+" object with id : "+id);
				}
				return id;
			}
		}
		catch(SQLException | RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
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
		
		if(fields == null || fields.size() <= 0) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
		}
	}
	
	private String constuctInsertStatement() {
		
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
			
		for(FacilioField field : fields) {
			sql.append(", ?");
		}
		
		sql.append(")");
		
		return sql.toString();
	}
	
	private void appendFieldValues(Map<String, Object> properties, PreparedStatement pstmt) throws SQLException {
		int paramIndex = 1;
		for(FacilioField field : fields) {
			Object value = null;
			if(field.getDataType() == FieldType.LOOKUP) {
				Map<String, Object> moduleProps = (Map<String, Object>) properties.get(field.getName()); 
				if(moduleProps != null) {
					value = moduleProps.get("id");
				}
			}
			else {
				value = properties.get(field.getName());
			}
			
			FieldUtil.castOrParseValueAsPerType(pstmt, paramIndex, field.getDataType(), value);
			paramIndex++;
		}
	}
	
	private Map<String, Object> getAsProperties(E bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> properties = null;
		
		if(bean != null) {
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
