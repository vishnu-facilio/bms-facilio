package com.facilio.bmsconsole.modules;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;

public class InsertRecordBuilder<E extends ModuleBaseWithCustomFields> {
	
	private String moduleName;
	private FacilioModule module;
	private List<FacilioField> fields;
	private int level = 1;
	
	public InsertRecordBuilder () {
		// TODO Auto-generated constructor stub
	}
	
	public InsertRecordBuilder<E> moduleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	
	public InsertRecordBuilder<E> module(FacilioModule module) {
		return this;
	}
	
	public InsertRecordBuilder<E> dataTableName(String dataTableName) {
		return this;
	}
	
	public InsertRecordBuilder<E> fields(List<FacilioField> fields) {
		this.fields = fields;
		return this;
	}
	
	public InsertRecordBuilder<E> level(int level) {
		this.level = level;
		return this;
	}
	
	@Deprecated
	public InsertRecordBuilder<E> connection(Connection conn) {
		return this;
	}
	
	private List<FacilioModule> splitModules() {
		List<FacilioModule> modules = new ArrayList<>();
		FacilioModule extendModule = module;
		while(extendModule != null) {
			modules.add(0, extendModule);
			extendModule = extendModule.getExtendModule();
		}
		
		return modules;
	}
	
	private Map<Long, List<FacilioField>> splitFields() {
		Map<Long, List<FacilioField>> fieldMap = new HashMap<>();
		
		for(FacilioField field : fields) {
			List<FacilioField> moduleFields = fieldMap.get(field.getExtendedModule().getModuleId());
			if(moduleFields == null) {
				moduleFields = new ArrayList<>();
				fieldMap.put(field.getExtendedModule().getModuleId(), moduleFields);
			}
			moduleFields.add(field);
		}
		
		return fieldMap;
	}
	
	public long insert(E bean) throws Exception {
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name cannot be empty");
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		module = modBean.getModule(moduleName);
		
		List<FacilioModule> modules = splitModules();
		Map<Long, List<FacilioField>> fieldMap = splitFields();
		
		Map<String, Object> moduleProps = FieldUtil.<E>getAsProperties(bean);
		moduleProps.put("orgId", OrgInfo.getCurrentOrgInfo().getOrgid());
		moduleProps.put("moduleId", module.getModuleId());
		
		for(FacilioField field : fields) {
			if(field.getDataType() == FieldType.LOOKUP) {
				Map<String, Object> lookupProps = (Map<String, Object>) moduleProps.get(field.getName()); 
				if(lookupProps != null) {
					moduleProps.put(field.getName(), lookupProps.get("id"));
				}
			}
		}
		
		int currentLevel = 1;
		for(FacilioModule currentModule : modules) {
			if(currentLevel >= level) {
				List<FacilioField> currentFields = fieldMap.get(currentModule.getModuleId());
				if(currentFields == null) {
					currentFields = new ArrayList<>();
				}
				currentFields.add(FieldFactory.getIdField(currentModule));
				currentFields.add(FieldFactory.getOrgIdField(currentModule));
				currentFields.add(FieldFactory.getModuleIdField(currentModule));
				
				moduleProps.put("moduleId", currentModule.getModuleId());
				
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
																.table(currentModule.getTableName())
																.fields(currentFields)
																.addRecord(moduleProps);
				
				insertBuilder.save();
			}
			currentLevel++;
		}
		return (long) moduleProps.get("id");
	}
}
