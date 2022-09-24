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
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ClientContactModule extends BaseModuleConfig{

    public ClientContactModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.CLIENT_CONTACT);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> clientContact = new ArrayList<FacilioView>();
        clientContact.add(getAllHiddenClientContacts().setOrder(order++));
        clientContact.add(getAllClientContacts().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.CLIENT_CONTACT);
        groupDetails.put("views", clientContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHiddenClientContacts() {

        FacilioModule clientContactModule = ModuleFactory.getClientContactModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Client Contacts");
        allView.setModuleName(clientContactModule.getName());
        allView.setSortFields(sortFields);

        allView.setHidden(true);

        return allView;
    }

    private static FacilioView getAllClientContacts() {

        FacilioModule clientContactModule = ModuleFactory.getClientContactModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all-contacts");
        allView.setDisplayName("All Client Contacts");
        allView.setModuleName(clientContactModule.getName());
        allView.setSortFields(sortFields);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule clientContactModule = modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT);

        FacilioForm clientContactForm = new FacilioForm();
        clientContactForm.setDisplayName("NEW VENDOR CONTACT");
        clientContactForm.setName("default_clientcontact_web");
        clientContactForm.setModule(clientContactModule);
        clientContactForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        clientContactForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> clientContactFormFields = new ArrayList<>();
        clientContactFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        clientContactFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 2, 1));
        clientContactFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL, 3, 1));
        clientContactFormFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.REQUIRED, "client", 4, 1));
        clientContactFormFields.add(new FormField("isPrimaryContact", FacilioField.FieldDisplayType.DECISION_BOX, "Primary Contact", FormField.Required.OPTIONAL, 5, 1));
//        clientContactForm.setFields(clientContactFormFields);

        FormSection Section = new FormSection("Default", 1, clientContactFormFields, false);
        Section.setSectionType(FormSection.SectionType.FIELDS);
        clientContactForm.setSections(Collections.singletonList(Section));
        clientContactForm.setIsSystemForm(true);
        clientContactForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(clientContactForm);
    }
}
