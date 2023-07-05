package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseFieldContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsole.ModuleSettingConfig.util.ModuleSettingConfigUtil;
import com.facilio.bmsconsole.context.ModuleSettingContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class SiteModule extends BaseModuleConfig {
    public SiteModule(){
        setModuleName(FacilioConstants.ContextNames.SITE);
    }

    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);

            FacilioStatus activeStatus = new FacilioStatus();
            activeStatus.setStatus("active");
            activeStatus.setDisplayName("Active");
            activeStatus.setTypeCode(1);
            TicketAPI.addStatus(activeStatus, siteModule);

            FacilioStatus inactiveStatus = new FacilioStatus();
            inactiveStatus.setStatus("inactive");
            inactiveStatus.setDisplayName("In Active");
            inactiveStatus.setTypeCode(2);
            TicketAPI.addStatus(inactiveStatus, siteModule);

            StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
            stateFlowRuleContext.setName("Default Stateflow");
            stateFlowRuleContext.setModuleId(siteModule.getModuleId());
            stateFlowRuleContext.setModule(siteModule);
            stateFlowRuleContext.setActivityType(EventType.CREATE);
            stateFlowRuleContext.setExecutionOrder(1);
            stateFlowRuleContext.setStatus(true);
            stateFlowRuleContext.setDefaltStateFlow(true);
            stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
            stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
            WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

            StateflowTransitionContext activeToInactive = new StateflowTransitionContext();
            activeToInactive.setName("Mark as Inactive");
            activeToInactive.setModule(siteModule);
            activeToInactive.setModuleId(siteModule.getModuleId());
            activeToInactive.setActivityType(EventType.STATE_TRANSITION);
            activeToInactive.setExecutionOrder(1);
            activeToInactive.setButtonType(1);
            activeToInactive.setFromStateId(activeStatus.getId());
            activeToInactive.setToStateId(inactiveStatus.getId());
            activeToInactive.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
            activeToInactive.setType(AbstractStateTransitionRuleContext.TransitionType.NORMAL);
            activeToInactive.setStateFlowId(stateFlowRuleContext.getId());
            WorkflowRuleAPI.addWorkflowRule(activeToInactive);

            addDefaultSiteModuleConfig(siteModule.getModuleId());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addDefaultSiteModuleConfig(long siteModuleId) throws Exception {

        ModuleSettingContext moduleSettingContext = new ModuleSettingContext();

        moduleSettingContext.setStatus(true);
        moduleSettingContext.setModuleId(siteModuleId);
        moduleSettingContext.setConfigurationName(FacilioConstants.ContextNames.SITE_MAP_VIEW);
        moduleSettingContext.setDescription("Configure to show Google map view in the site list page");
        moduleSettingContext.setDisplayName("Site Map View");
        moduleSettingContext.setStatusDependent(true);

        ModuleSettingConfigUtil.insertModuleConfiguration(moduleSettingContext);
    }

//    private static void createSiteDefaultForm(ModuleBean modBean, FacilioModule siteModule) throws Exception {
//        FacilioForm defaultForm = new FacilioForm();
//        defaultForm.setName("standard");
//        defaultForm.setModule(siteModule);
//        defaultForm.setDisplayName("Standard");
//        defaultForm.setFormType(FacilioForm.FormType.WEB);
//        defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
//        defaultForm.setShowInWeb(true);
//
//        FormSection section = new FormSection();
//        section.setName("Default Section");
//        section.setSectionType(FormSection.SectionType.FIELDS);
//        section.setShowLabel(true);
//
//        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(siteModule.getName()));
//        List<FormField> fields = new ArrayList<>();
//        fields.add(new FormField(fieldMap.get("name").getFieldId(), "name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
//        fields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
//        fields.add(new FormField(fieldMap.get("area").getFieldId(), "area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 3, 1));
//        fields.add(new FormField(fieldMap.get("maxOccupancy").getFieldId(), "maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 3, 1));
//        fields.add(new FormField(fieldMap.get("location").getFieldId(), "location", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Location", FormField.Required.OPTIONAL, 4, 1));
//        fields.add(new FormField(fieldMap.get("managedBy").getFieldId(), "managedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Managed By", FormField.Required.OPTIONAL, 5, 1));
//        fields.add(new FormField(fieldMap.get("siteType").getFieldId(), "siteType", FacilioField.FieldDisplayType.NUMBER, "Site Type", FormField.Required.OPTIONAL, 6, 1));
//        fields.add(new FormField(fieldMap.get("grossFloorArea").getFieldId(), "grossFloorArea", FacilioField.FieldDisplayType.DECIMAL, "Gross Floor Area", FormField.Required.OPTIONAL, 7, 1));
//        fields.add(new FormField(fieldMap.get("weatherStation").getFieldId(), "weatherStation", FacilioField.FieldDisplayType.NUMBER, "Weather Station", FormField.Required.OPTIONAL, 8, 1));
//        fields.add(new FormField(fieldMap.get("cddBaseTemperature").getFieldId(), "cddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "CDD Base Temperature", FormField.Required.OPTIONAL, 9, 1));
//        fields.add(new FormField(fieldMap.get("hddBaseTemperature").getFieldId(), "hddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "HDD Base Temperature", FormField.Required.OPTIONAL, 10, 1));
//        fields.add(new FormField(fieldMap.get("wddBaseTemperature").getFieldId(), "wddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "WDD Base Temperature", FormField.Required.OPTIONAL, 11, 1));
//        fields.add(new FormField(fieldMap.get("timeZone").getFieldId(), "timeZone", FacilioField.FieldDisplayType.TEXTBOX, "Time Zone", FormField.Required.OPTIONAL, 12, 1));
//        fields.add(new FormField(fieldMap.get("client").getFieldId(), "client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.OPTIONAL, 13, 1));
//        fields.add(new FormField(fieldMap.get("boundaryRadius").getFieldId(), "boundaryRadius", FacilioField.FieldDisplayType.NUMBER, "Boundary Radius", FormField.Required.OPTIONAL, 14, 1));
//
//        section.setFields(fields);
//        section.setSequenceNumber(1);
//
//        defaultForm.setSections(Collections.singletonList(section));
//        FormsAPI.createForm(defaultForm, siteModule);
//    }

    public static void addStateflowFieldsToExistingSites() throws Exception {
//        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
//        createSiteDefaultForm(modBean, siteModule);
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> site = new ArrayList<FacilioView>();
        site.add(getAllSites().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SITE);
        groupDetails.put("views", site);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllSites() {

        FacilioModule siteModule = ModuleFactory.getSiteModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Sites");
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);

        FacilioForm defaultSiteForm = new FacilioForm();
        defaultSiteForm.setName("default_site_web");
        defaultSiteForm.setModule(siteModule);
        defaultSiteForm.setDisplayName("Site");
        defaultSiteForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));
        defaultSiteForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultSiteForm.setShowInWeb(true);

        List<FormField> defaultSiteFormFields = new ArrayList<>();
        defaultSiteFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultSiteFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultSiteFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 3, 1));
        defaultSiteFormFields.add(new FormField("managedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Managed By", FormField.Required.OPTIONAL, 4, 2));
        defaultSiteFormFields.add(new FormField("siteType", FacilioField.FieldDisplayType.SELECTBOX, "Site Type", FormField.Required.OPTIONAL, 4, 3));
        defaultSiteFormFields.add(new FormField("grossFloorArea", FacilioField.FieldDisplayType.DECIMAL, "Gross Floor Area", FormField.Required.OPTIONAL, 5, 2));
        defaultSiteFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Total Area", FormField.Required.OPTIONAL, 5, 3));
        defaultSiteFormFields.add(new FormField("cddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "CDD Base Temperature", FormField.Required.OPTIONAL, 6, 2));
        defaultSiteFormFields.add(new FormField("hddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "HDD Base Temperature", FormField.Required.OPTIONAL, 6, 3));
        defaultSiteFormFields.add(new FormField("wddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "WDD Base Temperature", FormField.Required.OPTIONAL, 7, 2));
        defaultSiteFormFields.add(new FormField("timeZone", FacilioField.FieldDisplayType.TIMEZONE, "Time Zone", FormField.Required.OPTIONAL, 8, 3));
        defaultSiteFormFields.add(new FormField("boundaryRadius", FacilioField.FieldDisplayType.NUMBER, "Boundary Radius", FormField.Required.OPTIONAL, 9, 2));
        defaultSiteFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",10, 2));

//        defaultSiteForm.setFields(defaultSiteFormFields);

        FormSection section = new FormSection("Default", 1, defaultSiteFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        defaultSiteForm.setSections(Collections.singletonList(section));
        defaultSiteForm.setIsSystemForm(true);
        defaultSiteForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(defaultSiteForm);
    }

    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("noOfBuildings");
        fieldNames.add("sysCreatedBy");
        fieldNames.add("sysCreatedTime");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());;

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

}
