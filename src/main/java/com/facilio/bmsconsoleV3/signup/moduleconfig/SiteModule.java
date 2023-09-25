package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsole.ModuleSettingConfig.util.ModuleSettingConfigUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;
import static com.facilio.util.SummaryWidgetUtil.addSummaryFieldInWidgetGroup;

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

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNameList = new ArrayList<>();
        appNameList.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
        for (String appName : appNameList) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createSiteDefaultPage(app, module, true, false));
        }
        return appNameVsPage;
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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);

        ArrayList<FacilioForm> siteForms = new ArrayList<>();

        FacilioForm defaultSiteForm = new FacilioForm();
        defaultSiteForm.setName("default_site_web");
        defaultSiteForm.setModule(siteModule);
        defaultSiteForm.setDisplayName("Site");
        defaultSiteForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
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
        siteForms.add(defaultSiteForm);

        FacilioForm fsmSiteForm = new FacilioForm();
        fsmSiteForm.setName("default_site_fsm");
        fsmSiteForm.setModule(siteModule);
        fsmSiteForm.setDisplayName("Site");
        fsmSiteForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP));
        fsmSiteForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        fsmSiteForm.setShowInWeb(true);

        List<FormField> fsmSiteFormFields = new ArrayList<>();
        fsmSiteFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        fsmSiteFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        fsmSiteFormFields.add(new FormField("territory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Territory", FormField.Required.REQUIRED, 3, 1));
        fsmSiteFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 4, 1));
        fsmSiteFormFields.add(new FormField("managedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Managed By", FormField.Required.OPTIONAL, 5, 2));
        fsmSiteFormFields.add(new FormField("siteType", FacilioField.FieldDisplayType.SELECTBOX, "Site Type", FormField.Required.OPTIONAL, 6, 3));
        fsmSiteFormFields.add(new FormField("grossFloorArea", FacilioField.FieldDisplayType.DECIMAL, "Gross Floor Area", FormField.Required.OPTIONAL, 7, 2));
        fsmSiteFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Total Area", FormField.Required.OPTIONAL, 8, 3));
        fsmSiteFormFields.add(new FormField("cddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "CDD Base Temperature", FormField.Required.OPTIONAL, 9, 2));
        fsmSiteFormFields.add(new FormField("hddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "HDD Base Temperature", FormField.Required.OPTIONAL, 9, 3));
        fsmSiteFormFields.add(new FormField("wddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "WDD Base Temperature", FormField.Required.OPTIONAL, 10, 2));
        fsmSiteFormFields.add(new FormField("timeZone", FacilioField.FieldDisplayType.TIMEZONE, "Time Zone", FormField.Required.OPTIONAL, 11, 3));
        fsmSiteFormFields.add(new FormField("boundaryRadius", FacilioField.FieldDisplayType.NUMBER, "Boundary Radius", FormField.Required.OPTIONAL, 12, 2));
        fsmSiteFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",13, 2));

        FormSection formSection = new FormSection("Default", 1, fsmSiteFormFields, false);
        formSection.setSectionType(FormSection.SectionType.FIELDS);
        fsmSiteForm.setSections(Collections.singletonList(formSection));
        fsmSiteForm.setIsSystemForm(true);
        fsmSiteForm.setType(FacilioForm.Type.FORM);
        siteForms.add(fsmSiteForm);
        siteForms.add(remoteMonitoringSiteForm());

        return siteForms;
    }

    private FacilioForm remoteMonitoringSiteForm() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);

        FacilioForm defaultSiteForm = new FacilioForm();
        defaultSiteForm.setName("default_site_web_remotemonitor");
        defaultSiteForm.setModule(siteModule);
        defaultSiteForm.setDisplayName("Site");
        defaultSiteForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING));
        defaultSiteForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultSiteForm.setShowInWeb(true);

        List<FormField> defaultSiteFormFields = new ArrayList<>();
        defaultSiteFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultSiteFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultSiteFormFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.REQUIRED, "client",3, 2));
        defaultSiteFormFields.add(new FormField("managedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Managed By", FormField.Required.OPTIONAL, 4, 2));
        defaultSiteFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 5, 1));
        defaultSiteFormFields.add(new FormField("siteType", FacilioField.FieldDisplayType.SELECTBOX, "Site Type", FormField.Required.OPTIONAL, 6, 3));
        defaultSiteFormFields.add(new FormField("grossFloorArea", FacilioField.FieldDisplayType.DECIMAL, "Gross Floor Area", FormField.Required.OPTIONAL, 7, 2));
        defaultSiteFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Total Area", FormField.Required.OPTIONAL, 8, 3));
        defaultSiteFormFields.add(new FormField("cddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "CDD Base Temperature", FormField.Required.OPTIONAL, 9, 2));
        defaultSiteFormFields.add(new FormField("hddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "HDD Base Temperature", FormField.Required.OPTIONAL, 10, 3));
        defaultSiteFormFields.add(new FormField("wddBaseTemperature", FacilioField.FieldDisplayType.DECIMAL, "WDD Base Temperature", FormField.Required.OPTIONAL, 11, 2));
        defaultSiteFormFields.add(new FormField("timeZone", FacilioField.FieldDisplayType.TIMEZONE, "Time Zone", FormField.Required.OPTIONAL, 12, 3));
        defaultSiteFormFields.add(new FormField("boundaryRadius", FacilioField.FieldDisplayType.NUMBER, "Boundary Radius", FormField.Required.OPTIONAL, 13, 2));


        FormSection section = new FormSection("Default", 1, defaultSiteFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        defaultSiteForm.setSections(Collections.singletonList(section));
        defaultSiteForm.setIsSystemForm(true);
        defaultSiteForm.setType(FacilioForm.Type.FORM);
        return defaultSiteForm;
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

    private List<PagesContext> createSiteDefaultPage(ApplicationContext app, FacilioModule module, boolean isDefault, boolean isTemplate) throws Exception {
    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    FacilioModule siteModule = modBean.getModule("site");

    JSONObject locationWidgetParam = new JSONObject();
        locationWidgetParam.put("fieldName","location");

    JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SITE_ACTIVITY);

        JSONObject buildingsParam = new JSONObject();
        buildingsParam.put("moduleName","building");
        buildingsParam.put("parentName","site");

        JSONObject spaceParam = new JSONObject();
        spaceParam.put("moduleName","space");
        spaceParam.put("parentName","site");

        JSONObject notesModuleParam = new JSONObject();
        notesModuleParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME,"basespacenotes");

        JSONObject attachmentModuleParam = new JSONObject();
        attachmentModuleParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME,"basespaceattachments");

        return new ModulePages()
                .addPage("siteDefaultPage","Default Site Page","",null,isTemplate,isDefault,true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Site details",PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_20", 0, 0, null,getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("siteInsights","",null)
                .addWidget("siteLocationWidgets","Location Details", PageWidget.WidgetType.SPACE_LOCATION,"webSpaceLocation_19_3",0,0,locationWidgetParam,null)
                .widgetDone()
                .addWidget("siteInsights","Site Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_19_6",3,0,spaceParam,null)
                .widgetDone()
                .addWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS,"webOperatingHours_19_3",9,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("weatherCast","",null)
                .addWidget("weatherCard","Weather Card", PageWidget.WidgetType.WEATHER_CARD,"webWeatherCard_28_4",0,0,null,null)
                .widgetDone()
                .addWidget("depreciationAnalysis","Depreciation Analysis", PageWidget.WidgetType.DEPRECIATION_ANALYSIS,"webDepreciationAnalysis_28_8",4,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("HourlyForecast","",null)
                .addWidget("hourlyForecastWidget","Hourly forecast", PageWidget.WidgetType.HOURLY_FORECAST,"flexibleWebHourlyForecast_32",0,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 4, 0, null, getWidgetGroup(false,notesModuleParam,attachmentModuleParam))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("buildingsAndSpaces","Buildings & Spaces", PageTabContext.TabType.SIMPLE,true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("buildings","",null)
                .addWidget("buildings","Buildings", PageWidget.WidgetType.BUILDINGS,"flexibleWebBuildings_32",0,0,buildingsParam,null)
                .widgetDone()
                .sectionDone()
                .addSection("spaces","",null)
                .addWidget("spaces","Spaces", PageWidget.WidgetType.SPACES,"flexibleWebSpaces_32",0,0,spaceParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("plannedmaintenance", "", null)
                .addWidget("siteplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_36", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("unplannedmaintenance", "", null)
                .addWidget("siteunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_36", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                .addSection("siteworkorderdetails", null, null)
                .addWidget("siteworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("recentlyclosedppm", null, null)
                .addWidget("siterecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_23", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("readings", "Readings", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("sitereadings", null, null)
                .addWidget("sitereadings", "Readings", PageWidget.WidgetType.READINGS, "flexiblewebreadings_33", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("sitecommand", null, null)
                .addWidget("sitecommand", "Commands", PageWidget.WidgetType.COMMANDS_WIDGET, "flexiblewebcommandswidget_34", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("siteRelatedReadings", null, null)
                .addWidget("relatedReadings", "Related Readings", PageWidget.WidgetType.RELATED_READINGS, "flexiblewebrelatedreadings_33", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("safetyPlan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("hazards", "", null)
                .addWidget("sitehazards", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_28", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .addSection("precautions", "", null)
                .addWidget("sitePrecautions", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_28", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("siteRelatedlist", "Related List", "List of related records across modules")
                .addWidget("siterelated", "Related",PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 0, null,RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_29", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("siteactivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .layoutDone()
                .pageDone().getCustomPages();

}

    private JSONObject getWidgetGroup(boolean isMobile, JSONObject notesModuleParam, JSONObject attachmentModuleParam) throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_27", 0, 0, notesModuleParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_27", 0, 0, attachmentModuleParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField descFields = moduleBean.getField("description", moduleName);
        FacilioField managedByField = moduleBean.getField("managedBy", moduleName);
        FacilioField siteTypeField =moduleBean.getField("siteType",moduleName);
        FacilioField grossFloorAreaField =moduleBean.getField("grossFloorArea",moduleName);
        FacilioField areaField=moduleBean.getField("area",moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("primaryDetails");
        widgetGroup.setDisplayName("General Details");

        addSummaryFieldInWidgetGroup(widgetGroup, descFields,1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, managedByField, 2 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,siteTypeField,2,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup,grossFloorAreaField,2,3,1);
        addSummaryFieldInWidgetGroup(widgetGroup,areaField,2,4,1);

        widgetGroup.setColumns(4);

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField createdTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField modifiedByField =moduleBean.getField("sysModifiedBy",moduleName);
        FacilioField modifiedTimeField =moduleBean.getField("sysModifiedTime",moduleName);

        SummaryWidgetGroup systemGroup = new SummaryWidgetGroup();
        systemGroup.setName("secondaryDetails");
        systemGroup.setDisplayName("System Details");

        addSummaryFieldInWidgetGroup(systemGroup, sysCreatedByField,1, 1, 1);
        addSummaryFieldInWidgetGroup(systemGroup, createdTimeField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(systemGroup,modifiedByField,1,3,1);
        addSummaryFieldInWidgetGroup(systemGroup,modifiedTimeField,1,4,1);

        systemGroup.setColumns(4);
        FacilioField temperatureField1 =moduleBean.getField("cddBaseTemperature",moduleName);
        FacilioField temperatureField2 = moduleBean.getField("hddBaseTemperature", moduleName);
        FacilioField temperatureField3 = moduleBean.getField("wddBaseTemperature", moduleName);

        SummaryWidgetGroup weatherDetailsGroup = new SummaryWidgetGroup();
        weatherDetailsGroup.setName("weatherDetails");
        weatherDetailsGroup.setDisplayName("Weather Details");

        addSummaryFieldInWidgetGroup(weatherDetailsGroup, temperatureField1,1, 1, 1);
        addSummaryFieldInWidgetGroup(weatherDetailsGroup, temperatureField2, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(weatherDetailsGroup,temperatureField3,1,3,1);

        weatherDetailsGroup.setColumns(3);

        FacilioField timeZoneField = moduleBean.getField("timeZone", moduleName);
        FacilioField failureClassField = moduleBean.getField("failureClass", moduleName);
        FacilioField boundaryRadiusField=moduleBean.getField("boundaryRadius",moduleName);

        SummaryWidgetGroup otherWidgetGroup = new SummaryWidgetGroup();
        otherWidgetGroup.setName("otherDetails");
        otherWidgetGroup.setDisplayName("Other Details");

        addSummaryFieldInWidgetGroup(otherWidgetGroup,timeZoneField ,1, 1, 1);
        addSummaryFieldInWidgetGroup(otherWidgetGroup, failureClassField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(otherWidgetGroup,boundaryRadiusField,1,3,1);
        otherWidgetGroup.setColumns(3);



        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(systemGroup);
        widgetGroupList.add(weatherDetailsGroup);
        widgetGroupList.add(otherWidgetGroup);

        pageWidget.setDisplayName("Site details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }
}

