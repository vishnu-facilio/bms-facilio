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

public class NewsAndInformationModule extends BaseModuleConfig{
    public NewsAndInformationModule(){
        setModuleName(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> newsAndInformation = new ArrayList<FacilioView>();
        newsAndInformation.add(getAllNewsAndInformationView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION);
        groupDetails.put("views", newsAndInformation);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllNewsAndInformationView() {

        FacilioModule module = ModuleFactory.getNewsAndInformationModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All News and Information");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule newsAndInformationModule = modBean.getModule(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION);

        FacilioForm newsAndInformationForm = new FacilioForm();
        newsAndInformationForm.setDisplayName("News and Information");
        newsAndInformationForm.setName("default_"+ FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION +"_web");
        newsAndInformationForm.setModule(newsAndInformationModule);
        newsAndInformationForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        newsAndInformationForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> newsAndInformationFormFields = new ArrayList<>();
        newsAndInformationFormFields.add(new FormField("title", FacilioField.FieldDisplayType.TEXTBOX, "Title", FormField.Required.REQUIRED, 1, 1));
        FormField descField = new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1);
        descField.addToConfig("richText", true);
        newsAndInformationFormFields.add(descField);
        FormField attachment = new FormField("newsandinformationattachments", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, 3, 1);
        attachment.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
        newsAndInformationFormFields.add(attachment);
        newsAndInformationFormFields.add(new FormField("commentsAllowed", FacilioField.FieldDisplayType.DECISION_BOX, "Comments Allowed", FormField.Required.OPTIONAL, 5, 1));
        FormField field = new FormField("audience", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Audience", FormField.Required.REQUIRED, "audience",6, 1);
        field.setAllowCreateOptions(true);
        field.addToConfig("canShowLookupWizard",true);
        newsAndInformationFormFields.add(field);
//        newsAndInformationForm.setFields(newsAndInformationFormFields);

        FormSection section = new FormSection("Default", 1, newsAndInformationFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        newsAndInformationForm.setSections(Collections.singletonList(section));
        newsAndInformationForm.setIsSystemForm(true);
        newsAndInformationForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(newsAndInformationForm);
    }
}
