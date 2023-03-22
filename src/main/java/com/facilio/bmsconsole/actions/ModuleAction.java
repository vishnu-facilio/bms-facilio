package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.formFactory.FacilioFormChainFactory;
import com.facilio.bmsconsole.forms.FieldFormUsage;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class ModuleAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String addNewModule() throws Exception {
		FacilioChain addModulesChain = TransactionChainFactory.getAddModuleChain();
		FacilioContext context = addModulesChain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, getModuleDisplayName());
		context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		addModulesChain.execute();
		
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		setResult("module", module);
		setModuleId(module.getModuleId());
		return SUCCESS;
	}

	public String automationModules() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getAutomationModules();
		FacilioContext context = chain.getContext();
		chain.execute();

		setResult(FacilioConstants.ContextNames.MODULE_LIST, context.get(FacilioConstants.ContextNames.MODULE_LIST));
		return SUCCESS;
	}

	public String slaModules() throws Exception{
		FacilioChain chain = ReadOnlyChainFactory.getSlaModules();
		FacilioContext context = chain.getContext();
		chain.execute();

		setResult(ContextNames.MODULE_LIST,context.get(ContextNames.MODULE_LIST));
		return SUCCESS;
	}

	public String transactionRuleModules() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getTransactionRuleModules();
		FacilioContext context = chain.getContext();
		chain.execute();

		setResult(FacilioConstants.ContextNames.MODULE_LIST, context.get(FacilioConstants.ContextNames.MODULE_LIST));
		setResult(ContextNames.SUB_MODULES, context.get(ContextNames.SUB_MODULES));

		return SUCCESS;
	}
	
	
	public String v2RecordsDuplication() throws Exception {
		FacilioChain moduleRecordsDuplicateChain = TransactionChainFactory.getModuleRecordsDuplicateChain();
		FacilioContext context = moduleRecordsDuplicateChain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.ID, id);
		context.put(FacilioConstants.ContextNames.STARTING_NUMBER, startingNumber);
		context.put(FacilioConstants.ContextNames.DUPLICATE_OBJECT, duplicateObj);
		moduleRecordsDuplicateChain.execute();
		return SUCCESS;
		
	}
	public String v2AddModuleDB() throws Exception {
		FacilioChain addModulesChain = FacilioFormChainFactory.getAddModuleChain();
		FacilioContext context = addModulesChain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, getModuleDisplayName());
		context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
		context.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, description);
		context.put(FacilioConstants.ContextNames.STATE_FLOW_ENABLED, stateFlowEnabled);

		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());

		addModulesChain.execute();

		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		setResult("module", module);
		setResult(FacilioConstants.ContextNames.FORM, context.get(FacilioConstants.ContextNames.FORM));
		return SUCCESS;
	}
	
	
	public String v2AddModule() throws Exception {
		FacilioChain addModulesChain = TransactionChainFactory.getAddModuleChain();
		FacilioContext context = addModulesChain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, getModuleDisplayName());
		context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
		context.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, description);
		context.put(FacilioConstants.ContextNames.STATE_FLOW_ENABLED, stateFlowEnabled);
		context.put(ContextNames.FAILURE_REPORTING_ENABLED, failureReportingEnabled);

		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		
		addModulesChain.execute();
		
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		setResult("module", module);
		setResult(FacilioConstants.ContextNames.FORM, context.get(FacilioConstants.ContextNames.FORM));
		return SUCCESS;
	}

	private Boolean defaultModules;
	public Boolean getDefaultModules() {
		return defaultModules;
	}
	public void setDefaultModules(Boolean defaultModules) {
		this.defaultModules = defaultModules;
	}

	public String v2GetModuleList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getModuleList();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
		context.put(FacilioConstants.ContextNames.FETCH_DEFAULT_MODULES, defaultModules);
		
		chain.execute();
		
		setResult("moduleList", context.get(FacilioConstants.ContextNames.MODULE_LIST));
		return SUCCESS;
	}
	
	public String v2GetModulesList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getModulesList();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
		context.put(FacilioConstants.ContextNames.FETCH_DEFAULT_MODULES, defaultModules);
		
		chain.execute();
		
		setResult("modules", context.get(FacilioConstants.ContextNames.MODULE_LIST));
		return SUCCESS;
	}

	public String v2GetModule() throws Exception {
		FacilioChain c = ReadOnlyChainFactory.getModuleDetails();
		FacilioContext context = c.getContext();
		context.put(ContextNames.MODULE_NAME, moduleName);

		c.execute();

		setResult(ContextNames.MODULE, context.get(ContextNames.MODULE));
		setResult(ContextNames.MODULE_FIELD_COUNT, context.get(ContextNames.MODULE_FIELD_COUNT));
		return SUCCESS;
	}
	
	private String moduleDisplayName;
	public String getModuleDisplayName() {
		return moduleDisplayName;
	}
	public void setModuleDisplayName(String moduleDisplayName) {
		this.moduleDisplayName = moduleDisplayName;
	}

	public boolean stateFlowEnabled;

	public boolean isStateFlowEnabled() {
		return stateFlowEnabled;
	}

	public void setStateFlowEnabled(boolean stateFlowEnabled) {
		this.stateFlowEnabled = stateFlowEnabled;
	}

	@Getter
	@Setter
	private boolean failureReportingEnabled;

	public String v2UpdateModule() throws Exception {
		FacilioChain chain = TransactionChainFactory.getUpdateModuleChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, moduleDisplayName);
		context.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, description);
		context.put(FacilioConstants.ContextNames.STATE_FLOW_ENABLED, stateFlowEnabled);

		chain.execute();
		
		setResult(FacilioConstants.ContextNames.MODULE, context.get(FacilioConstants.ContextNames.MODULE));
		return SUCCESS;
	}
	
	public String addCustomFields() throws Exception {
		return addCustomFields(null);
	}
	
	private String addCustomFields(FacilioContext context) throws Exception {
		if (context == null) {
			context = new FacilioContext();
		}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		
		FacilioChain addFieldsChain = TransactionChainFactory.getAddFieldsChain();
		addFieldsChain.execute(context);
		
		setFieldIds((List<Long>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS));
		
		return SUCCESS;
	}

	public String v2addCustomFields() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(ContextNames.ALLOW_SAME_FIELD_DISPLAY_NAME, true);
		setFields(Collections.singletonList(field));
		addCustomFields(context);
		
		setFieldId(getFieldIds().get(0));
		fieldDetails();
		
		setResult("field", field);
		return SUCCESS;
	}
	
	private JSONObject fieldJson;
	public void setFieldJson(JSONObject fieldJson) throws Exception {
		this.fieldJson = fieldJson;
		setField(FieldUtil.parseFieldJson(this.fieldJson));
	}
	
	public String fieldList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		
		FacilioChain getFieldsChain = FacilioChainFactory.getGetFieldsChain();
		getFieldsChain.execute(context);
		
		setFields((List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST));
		
		return SUCCESS;
	}
	
	private Long fieldId;
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
	
	public String fieldDetails() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		setField(modBean.getField(fieldId));
		
		return SUCCESS;
	}
	
	public String updateField() throws Exception {
		updateCustomField(false);
		return SUCCESS;
	}
	
	public String updateCustomField(boolean avoidFieldDisplayNameDuplication) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_FIELD, field);
		context.put(FacilioConstants.ContextNames.CHECK_FIELD_DISPLAY_NAME_DUPLICATION, avoidFieldDisplayNameDuplication);
		FacilioChain updateFieldChain = FacilioChainFactory.getUpdateFieldChain();

		updateFieldChain.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}

	private String moduleFieldChangeTo;
	public String getModuleFieldChangeTo() {
		return moduleFieldChangeTo;
	}
	public void setModuleFieldChangeTo(String moduleFieldChangeTo) {
		this.moduleFieldChangeTo = moduleFieldChangeTo;
	}

	public String changeNameToLocalID() throws Exception {
		FacilioChain chain = FacilioChainFactory.changeNameToLocalIdChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.MODULE_CHANGE_TO, moduleFieldChangeTo);
		chain.execute();

		setResult(FacilioConstants.ContextNames.FIELD, context.get(FacilioConstants.ContextNames.FIELD));
		return SUCCESS;
	}

	public String canChangeNameLocalId() throws Exception {

		return SUCCESS;
	}
	
	public String v2updateField() throws Exception {
		updateCustomField(true);
		
		setFieldId(field.getId());
		fieldDetails();
		
		setResult("field", field);
		return SUCCESS;
	}
	
	public String deleteFields() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_IDS, fieldIds);
		
		FacilioChain deleteFieldsChain = FacilioChainFactory.getdeleteFieldsChain();
		deleteFieldsChain.execute(context);
		rowsDeleted = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}
	
	private String resourceType = null;
	
	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	private long assetId = -1;
	private long categoryId = -1;
		
	public long getAssetId() {
		return assetId;
	}

	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String modulesWithDateFields() throws Exception {
		FacilioChain fetchModules = FacilioChainFactory.getAllModulesWithDateField();
		FacilioContext context = fetchModules.getContext();
		fetchModules.execute(context);

		List<FacilioModule> modules = (List<FacilioModule>) context.get(ContextNames.MODULE_LIST);
		setModules(modules);
		setResult("modules", getModules());
		return SUCCESS;
	}

	private List<FacilioModule> modules;
	
	public String metadata() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.RESOURCE_TYPE, getResourceType());
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getCategoryId());
		FacilioChain metaField = FacilioChainFactory.getAllFieldsChain();
		metaField.execute(context);
		setMeta((JSONObject) context.get(FacilioConstants.ContextNames.META));
		return SUCCESS;
	}
	public String v2metadata() throws Exception {
		metadata();
		setResult("meta", getMeta());
		return SUCCESS;
	}
	
	public String metaFilterFields() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.RESOURCE_TYPE, getResourceType());
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getCategoryId());
		context.put(FacilioConstants.ContextNames.IS_FILTER, true);
		FacilioChain metaField = FacilioChainFactory.getAllFieldsChain();
		metaField.execute(context);
		setMeta((JSONObject) context.get(FacilioConstants.ContextNames.META));
		return SUCCESS;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public String fields() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		
		FacilioChain getFieldsChain = FacilioChainFactory.getGetFieldsChain();
		getFieldsChain.execute(context);
	
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		List<FacilioField> workorderFields = new ArrayList<>();
//		List<FacilioField> alarmFields = new ArrayList<>();
		if(FacilioConstants.ContextNames.WORK_ORDER.equals(getModuleName()))
		{
			for(FacilioField field : fields)
			{
				if(field.getName().equals("actualWorkStart")
				|| field.getName().equals("actualWorkEnd")
				|| field.getName().equals("estimatedEnd")
				|| field.getName().equals("noOfAttachments")
				|| field.getName().equals("noOfClosedTasks")
				|| field.getName().equals("noOfNotes")
				|| field.getName().equals("noOfTasks")
				|| field.getName().equals("scheduledStart")
				|| field.getName().equals("serialNumber")
				|| field.getName().equals("sourceType")
				|| field.getName().equals("assignmentGroup")
				|| field.getName().equals("createdTime"))
				{
					continue;
				}
				if(field.getName().equals("tenant")) {
					if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
					workorderFields.add(field);
					}
					continue;
				}
				workorderFields.add(field);
			}
			setFields(workorderFields);
		}
//		else if(FacilioConstants.ContextNames.ALARM.equals(getModuleName()))
//		{
//			for(FacilioField field : fields)
//			{
//				if(field.getName().equals("acknowledgedBy")
//				|| field.getName().equals("acknowledgedTime")
//				|| field.getName().equals("createdTime")
//				|| field.getName().equals("acknowledgedBy")
//				|| field.getName().equals("isAcknowledged")
//				|| field.getName().equals("alarmType")
//				|| field.getName().equals("modifiedTime")
//				|| field.getName().equals("clearedTime")
//				|| field.getName().equals("serialNumber")
//				|| field.getName().equals("sourceType")
//				|| field.getName().equals("assignmentGroup")
//				|| field.getName().equals("createdTime"))
//				{
//					continue;
//				}
//				alarmFields.add(field);
//			}
//		}
		else
		{
			setFields(fields);
		}
		
		return SUCCESS;
	}
	public String v2getFieldsList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put("handleStateField", true);
		
		FacilioChain getFieldsChain = FacilioChainFactory.getGetFieldsChain();
		getFieldsChain.execute(context);
	
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		setResult("fields", fields);
//		Map<Long, List<FacilioForm>> fieldVsForms = (Map<Long, List<FacilioForm>>) context.get(FacilioConstants.ContextNames.FORM_FIELD_MAP);
//		setResult("fieldVsForms", fieldVsForms);

		return SUCCESS;
	}

	public String v2getFieldUsageInForms() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(ContextNames.FIELD_ID, fieldId);
		context.put(ContextNames.FETCH_DISABLED_FORMS, true);
		context.put(ContextNames.FETCH_HIDDEN_FORMS, true);

		FacilioChain getFieldsChain = FacilioChainFactory.getGetFieldUsageChain();
		getFieldsChain.execute(context);

		List<FieldFormUsage> fieldUsage = (List<FieldFormUsage>) context.get(ContextNames.FIELD_FORM_USAGE_DETAILS);
		setResult("forms", fieldUsage);

		return SUCCESS;
	}

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private Long childModuleId;
	public Long getChildModuleId() {
		return childModuleId;
	}

	public void setChildModuleId(Long childModuleId) {
		this.childModuleId = childModuleId;
	}

	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private int moduleType;
	public int getModuleType() {
		return moduleType;
	}
	public void setModuleType(int moduleType) {
		this.moduleType = moduleType;
	}

	private FacilioField field;
	public FacilioField getField() {
		return field;
	}
	public void setField(FacilioField field) {
		this.field = field;
	}

	private JSONArray fieldJsons;
	public void setFieldJsons(JSONArray fieldJsons) throws Exception {
		this.fieldJsons = fieldJsons;
		setFields(FieldUtil.parseFieldJson(this.fieldJsons));
	}

	private List<FacilioField> fields;
	public List<FacilioField> getFields() {
		return fields;
	}
	public void setFields(List<FacilioField> fields) {
		this.fields = fields;
	}
	
	private JSONObject meta;
	public JSONObject getMeta() {
		return meta;
	}
	public void setMeta(JSONObject meta) {
		this.meta = meta;
	}
	
	private long moduleId;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private List<Long> fieldIds;
	public List<Long> getFieldIds() {
		return fieldIds;
	}
	public void setFieldIds(List<Long> fieldIds) {
		this.fieldIds = fieldIds;
	}
	
	private int rowsUpdated;
	public int getRowsUpdated() {
		return rowsUpdated;
	}
	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
	
	private int rowsDeleted;
	public int getRowsDeleted() {
		return rowsDeleted;
	}

	public void setRowsDeleted(int rowsDeleted) {
		this.rowsDeleted = rowsDeleted;
	}
	
	/************* V2 Apis *****************/

	private Boolean shouldFetchLookup;
	public Boolean getShouldFetchLookup() {
		return shouldFetchLookup;
	}
	public void setShouldFetchLookup(Boolean shouldFetchLookup) {
		this.shouldFetchLookup = shouldFetchLookup;
	}

	public String moduleDataDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getId());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(ContextNames.FETCH_LOOKUPS, shouldFetchLookup);
		
		FacilioChain dataDetailsChain = ReadOnlyChainFactory.fetchModuleDataDetailsChain();
		dataDetailsChain.execute(context);
		
//		setModuleData((ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.MODULE_DATA, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	
	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	Criteria criteria;
	
	private String clientCriteria;
	
	public String getClientCriteria() {
		return clientCriteria;
	}

	public void setClientCriteria(String clientCriteria) {
		this.clientCriteria = clientCriteria;
	}
	
	Long filterId;

	public Long getFilterId() {
		return filterId;
	}

	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}

	public String getModuleDataList() throws Exception {
		FacilioChain dataList = ReadOnlyChainFactory.fetchModuleDataListChain();
		FacilioContext context = dataList.getContext();
		constructListContext(context);
 		
 		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
 		context.put(FacilioConstants.ContextNames.FILTER_IDS, filterId);
 		context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, criteria);
 		context.put(ContextNames.FETCH_LOOKUPS, shouldFetchLookup);
		context.put(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, skipModuleCriteria);
		
		if (getClientCriteria() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getClientCriteria());
			Criteria newCriteria = FieldUtil.getAsBeanFromJson(json, Criteria.class);
			context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, newCriteria);
		}
		dataList.execute();
 		
 		moduleDatas = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
 		
 		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.RECORD_COUNT));
		}
		else {
			setResult(FacilioConstants.ContextNames.MODULE_DATA_LIST, moduleDatas);
		}
 		
		return SUCCESS;
	}
	
	public String addModuleData() throws Exception {
		addModuleDataCommon();
		return moduleDataDetails();
	}
	
	private void addModuleDataCommon() throws Exception {
		
		FacilioChain addModuleDataChain = FacilioChainFactory.addModuleDataChain();
		FacilioContext context = addModuleDataChain.getContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.CUSTOM_ACTIVITY);

		setModuleData();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.RECORD, moduleData);
		moduleData.parseFormData();
		
		// TODO.... Temporary. Will be changed to counter field soon
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, getWithLocalId());
		
		addModuleDataChain.execute();
		
		setId(moduleData.getId());
	}
	
	public String addBulkModuleData() throws Exception {
		
		if(bulkModuleDataList != null && !bulkModuleDataList.isEmpty()) {
			
			for(CustomModuleData moduleData1 : bulkModuleDataList) {
				
				moduleData = moduleData1;
				addModuleDataCommon();
			}
		}
		
		return SUCCESS;
	}

	private String moduleDataString;
	public String getModuleDataString() {
		return moduleDataString;
	}
	public void setModuleDataString(String moduleDataString) {
		this.moduleDataString = moduleDataString;
	}

	private String dataString;
	public String getDataString() {
		return dataString;
	}
	public void setDataString(String dataString) {
		this.dataString = dataString;
	}
	
	private boolean withLocalId = true;
	public boolean getWithLocalId() {
		return withLocalId;
	}
	public void setWithLocalId(boolean withLocalId) {
		this.withLocalId = withLocalId;
	}

	private long stateTransitionId = -1;
	public long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}

	private Long approvalTransitionId = null;
	public Long getApprovalTransitionId() {
		return approvalTransitionId;
	}
	public void setApprovalTransitionId(Long approvalTransitionId) {
		this.approvalTransitionId = approvalTransitionId;
	}

	private Map<String, Object> subFormFiles;
	public Map<String, Object> getSubFormFiles() {
		return subFormFiles;
	}
	public void setSubFormFiles(Map<String, Object> subFormFiles) {
		this.subFormFiles = subFormFiles;
	}

	private long accessType = -1L;

	public String getModuleLookUpFieldDetails() throws Exception {
		FacilioChain chain = FacilioChainFactory.getFieldsByModuleType();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		chain.execute();

		Map<String, List<Map<String, Object>>> result = (Map<String, List<Map<String, Object>>>) context.get(ContextNames.RESULT);
		setResult("fieldDetails", result);
		return SUCCESS;
	}

	public String fieldsByAccessType() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, accessType);
		context.put(FacilioConstants.ContextNames.MODULE_ID, moduleId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		FacilioChain fieldsByAccessType = FacilioChainFactory.getFieldsByAccessType();
		fieldsByAccessType.execute(context);
		setResult(ContextNames.FIELDS, context.get(FacilioConstants.ContextNames.FIELDS));
		return SUCCESS;
	}

	public String updateModuleData() throws Exception {
		
		updateModuleDataCommon();
		return moduleDataDetails();
	}
	
	public String bulkUpdateModuleData() throws Exception {
		
		if(bulkModuleDataList != null && !bulkModuleDataList.isEmpty()) {
			
			for(CustomModuleData moduleData1 : bulkModuleDataList) {
				
				moduleData = moduleData1;
				updateModuleDataCommon();
			}
		}
		
		return SUCCESS;
	}
	
	private void updateModuleDataCommon() throws Exception {
		
		FacilioChain updateModuleDataChain = FacilioChainFactory.updateModuleDataChain();
		
		FacilioContext context = updateModuleDataChain.getContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
		context.put(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID, approvalTransitionId);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.CUSTOM_ACTIVITY);

		setModuleData();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.RECORD, moduleData);
		CommonCommandUtil.addEventType(EventType.SCORING_RULE, context);
		moduleData.parseFormData();
		
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(moduleData.getId()));
		
		updateModuleDataChain.execute();
		
		setId(moduleData.getId());
	}
	
	public String deleteModuleData() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		
		FacilioChain deleteModuleDataChain = FacilioChainFactory.deleteModuleDataChain();
		deleteModuleDataChain.execute(context);
		
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		return SUCCESS;
	}
	
	private void setModuleData () throws Exception {
		if (dataString != null && !dataString.isEmpty()) {
			JSONParser parser = new JSONParser();
			Map<String, Object> data = (Map<String, Object>) parser.parse(dataString);
			if (moduleData == null) {
				moduleData = new CustomModuleData();
			}
			if (moduleData.getData() == null) {
				moduleData.setData(new HashMap<>());
			}
			moduleData.getData().putAll(data);
		}
		else if (StringUtils.isNotEmpty(moduleDataString)) {
			JSONParser parser = new JSONParser();
			Map<String, Object> data = (Map<String, Object>) parser.parse(moduleDataString);
			if (MapUtils.isNotEmpty(data)) {
				String moduleName = (String) data.get("moduleName");
				if (StringUtils.isNotEmpty(moduleName)) {
					setModuleName(moduleName);
				}
				if (data.containsKey("withLocalId")) {
					setWithLocalId((Boolean) data.get("withLocalId"));
				}
				if (data.containsKey("moduleData")) {
					moduleData = (CustomModuleData) FieldUtil.getAsBeanFromMap((Map<String, Object>) data.get("moduleData"), CustomModuleData.class);
				}
			}
		}

		if (MapUtils.isNotEmpty(subFormFiles) && moduleData != null && MapUtils.isNotEmpty(moduleData.getSubForm())) {
			moduleData.addSubFormFiles(subFormFiles);
		}
	}
	
	public String addRollUpField() throws Exception {

		FacilioChain addRollUpFieldsChain = TransactionChainFactory.getAddRollUpFieldsChain();
		FacilioContext context = addRollUpFieldsChain.getContext();
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		context.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, getDescription());
		context.put(FacilioConstants.ContextNames.CHILD_MODULE_ID, getChildModuleId());
		context.put(FacilioConstants.ContextNames.CHILD_FIELD_ID, getFieldId());
		context.put(FacilioConstants.ContextNames.AGGREGATE_FUNCTION_ID, getAggregateFunctionId());
		context.put(FacilioConstants.ContextNames.AGGREGATE_FIELD_ID, getAggregateFieldId());
		context.put(FacilioConstants.ContextNames.CHILD_CRITERIA, getCriteria());	
		addRollUpFieldsChain.execute();
		
		setResult(FacilioConstants.ContextNames.ROLL_UP_FIELDS,(List<RollUpField>) context.get(FacilioConstants.ContextNames.ROLL_UP_FIELDS));	
		return SUCCESS;
	}
	
	public String updateRollUpField() throws Exception {

		FacilioChain addRollUpFieldsChain = TransactionChainFactory.getUpdateRollUpFieldsChain();
		FacilioContext context = addRollUpFieldsChain.getContext();			
		context.put(FacilioConstants.ContextNames.MODULE_FIELD, field);
		context.put(FacilioConstants.ContextNames.CHECK_FIELD_DISPLAY_NAME_DUPLICATION, false);

		context.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, getDescription());
		context.put(FacilioConstants.ContextNames.CHILD_MODULE_ID, getChildModuleId());
		context.put(FacilioConstants.ContextNames.CHILD_FIELD_ID, getFieldId());
		context.put(FacilioConstants.ContextNames.AGGREGATE_FUNCTION_ID, getAggregateFunctionId());
		context.put(FacilioConstants.ContextNames.AGGREGATE_FIELD_ID, getAggregateFieldId());
		context.put(FacilioConstants.ContextNames.CHILD_CRITERIA, getCriteria());	
		addRollUpFieldsChain.execute();
		
		setResult(FacilioConstants.ContextNames.ROLL_UP_FIELDS,(List<RollUpField>) context.get(FacilioConstants.ContextNames.ROLL_UP_FIELDS));	
		return SUCCESS;
	}
	
	public String fetchSubModuleLookUpFields() throws Exception {
		FacilioChain chain = TransactionChainFactory.getSubModuleLookUpFieldsChain();
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());	
		chain.execute();
		setResult(FacilioConstants.ContextNames.SUB_MODULES,(List<FacilioModule>) chain.getContext().get(FacilioConstants.ContextNames.SUB_MODULES));	
		return SUCCESS;
	}
	
	public String getRollUpFields() throws Exception {
		FacilioChain chain = TransactionChainFactory.getRollUpFieldsChain();
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_IDS, fieldIds);	
		chain.execute();
		setResult(FacilioConstants.ContextNames.ROLL_UP_FIELDS,(List<RollUpField>) chain.getContext().get(FacilioConstants.ContextNames.ROLL_UP_FIELDS));	
		return SUCCESS;
	}
	public String getGeoLocationFields() throws Exception{
		FacilioChain chain = TransactionChainFactory.getGeoLocationFieldsChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.MODULE_NAME,moduleName);
		chain.execute();
		setResult(FacilioConstants.ContextNames.GEOLOCATION_FIELDS,context.get(FacilioConstants.ContextNames.GEOLOCATION_FIELDS));
		return SUCCESS;
	}
	
	private Integer aggregateFunctionId;
	private Long aggregateFieldId;

	public Integer getAggregateFunctionId() {
		return aggregateFunctionId;
	}

	public void setAggregateFunctionId(Integer aggregateFunctionId) {
		this.aggregateFunctionId = aggregateFunctionId;
	}

	public Long getAggregateFieldId() {
		return aggregateFieldId;
	}

	public void setAggregateFieldId(Long aggregateFieldId) {
		this.aggregateFieldId = aggregateFieldId;
	}

	private List<ModuleBaseWithCustomFields> moduleDatas;
	public List<ModuleBaseWithCustomFields> getModuleDatas() {
		return moduleDatas;
	}
	public void setModuleDatas(List<ModuleBaseWithCustomFields> moduleDatas) {
		this.moduleDatas = moduleDatas;
	}
	
	private List<CustomModuleData> bulkModuleDataList;

	public List<CustomModuleData> getBulkModuleDataList() {
		return bulkModuleDataList;
	}

	public void setBulkModuleDataList(List<CustomModuleData> bulkModuleDataList) {
		this.bulkModuleDataList = bulkModuleDataList;
	}

	private CustomModuleData moduleData;
	public CustomModuleData getModuleData() {
		return moduleData;
	}
	public void setModuleData(CustomModuleData moduleData) {
		this.moduleData = moduleData;
	}
	
	private JSONObject duplicateObj;
	
	public JSONObject getDuplicateObj() {
		return duplicateObj;
	}


	public void setDuplicateObj(JSONObject duplicateObj) {
		this.duplicateObj = duplicateObj;
	}
	private int startingNumber;
	


	public int getStartingNumber() {
		return startingNumber;
	}


	public void setStartingNumber(int startingNumber) {
		this.startingNumber = startingNumber;
	}

	private Long id;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	private List<Long> ids;
	
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public List<FacilioModule> getModules() {
		return modules;
	}

	public void setModules(List<FacilioModule> modules) {
		this.modules = modules;
	}

	public long getAccessType() {
		return accessType;
	}

	public void setAccessType(long accessType) {
		this.accessType = accessType;
	}
	
	public String fetchOperators() throws Exception {
		
		JSONObject operators = CommonCommandUtil.getOperators();
		setResult("operators", operators);
		
		return SUCCESS;
	}
}
