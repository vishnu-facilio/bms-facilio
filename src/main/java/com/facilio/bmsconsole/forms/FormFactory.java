package com.facilio.bmsconsole.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.context.WorkOrderContext.WOUrgency;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class FormFactory {
	
	private static final Map<String, FacilioForm> FORM_MAP = Collections.unmodifiableMap(initMap());
	private static final Map<String, Map<String, FacilioForm>> FORMS_LIST = Collections.unmodifiableMap(initFormsList());

	// TODO remove...use FORMS_LIST to get details
	private static Map<String, FacilioForm> initMap() {
		Map<String, FacilioForm> forms = new HashMap<>();
		forms.put("workOrderForm", getWebWorkOrderForm());
		forms.put("serviceWorkOrder", getServiceWorkOrderForm());
		forms.put("web_pm", getPMForm());
		forms.put("multi_web_pm", getMultiPMForm());
		forms.put("approvalForm", getApprovalForm());
		forms.put("default_asset", getAssetForm());
		forms.put("energymeter", getEnergyMeterForm());
		forms.put("tenant", getTenantForm());
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
		forms.put("portalVisitorForm", getPortalVisitorForm());
		forms.put("visitorPreRegisterForm", getVisitorPreRegisterForm());
		forms.put("default_visitorlogging_portal", getPortalVisitorPreRegisterForm());
		forms.put("vendor_contact_form", getVendorContactForm());
		forms.put("portal_vendor_contact_form", getPortalVendorForm());
		//visitor type forms
		
		forms.put("visitorLogForm", getVisitorLogForm());
		forms.put("watchListForm", getWatchListForm());
		forms.put("workpermitForm", getWorkPermitForm());
		forms.put("portalWorkpermitForm", getPortalWorkPermitForm());
		forms.put("insuranceForm", getInsuranceForm());
		forms.put("contactForm", getContactForm());
		forms.put("occupantForm", getOccupantForm());
		forms.put("printerForm",getPrinterForm());
		forms.put("portalContactForm", getPortalContactForm());
		forms.put("vendorDocumentForm",getVendorDocumentForm());
		forms.put("portalvendorDocumentForm",getPortalVendorDocumentForm());
		forms.put("client_form", getClientForm());
		forms.put("tenant_contact_form", getTenantContactForm());
		forms.put("portal_tenant_contact_form", getTenantContactPortalForm());
		forms.put("new_vendor_contact_form", getNewVendorContactForm());
		forms.put("client_contact_form", getClientContactForm());
		forms.put("employee_form", geEmployeeContactForm());
		forms.put("people_form", getPeopleForm());
		forms.put("tenantunit_form", geTenantUnitSpaceForm());

		forms.put("site", getSiteForm());
		forms.put("building", getBuildingForm());
		forms.put("spaceFromSite", getSpaceFormFromSite());
		forms.put("spaceFromBuilding", getSpaceFormFromBuilding());
		forms.put("spaceFromFloor", getSpaceFormFromFloor());
		forms.put("spaceFromSpace", getSpaceFormFromSpace());
		forms.put("floor", getFloorForm());
		/**** Do not add any forms here... Add in initFormsList() only ******/

		return forms;
	}
	
	public static Map<String, FacilioForm> getForms(String moduleName, List<String> appLinkNames) {
		Map<String, FacilioForm> forms = getForms(moduleName);
		if (MapUtils.isEmpty(forms)) {
			return new HashMap<>();
		}
		if (appLinkNames == null) {
			return forms;
		}
		return forms.entrySet().stream().filter(f -> appLinkNames.contains(f.getValue().getAppLinkName()))
	            .collect(Collectors.toMap(f -> f.getKey(), f -> f.getValue()));
	}
	
	public static Map<String, FacilioForm> getForms(String moduleName) {
		return FORMS_LIST.get(moduleName);
	}
	
	public static FacilioForm getDefaultForm(String moduleName, FacilioForm form, Boolean...onlyFields) {
		return getDefaultForm(moduleName, form.getAppLinkName(), onlyFields);
	}
	
	public static FacilioForm getDefaultForm(String moduleName, String appLinkName, Boolean...onlyFields) {
		return getForm(moduleName, getDefaultFormName(moduleName, appLinkName) , onlyFields);
	}
	
	public static String getDefaultFormName(String moduleName, String appLinkName) {	
		return "default_"+moduleName+"_"+ (FormsAPI.isPortalApp(appLinkName) ? "portal" : "web") ;
	}
	
	public static FacilioForm getForm(String moduleName, String formName, Boolean...onlyFields) {
		Map<String, FacilioForm> forms = getForms(moduleName);
		if (MapUtils.isEmpty(forms)) {
			return null;
		}
		FacilioForm form = forms.get(formName);
		if (form != null && (onlyFields == null || onlyFields.length == 0 || !onlyFields[0])) {
			form = new FacilioForm(form);
			// TODO add sections in formfactory initialization itself once all client supports
			if (moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
				List<FormSection> sections = new ArrayList<>();
				form.setSections(sections);
				int i = 1;
				List<FormField> defaultFields = new ArrayList<>();
				List<FormField> taskFields = new ArrayList<>();
				
				FormSection defaultSection = new FormSection("WORKORDER", i++, defaultFields, true);
				if (form.getAppLinkName() == ApplicationLinkNames.OCCUPANT_PORTAL_APP) {
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
				if (form.getAppLinkName() != ApplicationLinkNames.OCCUPANT_PORTAL_APP && !taskFields.isEmpty()) {
					FormSection taskSection = new FormSection("TASKS", i++, taskFields, true);
					sections.add(taskSection);
				}
				
//				form.setFields(null);
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
			} else if (moduleName.equals(FacilioConstants.ContextNames.QUOTE)) {
				List<FormSection> sections = new ArrayList<>();
				List<FormField> defaultFields = new ArrayList<>();
				List<FormField> billingAddressFields = new ArrayList<>();
				List<FormField> lineItemFields = new ArrayList<>();
				List<FormField> signatureFields = new ArrayList<>();

				form.setSections(sections);
				FormSection defaultSection = new FormSection("QUOTE INFORMATION", 1, defaultFields, true);
				FormSection billingSection = new FormSection("BILLING ADDRESS", 2, billingAddressFields, false);
				FormSection lineItemSection = new FormSection("QUOTE ITEMS", 3, lineItemFields, true);
				FormSection notesSection = new FormSection("NOTES", 4, signatureFields, false);

				form.getFields().forEach(field -> {
					if (field.getDisplayTypeEnum() == FieldDisplayType.QUOTE_LINE_ITEMS) {
						lineItemFields.add(field);
					} else if (field.getDisplayTypeEnum() == FieldDisplayType.QUOTE_ADDRESS && field.getName().equals("billToAddress")) {
						billingAddressFields.add(field);
					} else if (Arrays.asList("notes").contains(field.getName())) {
						signatureFields.add(field);
					} else {
						defaultFields.add(field);
					}
				});

				sections.add(defaultSection);
				sections.add(billingSection);
				sections.add(lineItemSection);
				sections.add(notesSection);
			} else if (moduleName.equals(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT)) {
				List<FormSection> sections = new ArrayList<>();
				List<FormField> defaultFields = new ArrayList<>();
				List<FormField> checklistFields = new ArrayList<>();

				form.setSections(sections);
				FormSection defaultSection = new FormSection("PERMIT INFORMATION", 1, defaultFields, true);
				FormSection checklistSection = new FormSection("CHECKLIST", 2, checklistFields, true);

				form.getFields().forEach(field -> {
					if (field.getDisplayTypeEnum() == FieldDisplayType.PERMIT_CHECKLIST) {
						checklistFields.add(field);
					} else {
						defaultFields.add(field);
					}
				});
				sections.add(defaultSection);
				if (CollectionUtils.isNotEmpty(checklistFields)) {
					sections.add(checklistSection);
				}
			}  else if (moduleName.equals(FacilioConstants.ContextNames.Budget.BUDGET)) {
				List<FormSection> sections = new ArrayList<>();
				List<FormField> defaultFields = new ArrayList<>();
				List<FormField> budgetAmountFields = new ArrayList<>();

				form.setSections(sections);
				FormSection defaultSection = new FormSection("BUDGET DETAILS", 1, defaultFields, true);
				FormSection budgetAmount = new FormSection("BUDGET AMOUNTS", 2, budgetAmountFields, true);

				form.getFields().forEach(field -> {
					if (field.getDisplayTypeEnum() == FieldDisplayType.BUDGET_AMOUNT) {
						budgetAmountFields.add(field);
					} else {
						defaultFields.add(field);
					}
				});
				sections.add(defaultSection);
				if (CollectionUtils.isNotEmpty(budgetAmountFields)) {
					sections.add(budgetAmount);
				}
			}  else if (moduleName.equals(ContextNames.FacilityBooking.FACILITY)) {
				List<FormSection> sections = new ArrayList<>();
				List<FormField> defaultFields = new ArrayList<>();
				List<FormField> slotAvailabilityFields = new ArrayList<>();

				form.setSections(sections);
				FormSection defaultSection = new FormSection("DETAILS", 1, defaultFields, false);
				FormSection availabilitySection = new FormSection("AVAILABILITY", 2, slotAvailabilityFields, true);

				form.getFields().forEach(field -> {
					if (field.getDisplayTypeEnum() == FieldDisplayType.FACILITY_AVAILABILITY) {
						slotAvailabilityFields.add(field);
					} else {
						defaultFields.add(field);
					}
				});
				sections.add(defaultSection);
				if (CollectionUtils.isNotEmpty(slotAvailabilityFields)) {
					sections.add(availabilitySection);
				}
			} else if (moduleName.equals(ContextNames.FacilityBooking.FACILITY_BOOKING)) {
				List<FormSection> sections = new ArrayList<>();
				List<FormField> defaultFields = new ArrayList<>();
				List<FormField> timeSlotFields = new ArrayList<>();
				List<FormField> attendeeFields = new ArrayList<>();

				form.setSections(sections);
				FormSection defaultSection = new FormSection("DETAILS", 1, defaultFields, false);
				FormSection timeSlotSection = new FormSection("TIME SLOTS", 2, timeSlotFields, false);
				FormSection attendeeSection = new FormSection("ATTENDEES", 3, attendeeFields, true);

				form.getFields().forEach(field -> {
					if (field.getDisplayTypeEnum() == FieldDisplayType.FACILITY_BOOKING_SLOTS) {
						timeSlotFields.add(field);
					} else if (Arrays.asList("internalAttendees").contains(field.getName())) {
						attendeeFields.add(field);
					} else {
						defaultFields.add(field);
					}
				});
				sections.add(defaultSection);
				if (CollectionUtils.isNotEmpty(timeSlotFields)) {
					sections.add(timeSlotSection);
				}
				if (CollectionUtils.isNotEmpty(attendeeFields)) {
					sections.add(attendeeSection);
				}
			}
			else if (form.getSections() == null && form.getFields() != null) {
				FormSection section = new FormSection("Default", 1, form.getFields(), false);
				form.setSections(Collections.singletonList(section));
			}
		}
		return form;
	}	
	
	private static Map<String, Map<String, FacilioForm>>  initFormsList() {
		List<FacilioForm> woForms = Arrays.asList(getWebWorkOrderForm(), getServiceWorkOrderForm(), getAlarmWorkOrderForm(), getMultiPMForm());
		List<FacilioForm> assetForms = Arrays.asList(getAssetForm());
		List<FacilioForm> energyMeterForm = Arrays.asList(getEnergyMeterForm());
		List<FacilioForm> tenantForm = Arrays.asList(getTenantForm());
		List<FacilioForm> poForm = Arrays.asList(getPurchaseOrderForm());
		List<FacilioForm> prForm = Arrays.asList(getPurchaseRequestForm());
		List<FacilioForm> visitorForms = Arrays.asList(getVisitorForm(), getPortalVisitorForm());
		List<FacilioForm> visitorLoggingForms = Arrays.asList(getPortalVisitorPreRegisterForm());
		List<FacilioForm> vendorsForms = Arrays.asList(getVendorsForm(), getPortalVendorForm());
		List<FacilioForm> insuranceForm = Arrays.asList(getInsuranceForm(),getPortalInsuranceForm());
		List<FacilioForm> watchListForm = Arrays.asList(getWatchListForm());
		List<FacilioForm> occupantFormsList = Arrays.asList(getOccupantForm());
		List<FacilioForm> serviceRequestFormsList = Arrays.asList(getServiceRequestForm());
		List<FacilioForm> vendorDocumentFormsList = Arrays.asList(getVendorDocumentForm(), getPortalVendorDocumentForm());
		List<FacilioForm> contactFormsList = Arrays.asList(getContactForm(), getPortalContactForm());
		List<FacilioForm> safetyPlanFormsList = Arrays.asList(getSafetyPlanForm());
		List<FacilioForm> hazardFormsList = Arrays.asList(getHazardForm());
		List<FacilioForm> precautionFormsList = Arrays.asList(getPrecautionForm());
		List<FacilioForm> clientFormsList = Arrays.asList(getClientForm());
		List<FacilioForm> tenantcontactFormsList = Arrays.asList(getTenantContactForm(), getTenantContactPortalForm());
		List<FacilioForm> vendorContactFormsList = Arrays.asList(getNewVendorContactForm());
		List<FacilioForm> clientContactFormsList = Arrays.asList(getClientContactForm());
		List<FacilioForm> tenantUnitFormsList = Arrays.asList(geTenantUnitSpaceForm());
		List<FacilioForm> employeeFormsList = Arrays.asList(geEmployeeContactForm());
		List<FacilioForm> peopleFormsList = Arrays.asList(getPeopleForm());

		List<FacilioForm> reservationFormsList = Arrays.asList(getReservationForm());
		List<FacilioForm> rentalLeaseFormsList = Arrays.asList(getRentalLeaseContractForm());
		List<FacilioForm> invReqFormsList = Arrays.asList(getInventoryRequestForm());
		List<FacilioForm> itemTypesFormsList = Arrays.asList(getItemTypesForm());
		List<FacilioForm> toolTypesFormsList = Arrays.asList(getTooltypesForm());
		List<FacilioForm> quotationFormsList = Arrays.asList(getQuotationForm());
		List<FacilioForm> serviceFormsList = Arrays.asList(getServiceForm());
		List<FacilioForm> announcementFormsList = Arrays.asList(getAnnouncementForm());
		List<FacilioForm> newsAndInformationFormsList = Arrays.asList(getNewsAndInformationForm());
		List<FacilioForm> neighbourhoodFormsList = Arrays.asList(getNeighbourhoodForm(),getNeighbourhoodPortalForm());
		List<FacilioForm> dealsAndOffersFormsList = Arrays.asList(getDealsAndOffersForm(), getDealsAndOffersPortalForm());
		List<FacilioForm> contactDirectoryFormsList = Arrays.asList(getContactDirectoryForm());
		List<FacilioForm> adminDocumentsFormsList = Arrays.asList(getAdminDocumentsForm());
		List<FacilioForm> budgetFormsList = Arrays.asList(getBudgetForm());
		List<FacilioForm> chartOfAccountFormsList = Arrays.asList(getChartOfAccountForm());
		List<FacilioForm> accountTypeForm = Arrays.asList(getAccountTypeForm());

		List<FacilioForm> workPermitForm = Arrays.asList(getWorkPermitForm(),getPortalWorkPermitForm());
		List<FacilioForm> workPermitTypeForm = Arrays.asList(getWorkPermitTypeForm());
		List<FacilioForm> workPermitTypeChecklistCategoryForm = Arrays.asList(getWorkPermitTypeChecklistCategoryForm());
		List<FacilioForm> workPermitTypeChecklistForm = Arrays.asList(getWorkPermitTypeChecklistForm());

		List<FacilioForm> facilityFormsList = Arrays.asList(getFacilityCreationForm());
		List<FacilioForm> facilityBookingFormsList = Arrays.asList(getFacilityBookingForm(), getFacilityBookingPortalForm());
		List<FacilioForm> facilitySpecialAvailabilityFormsList = Arrays.asList(getFacilitySpecialAvailabilityForm());
		List<FacilioForm> amenityFormsList = Arrays.asList(getAmenityForm());
		List<FacilioForm> externalAttendeeFormList = Arrays.asList(getExternalAttendeeForm());
		List<FacilioForm> siteFormList = Arrays.asList(getSiteForm());
		List<FacilioForm> buildingFormList = Arrays.asList(getBuildingForm());
		List<FacilioForm> spaceFormList = Arrays.asList(getSpaceFormFromSite(),getSpaceFormFromBuilding(),getSpaceFormFromFloor(),getSpaceFormFromSpace());
		List<FacilioForm> floorFormList = Arrays.asList(getFloorForm());
		
		List<FacilioForm> warrantyContractFormsList = Arrays.asList(getWarrantyContractForm());

		return ImmutableMap.<String, Map<String, FacilioForm>>builder()
				.put(FacilioConstants.ContextNames.WORK_ORDER, getFormMap(woForms))
				.put(FacilioConstants.ContextNames.ASSET, getFormMap(assetForms))
				.put(FacilioConstants.ContextNames.ENERGY_METER, getFormMap(energyMeterForm))
				.put(FacilioConstants.ContextNames.TENANT, getFormMap(tenantForm))
				.put(FacilioConstants.ContextNames.PURCHASE_ORDER, getFormMap(poForm))
				.put(FacilioConstants.ContextNames.PURCHASE_REQUEST, getFormMap(prForm))
				.put(FacilioConstants.ContextNames.VISITOR,getFormMap(visitorForms))
				.put(FacilioConstants.ContextNames.INSURANCE, getFormMap(insuranceForm))
				.put(FacilioConstants.ContextNames.VENDORS, getFormMap(vendorsForms))
				.put(FacilioConstants.ContextNames.WATCHLIST, getFormMap(watchListForm))
				.put(FacilioConstants.ContextNames.OCCUPANT, getFormMap(occupantFormsList))
				.put(FacilioConstants.ContextNames.SERVICE_REQUEST,getFormMap(serviceRequestFormsList))
				.put(FacilioConstants.ContextNames.VENDOR_DOCUMENTS,getFormMap(vendorDocumentFormsList))
				.put(FacilioConstants.ContextNames.CONTACT,getFormMap(contactFormsList))
				.put(FacilioConstants.ContextNames.SAFETY_PLAN,getFormMap(safetyPlanFormsList))
				.put(FacilioConstants.ContextNames.HAZARD,getFormMap(hazardFormsList))
				.put(FacilioConstants.ContextNames.PRECAUTION,getFormMap(precautionFormsList))
				.put(FacilioConstants.ContextNames.CLIENT, getFormMap(clientFormsList))
				.put(FacilioConstants.ContextNames.TENANT_CONTACT, getFormMap(tenantcontactFormsList))
				.put(FacilioConstants.ContextNames.VENDOR_CONTACT, getFormMap(vendorContactFormsList))
				.put(FacilioConstants.ContextNames.CLIENT_CONTACT, getFormMap(clientContactFormsList))
				.put(FacilioConstants.ContextNames.TENANT_UNIT_SPACE, getFormMap(tenantUnitFormsList))
				.put(FacilioConstants.ContextNames.EMPLOYEE, getFormMap(employeeFormsList))
				.put(FacilioConstants.ContextNames.Reservation.RESERVATION, getFormMap(reservationFormsList))
				.put(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS, getFormMap(rentalLeaseFormsList))
				.put(FacilioConstants.ContextNames.VISITOR_LOGGING, getFormMap(visitorLoggingForms))
				.put(FacilioConstants.ContextNames.INVENTORY_REQUEST, getFormMap(invReqFormsList))
				.put(FacilioConstants.ContextNames.ITEM_TYPES, getFormMap(itemTypesFormsList))
				.put(FacilioConstants.ContextNames.TOOL_TYPES, getFormMap(toolTypesFormsList))
				.put(FacilioConstants.ContextNames.QUOTE, getFormMap(quotationFormsList))
				.put(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT, getFormMap(workPermitForm))
				.put(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE, getFormMap(workPermitTypeForm))
				.put(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST_CATEGORY, getFormMap(workPermitTypeChecklistCategoryForm))
				.put(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST, getFormMap(workPermitTypeChecklistForm))
				.put(FacilioConstants.ContextNames.SERVICE, getFormMap(serviceFormsList))
				.put(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT, getFormMap(announcementFormsList))
				.put(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD, getFormMap(neighbourhoodFormsList))
				.put(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION, getFormMap(newsAndInformationFormsList))
				.put(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS, getFormMap(dealsAndOffersFormsList))
				.put(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY, getFormMap(contactDirectoryFormsList))
				.put(FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS, getFormMap(adminDocumentsFormsList))
				.put(FacilioConstants.ContextNames.Budget.BUDGET, getFormMap(budgetFormsList))
				.put(ContextNames.Budget.CHART_OF_ACCOUNT, getFormMap(chartOfAccountFormsList))
				.put(ContextNames.Budget.ACCOUNT_TYPE, getFormMap(accountTypeForm))
				.put(ContextNames.PEOPLE, getFormMap(peopleFormsList))
				.put(ContextNames.FacilityBooking.FACILITY, getFormMap(facilityFormsList))
				.put(ContextNames.FacilityBooking.FACILITY_BOOKING, getFormMap(facilityBookingFormsList))
				.put(ContextNames.FacilityBooking.FACILITY_SPECIAL_AVAILABILITY, getFormMap(facilitySpecialAvailabilityFormsList))
				.put(ContextNames.FacilityBooking.AMENITY, getFormMap(amenityFormsList))
				.put(ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE, getFormMap(externalAttendeeFormList))
				.put(ContextNames.SITE, getFormMap(siteFormList))
				.put(ContextNames.BUILDING, getFormMap(buildingFormList))
				.put(ContextNames.SPACE, getFormMap(spaceFormList))
				.put(ContextNames.FLOOR, getFormMap(floorFormList))
				.put(ContextNames.WARRANTY_CONTRACTS, getFormMap(warrantyContractFormsList))
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
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		return form;
	}
	
	public static FacilioForm getApprovalForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Approval");
		form.setName("workOrder");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getWebApprovalFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getMobileApprovalForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Approval");
		form.setName("mobile_approval");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getMobileApprovalFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getMobileWorkOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("SUBMIT WORKORDER");
		form.setName("mobile_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getMobileWorkOrderFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getMobileAssetForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Asset");
		form.setName("default_asset_mobile");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ASSET));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getMobileAssetFormFields());
		form.setShowInMobile(true);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
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
		fields.add(new FormField("qrVal", FieldDisplayType.TEXTBOX, "QR Value", Required.OPTIONAL, 15, 1));
		return Collections.unmodifiableList(fields);
	}
	
	
	public static List<FormField> getMetaFormFieldApprovals(List<FacilioField> allFields) throws Exception {
		List<FormField> fields = new ArrayList<>();

		List<FacilioField> facilioFields = new ArrayList(); 
		int i = 0;
		for(FacilioField fieldObject: allFields) {
		if(FieldFactory.Fields.APPROVAL_FORM_FIELDS.contains(fieldObject.getName()) || !fieldObject.isDefault()) {
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

	public static FacilioForm getFloorForm() {
		FacilioModule floorModule = ModuleFactory.getModule(ContextNames.FLOOR);

		FacilioForm defaultForm = new FacilioForm();
		defaultForm.setName("default_floor_web");
		defaultForm.setModule(floorModule);
		defaultForm.setDisplayName("Standard");
		defaultForm.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
		defaultForm.setShowInWeb(true);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
		fields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 3, 1));
		fields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 3, 1));
		fields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, 4, 1));
		fields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL, 5, 1));
		fields.add(new FormField("floorlevel", FacilioField.FieldDisplayType.NUMBER, "Floor Level", FormField.Required.OPTIONAL, 6, 1));

		defaultForm.setFields(fields);
		return defaultForm;
	}

	public static FacilioForm getSpaceFormFromSite() {
		FacilioModule spaceModule = ModuleFactory.getModule(ContextNames.SPACE);

		FacilioForm defaultForm = new FacilioForm();
		defaultForm.setName("default_space_web_site");
		defaultForm.setModule(spaceModule);
		defaultForm.setDisplayName("Standard");
		defaultForm.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
		defaultForm.setShowInWeb(true);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
		fields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
		fields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
		fields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site Associated", FormField.Required.REQUIRED,"site", 5, 1,true));
		fields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
		fields.add(new FormField("location", FieldDisplayType.GEO_LOCATION, "Location", Required.OPTIONAL, 7, 1));
		
		try {
			if(AccountUtil.isFeatureEnabled(FeatureLicense.RESOURCE_BOOKING)) {
				fields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
				fields.add(new FormField("unitReservationCost", FieldDisplayType.TEXTBOX, "Unit Reservation Cost", Required.OPTIONAL, 9, 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		defaultForm.setFields(fields);
		return defaultForm;
	}
	
	public static FacilioForm getSpaceFormFromBuilding() {
		FacilioModule spaceModule = ModuleFactory.getModule(ContextNames.SPACE);

		FacilioForm defaultForm = new FacilioForm();
		defaultForm.setName("default_space_web_building");
		defaultForm.setModule(spaceModule);
		defaultForm.setDisplayName("Standard");
		defaultForm.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
		defaultForm.setShowInWeb(true);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
		fields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
		fields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
		fields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building Associated", FormField.Required.REQUIRED,"building", 5, 1,true));
		fields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
		fields.add(new FormField("location", FieldDisplayType.GEO_LOCATION, "Location", Required.OPTIONAL, 7, 1));

		try {
			if(AccountUtil.isFeatureEnabled(FeatureLicense.RESOURCE_BOOKING)) {
				fields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
				fields.add(new FormField("unitReservationCost", FieldDisplayType.TEXTBOX, "Unit Reservation Cost", Required.OPTIONAL, 9, 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		defaultForm.setFields(fields);
		return defaultForm;
	}
	
	public static FacilioForm getSpaceFormFromFloor() {
		FacilioModule spaceModule = ModuleFactory.getModule(ContextNames.SPACE);

		FacilioForm defaultForm = new FacilioForm();
		defaultForm.setName("default_space_web_floor");
		defaultForm.setModule(spaceModule);
		defaultForm.setDisplayName("Standard");
		defaultForm.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
		defaultForm.setShowInWeb(true);
		
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
		fields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
		fields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
		fields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor Associated", FormField.Required.REQUIRED,"floor", 5, 1,true));
		fields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
		fields.add(new FormField("location", FieldDisplayType.GEO_LOCATION, "Location", Required.OPTIONAL, 7, 1));
		
		try {
			if(AccountUtil.isFeatureEnabled(FeatureLicense.RESOURCE_BOOKING)) {
				fields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
				fields.add(new FormField("unitReservationCost", FieldDisplayType.TEXTBOX, "Unit Reservation Cost", Required.OPTIONAL, 9, 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		defaultForm.setFields(fields);
		return defaultForm;
	}
	
	public static FacilioForm getSpaceFormFromSpace() {
		FacilioModule spaceModule = ModuleFactory.getModule(ContextNames.SPACE);

		FacilioForm defaultForm = new FacilioForm();
		defaultForm.setName("default_space_web_space");
		defaultForm.setModule(spaceModule);
		defaultForm.setDisplayName("Standard");
		defaultForm.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
		defaultForm.setShowInWeb(true);
		
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
		fields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
		fields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
		fields.add(new FormField("parentSpace", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space Associated", FormField.Required.REQUIRED,"space", 5, 1,true));
		fields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
		fields.add(new FormField("location", FieldDisplayType.GEO_LOCATION, "Location", Required.OPTIONAL, 7, 1));
		
		try {
			if(AccountUtil.isFeatureEnabled(FeatureLicense.RESOURCE_BOOKING)) {
				fields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
				fields.add(new FormField("unitReservationCost", FieldDisplayType.TEXTBOX, "Unit Reservation Cost", Required.OPTIONAL, 9, 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		defaultForm.setFields(fields);
		return defaultForm;
	}

	public static FacilioForm getBuildingForm() {
		FacilioModule buildingModule = ModuleFactory.getModule(ContextNames.BUILDING);

		FacilioForm defaultForm = new FacilioForm();
		defaultForm.setName("default_building_web");
		defaultForm.setModule(buildingModule);
		defaultForm.setDisplayName("Standard");
		defaultForm.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
		defaultForm.setShowInWeb(true);

//		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(buildingModule.getName()));
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
		fields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 3, 1));
		fields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 3, 1));
		fields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, 4, 1));

		fields.add(new FormField("location", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Location", FormField.Required.OPTIONAL, 5, 1));
		fields.add(new FormField("managedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Managed By", FormField.Required.OPTIONAL, 6, 1));
		fields.add(new FormField("grossFloorArea", FacilioField.FieldDisplayType.DECIMAL, "Gross Floor Area", FormField.Required.OPTIONAL, 7, 1));
		defaultForm.setFields(fields);
		return defaultForm;
	}

	public static FacilioForm getSiteForm() {
		FacilioModule siteModule = ModuleFactory.getModule(ContextNames.SITE);

		FacilioForm defaultForm = new FacilioForm();
		defaultForm.setName("default_site_web");
		defaultForm.setModule(siteModule);
		defaultForm.setDisplayName("Standard");
		defaultForm.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
		defaultForm.setShowInWeb(true);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
		fields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 3, 1));
		fields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 3, 1));
		fields.add(new FormField("location", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Location", FormField.Required.OPTIONAL, 4, 1));
		fields.add(new FormField("managedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Managed By", FormField.Required.OPTIONAL, 5, 1));
		fields.add(new FormField("siteType", FacilioField.FieldDisplayType.NUMBER, "Site Type", FormField.Required.OPTIONAL, 6, 1));
		fields.add(new FormField("grossFloorArea", FacilioField.FieldDisplayType.DECIMAL, "Gross Floor Area", FormField.Required.OPTIONAL, 7, 1));
		fields.add(new FormField("weatherStation", FacilioField.FieldDisplayType.NUMBER, "Weather Station", FormField.Required.OPTIONAL, 8, 1));
		fields.add(new FormField("cddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "CDD Base Temperature", FormField.Required.OPTIONAL, 9, 1));
		fields.add(new FormField("hddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "HDD Base Temperature", FormField.Required.OPTIONAL, 10, 1));
		fields.add(new FormField("wddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "WDD Base Temperature", FormField.Required.OPTIONAL, 11, 1));
		fields.add(new FormField("timeZone", FacilioField.FieldDisplayType.TEXTBOX, "Time Zone", FormField.Required.OPTIONAL, 12, 1));
		fields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.OPTIONAL, 13, 1));
		fields.add(new FormField("boundaryRadius", FacilioField.FieldDisplayType.NUMBER, "Boundary Radius", FormField.Required.OPTIONAL, 14, 1));
		defaultForm.setFields(fields);
		return defaultForm;
	}

	public static FacilioForm getWebWorkOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Standard");
		form.setName("default_workorder_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getWebWorkOrderFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getAlarmWorkOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Alarm Workorder");
		form.setName("alarm_workorder_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getAlarmWorkOrderFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		form.setHideInList(true);
		form.setIgnoreCustomFields(true);
		return form;
	}
	
	private static FacilioForm getAssetForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Standard");
		form.setName("default_asset_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ASSET));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		form.setFields(getWebAssetFormFields());
		return form;
	}
	
	private static FacilioForm getEnergyMeterForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Energy Meter");
		form.setName("default_energymeter_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ENERGY_METER));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		form.setFields(getWebEnergyMeterFormFields());
		return form;
	}
	
	private static FacilioForm getTenantForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Tenant");
		form.setName("default_tenant_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TENANT));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		form.setFields(getTenantsFormField());
		return form;
	}
	
	private static FacilioForm getPMForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PREVENTIVE MAINTENANCE");
		form.setName("web_pm");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		form.setFields(getWebPMFormFields());
		return form;
	}

	private static FacilioForm getMultiPMForm()  {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PREVENTIVE MAINTENANCE");
		form.setName("multi_web_pm");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		form.setFields(getMultiSiteWebPMFormFields());
		form.setHideInList(true);
		return form;
	}
	
	public static FacilioForm getStoreRoomForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW STORE ROOM");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.STORE_ROOM));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getStoreRoomFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getItemTypesForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW ITEM TYPE");
		form.setName("default_itemTypes_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ITEM_TYPES));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getItemTypesFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getInventoryCategoryForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW CATEGORY");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.INVENTORY_CATEGORY));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getInventoryCategoryFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	
	public static FacilioForm getLocationForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW LOCATION");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.LOCATION));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getLocationFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	public static FacilioForm getTooltypesForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW TOOL TYPE");
		form.setName("default_toolTypes_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TOOL_TYPES));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getToolTypesFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getVendorsForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW VENDOR");
		form.setName("default_vendors_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VENDORS));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVendorsFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getVendorContactForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW VENDOR");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VENDORS));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVendorContactFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getPortalVendorForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW VENDOR");
		form.setName("default_vendors_portal");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VENDORS));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPortalVendorFormField());
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		return form;
	}


	public static FacilioForm getTenantsForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("TENANT");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TENANT));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getTenantsFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

    public static FacilioForm getItemForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("ADD ITEM");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ITEM));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getItemFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	public static FacilioForm getItemWithIndTrackForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("ADD ITEM");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.ITEM));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getItemWithIndTrackFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getStockedToolsForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("ADD TOOL");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TOOL));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getToolFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	public static FacilioForm getToolWithIndTrackForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("ADD TOOL");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TOOL));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getToolTrackWithIndFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getClientForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW CLIENT");
		form.setName("default_client_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.CLIENT));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getClientFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	public static FacilioForm geTenantUnitSpaceForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW TENANT UNIT");
		form.setName("default_tenantunit_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getTenantUnitFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	public static FacilioForm getTenantContactForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW TENANT CONTACT");
		form.setName("default_tenantcontact_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TENANT_CONTACT));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getTenantContactsFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	public static FacilioForm getTenantContactPortalForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW TENANT CONTACT");
		form.setName("default_tenantcontact_portal");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.TENANT_CONTACT));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getTenantContactsPortalFormField());
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		return form;
	}

	public static FacilioForm getNewVendorContactForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW VENDOR CONTACT");
		form.setName("default_vendorcontact_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getVendorContactsFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	public static FacilioForm getClientContactForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW VENDOR CONTACT");
		form.setName("default_clientcontact_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getClientContactsFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	public static FacilioForm geEmployeeContactForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW EMPLOYEE");
		form.setName("default_employee_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.EMPLOYEE));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getEmployeeContactsFormField());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
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
	//	fields.add(new FormField("workPermitNeeded", FieldDisplayType.DECISION_BOX, "Work Permit Needed", Required.OPTIONAL, 12, 1));
	//	fields.add(new FormField("safetyPlan", FieldDisplayType.LOOKUP_SIMPLE, "Safety Plan", Required.OPTIONAL,"safetyPlan", 12, 1));


		fields.add(new FormField("tasks", FieldDisplayType.TASKS, "TASKS", Required.OPTIONAL, 13, 1));
		return Collections.unmodifiableList(fields);
	}
	
	private static List<FormField> getAlarmWorkOrderFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "ticketcategory", 2, 1));
		fields.add(new FormField("assignment", FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 4, 1));
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
		//fields.add(new FormField("workPermitNeeded", FieldDisplayType.DECISION_BOX, "Work Permit Needed", Required.OPTIONAL, 11, 1));
		//fields.add(new FormField("safetyPlan", FieldDisplayType.LOOKUP_SIMPLE, "Safety Plan", Required.OPTIONAL,"safetyPlan", 11, 1));

		
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
		fields.add(new FormField("duration", FieldDisplayType.DURATION, "Due Duration", Required.OPTIONAL, "duration", 6, 1));
		fields.add(new FormField("estimatedWorkDuration", FieldDisplayType.DURATION, "Estimated Duration", Required.OPTIONAL, "duration", 7, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, "vendors", 11, 1));
		fields.add(new FormField("groups",FieldDisplayType.LOOKUP_SIMPLE,"Team", Required.OPTIONAL, "groups", 8, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, "attachment", 9, 1));
		return Collections.unmodifiableList(fields);
	}

	private static List<FormField> getMultiSiteWebPMFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("type",FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", Required.REQUIRED, "tickettype", 2, 1));
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 3, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "ticketcategory", 5, 2));
		fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 5, 3));
		fields.add(new FormField("duration", FieldDisplayType.DURATION, "Due Duration", Required.OPTIONAL, "duration", 6, 1));
		fields.add(new FormField("estimatedWorkDuration", FieldDisplayType.DURATION, "Estimated Duration", Required.OPTIONAL, "duration", 7, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, "vendors", 11, 1));
		fields.add(new FormField("groups",FieldDisplayType.LOOKUP_SIMPLE,"Team", Required.OPTIONAL, "groups", 8, 1));
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, "attachment", 9, 1));
		return Collections.unmodifiableList(fields);
	}
	
	private static List<FormField> getWebAssetFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, "name", 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 2));
		FormField categoryField = new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.REQUIRED, "assetcategory", 4, 2);
		categoryField.setIsDisabled(true);
		fields.add(categoryField);
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
	
	private static List<FormField> getWebEnergyMeterFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, "name", 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 2));
		FormField categoryField = new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.REQUIRED, "assetcategory", 4, 2);
		categoryField.setIsDisabled(true);
		fields.add(categoryField);
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
		fields.add(new FormField("childMeterExpression", FieldDisplayType.TEXTBOX, "Expression", Required.OPTIONAL, 15, 2));
		fields.add(new FormField("isVirtual", FieldDisplayType.DECISION_BOX, "Is Virtual", Required.OPTIONAL, 16, 2));
		fields.add(new FormField("purpose", FieldDisplayType.LOOKUP_SIMPLE, "Purpose", Required.OPTIONAL, 17, 2));
		fields.add(new FormField("purposeSpace", FieldDisplayType.SPACECHOOSER, "Operational Space", Required.OPTIONAL, 18, 2));
		fields.add(new FormField("root", FieldDisplayType.DECISION_BOX, "Is Root", Required.OPTIONAL, 19, 2));
		fields.add(new FormField("multiplicationFactor", FieldDisplayType.TEXTBOX, "Multiplication Factor", Required.OPTIONAL, 20, 2));
		
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
		urgency.setValueObject(WOUrgency.NOTURGENT.getValue());
		fields.add(urgency);
		fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachment", Required.OPTIONAL, 6, 1));
		return Collections.unmodifiableList(fields);
	}
	
	public static List<FormField> getRequesterFormFields(boolean getRequesterDetails, boolean isMandatory) throws Exception {
		List<FormField> fields = new ArrayList<>();
		Required required = isMandatory ? Required.REQUIRED : Required.OPTIONAL;
		if (AccountUtil.getCurrentAccount().isFromMobile() || getRequesterDetails) {
			fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Requester Name", required, 1, 1));
			fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Requester Email", required, 2, 1));
		}
		else {
			fields.add(new FormField("requester", FieldDisplayType.REQUESTER, "Requester", required, ContextNames.REQUESTER, 1, 1));
		}
		return fields;
	}
	
	
	private static List<FormField> getStoreRoomFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("site", FieldDisplayType.LOOKUP_SIMPLE, "Located Site", Required.REQUIRED, "site", 3, 2));
		fields.add(new FormField("location", FieldDisplayType.LOOKUP_SIMPLE, "Location", Required.OPTIONAL, "location", 3, 3).setAllowCreateOptions(true).setCreateFormName("location_form"));
		fields.add(new FormField("owner", FieldDisplayType.USER, "Owner", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("sites", FieldDisplayType.SITEMULTICHOOSER, "Serving Sites", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("isApprovalNeeded", FieldDisplayType.DECISION_BOX, "Approval Needed", Required.OPTIONAL, 6, 2));
		fields.add(new FormField("isGatePassRequired", FieldDisplayType.DECISION_BOX, "Gate Pass Needed", Required.OPTIONAL, 6, 3));

		return fields;
	}
	
	private static List<FormField> getItemTypesFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("photoId", FieldDisplayType.IMAGE, "Photo", Required.OPTIONAL, 1, 1));
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "inventoryCategory", 4, 2).setAllowCreateOptions(true).setCreateFormName("item_category_form"));
		fields.add(new FormField("sellingPrice", FieldDisplayType.NUMBER, "Selling Price", Required.OPTIONAL,  5, 2));
		fields.add(new FormField("minimumQuantity", FieldDisplayType.NUMBER, "Minimum Quantity", Required.OPTIONAL, 5, 3));
		fields.add(new FormField("isRotating", FieldDisplayType.DECISION_BOX, "Is Rotating", Required.OPTIONAL, 6, 2));
		fields.add(new FormField("isApprovalNeeded", FieldDisplayType.DECISION_BOX, "Approval Needed", Required.OPTIONAL, 7, 3));
		fields.add(new FormField("isConsumable", FieldDisplayType.DECISION_BOX, "To Be Issued", Required.OPTIONAL, 8, 2));
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
		fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "inventoryCategory", 3, 1).setAllowCreateOptions(true).setCreateFormName("item_category_form"));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("isRotating", FieldDisplayType.DECISION_BOX, "Is Rotating", Required.OPTIONAL, 6, 2));
		fields.add(new FormField("isApprovalNeeded", FieldDisplayType.DECISION_BOX, "Approval Needed", Required.OPTIONAL, 7, 3));

		return fields;
	}
	
	private static List<FormField> getVendorsFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("website", FieldDisplayType.TEXTBOX, "Website", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("primaryContactName", FieldDisplayType.TEXTBOX, "Primary Contact Name", Required.REQUIRED, 4, 1));
		fields.add(new FormField("primaryContactEmail", FieldDisplayType.TEXTBOX, "Primary Contact E-mail", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("primaryContactPhone", FieldDisplayType.TEXTBOX, "Primary Contact Phone", Required.REQUIRED, 6, 1));
		
		fields.add(new FormField("address", FieldDisplayType.ADDRESS, "Address", Required.OPTIONAL, 7, 1));
	//	fields.add(new FormField("registeredBy", FieldDisplayType.LOOKUP_SIMPLE, "Registered By", Required.OPTIONAL, "requester",8, 1));
		
		return fields;
	}

	private static List<FormField> getVendorContactFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("website", FieldDisplayType.TEXTBOX, "Website", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("primaryContactName", FieldDisplayType.TEXTBOX, "Primary Contact Name", Required.REQUIRED, 4, 1));
		fields.add(new FormField("primaryContactEmail", FieldDisplayType.TEXTBOX, "Primary Contact E-mail", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("primaryContactPhone", FieldDisplayType.TEXTBOX, "Primary Contact Phone", Required.REQUIRED, 6, 1));
		
		fields.add(new FormField("address", FieldDisplayType.ADDRESS, "Address", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("registeredBy", FieldDisplayType.LOOKUP_SIMPLE, "Registered By", Required.OPTIONAL, "requester",8, 1));
		
		fields.add(new FormField("vendorContacts", FieldDisplayType.VENDOR_CONTACTS, "Contacts", Required.OPTIONAL, 9, 1));
		return fields;
	}

	
	private static List<FormField> getPortalVendorFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("website", FieldDisplayType.TEXTBOX, "Website", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("primaryContactName", FieldDisplayType.TEXTBOX, "Contact Name", Required.REQUIRED, 4, 1));
		fields.add(new FormField("primaryContactEmail", FieldDisplayType.TEXTBOX, "Contact E-mail", Required.REQUIRED, 5, 1));
		fields.add(new FormField("primaryContactPhone", FieldDisplayType.TEXTBOX, "Contact Phone", Required.REQUIRED, 6, 1));
		fields.add(new FormField("address", FieldDisplayType.ADDRESS, "Address", Required.OPTIONAL, 7, 1));

		return fields;
	}


	private static List<FormField> getTenantsFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("avatar",FieldDisplayType.IMAGE,"Tenant Logo",Required.OPTIONAL,1,1));
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 4, 1));
		fields.add(new FormField("primaryContactName", FieldDisplayType.TEXTBOX, "Primary Contact Name", Required.REQUIRED, 6, 1));
		fields.add(new FormField("primaryContactEmail", FieldDisplayType.TEXTBOX, "Primary Contact E-mail", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("primaryContactPhone", FieldDisplayType.TEXTBOX, "Primary Contact Phone", Required.REQUIRED, 8, 1));
		fields.add(new FormField("tenantType", FieldDisplayType.SELECTBOX, "Tenant Type", Required.OPTIONAL, 9, 1));
		fields.add(new FormField("inTime", FieldDisplayType.DATE, "Lease Start Date", Required.OPTIONAL, 10, 1));
		fields.add(new FormField("outTime", FieldDisplayType.DATE, "Lease End Date", Required.OPTIONAL, 11, 1));
		fields.add(new FormField("address", FieldDisplayType.ADDRESS, "Address", Required.OPTIONAL, 12, 1));
//		fields.add(new FormField("spaces",  FieldDisplayType.SPACEMULTICHOOSER, "Spaces", Required.REQUIRED, 5, 1));
		//fields.add(new FormField("logo", FieldDisplayType.LOGO, "Logo", Required.OPTIONAL, 1, 1));
		//fields.add(new FormField("tenantContacts", FieldDisplayType.VENDOR_CONTACTS , "Contacts", Required.OPTIONAL, 10, 1));
		//fields.add(new FormField("utilityMeters", FieldDisplayType.ASSETMULTICHOOSER, "UTILITY METERS", Required.OPTIONAL, 12, 1));

		return fields;
	}

	private static List<FormField> getTenantContactsFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.REQUIRED, "tenant", 4, 1));
		fields.add(new FormField("isPrimaryContact", FieldDisplayType.DECISION_BOX, "Primary Contact", Required.OPTIONAL, 5, 1));
		
		return fields;
	}

	private static List<FormField> getTenantContactsPortalFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.REQUIRED, "tenant", 4, 1));

		return fields;
	}

	private static List<FormField> getVendorContactsFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED, "vendors", 4, 1));
		fields.add(new FormField("isPrimaryContact", FieldDisplayType.DECISION_BOX, "Primary Contact", Required.OPTIONAL, 5, 1));

		return fields;
	}

	private static List<FormField> getClientContactsFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("client", FieldDisplayType.LOOKUP_SIMPLE, "Client", Required.REQUIRED, "client", 4, 1));
		fields.add(new FormField("isPrimaryContact", FieldDisplayType.DECISION_BOX, "Primary Contact", Required.OPTIONAL, 5, 1));

		return fields;
	}

	private static List<FormField> getEmployeeContactsFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("isAssignable", FieldDisplayType.DECISION_BOX, "Is Assignable", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("isLabour", FieldDisplayType.DECISION_BOX, "Is Labour", Required.OPTIONAL, 5, 3));


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
		fields.add(new FormField("storeRoom", FieldDisplayType.LOOKUP_SIMPLE, "Store Room", Required.REQUIRED, "storeRoom", 2, 1).setAllowCreateOptions(true).setCreateFormName("store_room_form"));
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
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getPurchaseRequestForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PURCHASE REQUEST");
		form.setName("default_purchaserequest_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getPurchaseRequestFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	
	public static FacilioForm getPurchaseOrderForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PURCHASE ORDER");
		form.setName("default_purchaseorder_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getPurchaseOrderFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getPurchaseContractForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PURCHASE CONTRACT");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACTS));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getPurchaseContractFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	public static FacilioForm getLabourContractForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("LABOUR CONTRACT");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.LABOUR_CONTRACTS));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getLabourContractFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	public static FacilioForm getPoRequesterForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("REQUESTER");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.REQUESTER));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPoRequesterFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getConnectedAppForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("CONNECTED APP");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.CONNECTED_APPS));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getConnectedAppFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	private static List<FormField> getLabourFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 4, 1));
		//fields.add(new FormField("address", FieldDisplayType.TEXTAREA, "Address", Required.OPTIONAL,5, 1));
		fields.add(new FormField("location", FieldDisplayType.ADDRESS, "Address", Required.OPTIONAL,5, 1));
		fields.add(new FormField("user", FieldDisplayType.USER, "User", Required.OPTIONAL, 6, 1));
		//fields.add(new FormField("unitType", FieldDisplayType.SELECTBOX, "Wage Type", Required.REQUIRED, 7, 1));
		fields.add(new FormField("cost", FieldDisplayType.DECIMAL, "Rate Per Hour", Required.OPTIONAL, 8, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.OPTIONAL, "site", 2, 1));
		fields.add(new FormField("availability", FieldDisplayType.DECISION_BOX, "Active", Required.OPTIONAL, 9, 1));
		
		return fields;
	}

	private static List<FormField> getPurchaseRequestFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, "vendors", 3, 2).setAllowCreateOptions(true).setCreateFormName("vendors_form"));
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
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED, "vendors", 3, 2).setAllowCreateOptions(true).setCreateFormName("vendors_form"));
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
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED, "vendors", 3, 2).setAllowCreateOptions(true).setCreateFormName("vendors_form"));
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
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED, "vendors", 3, 1).setAllowCreateOptions(true).setCreateFormName("vendors_form"));
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
		fields.add(new FormField("sandBoxBaseUrl", FieldDisplayType.TEXTBOX, "Sandbox Base URL", Required.REQUIRED, 4, 1));
		fields.add(new FormField("productionBaseUrl", FieldDisplayType.TEXTBOX, "Production Base URL", Required.REQUIRED, 4, 1));
		fields.add(new FormField("startUrl", FieldDisplayType.TEXTBOX, "Welcome URL", Required.REQUIRED, 5, 1));
		fields.add(new FormField("showInLauncher", FieldDisplayType.DECISION_BOX, "Show in Launcher", Required.OPTIONAL, 6, 1));
		return fields;
	}
	
	public static FacilioForm getInventoryRequestForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("INVENTORY REQUEST");
		form.setName("default_inventoryrequest_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getInventoryRequestFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
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
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
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
		form.setName("default_service_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.SERVICE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getServiceFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	private static List<FormField> getServiceFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("duration", FieldDisplayType.NUMBER, "Duration(Hr)", Required.REQUIRED, 3, 1));
		fields.add(new FormField("paymentType", FieldDisplayType.SELECTBOX, "Payment Type", Required.REQUIRED, 4, 1));
		fields.add(new FormField("buyingPrice", FieldDisplayType.DECIMAL, "Buying Price", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("sellingPrice", FieldDisplayType.DECIMAL, "Selling Price", Required.OPTIONAL, 6, 1));

		//fields.add(new FormField("serviceVendors", FieldDisplayType.SERVICEVENDORS, "Service Vendors", Required.REQUIRED, 6, 1));

		return fields;
	}
	
	public static FacilioForm getRentalLeaseContractForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("LEASE/RENTAL CONTRACT");
		form.setName("default_rentalleasecontracts_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getRentalLeaseFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	private static List<FormField> getRentalLeaseFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED, "vendors", 3, 2).setAllowCreateOptions(true).setCreateFormName("vendors_form"));
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
		form.setName("warrantyContractForm");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getWarrantyContractFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	private static List<FormField> getWarrantyContractFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED, "vendors", 3, 2).setAllowCreateOptions(true).setCreateFormName("vendors_form"));
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
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
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
		form.setName("default_reservation_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.Reservation.RESERVATION));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getReservationFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
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
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
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

	public static FacilioForm getPrinterForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Printers");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ModuleNames.PRINTERS));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPrinterFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getVendorDocumentForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Vendor Document");
		form.setName("default_vendorDocuments_web");
		form.setModule(ModuleFactory.getVendorDocumentsModule());
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVendorDocumentsFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getPortalVendorDocumentForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Vendor Document");
		form.setName("default_vendorDocuments_portal");
		form.setModule(ModuleFactory.getVendorDocumentsModule());
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVendorDocumentsFormFields());
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		return form;
	}
	
	private static List<FormField> getVendorDocumentsFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("documentName", FieldDisplayType.TEXTBOX, "Document Name", Required.REQUIRED, 1, 1));
		FormField type = new FormField("documentType", FieldDisplayType.SELECTBOX, "Document Type", Required.OPTIONAL, 2, 1);
		type.setAllowCreateOptions(true);
		fields.add(type);
		fields.add(new FormField("document", FieldDisplayType.FILE, "Document", Required.REQUIRED,3, 1));
		return fields;
	}
	
	private static List<FormField> getPrinterFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("ip", FieldDisplayType.TEXTBOX, "IP Address", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("model", FieldDisplayType.SELECTBOX, "Printer Model", Required.REQUIRED,3, 1));
		fields.add(new FormField("connectionMode", FieldDisplayType.SELECTBOX, "Printer Model", Required.REQUIRED,4, 1));
		return fields;
	}
	
	
	public static FacilioForm getVisitorKioskForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("VISITOR");
		form.setName("default_visitor_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getVisitorKioskFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
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
		form.setName("portal_visitor_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVisitorFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getPortalVisitorForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("VISITOR");
		form.setName("default_visitor_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVisitorFormFields());
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		return form;
	}

	private static List<FormField> getVisitorFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("avatar",FieldDisplayType.IMAGE,"Visitor Photo",Required.OPTIONAL,1,1));
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.REQUIRED, 3, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.REQUIRED, 4, 1));
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
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
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
		form.setName("default_visitorlogging_portal");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPotalVisitorPreRegisterFormFields());
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		return form;
	}

	private static List<FormField> getPotalVisitorPreRegisterFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("visitorPhone", FieldDisplayType.TEXTBOX, "Visitor Phone", Required.REQUIRED, 1, 1));
		fields.add(new FormField("visitorName", FieldDisplayType.TEXTBOX, "Visitor Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("visitorEmail", FieldDisplayType.TEXTBOX, "Visitor Email", Required.REQUIRED, 3, 1));
		fields.add(new FormField("visitedSpace", FieldDisplayType.SPACECHOOSER, "Space", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("inviteHost", FieldDisplayType.LOOKUP_SIMPLE, "Host", Required.OPTIONAL, "contact", 5, 1));
		fields.add(new FormField("expectedCheckInTime", FieldDisplayType.DATETIME, "Expected Check-in Time", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("expectedCheckOutTime", FieldDisplayType.DATETIME, "Expected Check-out Time", Required.OPTIONAL, 7, 1));
		fields.add(new FormField("purposeOfVisit", FieldDisplayType.SELECTBOX, "Purpose of Visit", Required.OPTIONAL, 8, 1));

		return fields;
	}
	
	public static FacilioForm getGuestForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Guest form for Vistor module");
		form.setName("Guest");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getGuestFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	private static List<FormField> getGuestFormFields()
	{
		List<FormField> fields=new ArrayList<>();
		
		fields.add(new FormField("host",FieldDisplayType.LOOKUP_SIMPLE,"Who do you want to meet",Required.OPTIONAL, "user",1,1));
		fields.add(new FormField("purposeOfVisit",FieldDisplayType.SELECTBOX,"What is the purpose of visit",Required.OPTIONAL,1,1).setAllowCreateOptions(true));
		fields.add(new FormField("visitor", FieldDisplayType.LOOKUP_SIMPLE, "Visitor", Required.OPTIONAL, "visitor", 1, 1));
		fields.add(new FormField("avatar",FieldDisplayType.IMAGE,"Visitor Photo",Required.OPTIONAL,1,1));
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
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	private static List<FormField> getVendorFormFields()
	{
		List<FormField> fields=new ArrayList<>();
		fields.add(new FormField("purposeOfVisit",FieldDisplayType.SELECTBOX,"What is the purpose of visit",Required.OPTIONAL,1,1).setAllowCreateOptions(true));
		fields.add(new FormField("host",FieldDisplayType.LOOKUP_SIMPLE,"Who do you want to meet",Required.OPTIONAL, "user",1,1));
		fields.add(new FormField("avatar",FieldDisplayType.IMAGE,"Visitor Photo",Required.OPTIONAL,1,1));
		fields.add(new FormField("visitor", FieldDisplayType.LOOKUP_SIMPLE, "Visitor", Required.OPTIONAL, "visitor", 1, 1));
		
		return fields;
	}
	
	public static FacilioForm getEmployeeForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Employee form for Vistor module");
		form.setName("Employee");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getEmployeeFormFields());
		
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	private static List<FormField> getEmployeeFormFields()
	{
		List<FormField> fields=new ArrayList<>();
		fields.add(new FormField("visitor", FieldDisplayType.LOOKUP_SIMPLE, "Visitor", Required.OPTIONAL, "visitor", 1, 1));
		fields.add(new FormField("avatar",FieldDisplayType.IMAGE,"Visitor Photo",Required.OPTIONAL,1,1));
		
		return fields;
	}

	public static FacilioForm getVisitorLogForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("VISITS");
		form.setName("web_default");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getVisitorLogFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	private static List<FormField> getVisitorLogFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("visitor", FieldDisplayType.LOOKUP_SIMPLE, "Visitor", Required.REQUIRED, "visitor", 1, 1).setAllowCreateOptions(true).setCreateFormName("visitorForm"));
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
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	private static List<FormField> getWatchListFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("avatar",FieldDisplayType.IMAGE,"Visitor Photo",Required.OPTIONAL,1,1));
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.REQUIRED, 3, 2));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 4, 3));
		fields.add(new FormField("aliases", FieldDisplayType.LONG_DESC, "Aliases", Required.OPTIONAL, 5, 2));
		fields.add(new FormField("physicalDescription", FieldDisplayType.LONG_DESC, "Physical Description", Required.OPTIONAL, 6, 2));
		fields.add(new FormField("isBlocked", FieldDisplayType.DECISION_BOX, "Blocked Entry", Required.OPTIONAL, 7, 2));
		fields.add(new FormField("isVip", FieldDisplayType.DECISION_BOX, "VIP", Required.OPTIONAL, 8, 3));
		fields.add(new FormField("remarks", FieldDisplayType.LONG_DESC, "Remarks", Required.OPTIONAL, 9, 2));
		
		return fields;
	}
	
	public static FacilioForm getWorkPermitForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("WORK PERMIT");
		form.setName("default_workpermit_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getWorkPermitFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getPortalWorkPermitForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("WORK PERMIT");
		form.setName("default_workpermit_portal");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPortalWorkPermitFormFields());
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		return form;
	}
	
	private static List<FormField> getWorkPermitFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 2));
		fields.add(new FormField("space", FieldDisplayType.SPACECHOOSER, "Location", Required.OPTIONAL, "basespace", 3, 3));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, "vendors", 4, 2));
		fields.add(new FormField("people", FieldDisplayType.LOOKUP_SIMPLE, "Contact", Required.OPTIONAL, "people", 4, 3));
		fields.add(new FormField("expectedStartTime", FieldDisplayType.DATETIME, "Valid From", Required.REQUIRED, 5, 2));
		fields.add(new FormField("expectedEndTime", FieldDisplayType.DATETIME, "Valid To", Required.REQUIRED, 5, 3));
		fields.add(new FormField("ticket", FieldDisplayType.LOOKUP_SIMPLE, "Work Order", Required.OPTIONAL, "ticket", 6, 1));
		fields.add(new FormField("workPermitType", FieldDisplayType.LOOKUP_SIMPLE, "Permit Type", Required.OPTIONAL, "workPermitType", 7, 1));

		fields.add(new FormField("checklist", FieldDisplayType.PERMIT_CHECKLIST, "Checklist", Required.OPTIONAL, 8, 1));
	
		return fields;
	}
	
	private static List<FormField> getPortalWorkPermitFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Permit Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("expectedStartTime", FieldDisplayType.DATETIME, "Valid From", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("expectedEndTime", FieldDisplayType.DATETIME, "Valid To", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED,"vendors", 6, 1));
		fields.add(new FormField("people", FieldDisplayType.LOOKUP_SIMPLE, "Contact", Required.OPTIONAL, "people",7, 1));
		return fields;
	}

	public static FacilioForm getInsuranceForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("INSURANCE");
		form.setName("default_insurance_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.INSURANCE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getInsuranceFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	public static FacilioForm getPortalInsuranceForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("INSURANCE");
		form.setName("default_insurance_portal");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.INSURANCE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPortalInsuranceFormFields());
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		return form;
	}
	
	private static List<FormField> getInsuranceFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("companyName", FieldDisplayType.TEXTBOX, "Company Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("validFrom", FieldDisplayType.DATE, "Valid From", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("validTill", FieldDisplayType.DATE, "Valid Till", Required.OPTIONAL, 3, 1));
		FormField vendorField = new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED,"vendors", 3, 2);
	//	vendorField.setHideField(true);
		fields.add(vendorField);
		fields.add(new FormField("insurance", FieldDisplayType.FILE, "Insurance", Required.OPTIONAL, 1, 1));
		return fields;
	}
	
	private static List<FormField> getPortalInsuranceFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("companyName", FieldDisplayType.TEXTBOX, "Company Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("validFrom", FieldDisplayType.DATE, "Valid From", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("validTill", FieldDisplayType.DATE, "Valid Till", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("insurance", FieldDisplayType.FILE, "Insurance", Required.OPTIONAL, 1, 1));
		return fields;
	}

	public static FacilioForm getPortalContactForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("CONTACT");
		form.setName("default_contact_portal");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.CONTACT));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getContactFormFields());
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);
		return form;
	}

	public static FacilioForm getContactForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("CONTACT");
		form.setName("default_contact_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.CONTACT));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getContactFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	private static List<FormField> getContactFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.REQUIRED, 2, 2));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 2, 3));
		fields.add(new FormField("contactType", FieldDisplayType.SELECTBOX, "Contact Type", Required.REQUIRED, 3, 1));
		fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, "vendors",4, 2));
		fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.OPTIONAL, "tenant",4, 3));
		fields.add(new FormField("client", FieldDisplayType.LOOKUP_SIMPLE, "Client", Required.OPTIONAL, "client",5, 2));
		//fields.add(new FormField("isPortalAccessNeeded", FieldDisplayType.DECISION_BOX, "Is Portal Access Needed", Required.OPTIONAL, 6, 2));
		fields.add(new FormField("isPrimaryContact", FieldDisplayType.DECISION_BOX, "Is Primary Contact", Required.OPTIONAL, 6, 3));

		return fields;
	}

	public static FacilioForm getOccupantForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("OCCUPANT");
		form.setName("default_occupant_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.OCCUPANT));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getOccupantFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	private static List<FormField> getOccupantFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.REQUIRED, 2, 2));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.REQUIRED, 2, 3));
		fields.add(new FormField("occupantType", FieldDisplayType.SELECTBOX, "Occupant Type", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.OPTIONAL, "tenant",4, 1));
		fields.add(new FormField("isPortalAccessNeeded", FieldDisplayType.DECISION_BOX, "Is Portal Access Needed", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("locatedSpace", FieldDisplayType.WOASSETSPACECHOOSER, "Located Space", Required.OPTIONAL, 6, 1));

		return fields;
	}

	public static FacilioForm getServiceRequestForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("SERVICE REQUEST");
		form.setName("default_serviceRequest_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getServiceRequestFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	private static List<FormField> getServiceRequestFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 1));
		fields.add(new FormField("resource", FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("assignment", FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("urgency", FieldDisplayType.LOOKUP_SIMPLE, "Urgency", Required.OPTIONAL, "servicerequestpriority", 6, 1));
		fields.add(new FormField("classification", FieldDisplayType.SELECTBOX, "Classification", Required.OPTIONAL, "classification" , 7, 1));
		fields.add(new FormField("requestType", FieldDisplayType.SELECTBOX, "Request Type", Required.OPTIONAL, "requestType" , 8, 1));
		fields.add(new FormField("ratingVal", FieldDisplayType.NUMBER, "Rating", Required.OPTIONAL, 9, 1));
		return fields;
	}

	private static List<FormField> getSafetyPlanFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.OPTIONAL,"site", 3, 1));
		return fields;
	}

	private static List<FormField> getHazardFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("type", FieldDisplayType.SELECTBOX, "Type", Required.OPTIONAL, 3, 1));
		return fields;
	}
	private static List<FormField> getPrecautionFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		return fields;
	}
	public static FacilioForm getSafetyPlanForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("SAFETY PLAN");
		form.setName("default_safetyPlan_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.SAFETY_PLAN));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getSafetyPlanFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	public static FacilioForm getHazardForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("HAZARD");
		form.setName("default_hazard_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.HAZARD));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getHazardFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	public static FacilioForm getPrecautionForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("PREACUTION");
		form.setName("default_precaution_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.PRECAUTION));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPrecautionFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	
	private static List<FormField> getClientFormField() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("website", FieldDisplayType.TEXTBOX, "Website", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("primaryContactName", FieldDisplayType.TEXTBOX, "Primary Contact Name", Required.REQUIRED, 4, 1));
		fields.add(new FormField("primaryContactEmail", FieldDisplayType.TEXTBOX, "Primary Contact E-mail", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("primaryContactPhone", FieldDisplayType.TEXTBOX, "Primary Contact Phone", Required.REQUIRED, 6, 1));
		fields.add(new FormField("address", FieldDisplayType.ADDRESS, "Address", Required.OPTIONAL, 7, 1));
		
		return fields;
	}


	private static List<FormField> getTenantUnitFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("area", FieldDisplayType.NUMBER, "Area", Required.OPTIONAL, 3, 2));
		fields.add(new FormField("maxOccupancy", FieldDisplayType.NUMBER, "Max Occupancy", Required.OPTIONAL, 3, 3));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED,"site", 4, 2));
		fields.add(new FormField("building", FieldDisplayType.LOOKUP_SIMPLE, "Building", Required.OPTIONAL,"building", 4, 3));
		FormField tenant = new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.OPTIONAL,"tenant", 5, 1);
		tenant.setHideField(true);
		fields.add(tenant);
		FormField isOccupied = new FormField("isOccupied", FieldDisplayType.DECISION_BOX, "Occupancy Status", Required.OPTIONAL, 5, 1);
		isOccupied.setHideField(true);
		fields.add(isOccupied);
		
		fields.add(new FormField("floor", FieldDisplayType.LOOKUP_SIMPLE, "Floor", Required.OPTIONAL,"floor", 5, 1));
		
		return fields;
	}

	private static FacilioForm getQuotationForm() {

		FacilioForm form = new FacilioForm();
		form.setDisplayName("Quote");
		form.setName("default_quote_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.QUOTE));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getQuotationFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	private static FacilioForm getAnnouncementForm() {

		FacilioForm form = new FacilioForm();
		form.setDisplayName("Announcement");
		form.setName("default_announcement_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setFields(getAnnouncementFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}
	private static List<FormField> getQuotationFormFields() {


		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("billDate", FieldDisplayType.DATE, "Bill Date", Required.OPTIONAL, 3, 2));
		fields.add(new FormField("expiryDate", FieldDisplayType.DATE, "Expiry Date", Required.REQUIRED, 3, 3));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED,"site", 4, 2));
		fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.REQUIRED,"tenant", 4, 3));
		fields.add(new FormField("contact", FieldDisplayType.LOOKUP_SIMPLE, "Contact", Required.OPTIONAL,"people", 5, 3));

		fields.add(new FormField("workorder", FieldDisplayType.LOOKUP_SIMPLE, "Workorder", Required.OPTIONAL,"workorder", 6, 1));


		fields.add(new FormField("billToAddress", FieldDisplayType.QUOTE_ADDRESS, "Bill To Address", Required.OPTIONAL, 7, 1));

		fields.add(new FormField("lineItems", FieldDisplayType.QUOTE_LINE_ITEMS, "Line Items", Required.REQUIRED, 9, 1));

		fields.add(new FormField("notes", FieldDisplayType.TEXTAREA, "Customer Notes", Required.OPTIONAL, 13, 1));
		fields.add(new FormField("signature", FieldDisplayType.SIGNATURE, "Signature", Required.OPTIONAL, 14, 1));

		return fields;
	}

	public static FacilioForm getWorkPermitTypeForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("WORK PERMIT TYPE");
		form.setName("default_workpermittype_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("type", FieldDisplayType.TEXTBOX, "Type", Required.REQUIRED, 1, 1));
		form.setFields(fields);
		return form;
	}

	public static FacilioForm getWorkPermitTypeChecklistCategoryForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("WORK PERMIT TYPE CHECKLIST CATEGORY");
		form.setName("default_workpermittypechecklistcategory_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST_CATEGORY));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));

		form.setFields(fields);
		return form;
	}

	public static FacilioForm getWorkPermitTypeChecklistForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("WORK PERMIT TYPE CHECKLIST");
		form.setName("default_workpermittypechecklist_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST_CATEGORY));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("item", FieldDisplayType.TEXTBOX, "Item", Required.REQUIRED, 1, 1));
		form.setFields(fields);
		return form;
	}

	private static List<FormField> getAnnouncementFormFields() {


		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("title", FieldDisplayType.TEXTBOX, "Title", Required.REQUIRED, 1, 1));
		FormField descField = new FormField("longDescription", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1);
		descField.addToConfig("richText", true);
		fields.add(descField);
		fields.add(new FormField("expiryDate", FieldDisplayType.DATE, "Expiry Date", Required.OPTIONAL, 3, 3));
		FormField categoryField = new FormField("category", FieldDisplayType.SELECTBOX, "Category", Required.REQUIRED,4, 2);
		categoryField.setAllowCreateOptions(true);
		fields.add(categoryField);

		descField.addToConfig("richText", true);

		FormField attachment = new FormField("announcementattachments", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, 5, 1);
		attachment.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
		fields.add(attachment);
		fields.add(new FormField("announcementsharing", FieldDisplayType.COMMUNITY_PUBLISHING, "Publish To", Required.REQUIRED, 6, 1));
		return fields;
	}

	private static FacilioForm getNewsAndInformationForm() {

		FacilioForm form = new FacilioForm();
		form.setDisplayName("News and Information");
		form.setName("default_"+ FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION +"_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("title", FieldDisplayType.TEXTBOX, "Title", Required.REQUIRED, 1, 1));
		FormField descField = new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1);
		descField.addToConfig("richText", true);
		fields.add(descField);

		FormField attachment = new FormField("newsandinformationattachments", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, 3, 1);
		attachment.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
		fields.add(attachment);

		fields.add(new FormField("newsandinformationsharing", FieldDisplayType.COMMUNITY_PUBLISHING, "Publish To", Required.REQUIRED, 4, 1));

		form.setFields(fields);


		return form;
	}

	private static FacilioForm getNeighbourhoodForm() {

		FacilioForm form = new FacilioForm();
		form.setDisplayName("Neighbourhood");
		form.setName("default_"+ FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD +"_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("title", FieldDisplayType.TEXTBOX, "Title", Required.REQUIRED, 1, 1));
		FormField descField = new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1);
		descField.addToConfig("richText", true);
		fields.add(descField);
		FormField categoryField = new FormField("category", FieldDisplayType.SELECTBOX, "Category", Required.OPTIONAL, 3, 1);
		categoryField.setAllowCreateOptions(true);
		fields.add(categoryField);

		fields.add(new FormField("location", FieldDisplayType.GEO_LOCATION, "Location", Required.OPTIONAL, 4, 1));


		FormField attachment = new FormField("neighbourhoodattachments", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, 5, 1);
		attachment.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
		fields.add(attachment);

		fields.add(new FormField("neighbourhoodsharing", FieldDisplayType.COMMUNITY_PUBLISHING, "Publish To", Required.REQUIRED, 6, 1));
		form.setFields(fields);

		return form;
	}

	private static FacilioForm getNeighbourhoodPortalForm() {

		FacilioForm form = new FacilioForm();
		form.setDisplayName("Neighbourhood");
		form.setName("default_"+ FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD +"_portal");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("title", FieldDisplayType.TEXTBOX, "Title", Required.REQUIRED, 1, 1));
		FormField descField = new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1);
		descField.addToConfig("richText", true);
		fields.add(descField);
		FormField categoryField = new FormField("category", FieldDisplayType.SELECTBOX, "Category", Required.OPTIONAL, 3, 1);
		categoryField.setAllowCreateOptions(true);
		fields.add(categoryField);

		fields.add(new FormField("location", FieldDisplayType.GEO_LOCATION, "Location", Required.OPTIONAL, 4, 1));


		FormField attachment = new FormField("neighbourhoodattachments", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, 5, 1);
		attachment.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
		fields.add(attachment);

		form.setFields(fields);

		return form;
	}

	private static FacilioForm getDealsAndOffersForm() {

		FacilioForm form = new FacilioForm();
		form.setDisplayName("Deals and Offers");
		form.setName("default_"+ FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS +"_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("title", FieldDisplayType.TEXTBOX, "Title", Required.REQUIRED, 1, 1));
		FormField descField = new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1);
		descField.addToConfig("richText", true);
		fields.add(descField);
		fields.add(new FormField("expiryDate", FieldDisplayType.DATE, "Expiry Date", Required.OPTIONAL, 3, 2));
		fields.add(new FormField("dealer", FieldDisplayType.TEXTBOX, "Dealer", Required.REQUIRED, 4, 1));
		fields.add(new FormField("neighbourhood", FieldDisplayType.LOOKUP_SIMPLE, "Neighbourhood", Required.REQUIRED, "neighbourhood",5, 1));

		FormField attachment = new FormField("dealsandoffersattachments", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, 6, 1);
		attachment.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
		fields.add(attachment);

		fields.add(new FormField("dealsandofferssharing", FieldDisplayType.COMMUNITY_PUBLISHING, "Publish To", Required.REQUIRED, 7, 1));
		form.setFields(fields);

		return form;
	}

	private static FacilioForm getDealsAndOffersPortalForm() {

		FacilioForm form = new FacilioForm();
		form.setDisplayName("Deals and Offers");
		form.setName("default_"+ FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS +"_portal");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("title", FieldDisplayType.TEXTBOX, "Title", Required.REQUIRED, 1, 1));
		FormField descField = new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1);
		descField.addToConfig("richText", true);
		fields.add(descField);
		fields.add(new FormField("expiryDate", FieldDisplayType.DATE, "Expiry Date", Required.OPTIONAL, 3, 2));
		fields.add(new FormField("dealer", FieldDisplayType.TEXTBOX, "Dealer", Required.REQUIRED, 4, 1));
		fields.add(new FormField("neighbourhood", FieldDisplayType.LOOKUP_SIMPLE, "Neighbourhood", Required.REQUIRED, "neighbourhood",5, 1));


		FormField attachment = new FormField("dealsandoffersattachments", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, 6, 1);
		attachment.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
		fields.add(attachment);

		form.setFields(fields);

		return form;
	}


	private static FacilioForm getContactDirectoryForm() {

		FacilioForm form = new FacilioForm();
		form.setDisplayName("Contact Directory");
		form.setName("default_"+ FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY +"_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("contactName", FieldDisplayType.TEXTBOX, "Contact Name", Required.REQUIRED, 2, 1));
		fields.add(new FormField("contactEmail", FieldDisplayType.TEXTBOX, "Contact Email", Required.REQUIRED, 3, 1));
		fields.add(new FormField("contactPhone", FieldDisplayType.TEXTBOX, "Contact Phone", Required.REQUIRED,4, 1));

		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 5, 1));
		fields.add(new FormField("category", FieldDisplayType.SELECTBOX, "Category", Required.OPTIONAL, 6, 1));
		fields.add(new FormField("contactdirectorysharing", FieldDisplayType.COMMUNITY_PUBLISHING, "Publish To", Required.REQUIRED, 7, 1));
		form.setFields(fields);

		return form;
	}

	private static FacilioForm getAdminDocumentsForm() {

		FacilioForm form = new FacilioForm();
		form.setDisplayName("Admin Documents");
		form.setName("default_"+ FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS +"_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("title", FieldDisplayType.TEXTBOX, "Title", Required.REQUIRED, 1, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("category", FieldDisplayType.SELECTBOX, "Category", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("file", FieldDisplayType.FILE, "File", Required.REQUIRED, 4, 1));
		fields.add(new FormField("admindocumentsharing", FieldDisplayType.COMMUNITY_PUBLISHING, "Publish To", Required.REQUIRED, 5, 1));
		form.setFields(fields);

		return form;
	}

	private static FacilioForm getBudgetForm() {

		FacilioForm form = new FacilioForm();
		form.setDisplayName("Budget");
		form.setName("default_"+ FacilioConstants.ContextNames.Budget.BUDGET +"_web");
		form.setModule(ModuleFactory.getModule(FacilioConstants.ContextNames.Budget.BUDGET));
		form.setLabelPosition(LabelPosition.LEFT);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("fiscalYearStart", FieldDisplayType.SELECTBOX, "Fiscal Year Start", Required.REQUIRED, 2, 2));
		fields.add(new FormField("fiscalYear", FieldDisplayType.SELECTBOX, "Fiscal Year", Required.REQUIRED, 2, 3));
		fields.add(new FormField("focalPointType", FieldDisplayType.SELECTBOX, "Scope", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("focalPointResource", FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("budgetamount", FieldDisplayType.BUDGET_AMOUNT, "Budget Amounts", Required.OPTIONAL, 5, 1));
		form.setFields(fields);

		return form;
	}

	private static FacilioForm getChartOfAccountForm() {

		FacilioForm form = new FacilioForm();
		form.setDisplayName("Chart Of Account");
		form.setName("default_"+ ContextNames.Budget.CHART_OF_ACCOUNT +"_web");
		form.setModule(ModuleFactory.getModule(ContextNames.Budget.CHART_OF_ACCOUNT));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("code", FieldDisplayType.TEXTBOX, "Code", Required.OPTIONAL,2, 1));
		FormField typeField = new FormField("type", FieldDisplayType.LOOKUP_SIMPLE, "Account Type", Required.REQUIRED, "accounttype",3, 1);
		typeField.setAllowCreate(true);
		fields.add(typeField);
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 4, 1));
		form.setFields(fields);

		return form;
	}

	private static FacilioForm getAccountTypeForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Account Type");
		form.setName("default_"+ ContextNames.Budget.ACCOUNT_TYPE +"_web");
		form.setModule(ModuleFactory.getModule(ContextNames.Budget.ACCOUNT_TYPE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("group", FieldDisplayType.SELECTBOX, "Category", Required.OPTIONAL,2, 1));
		form.setFields(fields);
		return form;
	}

	public static FacilioForm getPeopleForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("NEW PEOPLE");
		form.setName("default_people_web");
		form.setModule(ModuleFactory.getModule(ContextNames.PEOPLE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setFields(getPeopleFormFields());
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		return form;
	}

	private static List<FormField> getPeopleFormFields() {
		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL, 2, 1));
		fields.add(new FormField("phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL, 3, 1));
		fields.add(new FormField("isOccupantPortalAccess", FieldDisplayType.DECISION_BOX, "Occupant Portal Access", Required.OPTIONAL, 4, 1));
		fields.add(new FormField("peopleType", FieldDisplayType.SELECTBOX, "People Type", Required.OPTIONAL, 5, 1));

		return fields;
	}

	private static FacilioForm getFacilityCreationForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Facility");
		form.setName("default_"+ ContextNames.FacilityBooking.FACILITY +"_web");
		form.setModule(ModuleFactory.getModule(ContextNames.FacilityBooking.FACILITY));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, 1, 1));
		fields.add(new FormField("facilityType", FieldDisplayType.SELECTBOX, "Facility Type", Required.OPTIONAL,2, 2));
		fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site",2, 3));
		fields.add(new FormField("location", FieldDisplayType.LOOKUP_SIMPLE, "Location", Required.OPTIONAL, "basespace", 3, 2));
		FormField category = new FormField("category", FieldDisplayType.SELECTBOX, "Category", Required.OPTIONAL,3, 3);
		category.setAllowCreate(true);
		fields.add(category);
		fields.add(new FormField("manager", FieldDisplayType.LOOKUP_SIMPLE, "Facility Manager", Required.OPTIONAL, "people",4, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL,5, 1));
		fields.add(new FormField("userGuidance", FieldDisplayType.TEXTAREA, "User Guidance", Required.OPTIONAL,6, 1));
		fields.add(new FormField("usageCapacity", FieldDisplayType.NUMBER, "Usage Capacity", Required.REQUIRED,7, 2));
		fields.add(new FormField("bookingAdvancePeriodInDays", FieldDisplayType.NUMBER, "Booking Advance In Days", Required.REQUIRED,7, 3));
		fields.add(new FormField("maxSlotBookingAllowed", FieldDisplayType.NUMBER, "Max Slot Bookings Allowed Per Booking", Required.REQUIRED,8, 2));
		fields.add(new FormField("maxAttendeeCountPerBooking", FieldDisplayType.NUMBER, "Maximum attendee Count Per Booking", Required.OPTIONAL,8, 3));
		fields.add(new FormField("isAttendeeListNeeded", FieldDisplayType.DECISION_BOX, "Attendee List Needed", Required.REQUIRED,9, 2));
		fields.add(new FormField("isMultiBookingPerSlotAllowed", FieldDisplayType.DECISION_BOX, "Is Multi Booking Allowed Per slot", Required.REQUIRED,9, 3));
		FormField slotGeneratedUptoField = new FormField("slotGeneratedUpto", FieldDisplayType.DATETIME, "Slot Generated Upto", Required.OPTIONAL,9, 1);
		slotGeneratedUptoField.setHideField(true);
		fields.add(slotGeneratedUptoField);
		fields.add(new FormField("allowCancellationBefore", FieldDisplayType.NUMBER, "Allow Cancellation Before (Days)", Required.REQUIRED,10, 1));
		fields.add(new FormField("isChargeable", FieldDisplayType.DECISION_BOX, "Is Chargeable", Required.OPTIONAL,11, 1));
		fields.add(new FormField("pricePerSlot", FieldDisplayType.NUMBER, "Price Per slot", Required.OPTIONAL,12, 2));
		fields.add(new FormField("securityDeposit", FieldDisplayType.NUMBER, "Security Deposit", Required.OPTIONAL,12, 3));
		fields.add(new FormField("slotDuration", FieldDisplayType.DURATION, "Slot Duration", Required.REQUIRED,13, 1));
		fields.add(new FormField("amenities", FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Features / Amenties", Required.OPTIONAL,14, 1));
		fields.add(new FormField("facilityWeekdayAvailability", FieldDisplayType.FACILITY_AVAILABILITY, "Availability", Required.REQUIRED,15, 1));
		form.setFields(fields);
		return form;
	}

	private static FacilioForm getFacilityBookingForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Booking");
		form.setName("default_"+ ContextNames.FacilityBooking.FACILITY_BOOKING +"_web");
		form.setModule(ModuleFactory.getModule(ContextNames.FacilityBooking.FACILITY_BOOKING));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("facility", FieldDisplayType.LOOKUP_SIMPLE, "Facility", Required.REQUIRED, "facility",1, 1));
		fields.add(new FormField("reservedFor", FieldDisplayType.LOOKUP_SIMPLE, "Reserved For", Required.REQUIRED,"user",2, 1));
		fields.add(new FormField("noOfAttendees", FieldDisplayType.NUMBER, "Number Of Attendees", Required.REQUIRED,3, 1));
		fields.add(new FormField("bookingslot", FieldDisplayType.FACILITY_BOOKING_SLOTS, "Time Slots", Required.REQUIRED,4, 1));
		fields.add(new FormField("internalAttendees", FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Internal Attendees", Required.OPTIONAL,5, 1));
	
		form.setFields(fields);
		return form;
	}

	private static FacilioForm getFacilityBookingPortalForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Booking");
		form.setName("default_"+ ContextNames.FacilityBooking.FACILITY_BOOKING +"_portal");
		form.setModule(ModuleFactory.getModule(ContextNames.FacilityBooking.FACILITY_BOOKING));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.OCCUPANT_PORTAL_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("facility", FieldDisplayType.LOOKUP_SIMPLE, "Facility", Required.REQUIRED, "facility",1, 1));
		fields.add(new FormField("reservedFor", FieldDisplayType.LOOKUP_SIMPLE, "Reserved For", Required.REQUIRED,"user",2, 1));
		fields.add(new FormField("noOfAttendees", FieldDisplayType.NUMBER, "Number Of Attendees", Required.REQUIRED,3, 1));
		fields.add(new FormField("bookingslot", FieldDisplayType.FACILITY_BOOKING_SLOTS, "Time Slots", Required.REQUIRED,4, 1));
		fields.add(new FormField("internalAttendees", FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Internal Attendees", Required.OPTIONAL,5, 1));
		fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.REQUIRED, "tenant", 6, 1));

		form.setFields(fields);
		return form;
	}
	
	private static FacilioForm getAmenityForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Amenity");
		form.setName("default_"+ ContextNames.FacilityBooking.AMENITY +"_web");
		form.setModule(ModuleFactory.getModule(ContextNames.FacilityBooking.AMENITY));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED,1, 1));
		fields.add(new FormField("category", FieldDisplayType.SELECTBOX, "Category", Required.OPTIONAL,2, 1));
		fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL,3, 1));
		fields.add(new FormField("amenityLogo", FieldDisplayType.SELECTBOX, "Amenity Logo", Required.OPTIONAL,4, 1));
		form.setFields(fields);
		return form;
	}

	private static FacilioForm getExternalAttendeeForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("External Attendee");
		form.setName("default_"+ ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE +"_web");
		form.setModule(ModuleFactory.getModule(ContextNames.FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED,1, 1));
		fields.add(new FormField("email", FieldDisplayType.TEXTBOX, "Email", Required.OPTIONAL,2, 1));
		fields.add(new FormField("Phone", FieldDisplayType.TEXTBOX, "Phone", Required.OPTIONAL,3, 1));
		fields.add(new FormField("facilityBooking", FieldDisplayType.LOOKUP_SIMPLE, "Booking", Required.REQUIRED,4, 1));
		form.setFields(fields);
		return form;
	}

	private static FacilioForm getFacilitySpecialAvailabilityForm() {
		FacilioForm form = new FacilioForm();
		form.setDisplayName("Special Availability");
		form.setName("default_"+ ContextNames.FacilityBooking.FACILITY_SPECIAL_AVAILABILITY +"_web");
		form.setModule(ModuleFactory.getModule(ContextNames.FacilityBooking.FACILITY_SPECIAL_AVAILABILITY));
		form.setLabelPosition(LabelPosition.TOP);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		List<FormField> fields = new ArrayList<>();
		fields.add(new FormField("remarks", FieldDisplayType.TEXTBOX, "Remarks", Required.REQUIRED,1, 1));
		fields.add(new FormField("startDate", FieldDisplayType.DATE, "Start Date", Required.REQUIRED,"user",2, 2));
		fields.add(new FormField("endDate", FieldDisplayType.DATE, "End Date", Required.REQUIRED,2, 3));
		fields.add(new FormField("startTime", FieldDisplayType.TIME, "Start Time", Required.REQUIRED,3, 2));
		fields.add(new FormField("endTime", FieldDisplayType.TIME, "End Time", Required.REQUIRED,3, 3));
		fields.add(new FormField("cost", FieldDisplayType.NUMBER, "Cost", Required.OPTIONAL,4, 2));
		fields.add(new FormField("specialType", FieldDisplayType.SELECTBOX, "Type", Required.REQUIRED,4, 3));
		form.setFields(fields);
		return form;
	}

}
