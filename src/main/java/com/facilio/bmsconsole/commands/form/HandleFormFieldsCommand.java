package com.facilio.bmsconsole.commands.form;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormSourceType;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class HandleFormFieldsCommand extends FacilioCommand {
	
	boolean isAssetModule = false;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName == null) {
			return false;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		if (form != null) {
			FormSourceType formSourceType = (FormSourceType) context.getOrDefault(ContextNames.FORM_SOURCE, FormSourceType.FROM_FORM);
			boolean isFromBuilder = formSourceType == formSourceType.FROM_BUILDER;
			isAssetModule = AssetsAPI.isAssetsModule(module);
			for(FormField field: form.getFields()) {
				if (formSourceType == FormSourceType.FROM_BULK_FORM) {
					field.setValue(null);
				}
				else if (!isFromBuilder) {
					handleDefaultValue(field);
				}
				setLookupName(field, moduleName, isFromBuilder);
				addFilters(module, field);
			}
		}
		
		return false;
	}
	
	private void handleDefaultValue(FormField formField) throws Exception {
		Object value = formField.getValue();
		if (formField.getField() != null && value != null) {
			switch(formField.getField().getDataTypeEnum()) {
				case DATE:
				case DATE_TIME:
				case LOOKUP:
					String val = value.toString();
					if (val.startsWith("${")) {
						value = FormsAPI.resolveDefaultValPlaceholder(val);
					}
					break;
			}
			
			formField.setValueObject(value);
		}
		
	}
	
	private void addFilters(FacilioModule module, FormField formField) {
		if (isAssetModule) {
			switch(formField.getName()) {
				case "rotatingItem":
					setRotatingFilter("itemType", formField);
					break;
				case "rotatingTool":
					setRotatingFilter("toolType", formField);
					break;
			}
		}
	}
	
	private void setLookupName(FormField formField, String moduleName, boolean isFromBuilder) throws Exception {
		FacilioField field = formField.getField();
		boolean isLookup = formField.getDisplayTypeEnum() == FieldDisplayType.LOOKUP_SIMPLE || formField.getDisplayTypeEnum() == FieldDisplayType.MULTI_LOOKUP_SIMPLE;
		// If its a lookup field or a special lookup form field
		if ((field != null && field instanceof BaseLookupField && !isFromBuilder && isLookup) || isLookup) {
			if (formField.getConfig() != null) {
				JSONObject config = formField.getConfig();
				boolean filterEnabled = (boolean) config.getOrDefault("isFiltersEnabled", false);
				if (filterEnabled) {
					String lookupName = (String) config.get("lookupModuleName");
					if (lookupName == null) { // Temp..needs to remove
						int filterValue = Integer.parseInt(formField.getConfig().get("filterValue").toString());
						lookupName = getModuleName(filterValue);
					}
					formField.setLookupModuleName(lookupName);
					return;
				}
			}
		}
	}
	
	// Temp..needs to remove
	private String getModuleName(int val) {
		Map<Integer, String> names= new HashMap<>();
		names.put(1, "building");
		names.put(2, "asset");
		names.put(3, "tenantcontact");
		names.put(4, "clientcontact");
		names.put(5, "vendorcontact");
		names.put(6, "employee");
		return names.get(val);
	}
	
	private void setRotatingFilter(String type, FormField formField) {
        JSONObject operator = new JSONObject();
        operator.put("operatorId", LookupOperator.LOOKUP.getOperatorId());
        JSONArray values = new JSONArray();
        operator.put("lookupOperatorId", BooleanOperators.IS.getOperatorId());
        operator.put("field", "isRotating");
        values.add(String.valueOf(true));
        operator.put("value", values);

        formField.addToFilters(type, operator);
    }
	
}
