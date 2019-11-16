package com.facilio.bmsconsole.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkOrderContext.WOUrgency;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.twilio.rest.authy.v1.Form;

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
		forms.put("serviceForm", getServiceForm());
		forms.put("rentalLeaseContractForm", getRentalLeaseContractForm());
		forms.put("warrantyContractForm", getWarrantyContractForm());
		forms.put("termsAndConditionForm", getTermsAndConditionForm());
		forms.put("reservationForm", getReservationForm());
		forms.put("devicesForm",getDevicesForm());
		forms.put("kioskForm", getVisitorKioskForm());
		forms.put("visitorForm", getVisitorForm());
		forms.put("visitorPreRegisterForm", getVisitorPreRegisterForm());
		forms.put("portalVisitorPreRegisterForm", getPortalVisitorPreRegisterForm());
		forms.put("vendor_contact_form", getVendorContactForm());
		forms.put("portal_vendor_contact_form", getPortalVendorContactForm());
		//visitor type forms
		
		forms.put("visitorLogForm", getVisitorLogForm());
		forms.put("watchListForm", getWatchListForm());
		forms.put("workpermitForm", getWorkPermitForm());
		forms.put("portalWorkpermitForm", getPortalWorkPermitForm());
		forms.put("insuranceForm", getInsuranceForm());
		forms.put("contactForm", getContactForm());

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
						.put(FacilioConstants.ContextNames.INSURANCE, getInsuranceForm())
						.build())
        			
				.build();
	}
	
	public static Map<String, FacilioForm> getForms(String moduleName, FormType formtype) {
		Map<String, FacilioForm> forms = getForms(moduleName);
		if (MapUtils.isEmpty(forms)) {
			return new HashMap<>();
		}
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
		return getDefaultForm(moduleName, form.getFormTypeVal(), onlyFields);
	}
	
	public static FacilioForm getDefaultForm(String moduleName, String formTypeVal, Boolean...onlyFields) {
		return getForm(moduleName, getDefaultFormName(moduleName, formTypeVal) , onlyFields);
	}
	
	public static String getDefaultFormName(String moduleName, String formTypeVal) {
		return "default_"+moduleName+"_"+formTypeVal;
	}
	
	public static FacilioForm getForm(String moduleName, String formName, Boolean...onlyFields) {
		Map<String, FacilioForm> forms = getForms(moduleName);
		if (MapUtils.isEmpty(forms)) {
			return null;
		}
		FacilioForm form = forms.get(formName);
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
			else if (moduleName.equals(FacilioConstants.ContextNames.ASSET)) {
				List<FormSection> sections = new ArrayList<>();
				form.setSections(sections);
				FormSection section = new FormSection("Asset", 1, form.getFields(), true);
				sections.add(section);
			}
			else if (moduleName.equals(FacilioConstants.ContextNames.PURCHASE_ORDER)) {
				List<FormSection> sections = new ArrayList<>();
				
				List<FormField> defaultFields = new ArrayList<>();
				List<FormField> lineItemFields = new ArrayList<>();
				List<FormField> billingAddressFields = new ArrayList<>();
				List<FormField> shippingAddressFields = new ArrayList<>();
				
				form.setSections(sections);
				FormSection defaultSection = new FormSection("Purchase Order", 1, defaultFields, true);
				FormSection billingSection = new FormSection("Billing Address", 2, billingAddressFields, true);
				FormSection shippingSection = new FormSection("Shipping Address", 3, shippingAddressFields, true);
				FormSection lineItemSection = new FormSection("Line Items", 4, lineItemFields, true);
				
				 form.getFields().forEach(field -> {
					 if (field.getDisplayTypeEnum() == FieldDisplayType.LINEITEMS) {
						 lineItemFields.add(field);
					 }
					 else if (field.getDisplayTypeEnum() == FieldDisplayType.SADDRESS && field.getDisplayName().equals("BILLING ADDRESS")) {
						 billingAddressFields.add(field);
					 }
					 else if (field.getDisplayTypeEnum() == FieldDisplayType.SADDRESS && field.getDisplayName().equals("SHIPPING ADDRESS")) {
						 shippingAddressFields.add(field);
					 }
					 else {
						 defaultFields.add(field);
					 }
				 });
				
				sections.add(defaultSection);
				sections.add(billingSection);
				sections.add(shippingSection);
				sections.add(lineItemSection);
			}
			else if (moduleName.equals(FacilioConstants.ContextNames.PURCHASE_REQUEST)) {
				List<FormSection> sections = new ArrayList<>();
				List<FormField> defaultFields = new ArrayList<>();
				List<FormField> lineItemFields = new ArrayList<>();
				List<FormField> billingAddressFields = new ArrayList<>();
				List<FormField> shippingAddressFields = new ArrayList<>();
				
				form.setSections(sections);
				FormSection defaultSection = new FormSection("Purchase Request", 1, defaultFields, true);
				FormSection billingSection = new FormSection("Billing Address", 2, billingAddressFields, true);
				FormSection shippingSection = new FormSection("Shipping Address", 3, shippingAddressFields, true);
				FormSection lineItemSection = new FormSection("Line Items", 4, lineItemFields, true);
				
				 form.getFields().forEach(field -> {
					 if (field.getDisplayTypeEnum() == FieldDisplayType.LINEITEMS) {
						 lineItemFields.add(field);
					 }
					 else if (field.getDisplayTypeEnum() == FieldDisplayType.SADDRESS && field.getDisplayName().equals("BILLING ADDRESS")) {
						 billingAddressFields.add(field);
					 }
					 else if (field.getDisplayTypeEnum() == FieldDisplayType.SADDRESS && field.getDisplayName().equals("SHIPPING ADDRESS")) {
						 shippingAddressFields.add(field);
					 }
					 else {
						 defaultFields.add(field);
					 }
				 });
				
				sections.add(defaultSection);
				sections.add(billingSection);
				sections.add(shippingSection);
				sections.add(lineItemSection);
			}
		}
		return form;
	}
	
	private static Map<String, Map<String, FacilioForm>>  initFormsList() {
		List<FacilioForm> woForms = Arrays.asList(getWebWorkOrderForm(), getServiceWorkOrderForm());
		List<FacilioForm> assetForms = Arrays.asList(getAssetForm());
		List<FacilioForm> poForm = Arrays.asList(getPurchaseOrderForm());
		List<FacilioForm> prForm = Arrays.asList(getPurchaseRequestForm());
		List<FacilioForm> visitorTypeForms = Arrays.asList(getGuestForm(),getEmployeeForm(),getVendorForm());
		List<FacilioForm> visitorForms = Arrays.asList(getVisitorForm());
		List<FacilioForm> vendorsForms = Arrays.asList(getVendorsForm());
		
		List<FacilioForm> workPermitForm = Arrays.asList(getWorkPermitForm(),getPortalWorkPermitForm());
		List<FacilioForm> insuranceForm = Arrays.asList(getInsuranceForm(),getPortalInsuranceForm());
		
		return ImmutableMap.<String, Map<String, FacilioForm>>builder()
				.put(FacilioConstants.ContextNames.WORK_ORDER, getFormMap(woForms))
				.put(FacilioConstants.ContextNames.ASSET, getFormMap(assetForms))
				.put(FacilioConstants.ContextNames.PURCHASE_ORDER, getFormMap(poForm))
				.put(FacilioConstants.ContextNames.PURCHASE_REQUEST, getFormMap(prForm))
				.put(FacilioConstants.ContextNames.VISITOR_LOGGING,getFormMap(visitorTypeForms))
				.put(FacilioConstants.ContextNames.VISITOR,getFormMap(visitorForms))
				.put(FacilioConstants.ContextNames.WORKPERMIT, getFormMap(workPermitForm))
				.put(FacilioConstants.ContextNames.INSURANCE, getFormMap(insuranceForm))
				.put(FacilioConstants.ContextNames.VENDORS, getFormMap(vendorsForms))
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
		form.setDisplayName("Standard");
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
		form.setDisplayName("Standard");
		form.setName("default_workorder_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getWebWorkOrderFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static FacilioForm getAssetForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Standard");
		form.setName("default_asset_web");
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
		form.setName("default_vendors_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VENDORS));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVendorsFormField());
		form.setFormType(FormType.WEB);
		return form;
	}

	public static FacilioForm getVendorContactForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW VENDOR");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VENDORS));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVendorContactFormField());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getPortalVendorContactForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW VENDOR");
		form.setName("portal_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VENDORS));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPortalVendorContactFormField());
		form.setFormType(FormType.PORTAL);
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
		fields.add(getSiteField());
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.addAll(getWoClassifierFields());
		fields.add(getWoResourceField());
		fields.add(new FormField("assignment", FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, "attachment", 8, 1));
		fields.add(new FormField("parentWO", FieldDisplayType.LOOKUP_SIMPLE, "Parent WorkOrder", Required.OPTIONAL, 9, 1));
		fields.add(new FormField("sendForApproval", FieldDisplayType.DECISION_BOX, "Send For Approval", Required.OPTIONAL, 10, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, 11, 1));
		
		fields.add(new FormField("tasks", FieldDisplayType.TASKS, "TASKS", Required.OPTIONAL, 12, 1));
		return Collections.unmodifiableList(fields);
	}
	
	public static List<FormField> getWoClassifierFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "ticketcategory", 4, 2));
		fields.add(new FormField("type",FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", Required.OPTIONAL, "tickettype", 4, 3));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 5, 1));
		return fields;
	}
	
	public static FormField getSiteField() {
		return new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 2, 1);
	}
	
	public static FormField getWoResourceField() {
		return new FormField("resource", FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.OPTIONAL, 6, 1);
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
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, 10, 1));
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
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, "vendors", 10, 1));
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
		fields.add(new FormField("geoLocationEnabled", FieldDisplayType.DECISION_BOX, "Is Movable",Required.OPTIONAL, 13,2));
		fields.add(new FormField("moveApprovalNeeded", FieldDisplayType.DECISION_BOX, "Is Move Approval Needed",Required.OPTIONAL, 13,2));
		fields.add(new FormField("boundaryRadius", FieldDisplayType.NUMBER, "Boundary Radius", Required.OPTIONAL, 14, 2));
		
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
		fields.add(new FormField("minimumQuantity", FieldDisplayType.NUMBER, "Minimum Quantity", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("isRotating", FieldDisplayType.DECISION_BOX, "Is Rotating", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("isApprovalNeeded", FieldDisplayType.DECISION_BOX, "Approval Needed", Required.OPTIONAL, 5, 3));
		fields.add(new FormField("isConsumable", FieldDisplayType.DECISION_BOX, "To Be Issued", Required.OPTIONAL, 6, 2));
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
		fields.add(new FormField("registeredBy", FieldDisplayType.LOOKUP_SIMPLE, "Registered By", Required.OPTIONAL, "requester",7, 1));
		return fields;
	}

	private static List<FormField> getVendorContactFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "E-mail", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("website", FieldDisplayType.TEXTBOX, "Website", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("address", FieldDisplayType.ADDRESS, "Address", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("registeredBy", FieldDisplayType.LOOKUP_SIMPLE, "Registered By", Required.OPTIONAL, "requester",7, 1));
		fields.add(new FormField("vendorContacts", FieldDisplayType.VENDOR_CONTACTS, "Contacts", Required.OPTIONAL, 8, 1));
		return fields;
	}

	
	private static List<FormField> getPortalVendorContactFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "E-mail", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("website", FieldDisplayType.TEXTBOX, "Website", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("address", FieldDisplayType.ADDRESS, "Address", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("vendorContacts", FieldDisplayType.VENDOR_CONTACTS, "Contacts", Required.OPTIONAL, 8, 1));
		return fields;
	}


	private static List<FormField> getTenantsFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 4, 1));
		fields.add(new FormField("spaces", FieldDisplayType.SPACEMULTICHOOSER, "Spaces", Required.REQUIRED, 5, 1));
		//fields.add(new FormField("contact_name", FieldDisplayType.TEXTBOX, "Contact Name", Required.REQUIRED, 6, 1));
		//fields.add(new FormField("contact_email", FieldDisplayType.TEXTBOX, "Contact Email", Required.REQUIRED, 7, 1));
		fields.add(new FormField("inTime", FieldDisplayType.DATE, "Lease Start Time", Required.OPTIONAL, 8, 1));
		fields.add(new FormField("outTime", FieldDisplayType.DATE, "Lease End Time", Required.OPTIONAL, 9, 1));
		fields.add(new FormField("logo", FieldDisplayType.LOGO, "Logo", Required.OPTIONAL, 1, 1));
		fields.add(new FormField("tenantContacts", FieldDisplayType.VENDOR_CONTACTS , "Contacts", Required.OPTIONAL, 10, 1));
		fields.add(new FormField("utilityMeters", FieldDisplayType.ASSETMULTICHOOSER, "UTILITY METERS", Required.OPTIONAL, 11, 1));

		return fields;
	}

	private static List<FormField> getItemFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("itemType", FieldDisplayType.LOOKUP_SIMPLE, "Item Type", Required.REQUIRED, "itemTypes", 1, 2));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", Required.REQUIRED, "storeRoom", 1, 3));
		fields.add(new FormField("minimumQuantity", FieldDisplayType.NUMBER, "Minimum Quantity", Required.OPTIONAL, 2, 2));
//		fields.add(new FormField("status", FieldDisplayType.LOOKUP_SIMPLE, "Status", Required.OPTIONAL, "itemStatus", 2, 3));
		fields.add(new FormField("costType", FieldDisplayType.SELECTBOX, "Cost Type", Required.OPTIONAL, 2, 3));
		fields.add(new FormField("purchasedItems", FieldDisplayType.PURCHASEDITEM, "Purchased Item", Required.OPTIONAL, 3, 1));

		return fields;
	}

	private static List<FormField> getItemWithIndTrackFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("itemType", FieldDisplayType.LOOKUP_SIMPLE, "Item Type", Required.REQUIRED, "itemTypes", 1, 1));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Store Room", Required.REQUIRED, "storeRoom", 2, 1));
		fields.add(new FormField("minimumQuantity", FieldDisplayType.NUMBER, "Minimum Quantity", Required.OPTIONAL, 3, 1));
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
		fields.add(new FormField("rate", FieldDisplayType.DECIMAL, "Rate/Hour", Required.OPTIONAL, 6, 2));
		fields.add(new FormField("minimumQuantity", FieldDisplayType.DECIMAL, "Minimum Quantity", Required.OPTIONAL, 6, 3));
		
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
		form.setName("default_purchaserequest_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getPurchaseRequestFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	
	public static FacilioForm getPurchaseOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PURCHASE ORDER");
		form.setName("default_purchaseorder_web");
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
		fields.add(new FormField("requestedBy", FieldDisplayType.LOOKUP_SIMPLE, "Requested By", Required.OPTIONAL, "user", 5, 1));
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
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", Required.OPTIONAL, "storeRoom", 3, 3));
		fields.add(new FormField("orderedTime", FieldDisplayType.DATE, "Ordered Date", Required.OPTIONAL, 4, 2));
		fields.add(new FormField("requiredTime", FieldDisplayType.DATE, "Expected Delivery Date", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("requestedBy", FieldDisplayType.LOOKUP_SIMPLE, "Requested By", Required.OPTIONAL, "user", 5, 1));
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
		fields.add(new FormField("payment", FieldDisplayType.SCHEDULER_INFO, "SCHEDULER INFO", Required.REQUIRED, 6, 1));
		
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
		fields.add(new FormField("payment", FieldDisplayType.SCHEDULER_INFO, "SCHEDULER INFO", Required.REQUIRED, 7, 1));
		
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
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("workOrder", FieldDisplayType.TEXTBOX, "Work Order", Required.REQUIRED, 3, 2));
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", Required.OPTIONAL, "storeRoom", 3, 3));
		fields.add(new FormField("requestedBy", FieldDisplayType.USER, "Requested By", Required.OPTIONAL, "requester", 4, 2));
		fields.add(new FormField("requestedFor", FieldDisplayType.USER, "Requested For", Required.OPTIONAL, "requester", 4, 3));
		fields.add(new FormField("requestedTime", FieldDisplayType.DATE, "Requested Date", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("requiredTime", FieldDisplayType.DATE, "Required Date", Required.OPTIONAL, 5, 3));
		fields.add(new FormField("lineItems", FieldDisplayType.INVREQUEST_LINE_ITEMS, "LINE ITEMS", Required.REQUIRED, 6, 1));
		
		return fields;
	}
	
	public static FacilioForm getServiceForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Service");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.SERVICE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getServiceFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static List<FormField> getServiceFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("duration", FieldDisplayType.NUMBER, "Duration(Hr)", Required.REQUIRED, 3, 1));
		fields.add(new FormField("serviceVendors", FieldDisplayType.SERVICEVENDORS, "Service Vendors", Required.REQUIRED, 4, 1));
		
		return fields;
	}
	
	public static FacilioForm getRentalLeaseContractForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("RENTAL LEASE CONTRACT");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getRentalLeaseFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static List<FormField> getRentalLeaseFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED, "vendors", 3, 2).setAllowCreate(true).setCreateFormName("vendors_form"));
		fields.add(new FormField("rentalLeaseContractType", FieldDisplayType.SELECTBOX, "Type", Required.REQUIRED, 3, 3));
		fields.add(new FormField("renewalDate", FieldDisplayType.DATE, "Renewal Date", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("fromDate", FieldDisplayType.DATE, "From Date", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("endDate", FieldDisplayType.DATE, "End Date", Required.OPTIONAL, 5, 3));
		fields.add(new FormField("lineItems", FieldDisplayType.LINEITEMS, "LINE ITEMS", Required.REQUIRED, 6, 1));
		fields.add(new FormField("payment", FieldDisplayType.SCHEDULER_INFO, "SCHEDULER INFO", Required.REQUIRED, 7, 1));

		return fields;
	}
	public static FacilioForm getWarrantyContractForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("WARRANTY CONTRACT");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getWarrantyContractFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static List<FormField> getWarrantyContractFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED, "vendors", 3, 2).setAllowCreate(true).setCreateFormName("vendors_form"));
		fields.add(new FormField("renewalDate", FieldDisplayType.DATE, "Renewal Date", Required.OPTIONAL, 3, 3));
		fields.add(new FormField("fromDate", FieldDisplayType.DATE, "From Date", Required.OPTIONAL, 4, 2));
		fields.add(new FormField("endDate", FieldDisplayType.DATE, "End Date", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("lineItems", FieldDisplayType.WARRANTY_LINE_ITEMS, "LINE ITEMS", Required.REQUIRED, 5, 1));
		fields.add(new FormField("payment", FieldDisplayType.SCHEDULER_INFO, "SCHEDULER INFO", Required.REQUIRED, 6, 1));

		return fields;
	}
	
	public static FacilioForm getTermsAndConditionForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("TERMS AND CONDITION");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getTermsAndConditionFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static List<FormField> getTermsAndConditionFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("termType", FieldDisplayType.TEXTBOX, "Term Type", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("shortDesc", FieldDisplayType.TEXTAREA, "Short Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("longDesc", FieldDisplayType.LONG_DESC, "Long Description", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("defaultOnPo", FieldDisplayType.DECISION_BOX, "Default On PO", Required.OPTIONAL, 5, 2));
		
		return fields;
	}
	
	public static FacilioForm getReservationForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("RESERVATIONS");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.Reservation.RESERVATION));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getReservationFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static List<FormField> getReservationFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 2));
		fields.add(new FormField("space", FieldDisplayType.RESERVABLE_SPACES, "Space", Required.REQUIRED, 4, 1));
		fields.add(new FormField("durationType", FieldDisplayType.SELECTBOX, "Duration Type", Required.REQUIRED, 5, 1));
		fields.add(new FormField("scheduledStartTime", FieldDisplayType.DATETIME, "Scheduled Start Time", Required.REQUIRED, 6, 2));
//		fields.add(new FormField("actualStartTime", FieldDisplayType.DATETIME, "Actual Start Time", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("scheduledEndTime", FieldDisplayType.DATETIME, "Scheduled End Time", Required.OPTIONAL, 6, 3));
//		fields.add(new FormField("actualEndTime", FieldDisplayType.DATETIME, "Actual End Time", Required.OPTIONAL, 5, 3));
		fields.add(new FormField("noOfAttendees", FieldDisplayType.NUMBER, "No. Of Attendees", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("reservedFor", FieldDisplayType.USER, "Reserved For", Required.REQUIRED, 8, 1));
		fields.add(new FormField("internalAttendees", FieldDisplayType.MULTI_USER_LIST, "Internal Attendees", Required.OPTIONAL, 9, 1));
		fields.add(new FormField("externalAttendees", FieldDisplayType.EXTERNAL_ATTENDEES, "External Attendees", Required.OPTIONAL, 10, 1));


		return fields;
	}
	
	
	public static FacilioForm getDevicesForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("DEVICES");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ModuleNames.DEVICES));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getDevicesFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	private static List<FormField> getDevicesFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 2));		
		fields.add(new FormField("associatedResource", FieldDisplayType.WOASSETSPACECHOOSER, "Space", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("deviceType", FieldDisplayType.SELECTBOX, "Device Type", Required.REQUIRED,5, 1));		
		return fields;
	}
	
	
	public static FacilioForm getVisitorKioskForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("VISITOR");
		form.setName("default_visitor_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getVisitorKioskFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static List<FormField> getVisitorKioskFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("phone", FieldDisplayType.NUMBER, "Enter your mobile number", Required.REQUIRED, 1, 1));
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Hi,What is your full name?", Required.REQUIRED, 1, 1));
		fields.add(new FormField("email", FieldDisplayType.EMAIL, "What is your email id?", Required.OPTIONAL, 2, 1));
//		fields.add(new FormField("host", FieldDisplayType.USER, "Host", Required.OPTIONAL, 2, 1));
		//fields.add(new FormField("location", FieldDisplayType.SADDRESS, "Location", Required.OPTIONAL, 3, 1));
		
		return fields;
	}

	public static FacilioForm getVisitorForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("VISITOR");
		form.setName("default_visitor_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVisitorFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}

	private static List<FormField> getVisitorFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.REQUIRED, 2, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("isBlocked", FieldDisplayType.DECISION_BOX, "Visitor Entry", Required.OPTIONAL, 4, 2));
		fields.add(new FormField("isVip", FieldDisplayType.DECISION_BOX, "VIP", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("location", FieldDisplayType.ADDRESS, "Location", Required.OPTIONAL, 5, 1));
		return fields;
	}

	public static FacilioForm getVisitorPreRegisterForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PRE REGISTER VISITOR");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR_INVITE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVisitorPreRegisterFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}

	private static List<FormField> getVisitorPreRegisterFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("inviteName", FieldDisplayType.TEXTBOX, "Invite Name", Required.REQUIRED, 1, 1));
//		fields.add(new FormField("expectedStartTime", FieldDisplayType.DATETIME, "Expected Start Time", Required.OPTIONAL, 2, 1));
//		fields.add(new FormField("expectedEndTime", FieldDisplayType.DATETIME, "Expected End Time", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("inviteHost", FieldDisplayType.USER, "Host", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("visitorType", FieldDisplayType.LOOKUP_SIMPLE, "Visitor Type", Required.OPTIONAL, "visitorType", 3, 1));
		fields.add(new FormField("recurringVisitor", FieldDisplayType.RECURRING_VISITOR , "RECURRING VISITOR", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("invitees", FieldDisplayType.VISITOR_INVITEES , "VISITORS", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("requestedBy", FieldDisplayType.LOOKUP_SIMPLE, "Requested By", Required.OPTIONAL, "requester",6, 1));

		return fields;
	}
	
	public static FacilioForm getPortalVisitorPreRegisterForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PRE REGISTER VISITOR");
		form.setName("portal_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR_INVITE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPotalVisitorPreRegisterFormFields());
		form.setFormType(FormType.PORTAL);
		return form;
	}

	private static List<FormField> getPotalVisitorPreRegisterFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("inviteName", FieldDisplayType.TEXTBOX, "Invite Name", Required.REQUIRED, 1, 1));
//		fields.add(new FormField("expectedStartTime", FieldDisplayType.DATETIME, "Expected Start Time", Required.OPTIONAL, 2, 1));
//		fields.add(new FormField("expectedEndTime", FieldDisplayType.DATETIME, "Expected End Time", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("inviteHost", FieldDisplayType.USER, "Host", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("visitorType", FieldDisplayType.LOOKUP_SIMPLE, "Visitor Type", Required.OPTIONAL, "visitorType", 3, 1));
		fields.add(new FormField("recurringVisitor", FieldDisplayType.RECURRING_VISITOR , "RECURRING VISITOR", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("invitees", FieldDisplayType.VISITOR_INVITEES , "VISITORS", Required.OPTIONAL, 5, 1));
//		FormField requestedByField = new FormField("requestedBy", FieldDisplayType.LOOKUP_SIMPLE, "Requested By", Required.OPTIONAL, "requester",6, 1);
//		requestedByField.setShowField(false);
//		fields.add(requestedByField);

		return fields;
	}
	
	public static FacilioForm getGuestForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Guest form for Vistor module");
		form.setName("Guest");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getGuestFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	private static List<FormField> getGuestFormFields()
	{
		List<FormField> fields=new ArrayList<>();
		
		fields.add(new FormField("purposeOfVisit",FieldDisplayType.TEXTBOX,"What is the purpose of visit",Required.REQUIRED,1,1));
		fields.add(new FormField("host",FieldDisplayType.USER,"Who do you want to meet",Required.REQUIRED,1,1));
		fields.add(new FormField("visitor", FieldDisplayType.LOOKUP_SIMPLE, "Visitor", Required.REQUIRED, "visitors", 1, 1).setAllowCreate(true).setCreateFormName("vendors_form"));
		fields.add(new FormField("avatar",FieldDisplayType.IMAGE,"Visitor Photo",Required.REQUIRED,1,1));

		return fields;
	}
	
	
	//vistor type vendor
	public static FacilioForm getVendorForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Vendor form for Vistor module");
		form.setName("Vendor");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVendorFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	private static List<FormField> getVendorFormFields()
	{
		List<FormField> fields=new ArrayList<>();
		fields.add(new FormField("purposeOfVisit",FieldDisplayType.TEXTBOX,"What is the purpose of visit",Required.OPTIONAL,1,1));
		fields.add(new FormField("host",FieldDisplayType.USER,"Who do you want to meet",Required.REQUIRED,1,1));
		fields.add(new FormField("avatar",FieldDisplayType.IMAGE,"Visitor Photo",Required.REQUIRED,1,1));
		fields.add(new FormField("visitor", FieldDisplayType.LOOKUP_SIMPLE, "Visitor", Required.REQUIRED, "visitors", 1, 1).setAllowCreate(true).setCreateFormName("vendors_form"));

		return fields;
	}
	
	public static FacilioForm getEmployeeForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Employee form for Vistor module");
		form.setName("Employee");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getEmployeeFormFields());
		
		form.setFormType(FormType.WEB);
		return form;
	}
	private static List<FormField> getEmployeeFormFields()
	{
		List<FormField> fields=new ArrayList<>();
		fields.add(new FormField("visitor", FieldDisplayType.LOOKUP_SIMPLE, "Visitor", Required.REQUIRED, "visitors", 1, 1).setAllowCreate(true).setCreateFormName("vendors_form"));
		fields.add(new FormField("avatar",FieldDisplayType.IMAGE,"Visitor Photo",Required.REQUIRED,1,1));


		
		return fields;
	}

	public static FacilioForm getVisitorLogForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("VISITS");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVisitorLogFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}

	private static List<FormField> getVisitorLogFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("visitor", FieldDisplayType.LOOKUP_SIMPLE, "Visitor", Required.REQUIRED, "visitor", 1, 1).setAllowCreate(true).setCreateFormName("visitorForm"));
		fields.add(new FormField("host", FieldDisplayType.USER, "Host", Required.OPTIONAL, "host", 2, 2));
		fields.add(new FormField("isApprovalNeeded", FieldDisplayType.DECISION_BOX, "Is Host Approval Needed", Required.OPTIONAL, 5, 3));
		return fields;
	}

	public static FacilioForm getWatchListForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("WATCH LIST");
		form.setName("default_watch_list_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WATCHLIST));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getWatchListFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}

	private static List<FormField> getWatchListFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("avatarId", FieldDisplayType.IMAGE, "Avatar", Required.OPTIONAL, 1, 1));
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.REQUIRED, 3, 2));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("aliases", FieldDisplayType.LONG_DESC, "Aliases", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("physicalDescription", FieldDisplayType.LONG_DESC, "Physical Description", Required.OPTIONAL, 6, 2));
		fields.add(new FormField("isBlocked", FieldDisplayType.DECISION_BOX, "Visitor Entry", Required.OPTIONAL, 7, 2));
		fields.add(new FormField("isVip", FieldDisplayType.DECISION_BOX, "VIP", Required.OPTIONAL, 8, 3));
		fields.add(new FormField("remarks", FieldDisplayType.LONG_DESC, "Remarks", Required.OPTIONAL, 9, 2));
		
		return fields;
	}
	
	public static FacilioForm getWorkPermitForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("WORK PERMIT");
		form.setName("default_workpermit_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORKPERMIT));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getWorkPermitFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getPortalWorkPermitForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("WORK PERMIT");
		form.setName("default_workpermit_portal");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORKPERMIT));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPortalWorkPermitFormFields());
		form.setFormType(FormType.PORTAL);
		return form;
	}
	
	private static List<FormField> getWorkPermitFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTBOX, "Descritption", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("isRecurring", FieldDisplayType.DECISION_BOX, "Is Recurring", Required.OPTIONAL, 3, 2));
		fields.add(new FormField("recurringInfoId", FieldDisplayType.RECURRING_VISITOR , "RECURRING VISITOR", Required.OPTIONAL, 4, 1));
		FormField ticketField = new FormField("ticket", FieldDisplayType.LOOKUP_SIMPLE, "Ticket", Required.OPTIONAL,"ticket", 3, 2);
		ticketField.setShowField(false);
		fields.add(ticketField);
		FormField vendorField = new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL,"vendors", 3, 2);
		vendorField.setShowField(false);
		fields.add(vendorField);
		fields.add(new FormField("workType", FieldDisplayType.SELECTBOX, "Work Type", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("requestedBy", FieldDisplayType.LOOKUP_SIMPLE, "Requested By", Required.OPTIONAL, "requester",6, 1));
		return fields;
	}
	
	private static List<FormField> getPortalWorkPermitFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTBOX, "Descritption", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("isRecurring", FieldDisplayType.DECISION_BOX, "Is Recurring", Required.OPTIONAL, 3, 2));
		fields.add(new FormField("recurringInfoId", FieldDisplayType.RECURRING_VISITOR , "RECURRING VISITOR", Required.OPTIONAL, 4, 1));
		FormField ticketField = new FormField("ticket", FieldDisplayType.LOOKUP_SIMPLE, "Ticket", Required.OPTIONAL,"ticket", 3, 2);
		ticketField.setShowField(false);
		fields.add(ticketField);
		FormField vendorField = new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL,"vendors", 3, 2);
		vendorField.setShowField(false);
		fields.add(vendorField);
		fields.add(new FormField("workType", FieldDisplayType.SELECTBOX, "Work Type", Required.OPTIONAL, 5, 1));
//		FormField requestedByField = new FormField("requestedBy", FieldDisplayType.LOOKUP_SIMPLE, "Requested By", Required.OPTIONAL, "requester",6, 1);
//		requestedByField.setShowField(false);
//		fields.add(requestedByField);
		return fields;
	}

	public static FacilioForm getInsuranceForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("INSURACNE");
		form.setName("default_insurance_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.INSURANCE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getInsuranceFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}
	
	public static FacilioForm getPortalInsuranceForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("INSURACNE");
		form.setName("default_insurance_portal");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.INSURANCE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getInsuranceFormFields());
		form.setFormType(FormType.PORTAL);
		return form;
	}
	
	private static List<FormField> getInsuranceFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("companyName", FieldDisplayType.TEXTBOX, "Company Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("validFrom", FieldDisplayType.DATE, "Valid From", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("validTill", FieldDisplayType.DATE, "Valid Till", Required.OPTIONAL, 3, 1));
		FormField vendorField = new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL,"vendor", 3, 2);
		vendorField.setShowField(false);
		fields.add(vendorField);
		FormField fileField= new FormField("insurance", FieldDisplayType.IMAGE, "File", Required.OPTIONAL, 1, 1);
		fileField.setShowField(true);
		fields.add(fileField);
		return fields;
	}

	public static FacilioForm getContactForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("CONTACT");
		form.setName("default_contact_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.CONTACT));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getContactFormFields());
		form.setFormType(FormType.WEB);
		return form;
	}

	private static List<FormField> getContactFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL, 2, 2));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 2, 3));
		fields.add(new FormField("contactType", FieldDisplayType.SELECTBOX, "Contact Type", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, "vendors",4, 2));
		fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.OPTIONAL, "tenant",4, 3));
		fields.add(new FormField("isPortalAccessNeeded", FieldDisplayType.DECISION_BOX, "Is Portal Access Needed", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("isPrimaryContact", FieldDisplayType.DECISION_BOX, "Is Primary Contact", Required.OPTIONAL, 5, 3));

		return fields;
	}



}
