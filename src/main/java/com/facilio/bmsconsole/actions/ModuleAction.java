package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ModuleAction extends FacilioAction {
	
	public String addNewModule() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		
		Chain addModulesChain = FacilioChainFactory.getAddModuleChain();
		addModulesChain.execute(context);
		
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		setModuleId(module.getModuleId());
		return SUCCESS;
	}
	
	public String addCustomFields() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		
		Chain addFieldsChain = FacilioChainFactory.getAddFieldsChain();
		addFieldsChain.execute(context);
		
		setFieldIds((List<Long>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS));
		
		return SUCCESS;
	}
	
	public String fieldList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		
		Chain getFieldsChain = FacilioChainFactory.getGetFieldsChain();
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
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_FIELD, field);
		
		Chain updateFieldChain = FacilioChainFactory.getUpdateFieldChain();
		updateFieldChain.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}
	
	public String deleteFields() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_IDS, fieldIds);
		
		Chain deleteFieldsChain = FacilioChainFactory.getdeleteFieldsChain();
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
		List<FacilioField> fields = new ArrayList();
		context.put(FacilioConstants.ContextNames.RESOURCE_TYPE, getResourceType());
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getCategoryId());
		Chain metaField = FacilioChainFactory.getAllFieldsChain();
		metaField.execute(context);
		setMeta((JSONObject) context.get(FacilioConstants.ContextNames.META));
		return SUCCESS;
	}
	public String metaFilterFields() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		List<FacilioField> fields = new ArrayList();
		context.put(FacilioConstants.ContextNames.RESOURCE_TYPE, getResourceType());
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getCategoryId());
		context.put(FacilioConstants.ContextNames.IS_FILTER, true);
		Chain metaField = FacilioChainFactory.getAllFieldsChain();
		metaField.execute(context);
		setMeta((JSONObject) context.get(FacilioConstants.ContextNames.META));
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String fields() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		
		Chain getFieldsChain = FacilioChainFactory.getGetFieldsChain();
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
	
	private FacilioField field;
	public FacilioField getField() {
		return field;
	}
	public void setField(FacilioField field) {
		this.field = field;
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
	
	public String moduleDataDetails() throws Exception {
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ID, getId());
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
			
			Chain dataDetailsChain = ReadOnlyChainFactory.fetchModuleDataDetailsChain();
			dataDetailsChain.execute(context);
			
			setModuleData((ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD));
			setResult(FacilioConstants.ContextNames.MODULE_DATA, moduleData);
		}
		catch(Exception e) {
			setResponseCode(1);
			setMessage(e);
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public String getModuleDataList() {
		try {
			FacilioContext context = new FacilioContext();
	 		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
	 		if(getFilters() != null)
	 		{	
		 		JSONParser parser = new JSONParser();
		 		JSONObject json = (JSONObject) parser.parse(getFilters());
		 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		}
	 		if (getSearch() != null) {
	 			JSONObject searchObj = new JSONObject();
	 			searchObj.put("fields", getSearchFields());
	 			searchObj.put("query", getSearch());
		 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
	 		}
	 		
	 		JSONObject pagination = new JSONObject();
	 		pagination.put("page", getPage());
	 		pagination.put("perPage", getPerPage());
	 		if (getPerPage() < 0) {
	 			pagination.put("perPage", 500);
	 		}
	 		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
	 		
	 		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
	 		Chain dataList = ReadOnlyChainFactory.fetchModuleDataListChain();
	 		dataList.execute(context);
	 		
	 		moduleDatas = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
	 		setResult(FacilioConstants.ContextNames.MODULE_DATA_LIST, moduleDatas);
			return SUCCESS;
		}
		catch(Exception e) {
			setResponseCode(1);
			setMessage(e);
			return ERROR;
		}
	}
	
	public String addModuleData() {
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
			context.put(FacilioConstants.ContextNames.RECORD, moduleData);
			Chain addModuleDataChain = FacilioChainFactory.addModuleDataChain();
			addModuleDataChain.execute(context);
			
			setId(moduleData.getId());
			return moduleDataDetails();
		}
		catch(Exception e) {
			setResponseCode(1);
			setMessage(e);
			return ERROR;
		}
	}
	
	public String updateModuleData() {
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
			context.put(FacilioConstants.ContextNames.RECORD, moduleData);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(moduleData.getId()));
			
			Chain updateModuleDataChain = FacilioChainFactory.updateModuleDataChain();
			updateModuleDataChain.execute(context);
			
			setId(moduleData.getId());
			return moduleDataDetails();
		}
		catch(Exception e) {
			setResponseCode(1);
			setMessage(e);
			return ERROR;
		}
	}
	
	public String deleteModuleData() {
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.DELETE);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
			
			Chain deleteModuleDataChain = FacilioChainFactory.deleteModuleDataChain();
			deleteModuleDataChain.execute(context);
			
			rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
			setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
			return SUCCESS;
		}
		catch(Exception e) {
			setResponseCode(1);
			setMessage(e);
			return ERROR;
		}
	}
	
	private List<ModuleBaseWithCustomFields> moduleDatas;
	public List<ModuleBaseWithCustomFields> getModuleDatas() {
		return moduleDatas;
	}
	public void setModuleDatas(List<ModuleBaseWithCustomFields> moduleDatas) {
		this.moduleDatas = moduleDatas;
	}
	
	private ModuleBaseWithCustomFields moduleData;
	public ModuleBaseWithCustomFields getModuleData() {
		return moduleData;
	}
	public void setModuleData(ModuleBaseWithCustomFields moduleData) {
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
