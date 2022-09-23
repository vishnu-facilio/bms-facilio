package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class DeliveriesModule extends BaseModuleConfig{

    public DeliveriesModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.DELIVERIES);
    }



    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> delivery = new ArrayList<FacilioView>();
        delivery.add(getAllDeliveriesView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.DELIVERIES);
        groupDetails.put("views", delivery);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllDeliveriesView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Deliveries");
        allView.setModuleName(FacilioConstants.ContextNames.DELIVERIES);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule deliveriesModule = modBean.getModule(FacilioConstants.ContextNames.DELIVERIES);

        FacilioForm deliveriesForm = new FacilioForm();
        deliveriesForm.setDisplayName("NEW DELIVERY");
        deliveriesForm.setName("default_deliveries_web");
        deliveriesForm.setModule(deliveriesModule);
        deliveriesForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        deliveriesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP));

        List<FormField> deliveriesFormFields = new ArrayList<>();
        FormField nameField = new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.OPTIONAL, 1, 1);
        nameField.setHideField(true);
        deliveriesFormFields.add(nameField);
        deliveriesFormFields.add(new FormField("trackingNumber", FacilioField.FieldDisplayType.TEXTBOX, "Tracking Number", FormField.Required.OPTIONAL, 2, 2));
        deliveriesFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Recipient", FormField.Required.OPTIONAL, "employee", 3, 2));
        deliveriesFormFields.add(new FormField("receivedTime", FacilioField.FieldDisplayType.DATETIME, "Received Time", FormField.Required.REQUIRED, 4, 2));
        deliveriesFormFields.add(new FormField("avatar", FacilioField.FieldDisplayType.IMAGE,"Photo", FormField.Required.OPTIONAL,5,2));
        deliveriesFormFields.add(new FormField("deliveryArea", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Delivery Area", FormField.Required.OPTIONAL, "deliveryArea", 6, 2));
        deliveriesFormFields.add(new FormField("carrier", FacilioField.FieldDisplayType.SELECTBOX, "Carrier", FormField.Required.REQUIRED, 7, 2));
        deliveriesFormFields.add(new FormField("signature", FacilioField.FieldDisplayType.SIGNATURE, "Signature", FormField.Required.OPTIONAL, 8, 1));

        FormSection deliveriesFormSection = new FormSection("Default", 1, deliveriesFormFields, false);
        deliveriesFormSection.setSectionType(FormSection.SectionType.FIELDS);
        deliveriesForm.setSections(Collections.singletonList(deliveriesFormSection));


        FacilioForm deliveriesPortalForm = new FacilioForm();
        deliveriesPortalForm.setDisplayName("NEW DELIVERY");
        deliveriesPortalForm.setName("default_deliveries_portal");
        deliveriesPortalForm.setModule(deliveriesModule);
        deliveriesPortalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
//        deliveriesPortalForm.setFields(deliveriesFormFields);
        deliveriesPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> deliveriesPortalFormFields = new ArrayList<>();
        FormField portalNameField = new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.OPTIONAL, 1, 1);
        portalNameField.setHideField(true);
        deliveriesPortalFormFields.add(portalNameField);
        deliveriesPortalFormFields.add(new FormField("trackingNumber", FacilioField.FieldDisplayType.TEXTBOX, "Tracking Number", FormField.Required.OPTIONAL, 2, 2));
        deliveriesPortalFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Recipient", FormField.Required.OPTIONAL, "employee", 3, 2));
        deliveriesPortalFormFields.add(new FormField("receivedTime", FacilioField.FieldDisplayType.DATETIME, "Received Time", FormField.Required.REQUIRED, 4, 2));
        deliveriesPortalFormFields.add(new FormField("avatar", FacilioField.FieldDisplayType.IMAGE,"Photo", FormField.Required.OPTIONAL,5,2));
        deliveriesPortalFormFields.add(new FormField("deliveryArea", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Delivery Area", FormField.Required.OPTIONAL, "deliveryArea", 6, 2));
        deliveriesPortalFormFields.add(new FormField("carrier", FacilioField.FieldDisplayType.SELECTBOX, "Carrier", FormField.Required.REQUIRED, 7, 2));
        deliveriesPortalFormFields.add(new FormField("signature", FacilioField.FieldDisplayType.SIGNATURE, "Signature", FormField.Required.OPTIONAL, 8, 1));

        FormSection deliveriesPortalFormSection = new FormSection("Default", 1, deliveriesPortalFormFields, false);
        deliveriesPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        deliveriesPortalForm.setSections(Collections.singletonList(deliveriesPortalFormSection));


        FacilioForm scanForDeliveriesForm = new FacilioForm();
        scanForDeliveriesForm.setDisplayName("SCAN FOR DELIVERY");
        scanForDeliveriesForm.setName("scan_for_delivery");
        scanForDeliveriesForm.setModule(deliveriesModule);
        scanForDeliveriesForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        scanForDeliveriesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP));

        List<FormField> scanForDeliveriesFormFields = new ArrayList<>();
        scanForDeliveriesFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Recipient", FormField.Required.REQUIRED, "employee", 1, 1));
        scanForDeliveriesFormFields.add(new FormField("deliveryArea", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Delivery Area", FormField.Required.REQUIRED, "deliveryArea", 2, 1));
        scanForDeliveriesFormFields.add(new FormField("deliveryNotes", FacilioField.FieldDisplayType.TEXTAREA, "Delivery Notes", FormField.Required.OPTIONAL, "deliveryNotes", 3, 1));
        FormField receivedTimeField = new FormField("receivedTime", FacilioField.FieldDisplayType.DATETIME, "Received Time", FormField.Required.REQUIRED, 4, 1);
        receivedTimeField.setHideField(true);
        receivedTimeField.setValue("${CURRENT_TIME}");
        scanForDeliveriesFormFields.add(receivedTimeField);
        FormField trackingNumberField = new FormField("trackingNumber", FacilioField.FieldDisplayType.TEXTBOX, "Tracking Number", FormField.Required.OPTIONAL, 5, 1);
        trackingNumberField.setHideField(true);
        scanForDeliveriesFormFields.add(trackingNumberField);
        FormField carrierField = new FormField("carrier", FacilioField.FieldDisplayType.SELECTBOX, "Carrier", FormField.Required.REQUIRED, 6, 1);
        carrierField.setHideField(true);
        scanForDeliveriesFormFields.add(carrierField);
//        scanForDeliveriesForm.setFields(scanForDeliveriesFormFields);

        FormSection scanForDeliveriesFormSection = new FormSection("Default", 1, scanForDeliveriesFormFields, false);
        scanForDeliveriesFormSection.setSectionType(FormSection.SectionType.FIELDS);
        scanForDeliveriesForm.setSections(Collections.singletonList(scanForDeliveriesFormSection));

        List<FacilioForm> deliveriesModuleForms = new ArrayList<>();
        deliveriesModuleForms.add(deliveriesForm);
        deliveriesModuleForms.add(deliveriesPortalForm);
        deliveriesModuleForms.add(scanForDeliveriesForm);

        return deliveriesModuleForms;
    }

}
