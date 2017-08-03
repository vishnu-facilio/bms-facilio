package com.facilio.bmsconsole.modules;

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

import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class SelectRecordsBuilder<E extends ModuleBaseWithCustomFields> {
	
	private static final int LEVEL = 2;
	
	private String dataTableName;
	private Class<E> beanClass;
	private List<FacilioField> selectFields;
	private Connection conn;
	private String whereCondition;
	private Object[] whereValues;
	private String orderBy;
	private int limit;
	private int level = 0;
	
	//Need where condition builder for custom field
	
	public SelectRecordsBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public SelectRecordsBuilder (int level) {
		this.level = level;
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
	
	private ResultSet getResultSet(PreparedStatement pstmt) throws SQLException {
		String sql = constructSelectStatement();
		pstmt = conn.prepareStatement(sql);
		
		if(whereValues != null) {
			for(int i=0; i<whereValues.length; i++) {
				Object value = whereValues[i];
				pstmt.setObject(i+1, value);
			}
		}
		
		return pstmt.executeQuery();
	}
	
	public List<E> getAsBean() throws Exception {
		checkForNull(true);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			rs = getResultSet(pstmt);
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
	
	public List<Map<String, Object>> getAsProps() throws Exception {
		checkForNull(false);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			rs = getResultSet(pstmt);
			List<Map<String, Object>> records = new ArrayList<>();
			while(rs.next()) {
				Map<String, Object> record = new HashMap<>();
				record.put("orgId", rs.getLong("ORGID"));
				record.put("moduleId", rs.getLong("MODULEID"));
				record.put("id", rs.getLong("ID"));
				for(FacilioField field : selectFields) {
					record.put(field.getName(), getVal(field, rs));
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
	
	private Object getVal(FacilioField field, ResultSet rs) throws Exception {
		if (field.getDataType() == FieldType.LOOKUP) {
			long id = (long) FieldUtil.getValueAsPerType(field, rs);
			if(id != 0) {
				LookupField lookupField = (LookupField) field;
				if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
					if(level <= LEVEL) {
						return LookupSpecialTypeUtil.getLookedupObject(lookupField.getSpecialType(), id);
					}
					else {
						return LookupSpecialTypeUtil.getEmptyLookedupObject(lookupField.getSpecialType(), id);
					}
				}
				else {
					Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModuleName(lookupField.getLookupModule().getName());
					if(moduleClass != null) {
						if(level <= LEVEL) {
							List<FacilioField> lookupBeanFields = FieldUtil.getAllFields(lookupField.getLookupModule().getName(), conn);
							SelectRecordsBuilder<ModuleBaseWithCustomFields> lookupBeanBuilder = new SelectRecordsBuilder<>(level+1)
																								.connection(conn)
																								.dataTableName(lookupField.getLookupModule().getTableName())
																								.beanClass(moduleClass)
																								.select(lookupBeanFields)
																								.where("ID = ?", id);
							List<ModuleBaseWithCustomFields> records = lookupBeanBuilder.getAsBean();
							if(records != null && records.size() > 0) {
								return records.get(0);
							}
							else {
								return null;
							}
						}
						else {
							ModuleBaseWithCustomFields lookedupModule = moduleClass.newInstance();
							lookedupModule.setId(id);
							return lookedupModule;
						}
					}
					else {
						throw new IllegalArgumentException("Unknown Module Name in Lookup field "+field);
					}
				}
			}
			else {
				return null;
			}
		}
		else {
			return FieldUtil.getValueAsPerType(field, rs);
		}
	}
	
	private void checkForNull(boolean checkBean) {
		if(dataTableName == null || dataTableName.isEmpty()) {
			throw new IllegalArgumentException("Data Table Name cannot be empty");
		}
		
		if(checkBean) {
			if(beanClass == null) {
				throw new IllegalArgumentException("Bean class object cannot be null");
			}
		}
		
		if(conn == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		
		if(selectFields == null || selectFields.size() <= 0) {
			throw new IllegalArgumentException("Select Fields cannot be null or empty");
		}
	}
	
	private E getBeanFromRS(ResultSet rs) throws Exception {
		E bean = beanClass.newInstance();
		Map<String, Object> customProps = new HashMap<>();
		bean.setOrgId(rs.getLong("ORGID"));
		bean.setModuleId(rs.getLong("MODULEID"));
		bean.setId(rs.getLong("ID"));
		for(FacilioField field : selectFields) {
			Object val = getVal(field, rs);
			if(val != null) {
				if(propertyExists(bean, field.getName())) {
					BeanUtils.setProperty(bean, field.getName(), val);
				}
				else {
					customProps.put(field.getName(), getVal(field, rs));
				}
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
		sql.append("SELECT ORGID, MODULEID, ID");
		
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
