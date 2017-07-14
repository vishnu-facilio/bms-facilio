package com.facilio.bmsconsole.modules;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class SelectRecordsBuilder<E extends ModuleBaseWithCustomFields> {
	
	private String dataTableName;
	private Class<E> beanClass;
	private List<FacilioField> selectFields;
	private Connection conn;
	private String whereCondition;
	private Object[] whereValues;
	private String orderBy;
	private int limit;
	
	//Need where condition builder for custom field
	
	public SelectRecordsBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public SelectRecordsBuilder<E> dataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
		return this;
	}
	
	public SelectRecordsBuilder<E> beanClass(Class<E> beanClass) {
		this.beanClass = beanClass;
		return this;
	}
	
	public SelectRecordsBuilder<E> select(List<FacilioField> selectFields) {
		this.selectFields = selectFields;
		return this;
	}
	
	public SelectRecordsBuilder<E> connection(Connection conn) {
		this.conn = conn;
		return this;
	}
	
	public SelectRecordsBuilder<E> where(String where, Object... values) {
		
		int count = StringUtils.countMatches(where, "?");
		if(values.length < count) {
			throw new IllegalArgumentException("No. of where values doesn't match the number of ?");
		}
		
		this.whereCondition = where;
		this.whereValues = values;
		return this;
	}
	
	public SelectRecordsBuilder<E> orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	public SelectRecordsBuilder<E> limit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public List<E> get() throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException {
		
		checkForNull();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = constructSelectStatement();
			System.out.println("Shiva select Records :::sql " + sql);
			System.out.println("Shiva select Records :::whereValues " + whereValues);
			
			pstmt = conn.prepareStatement(sql);
			
			if(whereValues != null) {
				for(int i=0; i<whereValues.length; i++) {
					Object value = whereValues[i];
					pstmt.setObject(i+1, value);
				}
			}
			
			rs = pstmt.executeQuery();
			List<E> records = new ArrayList<>();
			while(rs.next()) {
				E record = getBeanFromRS(rs);
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
	
	private void checkForNull() {
		if(dataTableName == null || dataTableName.isEmpty()) {
			throw new IllegalArgumentException("Data Table Name cannot be empty");
		}
		
		if(beanClass == null) {
			throw new IllegalArgumentException("Bean class object cannot be null");
		}
		
		if(conn == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		
		if(selectFields == null || selectFields.size() <= 0) {
			throw new IllegalArgumentException("Select Fields cannot be null or empty");
		}
	}
	
	private E getBeanFromRS(ResultSet rs) throws InstantiationException, IllegalAccessException, InvocationTargetException, SQLException {
		E bean = beanClass.newInstance();
		Map<String, Object> customProps = new HashMap<>();
		for(FacilioField field : selectFields) {
			if(propertyExists(bean, field.getName())) {
				BeanUtils.setProperty(bean, field.getName(), FieldUtil.getValueAsPerType(field, rs));
			}
			else {
				customProps.put(field.getName(), FieldUtil.getValueAsPerType(field, rs));
			}
		}
		bean.setCustomProps(customProps);
		return bean;
	}
	
	private boolean propertyExists (Object bean, String property) {
	    return PropertyUtils.isReadable(bean, property) && 
	            PropertyUtils.isWriteable(bean, property); 
	 } 
	
	private String constructSelectStatement() {
		
		if(selectFields == null || selectFields.size() <= 0) {
			throw new IllegalArgumentException("Fields cannot be null or empty.");
		}
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ORGID as orgId, MODULEID as moduleId");
		
		for(FacilioField field : selectFields) {
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
		
		if(whereCondition != null && !whereCondition.isEmpty()) {
			sql.append(" AND ")
				.append(whereCondition);
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
