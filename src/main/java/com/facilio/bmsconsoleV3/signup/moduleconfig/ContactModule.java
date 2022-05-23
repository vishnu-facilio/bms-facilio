package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ContactModule extends BaseModuleConfig{

    public ContactModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.CONTACT);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> contact = new ArrayList<FacilioView>();
        contact.add(getAllContactView().setOrder(order++));
        contact.add(getTenantContactView().setOrder(order++));
        contact.add(getVendorContactView().setOrder(order++));
        contact.add(getEmployeeContactView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.CONTACT);
        groupDetails.put("views", contact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllContactView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Contact");

        return allView;
    }

    private static FacilioView getTenantContactView() {

        FacilioView allView = new FacilioView();
        allView.setName("tenant");
        allView.setDisplayName("Tenant Contact");

        Criteria criteria = new Criteria();
        FacilioField contactType = FieldFactory.getField("contactType", "CONTACT_TYPE", ModuleFactory.getContactModule(), FieldType.SYSTEM_ENUM);
        criteria.addAndCondition(CriteriaAPI.getCondition(contactType, "1", PickListOperators.IS));
        allView.setCriteria(criteria);

        return allView;
    }

    private static FacilioView getVendorContactView() {

        FacilioView allView = new FacilioView();
        allView.setName("vendor");
        allView.setDisplayName("Vendor Contact");
        Criteria criteria = new Criteria();
        FacilioField contactType = FieldFactory.getField("contactType", "CONTACT_TYPE", ModuleFactory.getContactModule(),FieldType.SYSTEM_ENUM);
        criteria.addAndCondition(CriteriaAPI.getCondition(contactType, "2", PickListOperators.IS));
        allView.setCriteria(criteria);

        return allView;
    }

    private static FacilioView getEmployeeContactView() {

        FacilioView allView = new FacilioView();
        allView.setName("employee");
        allView.setDisplayName("Employee Contact");

        Criteria criteria = new Criteria();
        FacilioField contactType = FieldFactory.getField("contactType", "CONTACT_TYPE", ModuleFactory.getContactModule(),FieldType.SYSTEM_ENUM);
        criteria.addAndCondition(CriteriaAPI.getCondition(contactType, "3", PickListOperators.IS));
        allView.setCriteria(criteria);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule contactModule = modBean.getModule(FacilioConstants.ContextNames.CONTACT);

        FacilioForm contactForm = new FacilioForm();
        contactForm.setDisplayName("CONTACT");
        contactForm.setName("default_contact_web");
        contactForm.setModule(contactModule);
        contactForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        contactForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

//        --> THESE FormField IS COMMON FOR contactForm AND portalContactFormForm
        List<FormField> contactFormFields = new ArrayList<>();
        contactFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        contactFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.REQUIRED, 2, 2));
        contactFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 2, 3));
        contactFormFields.add(new FormField("contactType", FacilioField.FieldDisplayType.SELECTBOX, "Contact Type", FormField.Required.REQUIRED, 3, 1));
        contactFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, "vendors",4, 2));
        contactFormFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.OPTIONAL, "tenant",4, 3));
        contactFormFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.OPTIONAL, "client",5, 2));
        contactFormFields.add(new FormField("isPrimaryContact", FacilioField.FieldDisplayType.DECISION_BOX, "Is Primary Contact", FormField.Required.OPTIONAL, 6, 3));
//        --> THESE FormField IS COMMON FOR contactForm AND portalContactFormForm
//        contactForm.setFields(contactFormFields);

        FormSection Section = new FormSection("Default", 1, contactFormFields, false);
        Section.setSectionType(FormSection.SectionType.FIELDS);
        contactForm.setSections(Collections.singletonList(Section));

        FacilioForm portalContactFormForm = new FacilioForm();
        portalContactFormForm.setDisplayName("CONTACT");
        portalContactFormForm.setName("default_contact_portal");
        portalContactFormForm.setModule(contactModule);
        portalContactFormForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
//        portalContactFormForm.setFields(contactFormFields);
        portalContactFormForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        FormSection portalFormSection = new FormSection("Default", 1, contactFormFields, false);
        portalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        portalContactFormForm.setSections(Collections.singletonList(portalFormSection));

        List<FacilioForm> contactModuleForms = new ArrayList<>();
        contactModuleForms.add(contactForm);
        contactModuleForms.add(portalContactFormForm);

        return contactModuleForms;
    }

}
