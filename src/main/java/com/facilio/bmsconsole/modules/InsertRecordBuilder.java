package com.facilio.bmsconsole.modules;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;

public class InsertRecordBuilder<E extends ModuleBaseWithCustomFields> {
	
	private GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder();
	private String moduleName;
	private long moduleId = -1;
	private List<FacilioField> fields = new ArrayList<>();
	
	public InsertRecordBuilder () {
		// TODO Auto-generated constructor stub
	}
	
	public InsertRecordBuilder<E> moduleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	
	public InsertRecordBuilder<E> dataTableName(String dataTableName) {
		builder.table(dataTableName);
		return this;
	}
	
	public InsertRecordBuilder<E> fields(List<FacilioField> fields) {
		this.fields.add(FieldFactory.getOrgIdField());
		this.fields.add(FieldFactory.getModuleIdField());
		this.fields.addAll(fields);
		
		builder.fields(this.fields);
		return this;
	}
	
	public InsertRecordBuilder<E> connection(Connection conn) {
		builder.connection(conn);
		return this;
	}
	
	public long insert(E bean) throws Exception {
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name cannot be empty");
		}
		
		Map<String, Object> moduleProps = FieldUtil.<E>getAsProperties(bean);
		moduleProps.put("orgId", OrgInfo.getCurrentOrgInfo().getOrgid());
		moduleProps.put("moduleId", getModuleId());
		
		for(FacilioField field : fields) {
			if(field.getDataType() == FieldType.LOOKUP) {
				Map<String, Object> lookupProps = (Map<String, Object>) moduleProps.get(field.getName()); 
				if(lookupProps != null) {
					moduleProps.put(field.getName(), lookupProps.get("id"));
				}
			}
		}
		
		builder.addRecord(moduleProps);
		builder.save();
		return (long) moduleProps.get("id");
			
	}
	
	private long getModuleId() {
		if (this.moduleId <= 0) {
			try {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				this.moduleId = modBean.getModule(moduleName).getModuleId();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.moduleId;
	}
	
}
