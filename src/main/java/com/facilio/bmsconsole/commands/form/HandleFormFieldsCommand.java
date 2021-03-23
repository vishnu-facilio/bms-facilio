package com.facilio.bmsconsole.commands.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
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
			boolean isFromBuilder = (Boolean) context.getOrDefault("fromBuilder", false);
			isAssetModule = AssetsAPI.isAssetsModule(module);
			for(FormField field: form.getFields()) {
				if (!isFromBuilder) {
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
		if (formField.getDisplayTypeEnum() == FieldDisplayType.ATTACHMENT) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioModule> subModules = modBean.getSubModules(moduleName, ModuleType.ATTACHMENTS);
			formField.setLookupModuleName(subModules.get(0).getName());
		}
		else  {
			FacilioField field = formField.getField();
			if (field != null && field instanceof BaseLookupField) {
				if (!isFromBuilder && formField.getConfig() != null) {
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
				
				FacilioModule lookupMod = ((BaseLookupField) field).getLookupModule();
				if (lookupMod != null) {
					formField.setLookupModuleName(lookupMod.getName());
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
