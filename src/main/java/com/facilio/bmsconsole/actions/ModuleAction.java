package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.chain.FacilioChain;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;

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

	public String v2AddModule() throws Exception {
		FacilioChain addModulesChain = TransactionChainFactory.getAddModuleChain();
		FacilioContext context = addModulesChain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, getModuleDisplayName());
		context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
		context.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, description);
		
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
		context.put(FacilioConstants.ContextNames.ALLOW_SAME_FIELD_DISPLAY_NAME, true);
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
//		setFieldJson(fieldJson);
//		context.put(FacilioConstants.ContextNames.MODULE_FIELD, field);

		chain.execute();
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
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
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
	
	public String moduleDataDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getId());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
		FacilioChain dataDetailsChain = ReadOnlyChainFactory.fetchModuleDataDetailsChain();
		dataDetailsChain.execute(context);
		
//		setModuleData((ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.MODULE_DATA, context.get(FacilioConstants.ContextNames.RECORD));
		
		return SUCCESS;
	}
	
	public String getModuleDataList() throws Exception {
		FacilioContext context = constructListContext();
 		
 		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
 		FacilioChain dataList = ReadOnlyChainFactory.fetchModuleDataListChain();
 		dataList.execute(context);
 		
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
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
		setModuleData();
		context.put(FacilioConstants.ContextNames.RECORD, moduleData);
		
		// TODO.... Temporary. Will be changed to counter field soon
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, getWithLocalId());
		
		FacilioChain addModuleDataChain = FacilioChainFactory.addModuleDataChain();
		addModuleDataChain.execute(context);
		
		setId(moduleData.getId());
		return moduleDataDetails();
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
	
	public String updateModuleData() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
		setModuleData();
		context.put(FacilioConstants.ContextNames.RECORD, moduleData);
		
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(moduleData.getId()));
		
		FacilioChain updateModuleDataChain = FacilioChainFactory.updateModuleDataChain();
		updateModuleDataChain.execute(context);
		
		setId(moduleData.getId());
		return moduleDataDetails();
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
	}
	
	private List<ModuleBaseWithCustomFields> moduleDatas;
	public List<ModuleBaseWithCustomFields> getModuleDatas() {
		return moduleDatas;
	}
	public void setModuleDatas(List<ModuleBaseWithCustomFields> moduleDatas) {
		this.moduleDatas = moduleDatas;
	}
	
	private CustomModuleData moduleData;
	public CustomModuleData getModuleData() {
		return moduleData;
	}
	public void setModuleData(CustomModuleData moduleData) {
		this.moduleData = moduleData;
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
}
