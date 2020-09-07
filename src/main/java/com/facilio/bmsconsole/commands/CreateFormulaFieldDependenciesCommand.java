package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;

;

public class CreateFormulaFieldDependenciesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldContext formulaField = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		String formulaUnitString = (String) context.get(FacilioConstants.ContextNames.FORMULA_UNIT_STRING);
		int formulaUnit = (int) context.getOrDefault(FacilioConstants.ContextNames.FORMULA_UNIT, -1);
		if (formulaField != null) {
			formulaField.setInterval(FormulaFieldAPI.getDataInterval(formulaField));
			FormulaFieldAPI.validateFormula(formulaField, false);
			
			Boolean isFromulaFieldIOpperationFromMAndV = (Boolean) context.get(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V);
			
			if(isFromulaFieldIOpperationFromMAndV == null || !isFromulaFieldIOpperationFromMAndV) {
				Boolean calculateVmThroughFormula = (Boolean) context.get(FacilioConstants.OrgInfoKeys.CALCULATE_VM_THROUGH_FORMULA);
				FacilioField field = null;
				
				if(calculateVmThroughFormula != null && calculateVmThroughFormula) {
					field = formulaField.getReadingField();
				}
				else {
					field = FieldFactory.getField(null, formulaField.getName(), null, null, formulaField.getResultDataTypeEnum() == null? FieldType.DECIMAL : formulaField.getResultDataTypeEnum());
					field.setDisplayType(FacilioField.FieldDisplayType.ENPI);
				}

				if(field != null && field instanceof NumberField) {
					if (formulaUnitString != null) {
						((NumberField) field).setUnit(formulaUnitString);
					}
					else if (formulaUnit > 0) {
						((NumberField) field).setUnitId(formulaUnit);
						((NumberField) field).setMetric((int)context.get(FacilioConstants.ContextNames.FORMULA_METRIC));
					}
				}
				formulaField.setReadingField(field);
			}
			
			if (formulaField.getTriggerTypeEnum() == TriggerType.PRE_LIVE_READING) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> existingFields = modBean.getAllFields(formulaField.getModuleName());
				List<FacilioField> newFields = Collections.singletonList(formulaField.getReadingField());
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
			case ALL_SITES: // TODO Will be removed
				/*if (formulaField.getIncludedResources() == null || formulaField.getIncludedResources().isEmpty()) {
					setSpaceModuleParentList(FacilioConstants.ContextNames.SITE, SpaceAPI.getAllSites(), context);
				}
				else {
					context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, formulaField.getIncludedResources());
				}*/
				break;
			case ALL_BUILDINGS:
				if (formulaField.getIncludedResources() == null || formulaField.getIncludedResources().isEmpty()) {
					setSpaceModuleParentList(FacilioConstants.ContextNames.BUILDING, SpaceAPI.getAllBuildings(formulaField.getSiteId()), context);
				}
				else {
					context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, formulaField.getIncludedResources());
				}
				break;
			case ALL_FLOORS:
				if (formulaField.getIncludedResources() == null || formulaField.getIncludedResources().isEmpty()) {
					setSpaceModuleParentList(FacilioConstants.ContextNames.FLOOR, SpaceAPI.getAllFloors(formulaField.getSiteId()), context);
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
