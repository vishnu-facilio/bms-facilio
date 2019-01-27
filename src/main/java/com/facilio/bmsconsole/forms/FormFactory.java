package com.facilio.bmsconsole.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioField.FieldDisplayType;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class FormFactory {
	
	private static final Map<String, FacilioForm> FORM_MAP = Collections.unmodifiableMap(initMap());
	private static final Map<FormType, Multimap<String, FacilioForm>> ALL_FORMS = Collections.unmodifiableMap(initAllForms());

	private static Map<String, FacilioForm> initMap() {
		Map<String, FacilioForm> forms = new HashMap<>();
		forms.put("workOrderForm", getWorkOrderForm());
		forms.put("serviceWorkRequest", getServiceWorkRequestForm());
		forms.put("serviceWorkOrder", getServiceWorkOrderForm());
		forms.put("loggedInServiceWorkRequest", getLoggedInServiceWorkRequest());
		forms.put("web_pm", getPMForm());
		forms.put("approvalForm", getApprovalForm());
		return forms;
	}

	@SuppressWarnings("unchecked") // https://stackoverflow.com/a/11205178
	public static Map<String, Set<FacilioForm>> getAllForms(FormType formtype) {
		return (Map<String, Set<FacilioForm>>) (Map<?, ?>) Multimaps.asMap(ALL_FORMS.get(formtype));
	}
	
	private static Map<FormType, Multimap<String, FacilioForm>> initAllForms() {
		return ImmutableMap.<FormType, Multimap<String, FacilioForm>>builder()
				.put(FormType.MOBILE, ImmutableMultimap.<String, FacilioForm>builder()
						.put(FacilioConstants.ContextNames.WORK_ORDER, getMobileWorkOrderForm())
						.put(FacilioConstants.ContextNames.APPROVAL, getMobileApprovalForm()).build())
				.put(FormType.WEB, ImmutableMultimap.<String, FacilioForm>builder()
						.put(FacilioConstants.ContextNames.WORK_ORDER, getWebWorkOrderForm())
						.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, getWebWorkRequestForm()).build())
				.put(FormType.PORTAL, ImmutableMultimap.<String, FacilioForm>builder()
						.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, getServiceWorkRequestForm()).build())
				.build();
	}
	
	

	public static FacilioForm getForm(String name) {
		return FormFactory.FORM_MAP.get(name);
	}
	
	public static FacilioForm getLoggedInServiceWorkRequest() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("SUBMIT A REQUEST");
		form.setName("loggedInServiceWorkRequest");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER_REQUEST));
		form.setFormType(FormType.PORTAL);
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getLoggedInServiceWorkRequestFormFields());
		return form;
	}
	
	public static FacilioForm getServiceWorkRequestForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("SUBMIT A REQUEST");
		form.setName("serviceWorkRequest");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER_REQUEST));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getServiceWorkRequestFormFields());
		form.setFormType(FormType.PORTAL);
		return form;
	}
	public static FacilioForm getServiceWorkOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("SUBMIT A REQUEST");
		form.setName("serviceWorkOrder");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getServiceWorkRequestFormFields());
		form.setFormType(FormType.PORTAL);
		return form;
	}
	public static FacilioForm getWorkOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("WORKORDER");
		form.setName("workOrder");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER_REQUEST));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getWebWorkOrderFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	public static FacilioForm getApprovalForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Approval");
		form.setName("workOrder");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getWebApprovalFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getMobileApprovalForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Approval");
		form.setName("mobile_approval");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getMobileApprovalFormFields());
		form.setFormType(FormType.MOBILE);
		return form;
	}
	
	public static FacilioForm getMobileWorkOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("SUBMIT WORKORDER");
		form.setName("mobile_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getMobileWorkOrderFormFields());
		form.setFormType(FormType.MOBILE);
		return form;
	}
	
	private static List<FormField> getMobileApprovalFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.REQUIRED, 2, 1));
		fields.add(new FormField("site", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 1));
		fields.add(new FormField("resource", FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.REQUIRED, 4, 1));
		fields.add(new FormField("assignment", FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.REQUIRED, 5, 1));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 6, 1));
		fields.add(new FormField("urgency", FieldDisplayType.URGENCY, "Urgency", Required.OPTIONAL, "urgency" , 7, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 8, 1));
		return fields;
	}
	
	public static List<FormField> getMetaFormFieldApprovals(List<FacilioField> allFields) throws Exception {
		List<FormField> fields = new ArrayList<>();

		List<FacilioField> facilioFields = new ArrayList(); 
		int i = 0;
		for(FacilioField fieldObject: allFields) {
		if(FieldFactory.Fields.approvalFormFields.contains(fieldObject.getName()) || !fieldObject.isDefault()) {
			if (fieldObject.getName().equals("resource")) {
				fields.add(new FormField(fieldObject.getFieldId(),fieldObject.getName(), FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.OPTIONAL, i++, 1));
			} else if (fieldObject.getName().equals("assignmentGroup")) {
				fields.add(new FormField(fieldObject.getFieldId(), fieldObject.getName(), FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.REQUIRED, ++i, 1));
			} else if (fieldObject.getName().equals("urgency")) {
				fields.add(new FormField(fieldObject.getFieldId(), fieldObject.getName(), FieldDisplayType.URGENCY, fieldObject.getDisplayName(), Required.OPTIONAL, ++i, 1));
			} else {
				facilioFields.add(fieldObject);
			}
		 }
		}
		fields.add(new FormField("comment", FieldDisplayType.TICKETNOTES, "Comment", Required.OPTIONAL, "ticketnotes",6, 1));
		if (facilioFields.size() > 0) {
			fields.addAll(FormsAPI.getFacilioFieldsFromFormFields(facilioFields));
		}
		return fields;
	}
	
	private static List<FormField> getMobileWorkOrderFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.REQUIRED, 2, 1));
		fields.add(new FormField("site", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 1));
		fields.add(new FormField("resource", FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.REQUIRED, 4, 1));
		fields.add(new FormField("assignment", FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.REQUIRED, 5, 1));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 6, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 7, 1));
		return fields;
	}

	public static FacilioForm getWebWorkOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("SUBMIT WORKORDER");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getWebWorkOrderFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getWebWorkRequestForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("WORKORDER");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER_REQUEST));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getWebWorkRequestFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static FacilioForm getPMForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PREVENTIVE MAINTENANCE");
		form.setName("web_pm");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFormType(FormType.WEB);
		form.setFields(getWebPMFormFields());
		return form;
	}
	
	private static List<FormField> getWebWorkOrderFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 1, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "ticketcategory", 4, 2));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 4, 3));
		fields.add(new FormField("type",FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", Required.OPTIONAL, "tickettype", 5, 1));
		fields.add(new FormField("resource", FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("assignment", FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, "attachment", 8, 1));
		fields.add(new FormField("tasks", FieldDisplayType.TASKS, "Tasks", Required.OPTIONAL, 9, 1));
		return fields;
	}
	
	private static List<FormField> getWebPMFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("site", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 1, 1));
		fields.add(new FormField("type",FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", Required.REQUIRED, "tickettype", 2, 1));
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 3, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "ticketcategory", 5, 2));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 5, 3));
		fields.add(new FormField("duration", FieldDisplayType.DURATION, "Duration", Required.OPTIONAL, "duration", 6, 1));
		fields.add(new FormField("groups",FieldDisplayType.LOOKUP_SIMPLE,"Team", Required.OPTIONAL, "groups", 7, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, "attachment", 8, 1));
		return fields;
	}
	
	private static List<FormField> getWebApprovalFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 1, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "ticketcategory", 4, 2));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 4, 3));
		fields.add(new FormField("type",FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", Required.OPTIONAL, "tickettype", 5, 1));
		fields.add(new FormField("resource", FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("urgency", FieldDisplayType.URGENCY, "Urgency", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("assignment", FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.OPTIONAL, 8, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, "attachment", 9, 1));
		return fields;
	}

	private static List<FormField> getLoggedInServiceWorkRequestFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.OPTIONAL, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("urgency", FieldDisplayType.URGENCY, "Urgency", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 4, 1));
		return fields;
	}
	
	private static List<FormField> getServiceWorkRequestFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("requester", FieldDisplayType.REQUESTER, "Requester", Required.REQUIRED, 1, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site" ,2, 1));
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("urgency", FieldDisplayType.URGENCY, "Urgency", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 6, 1));
		return fields;
	}
	
	private static List<FormField> getWebWorkRequestFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.OPTIONAL, 1, 1));
		fields.add(new FormField("site", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("category", FieldDisplayType.SELECTBOX, "Category", Required.OPTIONAL, 4, 2));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 4, 3));
		fields.add(new FormField("type",FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("team", FieldDisplayType.TEAM, "Team", Required.REQUIRED, 6, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 7, 1));
		return fields;
	}

}
