package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;

public class GetAllFieldsCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		List<FacilioField> allFields = new ArrayList();
		List<FacilioField> fields = new ArrayList();
		String resourceType = (String) context.get(FacilioConstants.ContextNames.RESOURCE_TYPE);
		long categoryId = (long) context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Boolean isFilter = (Boolean) context.get(FacilioConstants.ContextNames.IS_FILTER);
		System.out.println("isFilter ++++++++++++++++++" + isFilter);
		if(resourceType!= null && categoryId != -1)
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
//			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
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
				allFields.addAll(readingFields);
			}
			
		}
		else
		{
			Chain getFieldsChain = FacilioChainFactory.getGetFieldsChain();
			getFieldsChain.execute(context);
			allFields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
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
		FacilioModule mod = modBean.getModule(moduleName);
	
		List<WorkflowEventContext> workflowEvents = WorkflowRuleAPI.getWorkflowEvents(orgId, mod.getModuleId());
		if (isFilter != null) {
			for(FacilioField fieldObject:allFields) {
				if (moduleName.equals(ContextNames.ALARM)) {
					if(FieldFactory.Fields.alarmsFieldsInclude.contains(fieldObject.getName())) {
						fields.add(fieldObject);
					}
				} else if (moduleName.equals("event")) {
					if(FieldFactory.Fields.entityFieldsInclucde.contains(fieldObject.getName())) {
						fields.add(fieldObject);
					}
				} 
				else if (moduleName.equals("workorder")) {
					if(!fieldObject.isDefault()) {
						fields.add(fieldObject);
					}
					else if(FieldFactory.Fields.workOrderFieldsInclude.contains(fieldObject.getName())) {
						fields.add(fieldObject);
					}
				}else {
					fields = allFields;
				}
			}
		} else {
			fields = allFields;
		}
		
		JSONObject meta = new JSONObject();
		meta.put("name", moduleName);
		meta.put("displayName", displayName);
		meta.put("fields", fields);
		meta.put("operators", operators);
		meta.put("reportOperators", reportOperators);
		meta.put("workflowEvents", workflowEvents);
		context.put(FacilioConstants.ContextNames.META, meta);		
		// TODO Auto-generated method stub
		return false;
	}

}
