package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class NeighbourhoodModule extends BaseModuleConfig{
    public NeighbourhoodModule(){
        setModuleName(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> neighbourhood = new ArrayList<FacilioView>();
        neighbourhood.add(getAllNeighbourhoodView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD);
        groupDetails.put("views", neighbourhood);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllNeighbourhoodView() {

        FacilioModule module = ModuleFactory.getNeighbourhoodModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Neighbourhood");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule neighbourhoodModule = modBean.getModule(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD);

        FacilioForm neighbourhoodForm = new FacilioForm();
        neighbourhoodForm.setDisplayName("Neighbourhood");
        neighbourhoodForm.setName("default_"+ FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD +"_web");
        neighbourhoodForm.setModule(neighbourhoodModule);
        neighbourhoodForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        neighbourhoodForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> neighbourhoodFormFields = new ArrayList<>();
        neighbourhoodFormFields.add(new FormField("title", FacilioField.FieldDisplayType.TEXTBOX, "Title", FormField.Required.REQUIRED, 1, 1));
        FormField descField = new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1);
        descField.addToConfig("richText", true);
        neighbourhoodFormFields.add(descField);
        FormField categoryField = new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.OPTIONAL, 3, 1);
        categoryField.setAllowCreateOptions(true);
        neighbourhoodFormFields.add(categoryField);
        neighbourhoodFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 4, 1));
        FormField attachment = new FormField("neighbourhoodattachments", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, 5, 1);
        attachment.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
        neighbourhoodFormFields.add(attachment);
        FormField field = new FormField("audience", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Audience", FormField.Required.REQUIRED, "audience",6, 1);
        field.setAllowCreateOptions(true);
        field.addToConfig("canShowLookupWizard",true);
        neighbourhoodFormFields.add(field);
        neighbourhoodForm.setFields(neighbourhoodFormFields);

        FormSection neighbourhoodFormSection = new FormSection("Default", 1, neighbourhoodFormFields, false);
        neighbourhoodFormSection.setSectionType(FormSection.SectionType.FIELDS);
        neighbourhoodForm.setSections(Collections.singletonList(neighbourhoodFormSection));
        neighbourhoodForm.setIsSystemForm(true);
        neighbourhoodForm.setType(FacilioForm.Type.FORM);

        FacilioForm neighbourhoodPortalForm = new FacilioForm();
        neighbourhoodPortalForm.setDisplayName("Neighbourhood");
        neighbourhoodPortalForm.setName("default_"+ FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD +"_portal");
        neighbourhoodPortalForm.setModule(neighbourhoodModule);
        neighbourhoodPortalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        neighbourhoodPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> fields = new ArrayList<>();
        fields.add(new FormField("title", FacilioField.FieldDisplayType.TEXTBOX, "Title", FormField.Required.REQUIRED, 1, 1));
        FormField descriptionField = new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1);
        descriptionField.addToConfig("richText", true);
        fields.add(descriptionField);
        FormField cateField = new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.OPTIONAL, 3, 1);
        cateField.setAllowCreateOptions(true);
        fields.add(cateField);
        fields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 4, 1));
        FormField attachments = new FormField("neighbourhoodattachments", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, 5, 1);
        attachments.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
        fields.add(attachments);
        neighbourhoodPortalForm.setFields(fields);

        FormSection neighbourhoodPortalFormSection = new FormSection("Default", 1, fields, false);
        neighbourhoodPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        neighbourhoodPortalForm.setSections(Collections.singletonList(neighbourhoodPortalFormSection));
        neighbourhoodPortalForm.setIsSystemForm(true);
        neighbourhoodPortalForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> neighbourhoodModuleForms = new ArrayList<>();
        neighbourhoodModuleForms.add(neighbourhoodForm);
        neighbourhoodModuleForms.add(neighbourhoodPortalForm);

        return neighbourhoodModuleForms;
    }
}
