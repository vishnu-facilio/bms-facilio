package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreateFormulaFieldDependenciesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldContext formulaField = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		String formulaUnit = (String) context.get(FacilioConstants.ContextNames.FORMULA_UNIT_STRING);
		if (formulaField != null) {
			FormulaFieldAPI.validateFormula(formulaField, false);
			
			FacilioField field = FieldFactory.getField(null, formulaField.getName(), null, null, formulaField.getResultDataTypeEnum() == null? FieldType.DECIMAL : formulaField.getResultDataTypeEnum());
			field.setDisplayType(FacilioField.FieldDisplayType.ENPI);
			if(formulaUnit != null && field instanceof NumberField) {
				((NumberField) field).setUnit(formulaUnit);
			}

			formulaField.setReadingField(field);
			formulaField.setInterval(FormulaFieldAPI.getDataInterval(formulaField));
			
			if (formulaField.getTriggerTypeEnum() == TriggerType.PRE_LIVE_READING) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> existingFields = modBean.getAllFields(formulaField.getModuleName());
				List<FacilioField> newFields = Collections.singletonList(field);
				context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, existingFields);
				context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, newFields);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, formulaField.getModuleName());
			}
			else {
				context.put(FacilioConstants.ContextNames.READING_NAME, formulaField.getName());
				context.put(FacilioConstants.ContextNames.MODULE_FIELD, formulaField.getReadingField());
				context.put(FacilioConstants.ContextNames.MODULE_DATA_INTERVAL, formulaField.getInterval());
				context.put(FacilioConstants.ContextNames.MODULE_TYPE, FormulaFieldAPI.getModuleTypeFromTrigger(formulaField.getTriggerTypeEnum()));
				setReadingParent(formulaField, context);
			}
		}
		return false;
	}
	
	private void setReadingParent(FormulaFieldContext formulaField, Context context) throws Exception {
		switch (formulaField.getResourceTypeEnum()) {
			case ONE_RESOURCE:
				context.put(FacilioConstants.ContextNames.PARENT_ID, formulaField.getResourceId());
				break;
			case ALL_SITES:
				if (formulaField.getIncludedResources() == null || formulaField.getIncludedResources().isEmpty()) {
					setSpaceModuleParentList(FacilioConstants.ContextNames.SITE, SpaceAPI.getAllSites(), context);
				}
				else {
					context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, formulaField.getIncludedResources());
				}
				break;
			case ALL_BUILDINGS:
				if (formulaField.getIncludedResources() == null || formulaField.getIncludedResources().isEmpty()) {
					setSpaceModuleParentList(FacilioConstants.ContextNames.BUILDING, SpaceAPI.getAllBuildings(), context);
				}
				else {
					context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, formulaField.getIncludedResources());
				}
				break;
			case ALL_FLOORS:
				if (formulaField.getIncludedResources() == null || formulaField.getIncludedResources().isEmpty()) {
					setSpaceModuleParentList(FacilioConstants.ContextNames.FLOOR, SpaceAPI.getAllFloors(), context);
				}
				else {
					context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, formulaField.getIncludedResources());
				}
				break;
			case SPACE_CATEGORY:
				context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.SPACE_CATEGORY);
				context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, formulaField.getSpaceCategoryId());
				context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getSpaceCategoryReadingRelModule());
				break;
			case ASSET_CATEGORY:
				context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.ASSET_CATEGORY);
				context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, formulaField.getAssetCategoryId());
				context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
				break;
		}
	}
	
	private void setSpaceModuleParentList(String moduleName, List<? extends BaseSpaceContext> spaces, Context context) {
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.SITE);
		
		if (spaces != null && !spaces.isEmpty()) {
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, spaces.stream().map(BaseSpaceContext::getId).collect(Collectors.toList()));
		}
	}
}
