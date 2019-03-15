package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;

public class GetFormListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioForm> formList=FormsAPI.getFormList((String)context.get(FacilioConstants.ContextNames.MODULE_NAME),(FormType) (context.get(FacilioConstants.ContextNames.FORM_TYPE)));
		context.put(FacilioConstants.ContextNames.FORMS, formList);
		return false;
	}

}
