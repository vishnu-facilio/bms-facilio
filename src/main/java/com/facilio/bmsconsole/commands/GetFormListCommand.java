package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;

public class GetFormListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Integer> formTypes = (List<Integer>) context.get(FacilioConstants.ContextNames.FORM_TYPE);
		Map<String, FacilioForm> forms = new LinkedHashMap<>(FormFactory.getForms((String)context.get(FacilioConstants.ContextNames.MODULE_NAME), formTypes));
		Map<String, FacilioForm> dbForms=FormsAPI.getFormsAsMap((String)context.get(FacilioConstants.ContextNames.MODULE_NAME), formTypes);
		
		if (dbForms != null) {
			for(Map.Entry<String, FacilioForm> entry :dbForms.entrySet()) {
				forms.put(entry.getKey(), entry.getValue());
			}
		}
		List<FacilioForm> formsList = new ArrayList<>(forms.values());
		formsList.removeIf(form -> form.isHideInList() || (AccountUtil.getCurrentAccount().isFromMobile() && !form.getShowInMobile()));

		context.put(FacilioConstants.ContextNames.FORMS, formsList);
		return false;
	}

}
