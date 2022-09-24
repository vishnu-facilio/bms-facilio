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

public class DeliveryAreaModule extends BaseModuleConfig{
    public DeliveryAreaModule(){
        setModuleName(FacilioConstants.ContextNames.DELIVERY_AREA);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> deliveryArea = new ArrayList<FacilioView>();
        deliveryArea.add(getAllDeliveryAreaView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.DELIVERY_AREA);
        groupDetails.put("views", deliveryArea);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllDeliveryAreaView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Delivery Areas");
        allView.setModuleName(FacilioConstants.ContextNames.DELIVERY_AREA);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule deliveryAreaModule = modBean.getModule(FacilioConstants.ContextNames.DELIVERY_AREA);

        FacilioForm deliveryAreaForm = new FacilioForm();
        deliveryAreaForm.setDisplayName("NEW DELIVERY AREA");
        deliveryAreaForm.setName("default_deliveryArea_web");
        deliveryAreaForm.setModule(deliveryAreaModule);
        deliveryAreaForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        deliveryAreaForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> deliveryAreaFormFields = new ArrayList<>();
        deliveryAreaFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        deliveryAreaFormFields.add(new FormField("location", FacilioField.FieldDisplayType.SPACECHOOSER, "Location", FormField.Required.OPTIONAL, "basespace", 2, 3));
        deliveryAreaFormFields.add(new FormField("isActive", FacilioField.FieldDisplayType.DECISION_BOX, "Is Active", FormField.Required.REQUIRED, 3, 2));
//        deliveryAreaForm.setFields(deliveryAreaFormFields);

        FormSection deliveryAreaFormSection = new FormSection("Default", 1, deliveryAreaFormFields, false);
        deliveryAreaFormSection.setSectionType(FormSection.SectionType.FIELDS);
        deliveryAreaForm.setSections(Collections.singletonList(deliveryAreaFormSection));
        deliveryAreaForm.setIsSystemForm(true);
        deliveryAreaForm.setType(FacilioForm.Type.FORM);


        FacilioForm deliveryAreaPortalForm = new FacilioForm();
        deliveryAreaPortalForm.setDisplayName("NEW DELIVERY AREA");
        deliveryAreaPortalForm.setName("default_deliveryArea_portal");
        deliveryAreaPortalForm.setModule(deliveryAreaModule);
        deliveryAreaPortalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        deliveryAreaPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> deliveryAreaPortalFormFields = new ArrayList<>();
        deliveryAreaPortalFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        deliveryAreaPortalFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Employee", FormField.Required.REQUIRED, "employee", 2, 2));
        deliveryAreaPortalFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 5, 2));
        deliveryAreaPortalFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL,"building", 6, 3));
        deliveryAreaPortalFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL,"floor", 7, 1));
//        deliveryAreaPortalForm.setFields(deliveryAreaPortalFormFields);

        FormSection deliveryAreaPortalFormSection = new FormSection("Default", 1, deliveryAreaPortalFormFields, false);
        deliveryAreaPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        deliveryAreaPortalForm.setSections(Collections.singletonList(deliveryAreaPortalFormSection));
        deliveryAreaPortalForm.setIsSystemForm(true);
        deliveryAreaPortalForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> deliveryAreaModuleForms = new ArrayList<>();
        deliveryAreaModuleForms.add(deliveryAreaForm);
        deliveryAreaModuleForms.add(deliveryAreaPortalForm);

        return deliveryAreaModuleForms;
    }
}
