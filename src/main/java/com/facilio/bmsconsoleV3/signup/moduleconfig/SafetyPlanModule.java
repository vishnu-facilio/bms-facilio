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

public class SafetyPlanModule extends BaseModuleConfig{
    public SafetyPlanModule(){
        setModuleName(FacilioConstants.ContextNames.SAFETY_PLAN);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> safetyPlan = new ArrayList<FacilioView>();
        safetyPlan.add(getAllSafetyPlansView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SAFETY_PLAN);
        groupDetails.put("views", safetyPlan);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllSafetyPlansView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Safety Plans");

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule safetyPlanModule = modBean.getModule(FacilioConstants.ContextNames.SAFETY_PLAN);

        FacilioForm safetyPlanForm = new FacilioForm();
        safetyPlanForm.setDisplayName("SAFETY PLAN");
        safetyPlanForm.setName("default_safetyPlan_web");
        safetyPlanForm.setModule(safetyPlanModule);
        safetyPlanForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        safetyPlanForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> safetyPlanFormFields = new ArrayList<>();
        safetyPlanFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        safetyPlanFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        safetyPlanFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 3, 1));
//        safetyPlanForm.setFields(safetyPlanFormFields);

        FormSection section = new FormSection("Default", 1, safetyPlanFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        safetyPlanForm.setSections(Collections.singletonList(section));
        safetyPlanForm.setIsSystemForm(true);
        safetyPlanForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(safetyPlanForm);
    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields maintenanceApp = new ScopeVariableModulesFields();
        maintenanceApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_maintenance_site"));
        maintenanceApp.setModuleId(module.getModuleId());
        maintenanceApp.setFieldName("siteId");

        scopeConfigList = Arrays.asList(maintenanceApp);
        return scopeConfigList;
    }
}

