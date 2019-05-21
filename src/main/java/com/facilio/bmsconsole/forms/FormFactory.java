package com.facilio.bmsconsole.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkOrderContext.WOUrgency;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class FormFactory {
	
	private static final Map<String, FacilioForm> FORM_MAP = Collections.unmodifiableMap(initMap());
	private static final Map<FormType, Multimap<String, FacilioForm>> ALL_FORMS = Collections.unmodifiableMap(initAllForms());
	private static final Map<String, Map<String, FacilioForm>> FORMS_LIST = Collections.unmodifiableMap(initFormsList());

	// TODO remove...use FORMS_LIST to get details
	private static Map<String, FacilioForm> initMap() {
		Map<String, FacilioForm> forms = new HashMap<>();
		forms.put("workOrderForm", getWebWorkOrderForm());
		forms.put("serviceWorkOrder", getServiceWorkOrderForm());
		forms.put("web_pm", getPMForm());
		forms.put("approvalForm", getApprovalForm());
		forms.put("default_asset", getAssetForm());
		forms.put("item_form", getItemForm());
		forms.put("item_track_form", getItemWithIndTrackForm());
		forms.put("store_room_form", getStoreRoomForm());
		forms.put("item_types_form", getItemTypesForm());
		forms.put("tool_types_form", getTooltypesForm());
		forms.put("vendors_form", getVendorsForm());
		forms.put("tool_form", getStockedToolsForm());
		forms.put("tool_track_form", getToolWithIndTrackForm());
		forms.put("item_category_form", getInventoryCategoryForm());
		forms.put("location_form", getLocationForm());
		forms.put("tenantForm", getTenantsForm());
		forms.put("labourForm", getLabourForm());
		forms.put("purchaseRequestForm", getPurchaseRequestForm());
		forms.put("purchaseOrderForm", getPurchaseOrderForm());
		forms.put("purchaseContractForm", getPurchaseContractForm());
		forms.put("labourContractForm", getLabourContractForm());
		forms.put("porequesterForm", getPoRequesterForm());
		forms.put("connectedAppForm", getConnectedAppForm());
		forms.put("inventoryRequestForm", getInventoryRequestForm());
		forms.put("inventoryRequestWOForm", getInventoryRequestWorkOrderForm());
			
		return forms;
	}
    
	@SuppressWarnings("unchecked") // https://stackoverflow.com/a/11205178
	public static Map<String, Set<FacilioForm>> getAllForms(FormType formtype) {
		return (Map<String, Set<FacilioForm>>) (Map<?, ?>) Multimaps.asMap(ALL_FORMS.get(formtype));
	}
	
	// TODO remove...use FORMS_LIST to get details
	private static Map<FormType, Multimap<String, FacilioForm>> initAllForms() {
		return ImmutableMap.<FormType, Multimap<String, FacilioForm>>builder()
				.put(FormType.MOBILE, ImmutableMultimap.<String, FacilioForm>builder()
						.put(FacilioConstants.ContextNames.WORK_ORDER, getMobileWorkOrderForm())
						.put(FacilioConstants.ContextNames.APPROVAL, getMobileApprovalForm())
						.put(FacilioConstants.ContextNames.ASSET, getMobileAssetForm()).build())
				.put(FormType.WEB, ImmutableMultimap.<String, FacilioForm>builder()
						.put(FacilioConstants.ContextNames.WORK_ORDER, getWebWorkOrderForm())
						.put(FacilioConstants.ContextNames.ASSET, getAssetForm())
						.put(FacilioConstants.ContextNames.TENANT, getTenantsForm())
						.put(FacilioConstants.ContextNames.PURCHASE_REQUEST, getPurchaseRequestForm())
						.put(FacilioConstants.ContextNames.PURCHASE_ORDER, getPurchaseOrderForm())
						.put(FacilioConstants.ContextNames.LABOUR, getLabourForm())
						.put(FacilioConstants.ContextNames.PURCHASE_CONTRACTS, getPurchaseContractForm())
						.put(FacilioConstants.ContextNames.LABOUR_CONTRACTS, getLabourContractForm())
						.put(FacilioConstants.ContextNames.INVENTORY_REQUEST, getInventoryRequestForm())
						.build())
        			
				.build();
	}
	
	public static Map<String, FacilioForm> getForms(String moduleName, FormType formtype) {
		Map<String, FacilioForm> forms = getForms(moduleName);
		if (formtype == null) {
			return forms;
		}
		return forms.entrySet().stream().filter(f -> f.getValue().getFormTypeEnum() == formtype)
	            .collect(Collectors.toMap(f -> f.getKey(), f -> f.getValue()));
	}
	
	public static Map<String, FacilioForm> getForms(String moduleName) {
		return FORMS_LIST.get(moduleName);
	}
	
	public static FacilioForm getDefaultForm(String moduleName, FacilioForm form, Boolean...onlyFields) {
		return getForm(moduleName, "default_"+moduleName+"_"+form.getFormTypeVal(), onlyFields);
	}
	
	public static FacilioForm getForm(String moduleName, String formName, Boolean...onlyFields) {
		FacilioForm form = getForms(moduleName).get(formName);
		if (onlyFields == null || onlyFields.length == 0 || !onlyFields[0]) {
			form = new FacilioForm(form);
			// TODO add sections in formfactory initialization itself once all client supports
			if (moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
				List<FormSection> sections = new ArrayList<>();
				form.setSections(sections);
				int i = 1;
				List<FormField> defaultFields = new ArrayList<>();
				List<FormField> taskFields = new ArrayList<>();
				
				FormSection defaultSection = new FormSection("WORKORDER", i++, defaultFields, true);
				if (form.getFormTypeEnum() == FormType.PORTAL) {
					defaultSection.setShowLabel(false);
				}
				sections.add(defaultSection);
				 form.getFields().forEach(field -> {
					 if (field.getDisplayTypeEnum() == FieldDisplayType.TASKS) {
						 taskFields.add(field);
					 }
					 else {
						 defaultFields.add(field);
					 }
				 });
				
//				List<FormField> task = form.getFields().stream().filter(field -> field.getDisplayTypeEnum() == FieldDisplayType.TASKS).collect(Collectors.toList());
				if (form.getFormTypeEnum() != FormType.PORTAL) {
					FormSection taskSection = new FormSection("TASKS", i++, taskFields, true);
					sections.add(taskSection);
				}
				
//				form.setFields(null);
			}
		}
		return form;
	}
	
	private static Map<String, Map<String, FacilioForm>>  initFormsList() {
		List<FacilioForm> woForms = Arrays.asList(getWebWorkOrderForm(), getServiceWorkOrderForm());
		List<FacilioForm> assetForms = Arrays.asList(getAssetForm());
		
		return ImmutableMap.<String, Map<String, FacilioForm>>builder()
				.put(FacilioConstants.ContextNames.WORK_ORDER, getFormMap(woForms))
				.put(FacilioConstants.ContextNames.ASSET, getFormMap(assetForms))
				.build();
	}
	
	private static Map<String, FacilioForm> getFormMap (List<FacilioForm> forms) {
		Builder<String, FacilioForm> formBuilder = ImmutableMap.<String, FacilioForm>builder();
		for(FacilioForm form: forms) {
			formBuilder.put(form.getName(), form);
		}
		return formBuilder.build();
	}

	public static FacilioForm getForm(String name) {
		return FormFactory.FORM_MAP.get(name);
	}
	
	public static FacilioForm getServiceWorkOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Default Request");
		form.setName("default_workorder_portal");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getServiceWorkOrderFormFields());
		form.setFormType(FormType.PORTAL);
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
			fields.addAll(FormsAPI.getFormFieldsFromFacilioFields(facilioFields, i));
		}
		return Collections.unmodifiableList(fields);
	}

	public static FacilioForm getWebWorkOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Workorder");
		form.setName("default_workorder_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getWebWorkOrderFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static FacilioForm getAssetForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Asset");
		form.setName("asset");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ASSET));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFormType(FormType.WEB);
		form.setFields(getWebAssetFormFields());
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
	
	public static FacilioForm getInventoryCategoryForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW CATEGORY");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.INVENTORY_CATEGORY));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getInventoryCategoryFormField());
		form.setFormType(FormType.WEB);
		return form;
	}

	
	public static FacilioForm getLocationForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW LOCATION");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.LOCATION));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getLocationFormField());
		form.setFormType(FormType.WEB);
		return form;
	}

	public static FacilioForm getTooltypesForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW TOOL TYPE");
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


	public static FacilioForm getTenantsForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("TENANT");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TENANT));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getTenantsFormField());
		form.setFormType(FormType.WEB);
		return form;
	}

    public static FacilioForm getItemForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("ADD ITEM");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ITEM));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getItemFormField());
		form.setFormType(FormType.WEB);
		return form;
	}

	public static FacilioForm getItemWithIndTrackForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("ADD ITEM");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ITEM));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getItemWithIndTrackFormField());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getStockedToolsForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("ADD TOOL");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TOOL));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getToolFormField());
		form.setFormType(FormType.WEB);
		return form;
	}

	public static FacilioForm getToolWithIndTrackForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("ADD TOOL");
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
		fields.add(new FormField("type",FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", Required.OPTIONAL, "tickettype", 4, 3));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 5, 1));
		fields.add(new FormField("resource", FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("assignment", FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, "attachment", 8, 1));
		fields.add(new FormField("sendForApproval", FieldDisplayType.DECISION_BOX, "Send For Approval", Required.OPTIONAL, 10, 1));
		fields.add(new FormField("tasks", FieldDisplayType.TASKS, "TASKS", Required.OPTIONAL, 11, 1));
		return Collections.unmodifiableList(fields);
	}

	private static List<FormField> getMobileWorkOrderFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("site", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 1));
		fields.add(new FormField("resource", FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("assignment", FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "ticketcategory", 6, 1));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 7, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 8, 1));
		fields.add(new FormField("sendForApproval", FieldDisplayType.DECISION_BOX, "Send For Approval", Required.OPTIONAL, 9, 1));
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
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 2));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.REQUIRED, "assetcategory", 4, 2));
		fields.add(new FormField("department", FieldDisplayType.LOOKUP_SIMPLE, "Department", Required.OPTIONAL,"assetdepartment", 4, 3));
		fields.add(new FormField("space", FieldDisplayType.SPACECHOOSER, "Asset Location", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("type", FieldDisplayType.LOOKUP_SIMPLE, "Type", Required.OPTIONAL,"assettype", 5, 3));
		fields.add(new FormField("manufacturer", FieldDisplayType.TEXTBOX, "Manufacturer", Required.OPTIONAL, 6, 2));
		fields.add(new FormField("supplier", FieldDisplayType.TEXTBOX, "Supplier", Required.OPTIONAL, 6, 3));
		fields.add(new FormField("model", FieldDisplayType.TEXTBOX, "Model", Required.OPTIONAL, 7, 2));
		fields.add(new FormField("serialNumber", FieldDisplayType.TEXTBOX, "Serial Number", Required.OPTIONAL, 7, 3));
		fields.add(new FormField("tagNumber", FieldDisplayType.TEXTBOX, "Tag", Required.OPTIONAL, 8, 2));
		fields.add(new FormField("partNumber", FieldDisplayType.TEXTBOX, "Part No.", Required.OPTIONAL, 8, 3));
		fields.add(new FormField("purchasedDate", FieldDisplayType.DATETIME, "Purchased Date", Required.OPTIONAL, 9, 2));
		fields.add(new FormField("retireDate", FieldDisplayType.DATETIME, "Retire Date", Required.OPTIONAL, 9, 3));
		fields.add(new FormField("unitPrice", FieldDisplayType.NUMBER, "Unit Price", Required.OPTIONAL, 10, 2));
		fields.add(new FormField("warrantyExpiryDate", FieldDisplayType.DATETIME, "Warranty Expiry Date", Required.OPTIONAL, 10, 3));
		fields.add(new FormField("qrVal", FieldDisplayType.TEXTBOX, "QR Value", Required.OPTIONAL, 11, 2));
		// new fields
		fields.add(new FormField("rotatingItem", FieldDisplayType.LOOKUP_SIMPLE, "Rotating Item",Required.OPTIONAL, "item", 12,2));
		fields.add(new FormField("rotatingTool", FieldDisplayType.LOOKUP_SIMPLE, "Rotating Tool",Required.OPTIONAL, "tool", 12,3));
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
	
	private static List<FormField> getServiceWorkOrderFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site" ,2, 1));
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 3, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 4, 1));
		FormField urgency = new FormField("urgency", FieldDisplayType.URGENCY, "Urgency", Required.OPTIONAL, 5, 1);
		urgency.setValue(WOUrgency.NOTURGENT.getValue());
		fields.add(urgency);
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 6, 1));
		return Collections.unmodifiableList(fields);
	}
	
	public static List<FormField> getRequesterFormFields(boolean fetchBoth) throws Exception {
		List<FormField> fields = new ArrayList<>();
		if (AccountUtil.getCurrentAccount().isFromMobile() || fetchBoth) {
			fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Requester Name", Required.REQUIRED, 1, 1));
			fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Requester Email", Required.REQUIRED, 2, 1));
		}
		else {
			fields.add(new FormField("requester", FieldDisplayType.REQUESTER, "Requester", Required.REQUIRED, 1, 1));
		}
		return fields;
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
		fields.add(new FormField("site", FieldDisplayType.LOOKUP_SIMPLE, "Located Site", Required.REQUIRED, "site", 3, 2));
		fields.add(new FormField("location", FieldDisplayType.LOOKUP_SIMPLE, "Location", Required.OPTIONAL, "location", 3, 3).setAllowCreate(true).setCreateFormName("location_form"));
		fields.add(new FormField("owner", FieldDisplayType.USER, "Owner", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("sites", FieldDisplayType.SITEMULTICHOOSER, "Sites", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("isApprovalNeeded", FieldDisplayType.DECISION_BOX, "Approval Needed", Required.OPTIONAL, 6, 2));
		fields.add(new FormField("isGatePassRequired", FieldDisplayType.DECISION_BOX, "Gate Pass Needed", Required.OPTIONAL, 6, 3));

		return fields;
	}
	
	private static List<FormField> getItemTypesFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("photoId", FieldDisplayType.IMAGE, "Photo", Required.OPTIONAL, 1, 1));
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "inventoryCategory", 4, 2).setAllowCreate(true).setCreateFormName("item_category_form"));
		fields.add(new FormField("minimumQuantity", FieldDisplayType.TEXTBOX, "Minimum Quantity", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("isRotating", FieldDisplayType.DECISION_BOX, "Is Rotating", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("isApprovalNeeded", FieldDisplayType.DECISION_BOX, "Approval Needed", Required.OPTIONAL, 5, 3));
		fields.add(new FormField("isConsumable", FieldDisplayType.DECISION_BOX, "Is Consumable", Required.OPTIONAL, 6, 2));
		return fields;
	}

	private static List<FormField> getInventoryCategoryFormField() {
		List<FormField> fields = new ArrayList<>();
//		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.OPTIONAL, 1, 1));
		fields.add(new FormField("displayName", FieldDisplayType.TEXTBOX, "Display Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		return fields;
	}

	private static List<FormField> getLocationFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("street", FieldDisplayType.TEXTBOX, "Street", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("city", FieldDisplayType.TEXTBOX, "City", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("state", FieldDisplayType.TEXTBOX, "State / Province", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("zip", FieldDisplayType.TEXTBOX, "Zip / Postal Code", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("country", FieldDisplayType.SELECTBOX, "Country", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("lat", FieldDisplayType.DECIMAL, "Latitude", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("lng", FieldDisplayType.DECIMAL, "Longitude", Required.OPTIONAL, 8, 1));
		return fields;
	}
	
	private static List<FormField> getToolTypesFormField() {
		List<FormField> fields = new ArrayList<>();
		
		fields.add(new FormField("photoId", FieldDisplayType.IMAGE, "Photo", Required.OPTIONAL, 1, 1));
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "inventoryCategory", 3, 1).setAllowCreate(true).setCreateFormName("item_category_form"));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("isRotating", FieldDisplayType.DECISION_BOX, "Is Rotating", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("isApprovalNeeded", FieldDisplayType.DECISION_BOX, "Approval Needed", Required.OPTIONAL, 5, 3));

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

	private static List<FormField> getTenantsFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 4, 1));
		fields.add(new FormField("spaces", FieldDisplayType.SPACEMULTICHOOSER, "Spaces", Required.REQUIRED, 5, 1));
		fields.add(new FormField("contact_name", FieldDisplayType.TEXTBOX, "Contact Name", Required.REQUIRED, 6, 1));
		fields.add(new FormField("contact_email", FieldDisplayType.TEXTBOX, "Contact Email", Required.REQUIRED, 7, 1));
		fields.add(new FormField("inTime", FieldDisplayType.DATE, "Lease Start Time", Required.OPTIONAL, 8, 1));
		fields.add(new FormField("outTime", FieldDisplayType.DATE, "Lease End Time", Required.OPTIONAL, 9, 1));
		fields.add(new FormField("logo", FieldDisplayType.LOGO, "Logo", Required.OPTIONAL, 1, 1));
		fields.add(new FormField("utilityMeters", FieldDisplayType.ASSETMULTICHOOSER, "UTILITY METERS", Required.OPTIONAL, 10, 1));

		return fields;
	}

	private static List<FormField> getItemFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("itemType", FieldDisplayType.LOOKUP_SIMPLE, "Item Type", Required.REQUIRED, "itemTypes", 1, 2));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", Required.REQUIRED, "storeRoom", 1, 3));
		fields.add(new FormField("minimumQuantity", FieldDisplayType.TEXTBOX, "Minimum Quantity", Required.OPTIONAL, 2, 2));
//		fields.add(new FormField("status", FieldDisplayType.LOOKUP_SIMPLE, "Status", Required.OPTIONAL, "itemStatus", 2, 3));
		fields.add(new FormField("costType", FieldDisplayType.SELECTBOX, "Cost Type", Required.OPTIONAL, 2, 3));
		fields.add(new FormField("purchasedItems", FieldDisplayType.PURCHASEDITEM, "Purchased Item", Required.OPTIONAL, 3, 1));

		return fields;
	}

	private static List<FormField> getItemWithIndTrackFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("itemType", FieldDisplayType.LOOKUP_SIMPLE, "Item Type", Required.REQUIRED, "itemTypes", 1, 1));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Store Room", Required.REQUIRED, "storeRoom", 2, 1));
		fields.add(new FormField("minimumQuantity", FieldDisplayType.TEXTBOX, "Minimum Quantity", Required.OPTIONAL, 3, 1));
//		fields.add(new FormField("status", FieldDisplayType.LOOKUP_SIMPLE, "Status", Required.OPTIONAL, "itemStatus", 3, 3));
//		fields.add(new FormField("purchasedItems", FieldDisplayType.PURCHASEDITEMT, "Purchased Item", Required.OPTIONAL, 4, 1));

		return fields;
	}

	private static List<FormField> getToolFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("toolType", FieldDisplayType.LOOKUP_SIMPLE, "Tool Type", Required.REQUIRED, "toolTypes", 1, 1));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Store Room", Required.REQUIRED, "storeRoom", 2, 1).setAllowCreate(true).setCreateFormName("store_room_form"));
//		fields.add(new FormField("status", FieldDisplayType.LOOKUP_SIMPLE, "Status", Required.OPTIONAL, "toolStatus", 3, 1));
		fields.add(new FormField("quantity", FieldDisplayType.DECIMAL, "Quantity", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("rate", FieldDisplayType.DECIMAL, "Rate/Hour", Required.OPTIONAL, 6, 1));
		
		return fields;
	}

	private static List<FormField> getToolTrackWithIndFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("toolType", FieldDisplayType.LOOKUP_SIMPLE, "Tool Type", Required.REQUIRED, "toolTypes", 1, 1));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", Required.REQUIRED, "storeRoom", 2, 1));
		fields.add(new FormField("rate", FieldDisplayType.DECIMAL, "Rate/Hour", Required.REQUIRED, 3, 1));
//		fields.add(new FormField("status", FieldDisplayType.LOOKUP_SIMPLE, "Status", Required.OPTIONAL, "toolStatus", 3, 1));
//		fields.add(new FormField("purchasedTools", FieldDisplayType.PURCHASEDTOOL, "Purchased Tool", Required.OPTIONAL, 4, 1));

		return fields;
	}
	
	public static FacilioForm getLabourForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("LABOUR");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.LABOUR));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getLabourFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getPurchaseRequestForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PURCHASE REQUEST");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getPurchaseRequestFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	public static FacilioForm getPurchaseOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PURCHASE ORDER");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getPurchaseOrderFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getPurchaseContractForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PURCHASE CONTRACT");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACTS));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getPurchaseContractFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	public static FacilioForm getLabourContractForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("LABOUR CONTRACT");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.LABOUR_CONTRACTS));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getLabourContractFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	public static FacilioForm getPoRequesterForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("REQUESTER");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.REQUESTER));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPoRequesterFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getConnectedAppForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("CONNECTED APP");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.CONNECTED_APPS));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getConnectedAppFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static List<FormField> getLabourFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.REQUIRED, 3, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 4, 1));
		//fields.add(new FormField("address", FieldDisplayType.TEXTAREA, "Address", Required.OPTIONAL,5, 1));
		fields.add(new FormField("location", FieldDisplayType.ADDRESS, "Address", Required.OPTIONAL,5, 1));
		fields.add(new FormField("user", FieldDisplayType.USER, "User", Required.OPTIONAL, 6, 1));
		//fields.add(new FormField("unitType", FieldDisplayType.SELECTBOX, "Wage Type", Required.REQUIRED, 7, 1));
		fields.add(new FormField("cost", FieldDisplayType.DECIMAL, "Rate Per Hour", Required.REQUIRED, 8, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 2, 1));
		fields.add(new FormField("availability", FieldDisplayType.DECISION_BOX, "Active", Required.REQUIRED, 9, 1));
		
		return fields;
	}

	private static List<FormField> getPurchaseRequestFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, "vendors", 3, 2).setAllowCreate(true).setCreateFormName("vendors_form"));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", Required.OPTIONAL, "storeRoom", 3, 3));
		fields.add(new FormField("requestedTime", FieldDisplayType.DATE, "Requested Date", Required.OPTIONAL, 4, 2));
		fields.add(new FormField("requiredTime", FieldDisplayType.DATE, "Required Date", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("requestedBy", FieldDisplayType.LOOKUP_SIMPLE, "Requested By", Required.OPTIONAL, "requester", 5, 1).setAllowCreate(true).setCreateFormName("porequesterForm"));
		fields.add(new FormField("billToAddress", FieldDisplayType.SADDRESS, "BILLING ADDRESS", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("shipToAddress", FieldDisplayType.SADDRESS, "SHIPPING ADDRESS", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("lineItems", FieldDisplayType.LINEITEMS, "LINE ITEMS", Required.REQUIRED, 8, 1));
		//	fields.add(new FormField("status", FieldDisplayType.SELECTBOX, "Status", Required.REQUIRED, 7, 1));
		//fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 2, 1));
		return fields;
	}
	
	private static List<FormField> getPurchaseOrderFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED, "vendors", 3, 2).setAllowCreate(true).setCreateFormName("vendors_form"));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", Required.REQUIRED, "storeRoom", 3, 3));
		fields.add(new FormField("orderedTime", FieldDisplayType.DATE, "Ordered Date", Required.OPTIONAL, 4, 2));
		fields.add(new FormField("requiredTime", FieldDisplayType.DATE, "Required Date", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("requestedBy", FieldDisplayType.LOOKUP_SIMPLE, "Requested By", Required.OPTIONAL, "requester", 5, 1).setAllowCreate(true).setCreateFormName("porequesterForm"));
		fields.add(new FormField("lineItems", FieldDisplayType.LINEITEMS, "LINE ITEMS", Required.REQUIRED, 8, 1));
		fields.add(new FormField("billToAddress", FieldDisplayType.SADDRESS, "BILLING ADDRESS", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("shipToAddress", FieldDisplayType.SADDRESS, "SHIPPING ADDRESS", Required.OPTIONAL, 7, 1));
		//	fields.add(new FormField("status", FieldDisplayType.SELECTBOX, "Status", Required.REQUIRED, 7, 1));
		//fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 2, 1));
		return fields;
	}
	
	private static List<FormField> getPurchaseContractFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED, "vendors", 3, 2).setAllowCreate(true).setCreateFormName("vendors_form"));
		fields.add(new FormField("fromDate", FieldDisplayType.DATE, "From Date", Required.OPTIONAL, 4, 2));
		fields.add(new FormField("endDate", FieldDisplayType.DATE, "End Date", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("renewalDate", FieldDisplayType.DATE, "Renewal Date", Required.OPTIONAL, 3, 3));
//		fields.add(new FormField("totalCost", FieldDisplayType.DECIMAL, "Total Cost", Required.OPTIONAL, 5, 3));
		fields.add(new FormField("lineItems", FieldDisplayType.LINEITEMS, "LINE ITEMS", Required.REQUIRED, 5, 1));
		
		return fields;
	}
	
	private static List<FormField> getLabourContractFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED, "vendors", 3, 1).setAllowCreate(true).setCreateFormName("vendors_form"));
		fields.add(new FormField("fromDate", FieldDisplayType.DATE, "From Date", Required.OPTIONAL, 4, 2));
		fields.add(new FormField("endDate", FieldDisplayType.DATE, "End Date", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("totalCost", FieldDisplayType.DECIMAL, "Total Cost", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("renewalDate", FieldDisplayType.DATE, "Renewal Date", Required.OPTIONAL, 5, 3));
		fields.add(new FormField("lineItems", FieldDisplayType.LABOUR_LINE_ITEMS, "LABOUR RECORDS", Required.REQUIRED, 6, 1));
		
		return fields;
	}
	
	private static List<FormField> getPoRequesterFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Requester Name", Required.REQUIRED, 1, 2));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Requester Email", Required.REQUIRED, 2, 2));
		return fields;
	}
	
	private static List<FormField> getConnectedAppFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("logoId", FieldDisplayType.IMAGE, "Logo", Required.OPTIONAL, 1, 1));
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("baseUrl", FieldDisplayType.TEXTBOX, "App URL", Required.REQUIRED, 4, 1));
		return fields;
	}
	
	public static FacilioForm getInventoryRequestForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("INVENTORY REQUEST");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getInventoryRequestFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static List<FormField> getInventoryRequestFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("requestedTime", FieldDisplayType.DATE, "Requested Date", Required.OPTIONAL, 3, 2));
		fields.add(new FormField("requiredTime", FieldDisplayType.DATE, "Required Date", Required.OPTIONAL, 3, 3));
		fields.add(new FormField("requestedBy", FieldDisplayType.USER, "Requested By", Required.OPTIONAL, "requester", 4, 2));
		fields.add(new FormField("requestedFor", FieldDisplayType.USER, "Requested For", Required.OPTIONAL, "requester", 4, 3));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", Required.OPTIONAL, "storeRoom", 5, 1));
		fields.add(new FormField("lineItems", FieldDisplayType.INVREQUEST_LINE_ITEMS, "LINE ITEMS", Required.REQUIRED, 6, 1));
		
		return fields;
	}
	
	public static FacilioForm getInventoryRequestWorkOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("INVENTORY REQUEST");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getInventoryRequestWorkOrderFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static List<FormField> getInventoryRequestWorkOrderFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("workOrder", FieldDisplayType.TEXTBOX, "Work Order", Required.REQUIRED, 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("requestedTime", FieldDisplayType.DATE, "Requested Date", Required.OPTIONAL, 4, 2));
		fields.add(new FormField("requiredTime", FieldDisplayType.DATE, "Required Date", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("requestedBy", FieldDisplayType.USER, "Requested By", Required.OPTIONAL, "requester", 5, 2));
		fields.add(new FormField("requestedFor", FieldDisplayType.USER, "Requested For", Required.OPTIONAL, "requester", 5, 3));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", Required.OPTIONAL, "storeRoom", 6, 1));
		fields.add(new FormField("lineItems", FieldDisplayType.INVREQUEST_LINE_ITEMS, "LINE ITEMS", Required.REQUIRED, 7, 1));
		
		return fields;
	}
}
