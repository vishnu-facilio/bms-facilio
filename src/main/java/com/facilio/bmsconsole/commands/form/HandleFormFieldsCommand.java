package com.facilio.bmsconsole.commands.form;

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
			
			if (value != null) {
				formField.setValueObject(value);
			}
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
