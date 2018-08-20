package com.facilio.bmsconsole.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.modules.FacilioField.FieldDisplayType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;

public class FormFactory {
	
	private static final Map<String, FacilioForm> FORM_MAP = Collections.unmodifiableMap(initMap());

	private static Map<String, FacilioForm> initMap() {
		Map<String, FacilioForm> forms = new HashMap<>();
		forms.put("serviceWorkRequest", getServiceWorkRequestForm());
		forms.put("loggedInServiceWorkRequest", getLoggedInServiceWorkRequest());
		return forms;
	}
	
	public static FacilioForm getForm(String name) {
		return FormFactory.FORM_MAP.get(name);
	}
	
	public static FacilioForm getLoggedInServiceWorkRequest() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("SUBMIT A REQUEST");
		form.setName("loggedInServiceWorkRequest");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER_REQUEST));
		form.setFields(getLoggedInServiceWorkRequestFormFields());
		return form;
	}
	
	public static FacilioForm getServiceWorkRequestForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("SUBMIT A REQUEST");
		form.setName("serviceWorkRequest");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER_REQUEST));
		form.setFields(getServiceWorkRequestFormFields());
		return form;
	}
	
	private static List<FormField> getLoggedInServiceWorkRequestFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.OPTIONAL, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2));
		fields.add(new FormField("urgency", FieldDisplayType.URGENCY, "Urgency", Required.OPTIONAL, 3));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 4));
		return fields;
	}
	
	private static List<FormField> getServiceWorkRequestFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("requester", FieldDisplayType.REQUESTER, "Requester", Required.REQUIRED, 1));
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.OPTIONAL, 2));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3));
		fields.add(new FormField("urgency", FieldDisplayType.URGENCY, "Urgency", Required.OPTIONAL, 4));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 5));
		return fields;
	}
	
}
