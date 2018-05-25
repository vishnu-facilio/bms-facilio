package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;

public class CreateFormulaFieldDependenciesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldContext formulaField = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		if (formulaField != null) { 
			
			if (formulaField.getResourceTypeEnum() == null) {
				throw new IllegalArgumentException("Result data type cannot be null for addition of FormulaField");
			}
			
			FacilioField field = FieldFactory.getField(null, formulaField.getName(), null, null, formulaField.getResultDataTypeEnum());
			field.setDisplayType(FacilioField.FieldDisplayType.ENPI);
			formulaField.setReadingField(field);
			
			context.put(FacilioConstants.ContextNames.READING_NAME, formulaField.getName());
			context.put(FacilioConstants.ContextNames.MODULE_FIELD, formulaField.getReadingField());
			setModuleType(formulaField, context);
			setReadingParent(formulaField, context);
		}
		return false;
	}
	
	private void setReadingParent(FormulaFieldContext formulaField, Context context) {
		switch (formulaField.getResourceTypeEnum()) {
		case ONE_RESOURCE:
			context.put(FacilioConstants.ContextNames.PARENT_ID, formulaField.getResourceId());
			break;
		case ALL_SITES:
			setSpaceParentModule(context, formulaField, FacilioConstants.ContextNames.SITE);
			break;
		case ALL_BUILDINGS:
			setSpaceParentModule(context, formulaField, FacilioConstants.ContextNames.BUILDING);
			break;
		case ALL_FLOORS:
			setSpaceParentModule(context, formulaField, FacilioConstants.ContextNames.FLOOR);
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
	
	private void setSpaceParentModule(Context context, FormulaFieldContext formulaField, String moduleName) {
		if (formulaField.getIncludedResources() == null || formulaField.getIncludedResources().isEmpty()) {
			context.put(FacilioConstants.ContextNames.PARENT_MODULE, moduleName);
		}
		else {
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, formulaField.getIncludedResources());
		}
	}
	
	private void setModuleType(FormulaFieldContext formulaField, Context context) {
		switch (formulaField.getTriggerTypeEnum()) {
			case SCHEDULE:
				context.put(FacilioConstants.ContextNames.MODULE_TYPE, FacilioModule.ModuleType.SCHEDULED_FORMULA);
				break;
			case LIVE_READING:
				context.put(FacilioConstants.ContextNames.MODULE_TYPE, FacilioModule.ModuleType.LIVE_FORMULA);
				break;
		}
	}
}
