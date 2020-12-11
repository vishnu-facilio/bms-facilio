package com.facilio.bmsconsole.commands.form;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateTimeUtil;

public class HandleFormFieldsCommand extends FacilioCommand {

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
			for(FormField field: form.getFields()) {
				handleDefaultValue(field);
				addFilters(module, field);
			}
		}
		
		return false;
	}
	
	private void handleDefaultValue(FormField formField) throws Exception {
		if (formField.getField() != null) {
			Object value = null;
			switch(formField.getField().getDataTypeEnum()) {
				case DATE:
				case DATE_TIME:
					if (formField.getConfig() != null) {
						Boolean setToday = (Boolean) formField.getConfig().get("setToday");
						if (setToday != null && setToday) {
							value = DateTimeUtil.getDayStartTime();
						}
						else if (formField.getConfig().containsKey("dayCount")) {
							Integer dayCount = Integer.parseInt(formField.getConfig().get("dayCount").toString());
							value = DateTimeUtil.addDays(DateTimeUtil.getDayStartTime(), dayCount);
						}
					}
					break;
					
				case LOOKUP:
					if (formField.getValue() != null && ((LookupField)formField.getField()).getLookupModule().getName().equals(ContextNames.RESOURCE)) {
						String val = formField.getValue().toString();
						if (StringUtils.isNumeric(val)) {
							value = ResourceAPI.getResource(Long.parseLong(val.toString()));
						}
					}
					break;
			}
			
			if (value != null) {
				formField.setValueObject(value);
			}
		}
		
	}
	
	private void addFilters(FacilioModule module, FormField formField) {
		if (AssetsAPI.isAssetsModule(module)) {
			switch(formField.getName()) {
				case "rotatingItem":
				case "rotatingTool":
					JSONObject rotatingFilter = getRotatingFilter();
					formField.setFilters(rotatingFilter);
					break;
			}
		}
	}
	
	private JSONObject getRotatingFilter() {
        JSONObject operator = new JSONObject();
        operator.put("operatorId", LookupOperator.LOOKUP.getOperatorId());
        JSONArray values = new JSONArray();
        operator.put("lookupOperatorId", BooleanOperators.IS.getOperatorId());
        operator.put("field", "isRotating");
        values.add(String.valueOf(true));
        operator.put("value", values);

        JSONObject filter = new JSONObject();
        filter.put("itemType", operator);
        return filter;
    }

}
