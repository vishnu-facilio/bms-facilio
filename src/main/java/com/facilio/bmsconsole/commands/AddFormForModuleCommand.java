package com.facilio.bmsconsole.commands;

import java.util.ArrayList;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class AddFormForModuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		
		// creating Standard form for the new module
		FacilioForm form = new FacilioForm();
		form.setName("standard_" + module.getName());
		form.setDisplayName("Standard");
		form.setFormType(FormType.WEB);
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(new ArrayList<>());
		
		FormsAPI.createForm(form, module);
		context.put(FacilioConstants.ContextNames.FORM, form);
		return false;
	}

}
