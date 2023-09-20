package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.signup.jobPlan.AddJobPlanModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ClientModule extends BaseModuleConfig{

    public ClientModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.CLIENT);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> clientContact = new ArrayList<FacilioView>();
        clientContact.add(getAllClientView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.CLIENT);
        groupDetails.put("views", clientContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllClientView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Clients");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule clientModule = modBean.getModule(FacilioConstants.ContextNames.CLIENT);

        FacilioForm clientForm = new FacilioForm();
        clientForm.setDisplayName("NEW CLIENT");
        clientForm.setName("default_client_web");
        clientForm.setModule(clientModule);
        clientForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        clientForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        List<FormField> clientFormFields = new ArrayList<>();
        clientFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        clientFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        clientFormFields.add(new FormField("website", FacilioField.FieldDisplayType.TEXTBOX, "Website", FormField.Required.OPTIONAL, 3, 1));
        clientFormFields.add(new FormField("primaryContactName", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Name", FormField.Required.REQUIRED, 4, 1));
        clientFormFields.add(new FormField("primaryContactEmail", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact E-mail", FormField.Required.OPTIONAL, 5, 1));
        clientFormFields.add(new FormField("primaryContactPhone", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Phone", FormField.Required.REQUIRED, 6, 1));
        clientFormFields.add(new FormField("address", FacilioField.FieldDisplayType.ADDRESS, "Address", FormField.Required.OPTIONAL, 7, 1));
//        clientForm.setFields(clientFormFields);

        FormSection Section = new FormSection("Default", 1, clientFormFields, false);
        Section.setSectionType(FormSection.SectionType.FIELDS);
        clientForm.setSections(Collections.singletonList(Section));
        clientForm.setIsSystemForm(true);
        clientForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(clientForm);
    }
}
