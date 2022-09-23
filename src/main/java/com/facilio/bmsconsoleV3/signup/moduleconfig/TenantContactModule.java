package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class TenantContactModule extends BaseModuleConfig{
    public TenantContactModule(){
        setModuleName(FacilioConstants.ContextNames.TENANT_CONTACT);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tenantContact = new ArrayList<FacilioView>();
        tenantContact.add(getAllHiddenTenantContacts().setOrder(order++));
        tenantContact.add(getAllTenantContacts().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TENANT_CONTACT);
        groupDetails.put("views", tenantContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHiddenTenantContacts() {

        FacilioModule tenantContactModule = ModuleFactory.getTenantContactModule();

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(tenantContactModule);

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tenant Contacts");
        allView.setModuleName(tenantContactModule.getName());
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        allView.setHidden(true);

        return allView;
    }

    private static FacilioView getAllTenantContacts() {

        FacilioModule tenantContactModule = ModuleFactory.getTenantContactModule();

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(tenantContactModule);


        FacilioView allView = new FacilioView();
        allView.setName("all-contacts");
        allView.setDisplayName("All Tenant Contacts");
        allView.setModuleName(tenantContactModule.getName());
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule tenantContactModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);

        FacilioForm tenantContactForm = new FacilioForm();
        tenantContactForm.setDisplayName("NEW TENANT CONTACT");
        tenantContactForm.setName("default_tenantcontact_web");
        tenantContactForm.setModule(tenantContactModule);
        tenantContactForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        tenantContactForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> tenantContactFormfields = new ArrayList<>();
        tenantContactFormfields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        tenantContactFormfields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 2, 1));
        tenantContactFormfields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL, 3, 1));
        tenantContactFormfields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.REQUIRED, "tenant", 4, 1));
        tenantContactFormfields.add(new FormField("isPrimaryContact", FacilioField.FieldDisplayType.DECISION_BOX, "Primary Contact", FormField.Required.OPTIONAL, 5, 1));
//        tenantContactForm.setFields(tenantContactFormfields);

        FormSection tenantContactFormSection = new FormSection("Default", 1, tenantContactFormfields, false);
        tenantContactFormSection.setSectionType(FormSection.SectionType.FIELDS);
        tenantContactForm.setSections(Collections.singletonList(tenantContactFormSection));

        FacilioForm tenantContactPortalForm = new FacilioForm();
        tenantContactPortalForm.setDisplayName("NEW TENANT CONTACT");
        tenantContactPortalForm.setName("default_tenantcontact_portal");
        tenantContactPortalForm.setModule(tenantContactModule);
        tenantContactPortalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        tenantContactPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> tenantContactPortalFormFields = new ArrayList<>();
        tenantContactPortalFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        tenantContactPortalFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 2, 1));
        tenantContactPortalFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL, 3, 1));
        tenantContactPortalFormFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.REQUIRED, "tenant", 4, 1));
//        tenantContactPortalForm.setFields(tenantContactPortalFormFields);

        FormSection tenantContactPortalFormSection = new FormSection("Default", 1, tenantContactPortalFormFields, false);
        tenantContactPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        tenantContactPortalForm.setSections(Collections.singletonList(tenantContactPortalFormSection));

        List<FacilioForm> tenantContactModuleForms = new ArrayList<>();
        tenantContactModuleForms.add(tenantContactForm);
        tenantContactModuleForms.add(tenantContactPortalForm);

        return tenantContactModuleForms;
    }
}
