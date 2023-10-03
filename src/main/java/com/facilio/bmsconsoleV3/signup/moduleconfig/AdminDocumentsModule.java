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

public class AdminDocumentsModule extends BaseModuleConfig{
    public AdminDocumentsModule(){
        setModuleName(FacilioConstants.ContextNames.ADMIN_DOCUMENTS);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> adminDocuments = new ArrayList<FacilioView>();
        adminDocuments.add(getAllAdminDocumentsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ADMIN_DOCUMENTS);
        groupDetails.put("views", adminDocuments);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAdminDocumentsView() {

        FacilioModule module = ModuleFactory.getAdminDocumentsModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Admin Documents");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule adminDocumentsModule = modBean.getModule(FacilioConstants.ContextNames.ADMIN_DOCUMENTS);

        FacilioForm adminDocumentsForm = new FacilioForm();
        adminDocumentsForm.setDisplayName("Admin Documents");
        adminDocumentsForm.setName("default_"+ FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS +"_web");
        adminDocumentsForm.setModule(adminDocumentsModule);
        adminDocumentsForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        adminDocumentsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> adminDocumentsFormFields = new ArrayList<>();
        adminDocumentsFormFields.add(new FormField("title", FacilioField.FieldDisplayType.TEXTBOX, "Title", FormField.Required.REQUIRED, 1, 1));
        adminDocumentsFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        adminDocumentsFormFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.OPTIONAL, 3, 1));
        adminDocumentsFormFields.add(new FormField("file", FacilioField.FieldDisplayType.FILE, "File", FormField.Required.REQUIRED, 4, 1));
        FormField field = new FormField("audience", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Audience", FormField.Required.REQUIRED, "audience",5, 1);
        field.setAllowCreateOptions(true);
        field.addToConfig("canShowLookupWizard",true);
        adminDocumentsFormFields.add(field);
//        adminDocumentsForm.setFields(adminDocumentsFormFields);

        FormSection section = new FormSection("Default", 1, adminDocumentsFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        adminDocumentsForm.setSections(Collections.singletonList(section));
        adminDocumentsForm.setIsSystemForm(true);
        adminDocumentsForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(adminDocumentsForm);
    }
}
