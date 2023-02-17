package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3WorkOrderModuleSettingAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.constants.FacilioConstants.Induction;
import com.facilio.constants.FacilioConstants.Inspection;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetAllFieldsCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
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
			
			FacilioChain addCurrentOccupancy = FacilioChainFactory.getCategoryReadingsChain();
			addCurrentOccupancy.execute(context);
			
			List<FacilioModule> readingModules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			for(FacilioModule reading : readingModules) {
				List<FacilioField> readingFields = reading.getFields();
				allFields.addAll(readingFields);
			}
			
		}
		else
		{
			FacilioChain getFieldsChain = FacilioChainFactory.getGetFieldsChain();
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
		reportOperators.put("DateAggregateOperator", BmsAggregateOperators.DateAggregateOperator.values());
		reportOperators.put("NumberAggregateOperator", BmsAggregateOperators.NumberAggregateOperator.values());
		reportOperators.put("StringAggregateOperator", BmsAggregateOperators.StringAggregateOperator.values());
		reportOperators.put("SpaceAggregateOperator", BmsAggregateOperators.SpaceAggregateOperator.values());
		reportOperators.put("EnergyPurposeAggregateOperator", BmsAggregateOperators.EnergyPurposeAggregateOperator.values());
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
		FacilioModule mod = modBean.getModule(moduleName);
	
		if (isFilter != null) {
			for(FacilioField fieldObject:allFields) {
				if (moduleName.equals(ContextNames.ALARM)) {
					if(FieldFactory.Fields.ALARMS_FIELDS_INCLUDE.contains(fieldObject.getName())) {
						fields.add(fieldObject);
					}
				} else if (moduleName.equals("event")) {
					if(FieldFactory.Fields.ENTITY_FIELDS_INCLUDE.contains(fieldObject.getName())) {
						fields.add(fieldObject);
					}
				} 
				else if (moduleName.equals("workorder")) {
					if(!fieldObject.isDefault()) {
						fields.add(fieldObject);
					}
					else if(FieldFactory.Fields.WORK_ORDER_FIELDS_INCLUDE.contains(fieldObject.getName())) {
						fields.add(fieldObject);
					}
					else if(fieldObject.getName().equals("tenant") && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
						fields.add(fieldObject);
					}
					else if(fieldObject.getName().equals("client") && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLIENT)) {
						fields.add(fieldObject);
					}
					else if(fieldObject.getName().equals("safetyPlan") && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
						fields.add(fieldObject);
					}
					else if(fieldObject.getName().equals("workPermitNeeded") && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WORK_PERMIT)) {
						fields.add(fieldObject);
					}
					else if(fieldObject.getName().equals("workPermitIssued") && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WORK_PERMIT)) {
						fields.add(fieldObject);
					}
					
				}
				else if (moduleName.equals("asset")) {
					if(!fieldObject.isDefault()) {
						fields.add(fieldObject);
					} else if(FieldFactory.Fields.ASSET_FIELDS_INCLUDE.contains(fieldObject.getName())) {
						fields.add(fieldObject);
					}
				}
				else if (moduleName.equals("newreadingalarm") || moduleName.equals("bmsalarm") || moduleName.equals("mlAnomalyAlarm")) {
					if(!fieldObject.isDefault()) {
						fields.add(fieldObject);
					}else if(FieldFactory.Fields.NEW_ALARMS_FIELDS_INCLUDE.contains(fieldObject.getName())) {
						fields.add(fieldObject);
					}
				}
				else if (moduleName.equals(ContextNames.OPERATION_ALARM)) {
					if(!fieldObject.isDefault()) {
						fields.add(fieldObject);
					} else if(FieldFactory.Fields.NEW_OP_ALARMS_FIELDS_INCLUDE.contains(fieldObject.getName())) {
						fields.add(fieldObject);
					}
				}
				else if ( moduleName.equals(Inspection.INSPECTION_RESPONSE) || moduleName.equals(Induction.INDUCTION_RESPONSE) ) {
					if(!fieldObject.getName().equals("template") && !fieldObject.getName().equals("sysCreatedTime")) {
						fields.add(fieldObject);
					}
				} else if ("plannedmaintenance".equals(moduleName)) {
					if(!fieldObject.isDefault()) {
						fields.add(fieldObject);
					} else if(FieldFactory.Fields.PM_FIELDS_INCLUDE.contains(fieldObject.getName())) {
						fields.add(fieldObject);
					}
				} else {
					fields.add(fieldObject);
				}
			}
		} else {
			
			for(FacilioField fieldObject:allFields) {
				if ( moduleName.equals(Inspection.INSPECTION_RESPONSE) || moduleName.equals(Induction.INDUCTION_RESPONSE) ) {
					if(!fieldObject.getName().equals("template") && !fieldObject.getName().equals("sysCreatedTime")) {
						fields.add(fieldObject);
					}
				}
				else if (moduleName.equals(ContextNames.PLANNEDMAINTENANCE)) {
					if (!fieldObject.isDefault()) {
						fields.add(fieldObject);
					} else if (!FieldFactory.Fields.PM_FIELDS_EXCLUDE.contains(fieldObject.getName())) {
						fields.add(fieldObject);
					}
				}
				else {
					fields.add(fieldObject);
				}
			}
		}
		
		if (mod.isCustom() ) {
			List<FacilioField> fieldsToRemove = new ArrayList<>();
			for(FacilioField field : fields) {
				if (field.getName().equals("stateFlowId")) {
					fieldsToRemove.add(field);
				}
				else if (field.getName().equals("moduleState") && mod.getStateFlowEnabled() != null && !mod.getStateFlowEnabled()) {
					fieldsToRemove.add(field);
				}
			}
			fields.removeAll(fieldsToRemove);
		}
		
		if (Arrays.asList(ContextNames.WORK_ORDER, ContextNames.TENANT, ContextNames.ASSET, ContextNames.SERVICE_REQUEST,ContextNames.WorkPermit.WORKPERMIT).contains(mod.getName())) {
			fields.add(FieldFactory.getSiteIdField());
		}
		
		JSONObject meta = new JSONObject();
		meta.put("name", moduleName);
		meta.put("module", mod);
		meta.put("displayName", displayName);
		meta.put("fields", fields);
		meta.put("operators", operators);
		meta.put("reportOperators", reportOperators);
		fetchModuleSetting(moduleName, meta);

		context.put(FacilioConstants.ContextNames.META, meta);
		// TODO Auto-generated method stub
		return false;
	}

	private static void fetchModuleSetting(String moduleName, JSONObject meta) throws Exception {
		try {
			if (moduleName.equals(ContextNames.WORK_ORDER)){
				meta.put("moduleSetting", V3WorkOrderModuleSettingAPI.fetchWorkOrderModuleSettings());
			}
		}catch (Exception e){

		}
	}

}
