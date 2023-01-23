package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class WorkPermitModule extends BaseModuleConfig{
    public WorkPermitModule(){
        setModuleName(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workPermitModule = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);

        FacilioForm workPermitForm = new FacilioForm();
        workPermitForm.setDisplayName("WORK PERMIT");
        workPermitForm.setName("default_workpermit_web");
        workPermitForm.setModule(workPermitModule);
        workPermitForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        workPermitForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> workPermitFormDefaultFields = new ArrayList<>();
        workPermitFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        workPermitFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        workPermitFormDefaultFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 2));
        workPermitFormDefaultFields.add(new FormField("space", FacilioField.FieldDisplayType.SPACECHOOSER, "Location", FormField.Required.OPTIONAL, "basespace", 3, 3));
        workPermitFormDefaultFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, "vendors", 4, 2));
        workPermitFormDefaultFields.add(new FormField("people", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Contact", FormField.Required.OPTIONAL, "people", 4, 3));
        workPermitFormDefaultFields.add(new FormField("expectedStartTime", FacilioField.FieldDisplayType.DATETIME, "Valid From", FormField.Required.REQUIRED, 5, 2));
        workPermitFormDefaultFields.add(new FormField("expectedEndTime", FacilioField.FieldDisplayType.DATETIME, "Valid To", FormField.Required.REQUIRED, 5, 3));
        workPermitFormDefaultFields.add(new FormField("ticket", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Work Order", FormField.Required.OPTIONAL, "ticket", 6, 1));
        workPermitFormDefaultFields.add(new FormField("workPermitType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Permit Type", FormField.Required.OPTIONAL, "workPermitType", 7, 1));

        List<FormField> checklistFields = new ArrayList<>();
        checklistFields.add(new FormField("checklist", FacilioField.FieldDisplayType.PERMIT_CHECKLIST, "Checklist", FormField.Required.OPTIONAL, 8, 1));

        List<FormField> workPermitFormFields = new ArrayList<>();
        workPermitFormFields.addAll(workPermitFormDefaultFields);
        workPermitFormFields.addAll(checklistFields);


        FormSection defaultSection = new FormSection("PERMIT INFORMATION", 1, workPermitFormFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection checklistSection = new FormSection("CHECKLIST", 2, checklistFields, true);
        checklistSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(checklistSection);

        workPermitForm.setSections(sections);
        workPermitForm.setIsSystemForm(true);
        workPermitForm.setType(FacilioForm.Type.FORM);


        FacilioForm portalWorkPermitForm = new FacilioForm();
        portalWorkPermitForm.setDisplayName("WORK PERMIT");
        portalWorkPermitForm.setName("default_workpermit_portal");
        portalWorkPermitForm.setModule(workPermitModule);
        portalWorkPermitForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        portalWorkPermitForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> portalWorkPermitFormFields = new ArrayList<>();
        portalWorkPermitFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Permit Name", FormField.Required.REQUIRED, 1, 1));
        portalWorkPermitFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        portalWorkPermitFormFields.add(new FormField("expectedStartTime", FacilioField.FieldDisplayType.DATETIME, "Valid From", FormField.Required.OPTIONAL, 3, 1));
        portalWorkPermitFormFields.add(new FormField("expectedEndTime", FacilioField.FieldDisplayType.DATETIME, "Valid To", FormField.Required.OPTIONAL, 4, 1));
        portalWorkPermitFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.REQUIRED,"vendors", 6, 1));
        portalWorkPermitFormFields.add(new FormField("people", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Contact", FormField.Required.OPTIONAL, "people",7, 1));


        FormSection portalDefaultSection = new FormSection("PERMIT INFORMATION", 1, portalWorkPermitFormFields, true);
        portalDefaultSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> section = new ArrayList<>();
        section.add(portalDefaultSection);

        portalWorkPermitForm.setSections(section);
        portalWorkPermitForm.setIsSystemForm(true);
        portalWorkPermitForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> workPermitModuleForms = new ArrayList<>();
        workPermitModuleForms.add(workPermitForm);
        workPermitModuleForms.add(portalWorkPermitForm);

        return workPermitModuleForms;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workPermit = new ArrayList<FacilioView>();
        workPermit.add(getAllWorkPermitView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);
        groupDetails.put("views", workPermit);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkPermitView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Permit");

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
}
