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

public class ContactDirectoryModule extends BaseModuleConfig{

    public ContactDirectoryModule(){
        setModuleName(FacilioConstants.ContextNames.CONTACT_DIRECTORY);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> contactDirectory = new ArrayList<FacilioView>();
        contactDirectory.add(getAllContactDirectoryView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.CONTACT_DIRECTORY);
        groupDetails.put("views", contactDirectory);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllContactDirectoryView() {

        FacilioModule module = ModuleFactory.getContactDirectoryModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Contact Directory");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule contactDirectoryModule = modBean.getModule(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY);

        FacilioForm contactDirectoryForm = new FacilioForm();
        contactDirectoryForm.setDisplayName("Contact Directory");
        contactDirectoryForm.setName("default_"+ FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY +"_web");
        contactDirectoryForm.setModule(contactDirectoryModule);
        contactDirectoryForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        contactDirectoryForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> contactDirectoryFormFields = new ArrayList<>();
        contactDirectoryFormFields.add(new FormField("contactName", FacilioField.FieldDisplayType.TEXTBOX, "Contact Name", FormField.Required.REQUIRED, 2, 1));
        contactDirectoryFormFields.add(new FormField("contactEmail", FacilioField.FieldDisplayType.TEXTBOX, "Contact Email", FormField.Required.REQUIRED, 3, 1));
        contactDirectoryFormFields.add(new FormField("contactPhone", FacilioField.FieldDisplayType.TEXTBOX, "Contact Phone", FormField.Required.REQUIRED,4, 1));
        contactDirectoryFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 5, 1));
        contactDirectoryFormFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.OPTIONAL, 6, 1));
        FormField field = new FormField("audience", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Audience", FormField.Required.REQUIRED, "audience",7, 1);
        field.setAllowCreateOptions(true);
        field.addToConfig("canShowLookupWizard",true);
        contactDirectoryFormFields.add(field);
//        contactDirectoryForm.setFields(contactDirectoryFormFields);

        FormSection Section = new FormSection("Default", 1, contactDirectoryFormFields, false);
        Section.setSectionType(FormSection.SectionType.FIELDS);
        contactDirectoryForm.setSections(Collections.singletonList(Section));
        contactDirectoryForm.setIsSystemForm(true);
        contactDirectoryForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(contactDirectoryForm);
    }
}
