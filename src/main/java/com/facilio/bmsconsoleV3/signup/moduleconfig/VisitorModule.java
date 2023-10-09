package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class VisitorModule extends BaseModuleConfig{
    public VisitorModule(){
        setModuleName(FacilioConstants.ContextNames.VISITOR);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> visitor = new ArrayList<FacilioView>();
        visitor.add(getAllVisitorsView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VISITOR);
        groupDetails.put("views", visitor);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllVisitorsView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Visitors");

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule visitorModule = modBean.getModule(FacilioConstants.ContextNames.VISITOR);

        FacilioForm visitorKioskForm = new FacilioForm();
        visitorKioskForm.setDisplayName("VISITOR");
        visitorKioskForm.setName("default_visitor_web");
        visitorKioskForm.setModule(visitorModule);
        visitorKioskForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        visitorKioskForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> visitorKioskFormFields = new ArrayList<>();
        visitorKioskFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.NUMBER, "Enter your mobile number", FormField.Required.REQUIRED, 1, 1));
        visitorKioskFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Hi,What is your full name?", FormField.Required.REQUIRED, 1, 1));
        visitorKioskFormFields.add(new FormField("email", FacilioField.FieldDisplayType.EMAIL, "What is your email id?", FormField.Required.OPTIONAL, 2, 1));

        FormSection visitorKioskFormSection = new FormSection("Default", 1, visitorKioskFormFields, false);
        visitorKioskFormSection.setSectionType(FormSection.SectionType.FIELDS);
        visitorKioskForm.setSections(Collections.singletonList(visitorKioskFormSection));
        visitorKioskForm.setIsSystemForm(true);
        visitorKioskForm.setType(FacilioForm.Type.FORM);

        FacilioForm visitorForm = new FacilioForm();
        visitorForm.setDisplayName("VISITOR");
        visitorForm.setName("portal_visitor_web");
        visitorForm.setModule(visitorModule);
        visitorForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        visitorForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> visitorFormFields = new ArrayList<>();
        visitorFormFields.add(new FormField("avatar", FacilioField.FieldDisplayType.IMAGE,"Visitor Photo", FormField.Required.OPTIONAL,1,1));
        visitorFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        visitorFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.REQUIRED, 3, 1));
        visitorFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.REQUIRED, 4, 1));
        visitorFormFields.add(new FormField("location", FacilioField.FieldDisplayType.ADDRESS, "Location", FormField.Required.OPTIONAL, 5, 1));

        FormSection visitorFormSection = new FormSection("Default", 1, visitorFormFields, false);
        visitorFormSection.setSectionType(FormSection.SectionType.FIELDS);
        visitorForm.setSections(Collections.singletonList(visitorFormSection));
        visitorForm.setIsSystemForm(true);
        visitorForm.setType(FacilioForm.Type.FORM);


        FacilioForm portalVisitorForm = new FacilioForm();
        portalVisitorForm.setDisplayName("VISITOR");
        portalVisitorForm.setName("default_portal_visitor_web");
        portalVisitorForm.setModule(visitorModule);
        portalVisitorForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        portalVisitorForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> portalVisitorFormFields = new ArrayList<>();
        portalVisitorFormFields.add(new FormField("avatar", FacilioField.FieldDisplayType.IMAGE,"Visitor Photo", FormField.Required.OPTIONAL,1,1));
        portalVisitorFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        portalVisitorFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.REQUIRED, 3, 1));
        portalVisitorFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.REQUIRED, 4, 1));
        portalVisitorFormFields.add(new FormField("location", FacilioField.FieldDisplayType.ADDRESS, "Location", FormField.Required.OPTIONAL, 5, 1));

        FormSection portalVisitorFormSection = new FormSection("Default", 1, portalVisitorFormFields, false);
        portalVisitorFormSection.setSectionType(FormSection.SectionType.FIELDS);
        portalVisitorForm.setSections(Collections.singletonList(portalVisitorFormSection));
        portalVisitorForm.setIsSystemForm(true);
        portalVisitorForm.setType(FacilioForm.Type.FORM);


        List<FacilioForm> visitorModuleForms = new ArrayList<>();
        visitorModuleForms.add(visitorKioskForm);
        visitorModuleForms.add(visitorForm);
        visitorModuleForms.add(portalVisitorForm);

        return visitorModuleForms;
    }

}
