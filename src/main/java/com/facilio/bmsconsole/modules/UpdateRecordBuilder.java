package com.facilio.bmsconsole.modules;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdateRecordBuilder<E extends ModuleBaseWithCustomFields> {
	
	private GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder();
	private String moduleName;
	private long moduleId = -1;
	private String dataTableName;
	private Connection conn;
	private List<FacilioField> fields = new ArrayList<>();
	private String where;
	private Object[] whereValues;
	
	public UpdateRecordBuilder () {
		// TODO Auto-generated constructor stub
	}
	
	public UpdateRecordBuilder<E> moduleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	
	public UpdateRecordBuilder<E> dataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
		builder.table(dataTableName);
		return this;
	}
	
	public UpdateRecordBuilder<E> connection(Connection conn) {
		this.conn = conn;
		builder.connection(conn);
		return this;
	}
	
	public UpdateRecordBuilder<E> where(String where, Object... values) {
		this.where = where;
		this.whereValues = values;
		return this;
	}
	
	public UpdateRecordBuilder<E> fields(List<FacilioField> fields) {
		this.fields = fields;
		return this;
	}
	
	private long getModuleId() {
		if (this.moduleId <= 0) {
			try {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", conn);
				this.moduleId = modBean.getModule(moduleName).getModuleId();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.moduleId;
	}
	
	public int update(E bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		checkForNull();
		fields.add(FieldFactory.getOrgIdField(dataTableName));
		fields.add(FieldFactory.getModuleIdField(dataTableName));
		fields.add(FieldFactory.getIdField(dataTableName));
		builder.fields(fields);
		
		StringBuilder whereCondition = new StringBuilder(dataTableName);
		whereCondition.append(".ORGID = ? AND ")
						.append(dataTableName)
						.append(".MODULEID = ?");
		if(where != null && !where.isEmpty()) {
			whereCondition.append(" AND ")
							.append(where);
		}
		
		Object[] whereVals = null;
		if(whereValues != null) {
			whereVals = new Object[whereValues.length+2];
			whereVals[0] = OrgInfo.getCurrentOrgInfo().getOrgid();
			whereVals[1] = getModuleId();
			for(int i=0; i<whereValues.length; i++) {
				whereVals[i+2] = whereValues[i];
			}
		}
		else {
			whereVals = new Object[2];
			whereVals[0] = OrgInfo.getCurrentOrgInfo().getOrgid();
			whereVals[1] = getModuleId();
		}
		builder.where(whereCondition.toString(), whereVals);
		
		Map<String, Object> moduleProps = FieldUtil.<E>getAsProperties(bean);
		moduleProps.remove("orgId");
		moduleProps.remove("moduleId");
		moduleProps.remove("id");
		
		return builder.update(moduleProps);
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
		
		if(fields == null || fields.size() < 1) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
		}
	}
}
