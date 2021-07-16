package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.InsertBuilderIfc;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.facilio.modules.fields.InsertSupplementHandler;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class InsertRecordBuilder<E extends ModuleBaseWithCustomFields> implements InsertBuilderIfc<E> {
	
	private String moduleName;
	private FacilioModule module;
	private List<FacilioField> fields;
	private int level = 1;
	private List<E> records = null;
	private List<Map<String, Object>> recordProps = null;
	private boolean inserted = false;
	private boolean withChangeSet = false;
	private Map<Long, List<UpdateChangeSet>> changeSet;
	private boolean isWithLocalIdModule;
	private Connection conn = null;
	private int recordsPerBatch = -1;
	private List<SupplementRecord> insertSupplements;

	// TODO to be removed after everything is moved to v3
	private boolean ignoreSplNullHandling = false;

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

	private List<E> initRecords() {
		if (records == null) {
			records = new ArrayList<>();
		}
		return records;
	}

	@Override
	public InsertRecordBuilder<E> addRecord(E bean) {
		initRecords().add(bean);
		return this;
	}
	
	@Override
	public InsertRecordBuilder<E> addRecords(List<E> beans) {
		initRecords().addAll(beans);
		return this;
	}


	private List<Map<String, Object>> initRecordProps() {
		if (recordProps == null) {
			recordProps = new ArrayList<>();
		}
		return recordProps;
	}

	public InsertRecordBuilder<E> addRecordProp(Map<String, Object> prop) {
		initRecordProps().add(prop);
		return this;
	}

	public InsertRecordBuilder<E> addRecordProps(List<Map<String, Object>> props) {
		initRecordProps().addAll(props);
		return this;
	}
	
	public InsertRecordBuilder<E> withLocalId() {
		this.isWithLocalIdModule = true;
		return this;
	}
	
	@Override
	public List<E> getRecords() {
		// TODO Auto-generated method stub
		return initRecords();
	}

	public List<Map<String, Object>> getRecordProps() {
		// TODO Auto-generated method stub
		return initRecordProps();
	}
	
	public InsertRecordBuilder<E> withChangeSet() {
		this.withChangeSet = true;
		return this;
	}

	public InsertRecordBuilder<E> insertSupplement(SupplementRecord supplement) {
		Objects.requireNonNull(supplement, "Supplement record cannot be null");
		if (insertSupplements == null) {
			insertSupplements = new ArrayList<>();
		}
		insertSupplements.add(supplement);
		return this;
	}

	public InsertRecordBuilder<E> insertSupplements(Collection<? extends SupplementRecord> supplements) {
		Objects.requireNonNull(supplements, "Supplement records cannot be null");
		FacilioUtil.throwIllegalArgumentException(supplements.stream().anyMatch(Objects::isNull), "Supplement record cannot be null");
		if (insertSupplements == null) {
			insertSupplements = new ArrayList<>();
		}
		insertSupplements.addAll(supplements);
		return this;
	}
	
	public Map<Long, List<UpdateChangeSet>> getChangeSet() {
		
		if (!inserted) {
			throw new IllegalArgumentException("Update first and then get change set.");
		}
		return changeSet;
	}

	public InsertRecordBuilder<E> ignoreSplNullHandling() {
		this.ignoreSplNullHandling = true;
		return this;
	}
	
	private void checkForNullAndSanitize() throws Exception {
		
		if(CollectionUtils.isEmpty(fields)) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
		}
		
		if(module == null) {
			if(moduleName == null || moduleName.isEmpty()) {
				throw new IllegalArgumentException("Both Module and Module Name cannot be empty");
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			module = modBean.getModule(moduleName);
		}

		if (insertSupplements != null) {
			insertSupplements = insertSupplements.stream().filter(SupplementRecord.distinctSupplementRecord()).collect(Collectors.toList());
		}
	}
	
	@Override
	public void save() throws Exception {

		if(CollectionUtils.isEmpty(records) && CollectionUtils.isEmpty(recordProps)) {
			return;
		}

		checkForNullAndSanitize();

		List<Pair<FacilioModule, Long>> modules = splitModules();
		Map<Long, List<FacilioField>> fieldMap = splitFields();

		int beansSize = records == null ? 0 : records.size(), propsSize = recordProps == null ? 0 : recordProps.size();
		int totalRecords = beansSize + propsSize;
		long localId = getLocalId(modules, totalRecords);
		List<Map<String, Object>> props = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(records)) {
			for(E bean : records) {
				if (isWithLocalIdModule) {
					long currentLocalId = ++localId;
					bean.setLocalId(currentLocalId);
				}
				props.add(getAsProps(bean));
			}
		}
		if (CollectionUtils.isNotEmpty(this.recordProps)) {
			for (Map<String, Object> prop : this.recordProps) {
				addDefaultProps(prop, null);
				if (isWithLocalIdModule) {
					long currentLocalId = ++localId;
					prop.put("localId", currentLocalId);
				}
				props.add(prop);
			}
		}
		List<FileField> fileFields = new ArrayList<>();
		insertData(modules, fieldMap, props, fileFields);
		handleSupplements(props);

		Map<String, FacilioField> fieldNameMap = null;
		if (withChangeSet) {
			fieldNameMap = FieldFactory.getAsMap(fields);
			changeSet = new HashMap<>();
		}

		for (int itr = 0; itr < props.size(); itr++) {
			Map<String, Object> prop = props.get(itr);
			long id = (long) prop.get("id");
			if (withChangeSet) {
				List<UpdateChangeSet> changeList = FieldUtil.constructChangeSet(id, prop, fieldNameMap);
				changeSet.put(id, changeList);
			}
			removeFileCustomFields(fileFields, prop); //Removing any fileObject from map

			if (itr < beansSize) {
				E bean = records.get(itr);
				bean.setId(id);
				removeFileCustomFields(fileFields, bean.getData()); //Removing any fileObject from custom map
			}
		}
		inserted = true;
	}

	private void insertData(List<Pair<FacilioModule, Long>> modules, Map<Long, List<FacilioField>> fieldMap, List<Map<String, Object>> props, List<FileField> fileFields) throws SQLException {
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

				Collection<FacilioField> scopeFields = ScopeHandler.getInstance().updateValuesForInsertAndGetFields(currentModule, props);
				if (CollectionUtils.isNotEmpty(scopeFields)) {
					for(FacilioField field : scopeFields) {
						FacilioField fieldAlreadyPresent = currentFields.stream()
								  .filter(currentField -> (field.getName().equals(currentField.getName())))
								  .findAny()
								  .orElse(null);
						if(fieldAlreadyPresent == null) {
							currentFields.add(field);
						}
					}
				}

				if (currentLevel == 1 && FieldUtil.isSystemFieldsPresent(currentModule)) {
					currentFields.addAll(FieldFactory.getSystemPointFields(currentModule.getParentModule()));
				}

				if (FieldUtil.isBaseEntityRootModule(currentModule)) {
					currentFields.addAll(FieldFactory.getBaseModuleSystemFields(currentModule));
				}

				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(currentModule.getTableName())
						.fields(currentFields);

				if (ignoreSplNullHandling) {
					insertBuilder.ignoreSplNullHandling();
				}

				long moduleId = modulePair.getRight();
				for(Map<String, Object> beanProp : props) {
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
	}

	private void handleSupplements(List<Map<String, Object>> props) throws Exception {
		if (CollectionUtils.isNotEmpty(insertSupplements)) {
			for (SupplementRecord supplement : insertSupplements) {
				InsertSupplementHandler handler = supplement.newInsertHandler();
				if (handler != null) {
					handler.insertSupplements(props);
				}
			}
		}
	}

	private void removeFileCustomFields(List<FileField> fileFields, Map<String, Object> prop) {
		if (CollectionUtils.isNotEmpty(fileFields) && MapUtils.isNotEmpty(prop)) {
			for(FacilioField field : fileFields) {
				prop.remove(field.getName());
			}
		}
	}

	private long getLocalId (List<Pair<FacilioModule, Long>> modules, int size) throws Exception {
		long localId = -1;
		if(isWithLocalIdModule) {
			for (int i = modules.size() - 1; i >= 0; i--) {
				FacilioModule module = modules.get(i).getLeft();
				localId = ModuleLocalIdUtil.getAndUpdateModuleLocalId(module.getName(), size);
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
			if (field.getDataTypeEnum() != null && !field.getDataTypeEnum().isMultiRecord()) { //Not including multi record data
				FacilioModule module = field.getModule();
				long moduleId = -1;
				if (module != null) {
					moduleId = module.getModuleId();
				}
				List<FacilioField> moduleFields = fieldMap.get(moduleId);
				if (moduleFields == null) {
					moduleFields = new ArrayList<>();
					fieldMap.put(moduleId, moduleFields);
				}
				moduleFields.add(field);
			}
		}
		
		return fieldMap;
	}

	private void addDefaultProps(Map<String, Object> prop, E bean) {
		prop.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		prop.put("sysCreatedTime", System.currentTimeMillis());
		prop.put("sysModifiedTime", System.currentTimeMillis());
		if (AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getId() > 0) {
			prop.put("sysCreatedBy", AccountUtil.getCurrentUser().getId());
			prop.put("sysModifiedBy", AccountUtil.getCurrentUser().getId());
		}
		for(FacilioField field : fields) {
			switch (field.getDataTypeEnum()) {
				case LOOKUP:
					Object val = prop.get(field.getName());
					if(val != null && val instanceof Map) {
						Map<String, Object> lookupProps = (Map<String, Object>) val;
						if(lookupProps != null) {
							prop.put(field.getName(), lookupProps.get("id"));
						}
					}
					break;
				case ENUM:
					if (!field.isDefault()) {
						val = prop.get(field.getName());
						if (val != null) {
							val = FacilioUtil.castOrParseValueAsPerType(field, val);
							prop.put(field.getName(), val);
							if (bean != null) {
								bean.setDatum(field.getName(), val); //This is for workflow rules to work
							}
						}
					}
					break;
				default:
					break;
			}
		}
	}
	
	private Map<String, Object> getAsProps(E bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> prop = FieldUtil.getAsProperties(bean);
		addDefaultProps(prop, bean);
		return prop;
	}
	
	@Override
	public long insert(E bean) throws Exception {
		addRecord(bean);
		save();
		return bean.getId();
	}
	
}
