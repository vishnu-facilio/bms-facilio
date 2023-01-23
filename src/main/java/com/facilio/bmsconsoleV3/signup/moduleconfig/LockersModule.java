package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
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
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class LockersModule extends BaseModuleConfig{
    public LockersModule(){
        setModuleName(FacilioConstants.ContextNames.LOCKERS);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> lockers = new ArrayList<FacilioView>();
        lockers.add(getAllLockersView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.LOCKERS);
        groupDetails.put("views", lockers);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllLockersView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Lockers");
        allView.setModuleName(FacilioConstants.ContextNames.LOCKERS);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP));
        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule lockersModule = modBean.getModule(FacilioConstants.ContextNames.LOCKERS);

        FacilioForm lockersForm = new FacilioForm();
        lockersForm.setDisplayName("NEW LOCKER");
        lockersForm.setName("default_lockers_web");
        lockersForm.setModule(lockersModule);
        lockersForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        lockersForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> lockersFormFields = new ArrayList<>();
        lockersFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        lockersFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Employee", FormField.Required.REQUIRED, "employee", 2, 2));
        lockersFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 5, 2));
        lockersFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL,"building", 6, 3));
        lockersFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL,"floor", 7, 1));


        FormSection lockersFormSection = new FormSection("Default", 1, lockersFormFields, false);
        lockersFormSection.setSectionType(FormSection.SectionType.FIELDS);
        lockersForm.setSections(Collections.singletonList(lockersFormSection));
        lockersForm.setIsSystemForm(true);
        lockersForm.setType(FacilioForm.Type.FORM);

        FacilioForm lockersPortalForm = new FacilioForm();
        lockersPortalForm.setDisplayName("NEW LOCKER");
        lockersPortalForm.setName("default_lockers_portal");
        lockersPortalForm.setModule(lockersModule);
        lockersPortalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        lockersPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> lockersPortalFormFields = new ArrayList<>();
        lockersPortalFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        lockersPortalFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Employee", FormField.Required.REQUIRED, "employee", 2, 2));
        lockersPortalFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 5, 2));
        lockersPortalFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL,"building", 6, 3));
        lockersPortalFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL,"floor", 7, 1));

        FormSection lockersPortalFormSection = new FormSection("Default", 1, lockersPortalFormFields, false);
        lockersPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        lockersPortalForm.setSections(Collections.singletonList(lockersPortalFormSection));
        lockersPortalForm.setIsSystemForm(true);
        lockersPortalForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> lockersModuleForms = new ArrayList<>();
        lockersModuleForms.add(lockersForm);
        lockersModuleForms.add(lockersPortalForm);

        return lockersModuleForms;
    }
}
