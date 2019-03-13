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
		forms.put("assetForm", getAssetForm());
		forms.put("item_form", getItemForm());
		forms.put("item_track_form", getItemWithIndTrackForm());
		forms.put("store_room_form", getStoreRoomForm());
		forms.put("item_types_form", getItemTypesForm());
		forms.put("tool_types_form", getTooltypesForm());
		forms.put("vendors_form", getVendorsForm());
		forms.put("tool_form", getStockedToolsForm());
		forms.put("tool_track_form", getToolWithIndTrackForm());
		forms.put("item_category_form", getItemCategoryForm());
//		forms.put("location_form", getItemCategoryForm());
		return forms;
	}
    public static List<FormField> getFormFields(String modName) {
    	List<FormField> fields=new ArrayList();
    	if(modName.equals(FacilioConstants.ContextNames.WORK_ORDER))
    	{
    		fields.addAll(getWebWorkOrderFormFields());
    	}
    	else if(modName.equals(FacilioConstants.ContextNames.ASSET))
    	{
    		fields.addAll(getWebAssetFormFields());
    	}
    	else if(modName.equals(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE))
    	{
    		fields.addAll(getWebPMFormFields());
    	}
    	else if(modName.equals(FacilioConstants.ContextNames.APPROVAL))
    	{
    		 fields.addAll(getWebApprovalFormFields());
    	}
		return fields;
    	
    }
	@SuppressWarnings("unchecked") // https://stackoverflow.com/a/11205178
	public static Map<String, Set<FacilioForm>> getAllForms(FormType formtype) {
		return (Map<String, Set<FacilioForm>>) (Map<?, ?>) Multimaps.asMap(ALL_FORMS.get(formtype));
	}
	
	private static Map<FormType, Multimap<String, FacilioForm>> initAllForms() {
		return ImmutableMap.<FormType, Multimap<String, FacilioForm>>builder()
				.put(FormType.MOBILE, ImmutableMultimap.<String, FacilioForm>builder()
						.put(FacilioConstants.ContextNames.WORK_ORDER, getMobileWorkOrderForm())
						.put(FacilioConstants.ContextNames.APPROVAL, getMobileApprovalForm())
						.put(FacilioConstants.ContextNames.ASSET, getMobileAssetForm()).build())
				.put(FormType.WEB, ImmutableMultimap.<String, FacilioForm>builder()
						.put(FacilioConstants.ContextNames.WORK_ORDER, getWebWorkOrderForm())
						.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, getWebWorkRequestForm())
						.put(FacilioConstants.ContextNames.ASSET, getAssetForm())
						.put(FacilioConstants.ContextNames.INVENTORY, getInventoryForm()).build())
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
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
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
	
	public static FacilioForm getMobileAssetForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Asset");
		form.setName("mobile_asset");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ASSET));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getMobileAssetFormFields());
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
		return Collections.unmodifiableList(fields);
	}
	
	private static List<FormField> getMobileAssetFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("type", FieldDisplayType.LOOKUP_SIMPLE, "Type", Required.OPTIONAL,"assettype", 2, 1));
		fields.add(new FormField("space", FieldDisplayType.SPACECHOOSER, "Asset Location", Required.OPTIONAL, 3, 1 ));
		fields.add(new FormField("department", FieldDisplayType.LOOKUP_SIMPLE, "Department", Required.OPTIONAL,"assetdepartment", 4, 1));
		fields.add(new FormField("manufacturer", FieldDisplayType.TEXTBOX, "Manufacturer", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("model", FieldDisplayType.TEXTBOX, "Model", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("serialNumber", FieldDisplayType.TEXTBOX, "Serial Number", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("tagNumber", FieldDisplayType.TEXTBOX, "Tag", Required.OPTIONAL, 8, 1));
		fields.add(new FormField("partNumber", FieldDisplayType.TEXTBOX, "Part No.", Required.OPTIONAL, 9, 1));
		fields.add(new FormField("unitPrice", FieldDisplayType.NUMBER, "Unit Price", Required.OPTIONAL, 10, 1));
		fields.add(new FormField("supplier", FieldDisplayType.TEXTBOX, "Supplier", Required.OPTIONAL, 11, 1));
		fields.add(new FormField("purchasedDate", FieldDisplayType.DATE, "Purchased Date", Required.OPTIONAL, 12, 1));
		fields.add(new FormField("retireDate", FieldDisplayType.DATE, "Retire Date", Required.OPTIONAL, 13, 1));
		fields.add(new FormField("warrantyExpiryDate", FieldDisplayType.DATE, "Warranty Expiry Date", Required.OPTIONAL, 14, 1));
		return Collections.unmodifiableList(fields);
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
		return Collections.unmodifiableList(fields);
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
	
	private static FacilioForm getAssetForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Asset");
		form.setName("asset");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ASSET));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFormType(FormType.WEB);
		form.setFields(getWebAssetFormFields());
		return form;
	}
	private static FacilioForm getPMForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PREVENTIVE MAINTENANCE");
		form.setName("web_pm");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFormType(FormType.WEB);
		form.setFields(getWebPMFormFields());
		return form;
	}
	
	public static FacilioForm getInventoryForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("INVENTORY");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.INVENTORY));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getInventoryFormField());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getStoreRoomForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW STORE ROOM");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.STORE_ROOM));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getStoreRoomFormField());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getItemTypesForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW ITEM TYPE");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ITEM_TYPES));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getItemTypesFormField());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getItemCategoryForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW CATEGORY");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ITEM_TYPES_CATEGORY));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getItemCategoryFormField());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getTooltypesForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW TOOL");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TOOL_TYPES));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getToolTypesFormField());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getVendorsForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW VENDOR");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TOOL_TYPES));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVendorsFormField());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getItemForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW ITEM");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ITEM));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getItemFormField());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getItemWithIndTrackForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW ITEM");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ITEM));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getItemWithIndTrackFormField());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getStockedToolsForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW TOOL");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TOOL));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getToolFormField());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getToolWithIndTrackForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW TOOL");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TOOL));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getToolTrackWithIndFormField());
		form.setFormType(FormType.WEB);
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
		fields.add(new FormField("tasks", FieldDisplayType.TASKS, "TASKS", Required.OPTIONAL, 9, 1));
		return Collections.unmodifiableList(fields);
	}

	private static List<FormField> getMobileWorkOrderFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.REQUIRED, 2, 1));
		fields.add(new FormField("site", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 1));
		fields.add(new FormField("resource", FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.REQUIRED, 4, 1));
		fields.add(new FormField("assignment", FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.REQUIRED, 5, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "ticketcategory", 6, 1));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 7, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 8, 1));
		return Collections.unmodifiableList(fields);
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
		return Collections.unmodifiableList(fields);
	}
	
	private static List<FormField> getWebAssetFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, "name", 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "assetCategory", 3, 1));
		fields.add(new FormField("department", FieldDisplayType.LOOKUP_SIMPLE, "Department", Required.OPTIONAL,"assetdepartment", 3, 2));
		fields.add(new FormField("type", FieldDisplayType.LOOKUP_SIMPLE, "Type", Required.OPTIONAL,"assettype", 4, 2));
		fields.add(new FormField("manufacturer", FieldDisplayType.TEXTBOX, "Manufacturer", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("supplier", FieldDisplayType.TEXTBOX, "Supplier", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("model", FieldDisplayType.TEXTBOX, "Model", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("serialNumber", FieldDisplayType.TEXTBOX, "Serial Number", Required.OPTIONAL, 6, 2));
		fields.add(new FormField("tagNumber", FieldDisplayType.TEXTBOX, "Tag", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("partNumber", FieldDisplayType.TEXTBOX, "Part No.", Required.OPTIONAL, 7, 2));
		fields.add(new FormField("purchasedDate", FieldDisplayType.DATE, "Purchased Date", Required.OPTIONAL, 8, 1));
		fields.add(new FormField("retireDate", FieldDisplayType.DATE, "Retire Date", Required.OPTIONAL, 8, 2));
		fields.add(new FormField("unitPrice", FieldDisplayType.TEXTBOX, "Unit Price", Required.OPTIONAL, 9, 1));
		fields.add(new FormField("warrantyExpiryDate", FieldDisplayType.DATE, "Warranty Expiry Date", Required.OPTIONAL, 9, 2));
		fields.add(new FormField("qrVal", FieldDisplayType.TEXTBOX, "QR Value", Required.OPTIONAL, 10, 1));
		return Collections.unmodifiableList(fields);
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
		return Collections.unmodifiableList(fields);
	}

	private static List<FormField> getLoggedInServiceWorkRequestFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.OPTIONAL, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("urgency", FieldDisplayType.URGENCY, "Urgency", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 4, 1));
		return Collections.unmodifiableList(fields);
	}
	
	private static List<FormField> getServiceWorkRequestFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("requester", FieldDisplayType.REQUESTER, "Requester", Required.REQUIRED, 1, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site" ,2, 1));
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("urgency", FieldDisplayType.URGENCY, "Urgency", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 6, 1));
		return Collections.unmodifiableList(fields);
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
		return Collections.unmodifiableList(fields);
	}
	
	private static List<FormField> getInventoryFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "inventoryCategory", 4, 1));
		fields.add(new FormField("unitcost", FieldDisplayType.TEXTBOX, "Unit Cost", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("serialNumber", FieldDisplayType.TEXTBOX, "Serial Numver", Required.OPTIONAL, 6,1));
		fields.add(new FormField("quantity",FieldDisplayType.TEXTBOX,"Quantity", Required.OPTIONAL, "tickettype", 7, 1));
		fields.add(new FormField("minimumQuantity", FieldDisplayType.TEXTBOX, "Minimum Quantity", Required.OPTIONAL, 8, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL,"inventory_vendors", 9, 1));
		fields.add(new FormField("space", FieldDisplayType.WOASSETSPACECHOOSER, "Location", Required.OPTIONAL, 10, 1));
		fields.add(new FormField("qrVal", FieldDisplayType.TEXTBOX, "QR VALUE", Required.OPTIONAL, 11, 1));
		return Collections.unmodifiableList(fields);
	}
	
	private static List<FormField> getStoreRoomFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("location", FieldDisplayType.LOOKUP_SIMPLE, "Location", Required.OPTIONAL, "location", 3, 1));
//		fields.add(new FormField("location", FieldDisplayType.LOOKUP_SIMPLE, "Location", Required.OPTIONAL, "location", 3, 1).setAllowCreate(true).setCreateFormName("location_form"));
		fields.add(new FormField("owner", FieldDisplayType.USER, "Owner", Required.OPTIONAL, 4, 1));
		return fields;
	}
	
	private static List<FormField> getItemTypesFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("photoId", FieldDisplayType.IMAGE, "IMAGE", Required.OPTIONAL, 1, 1));	
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "itemTypesCategory", 3, 1).setAllowCreate(true).setCreateFormName("item_category_form"));
		fields.add(new FormField("status", FieldDisplayType.LOOKUP_SIMPLE, "Status", Required.OPTIONAL, "itemTypesStatus", 6, 1));
		fields.add(new FormField("minimumQuantity", FieldDisplayType.TEXTBOX, "Minimum Quantity", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("individualTracking", FieldDisplayType.DECISION_BOX, "Individual Tracking", Required.OPTIONAL, 8, 1));
		fields.add(new FormField("unit", FieldDisplayType.UNIT, "Unit", Required.OPTIONAL, 5,1));
		return fields;
	}
	
	private static List<FormField> getItemCategoryFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("displayName", FieldDisplayType.TEXTBOX, "Display Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 4, 1));
		return fields;
	}
	
	private static List<FormField> getToolTypesFormField() {
		List<FormField> fields = new ArrayList<>();
		
		fields.add(new FormField("photoId", FieldDisplayType.IMAGE, "IMAGE", Required.OPTIONAL, 1, 1));	
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "toolTypesCategory", 3, 1).setAllowCreate(true).setCreateFormName("item_category_form"));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("unit", FieldDisplayType.UNIT, "Unit", Required.OPTIONAL, 5,1));
		fields.add(new FormField("minimumQuantity", FieldDisplayType.TEXTBOX, "Minimum Quantity", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("status", FieldDisplayType.LOOKUP_SIMPLE, "Status", Required.OPTIONAL, "toolTypesStatus", 7, 1));
//		fields.add(new FormField("serialNumber", FieldDisplayType.TEXTBOX, "Serial Number", Required.OPTIONAL, 5,1));
		fields.add(new FormField("individualTracking", FieldDisplayType.DECISION_BOX, "Individual Tracking", Required.OPTIONAL, 8, 1));	

		return fields;
	}
	
	private static List<FormField> getVendorsFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "E-mail", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("website", FieldDisplayType.TEXTBOX, "Website", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("address", FieldDisplayType.ADDRESS, "Address", Required.OPTIONAL, 6, 1));
		
		return fields;
	}

	private static List<FormField> getItemFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("itemType", FieldDisplayType.LOOKUP_SIMPLE, "Item Type", Required.REQUIRED, "itemTypes", 1, 1));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Store Room", Required.REQUIRED, "storeRoom", 2, 1).setAllowCreate(true).setCreateFormName("store_room_form"));
//		fields.add(new FormField("site", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 2, 2));
		fields.add(new FormField("costType", FieldDisplayType.TEXTBOX, "Cost Type", Required.OPTIONAL, 3, 1));
//		fields.add(new FormField("issuingUnit", FieldDisplayType.UNIT, "Issuing Unit", Required.OPTIONAL, 3, 2));
		fields.add(new FormField("status", FieldDisplayType.LOOKUP_SIMPLE, "Status", Required.OPTIONAL, "itemStatus", 4, 1));
		fields.add(new FormField("purchasedItems", FieldDisplayType.PURCHASEDITEM, "Purchased Item", Required.OPTIONAL, 5, 1));
		
		return fields;
	}
	
	private static List<FormField> getItemWithIndTrackFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("itemType", FieldDisplayType.LOOKUP_SIMPLE, "Item Type", Required.REQUIRED, "itemTypes", 1, 1));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Store Room", Required.REQUIRED, "storeRoom", 2, 1));
		fields.add(new FormField("status", FieldDisplayType.LOOKUP_SIMPLE, "Status", Required.OPTIONAL, "itemStatus", 3, 1));
		fields.add(new FormField("purchasedItems", FieldDisplayType.PURCHASEDITEMT, "Purchased Item", Required.OPTIONAL, 4, 1));
		
		return fields;
	}
	
	private static List<FormField> getToolFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("toolType", FieldDisplayType.LOOKUP_SIMPLE, "Tool Type", Required.REQUIRED, "toolTypes", 1, 1));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Store Room", Required.REQUIRED, "storeRoom", 2, 1).setAllowCreate(true).setCreateFormName("store_room_form"));
		fields.add(new FormField("status", FieldDisplayType.LOOKUP_SIMPLE, "Status", Required.OPTIONAL, "toolStatus", 3, 1));
		fields.add(new FormField("quantity", FieldDisplayType.DECIMAL, "Quantity", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("rate", FieldDisplayType.DECIMAL, "Rate", Required.OPTIONAL, 6, 1));
		
		return fields;
	}
	
	private static List<FormField> getToolTrackWithIndFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("toolType", FieldDisplayType.LOOKUP_SIMPLE, "Tool Type", Required.REQUIRED, "toolTypes", 1, 1));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Store Room", Required.REQUIRED, "storeRoom", 2, 1));
		fields.add(new FormField("status", FieldDisplayType.LOOKUP_SIMPLE, "Status", Required.OPTIONAL, "toolStatus", 3, 1));
		fields.add(new FormField("purchasedTools", FieldDisplayType.PURCHASEDTOOL, "Purchased Tool", Required.OPTIONAL, 4, 1));
		
		return fields;
	}
}
