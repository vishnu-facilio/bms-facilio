package com.facilio.modules;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.InsertBuilderIfc;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.lang3.tuple.Pair;

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
	private int recordsPerBatch = -1;

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

	@Override
	public InsertRecordBuilder<E> recordsPerBatch(int recordsPerBatch) {
		this.recordsPerBatch = recordsPerBatch;
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

		List<Pair<FacilioModule, Long>> modules = splitModules();
		Map<Long, List<FacilioField>> fieldMap = splitFields();
		
		long localId = getLocalId(modules);
		
		List<Map<String, Object>> beanProps = new ArrayList<>();
		for(E bean : records) {
			if(isWithLocalIdModule) {
				bean.setLocalId(++localId);
			}
			beanProps.add(getAsProps(bean));
		}
		List<FileField> fileFields = new ArrayList<>();
		
		int currentLevel = 1;
		for(Pair<FacilioModule, Long> modulePair : modules) {
			if(currentLevel >= level) {
				FacilioModule currentModule = modulePair.getLeft();
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
				
				if (FieldUtil.isSystemFieldsPresent(currentModule)) {
					currentFields.addAll(FieldFactory.getSystemFields(currentModule));
				}
				
				if (FieldUtil.isBaseEntityRootModule(currentModule)) {
					currentFields.addAll(FieldFactory.getBaseModuleSystemFields(currentModule));
				}
				
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
																.table(currentModule.getTableName())
																.fields(currentFields);

				long moduleId = modulePair.getRight();
				for(Map<String, Object> beanProp : beanProps) {
					beanProp.put("moduleId", moduleId);
					insertBuilder.addRecord(beanProp);
				}
				
				if (conn != null) {
					insertBuilder.useExternalConnection(conn);
				}

				if (recordsPerBatch != -1) {
					insertBuilder.recordsPerBatch(recordsPerBatch);
				}
				
				insertBuilder.save();
				
				if (CollectionUtils.isNotEmpty(insertBuilder.getFileFields())) {
					fileFields.addAll(insertBuilder.getFileFields());
				}
			}
			currentLevel++;
		}
		
		
		
		Map<String, FacilioField> fieldNameMap = null;
		if (withChangeSet) {
			fieldNameMap = FieldFactory.getAsMap(fields);
			changeSet = new HashMap<>();
		}
		
		if (CollectionUtils.isNotEmpty(fileFields)) {
			fileFields = fileFields.stream().filter(field -> !field.isDefault()).collect(Collectors.toList());
		}
		
		for(int itr = 0; itr < records.size(); itr++) {
			Map<String, Object> beanProp = beanProps.get(itr);
			if(beanProp.get("id") != null) {
				long id = (long) beanProp.get("id");
				records.get(itr).setId(id);
				
				if (withChangeSet) {
					List<UpdateChangeSet> changeList = FieldUtil.constructChangeSet(id, beanProp, fieldNameMap);
					changeSet.put(id, changeList);
				}
				
				removeFileCustomFields(fileFields, records.get(itr));
				
			}
		}
		
	}
	
	private void removeFileCustomFields(List<FileField> fileFields, E bean) {
		if (CollectionUtils.isNotEmpty(fileFields) && MapUtils.isNotEmpty(bean.getData())) {
			for(FacilioField field : fileFields) {
				if (bean.getData().containsKey(field.getName())) {
					bean.getData().remove(field.getName());
				}
			}
		}
	}

	private long getLocalId (List<Pair<FacilioModule, Long>> modules) throws Exception {
		long localId = -1;
		if(isWithLocalIdModule) {
			for (int i = modules.size() - 1; i >= 0; i--) {
				FacilioModule module = modules.get(i).getLeft();
				localId = ModuleLocalIdUtil.getAndUpdateModuleLocalId(module.getName(), records.size());
				if (localId != -1) {
					break;
				}
			}
			
			if (localId == -1) {
				throw new IllegalArgumentException(modules.get(modules.size() - 1).getLeft().getName()+" doesn't have last local id. This is not supposed to happen");
			}
		}
		return localId;
	}
	
	private List<Pair<FacilioModule, Long>> splitModules() {
		List<Pair<FacilioModule, Long>> modules = new ArrayList<>();
		FacilioModule extendModule = module;
		long commonModuleId = -1;
		while(extendModule != null) {
			if (commonModuleId == -1 && extendModule.hideFromParents()) {
				commonModuleId = extendModule.getModuleId();
			}
			modules.add(0, Pair.of(extendModule, commonModuleId == -1 ? extendModule.getModuleId() : commonModuleId));
			extendModule = extendModule.getExtendModule();
		}
		
		return modules;
	}
	
	private Map<Long, List<FacilioField>> splitFields() {
		Map<Long, List<FacilioField>> fieldMap = new HashMap<>();
		
		// TODO check this again
		for(FacilioField field : fields) {
			FacilioModule module = field.getModule();
			long moduleId = -1;
			if (module != null) {
				moduleId = module.getModuleId();
			}
			List<FacilioField> moduleFields = fieldMap.get(moduleId);
			if(moduleFields == null) {
				moduleFields = new ArrayList<>();
				fieldMap.put(moduleId, moduleFields);
			}
			moduleFields.add(field);
		}
		
		return fieldMap;
	}
	
	private Map<String, Object> getAsProps(E bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> moduleProps = FieldUtil.getAsProperties(bean);
		moduleProps.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		moduleProps.put("sysCreatedTime", System.currentTimeMillis());
		moduleProps.put("sysModifiedTime", System.currentTimeMillis());
		if (AccountUtil.getCurrentUser() != null) {
			moduleProps.put("sysCreatedBy", AccountUtil.getCurrentUser().getId());
			moduleProps.put("sysModifiedBy", AccountUtil.getCurrentUser().getId());
		}
		for(FacilioField field : fields) {
			switch (field.getDataTypeEnum()) {
				case LOOKUP:
					Object val = moduleProps.get(field.getName());
					if(val != null && val instanceof Map) {
						Map<String, Object> lookupProps = (Map<String, Object>) val; 
						if(lookupProps != null) {
							moduleProps.put(field.getName(), lookupProps.get("id"));
						}
					}
					break;
				case ENUM:
					if (!field.isDefault()) {
						val = moduleProps.get(field.getName());
						if (val != null) {
							val = FacilioUtil.castOrParseValueAsPerType(field, val);
							moduleProps.put(field.getName(), val);
							bean.setDatum(field.getName(), val); //This is for workflow rules to work
						}
					}
					break;
				default:
					break;
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
