package com.facilio.bmsconsole.modules;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.InsertBuilderIfc;

public class InsertRecordBuilder<E extends ModuleBaseWithCustomFields> implements InsertBuilderIfc<E> {
	
	private String moduleName;
	private FacilioModule module;
	private List<FacilioField> fields;
	private int level = 1;
	private List<E> records = new ArrayList<>();
	private boolean inserted = false;
	private boolean withChangeSet = false;
	private Map<Long, List<UpdateChangeSet>> changeSet;
	private boolean isWithLocalIdModule;
	private Connection conn = null;

	public InsertRecordBuilder () {
		// TODO Auto-generated constructor stub
	}
	
	public InsertRecordBuilder<E> moduleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	
	public InsertRecordBuilder<E> module(FacilioModule module) {
		this.module = module;
		return this;
	}
	
	@Override
	public InsertRecordBuilder<E> table(String tableName) {
		return this;
	}
	
	@Override
	public InsertRecordBuilder<E> useExternalConnection (Connection conn) {
		this.conn = conn;
		return this;
	}
	
	@Override
	public InsertRecordBuilder<E> fields(List<FacilioField> fields) {
		this.fields = fields;
		return this;
	}
	
	public InsertRecordBuilder<E> level(int level) {
		this.level = level;
		return this;
	}
	

	
	@Override
	public InsertRecordBuilder<E> addRecord(E bean) {
		this.records.add(bean);
		return this;
	}
	
	@Override
	public InsertRecordBuilder<E> addRecords(List<E> beans) {
		this.records.addAll(beans);
		return this;
	}
	
	public InsertRecordBuilder<E> withLocalId() {
		this.isWithLocalIdModule = true;
		return this;
	}
	
	@Override
	public List<E> getRecords() {
		// TODO Auto-generated method stub
		return records;
	}
	
	public InsertRecordBuilder<E> withChangeSet() {
		this.withChangeSet = true;
		return this;
	}
	
	public Map<Long, List<UpdateChangeSet>> getChangeSet() {
		
		if (!inserted) {
			throw new IllegalArgumentException("Update first and then get change set.");
		}
		return changeSet;
	}
	
	private void checkForNull() throws Exception {
		
		if(fields == null || fields.size() < 1) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
		}
		
		if(module == null) {
			if(moduleName == null || moduleName.isEmpty()) {
				throw new IllegalArgumentException("Both Module and Module Name cannot be empty");
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			module = modBean.getModule(moduleName);
		}
	}
	
	@Override
	public void save() throws Exception {
		inserted = true;
		
		if(records.isEmpty()) {
			return;
		}
		
		checkForNull();
		
		List<FacilioModule> modules = splitModules();
		Map<Long, List<FacilioField>> fieldMap = splitFields();
		
		long localId = getLocalId(modules);
		
		List<Map<String, Object>> beanProps = new ArrayList<>();
		for(E bean : records) {
			if(isWithLocalIdModule) {
				bean.setLocalId(++localId);
			}
			bean.setSysCreatedTime(System.currentTimeMillis());
			bean.setTtime(System.currentTimeMillis());
			bean.setModifiedTime(System.currentTimeMillis());
			beanProps.add(getAsProps(bean));
		}
		
		int currentLevel = 1;
		for(FacilioModule currentModule : modules) {
			
			if(currentLevel >= level) {
				List<FacilioField> currentFields = fieldMap.get(currentModule.getModuleId());
				if(currentFields == null) {
					currentFields = new ArrayList<>();
				}
				currentFields.add(FieldFactory.getIdField(currentModule));
				/*currentFields.add(FieldFactory.getOrgIdField(currentModule));*/
				currentFields.add(FieldFactory.getModuleIdField(currentModule));
				
				if (FieldUtil.isSiteIdFieldPresent(currentModule)) {
					currentFields.add(FieldFactory.getSiteIdField(currentModule));
				}
				
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
																.table(currentModule.getTableName())
																.fields(currentFields);
				
				for(Map<String, Object> beanProp : beanProps) {
					beanProp.put("moduleId", currentModule.getModuleId());
					insertBuilder.addRecord(beanProp);
				}
				
				if (conn != null) {
					insertBuilder.useExternalConnection(conn);
				}
				
				insertBuilder.save();
			}
			currentLevel++;
		}
		
		Map<String, FacilioField> fieldNameMap = null;
		if (withChangeSet) {
			fieldNameMap = FieldFactory.getAsMap(fields);
			changeSet = new HashMap<>();
		}
		
		for(int itr = 0; itr < records.size(); itr++) {
			Map<String, Object> beanProp = beanProps.get(itr);
			if(beanProp.get("id") != null) {
				long id = (long) beanProp.get("id");
				records.get(itr).setId(id);
				
				if (withChangeSet) {
					List<UpdateChangeSet> changeList = constructChangeSet(id, beanProp, fieldNameMap);
					changeSet.put(id, changeList);
				}
			}
		}
		
	}
	
	private List<UpdateChangeSet> constructChangeSet(long recordId, Map<String, Object> prop, Map<String, FacilioField> fieldMap) {
		Set<String> fieldNames = fieldMap.keySet();
		List<UpdateChangeSet> changeList = new ArrayList<>();
		for (Map.Entry<String, Object> entry : prop.entrySet()) {
			if (fieldNames.contains(entry.getKey())) {
				UpdateChangeSet currentChange = new UpdateChangeSet();
				currentChange.setFieldId(fieldMap.get(entry.getKey()).getFieldId());
				currentChange.setNewValue(entry.getValue());
				currentChange.setRecordId(recordId);
				changeList.add(currentChange);
			}
		}
		return changeList;
	}
	
	private long getLocalId (List<FacilioModule> modules) throws Exception {
		long localId = -1;
		if(isWithLocalIdModule) {
			for (int i = modules.size() - 1; i >= 0; i--) {
				FacilioModule module = modules.get(i);
				localId = ModuleLocalIdUtil.getAndUpdateModuleLocalId(module.getName(), records.size());
				if (localId != -1) {
					break;
				}
			}
			
			if (localId == -1) {
				throw new IllegalArgumentException(modules.get(modules.size() - 1).getName()+" doesn't have last local id. This is not supposed to happen");
			}
		}
		return localId;
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
	
	private Map<String, Object> getAsProps(E bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> moduleProps = FieldUtil.getAsProperties(bean);
		moduleProps.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		
		for(FacilioField field : fields) {
			if(field.getDataTypeEnum() == FieldType.LOOKUP) {
				Object val = moduleProps.get(field.getName());
				if(val != null && val instanceof Map) {
					Map<String, Object> lookupProps = (Map<String, Object>) val; 
					if(lookupProps != null) {
						moduleProps.put(field.getName(), lookupProps.get("id"));
					}
				}
			}
		}
		return moduleProps;
	}
	
	@Override
	public long insert(E bean) throws Exception {
		addRecord(bean);
		save();
		return bean.getId();
	}
}
