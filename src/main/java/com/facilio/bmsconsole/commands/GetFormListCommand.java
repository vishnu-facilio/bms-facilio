package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;

public class GetFormListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, FacilioForm> forms = new LinkedHashMap<>(FormFactory.getForms((String)context.get(FacilioConstants.ContextNames.MODULE_NAME)));
		Map<String, FacilioForm> dbForms=FormsAPI.getFormsAsMap((String)context.get(FacilioConstants.ContextNames.MODULE_NAME),(FormType) (context.get(FacilioConstants.ContextNames.FORM_TYPE)));
		if (dbForms != null) {
			for(Map.Entry<String, FacilioForm> entry :dbForms.entrySet()) {
				forms.put(entry.getKey(), entry.getValue());
			}
		}
		context.put(FacilioConstants.ContextNames.FORMS, new ArrayList<>(forms.values()));
		return false;
	}

}
