package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.WorkflowEventContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.opensymphony.xwork2.ActionSupport;

public class ModuleAction extends ActionSupport {
	
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
		if(resourceType!= null && getCategoryId() != -1)
		{
			FacilioModule module = null;
			if(resourceType.equalsIgnoreCase("asset"))
			{
				module = ModuleFactory.getAssetCategoryReadingRelModule();
			}
			else
			{
				module = ModuleFactory.getSpaceCategoryReadingRelModule();
			}
			context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getCategoryId());
			context.put(FacilioConstants.ContextNames.LIMIT_VALUE, -1);
//			context.put(FacilioConstants.ContextNames.PARENT_ID, getAssetId());
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			List<FacilioModule> moduleList = new ArrayList<>();
			moduleList.add(modBean.getModule(moduleName));
			
			context.put(FacilioConstants.ContextNames.MODULE_LIST, moduleList);
			
			Chain addCurrentOccupancy = FacilioChainFactory.getCategoryReadingsChain();
			addCurrentOccupancy.execute(context);
			
			List<FacilioModule> readingModules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			for(FacilioModule reading : readingModules) {
				List<FacilioField> readingFields = reading.getFields();
				fields.addAll(readingFields);
			}
			
		}
		else
		{
			Chain getFieldsChain = FacilioChainFactory.getGetFieldsChain();
			getFieldsChain.execute(context);
		
			fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
		}
		
		String displayName = (String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME);
		if(displayName == null)
		{
			displayName = (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
		}

		JSONObject operators = new JSONObject();
		for (FieldType ftype : FieldType.values()) {
			operators.put(ftype.name(), ftype.getOperators());
		}
		
		JSONObject reportOperators = new JSONObject();
		reportOperators.put("DateAggregateOperator", FormulaContext.DateAggregateOperator.values());
		reportOperators.put("NumberAggregateOperator", FormulaContext.NumberAggregateOperator.values());
		reportOperators.put("StringAggregateOperator", FormulaContext.StringAggregateOperator.values());
		reportOperators.put("SpaceAggregateOperator", FormulaContext.SpaceAggregateOperator.values());
		reportOperators.put("EnergyPurposeAggregateOperator", FormulaContext.EnergyPurposeAggregateOperator.values());
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
		FacilioModule mod = modBean.getModule(getModuleName());
		
		List<WorkflowEventContext> workflowEvents = WorkflowRuleAPI.getWorkflowEvents(orgId, mod.getModuleId());
		
		JSONObject meta = new JSONObject();
		meta.put("name", getModuleName());
		meta.put("displayName", displayName);
		meta.put("fields", fields);
		meta.put("operators", operators);
		meta.put("reportOperators", reportOperators);
		meta.put("workflowEvents", workflowEvents);
		setMeta(meta);
		
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
}
