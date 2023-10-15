package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;
import static com.facilio.util.SummaryWidgetUtil.addSummaryFieldInWidgetGroup;

import java.util.*;

public class BuildingModule extends BaseModuleConfig {

    public BuildingModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.BUILDING);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING);

        FacilioStatus activeStatus = new FacilioStatus();
        activeStatus.setStatus("active");
        activeStatus.setDisplayName("Active");
        activeStatus.setTypeCode(1);
//        TicketAPI.addStatus(activeStatus, buildingModule);

        FacilioStatus inactiveStatus = new FacilioStatus();
        inactiveStatus.setStatus("inactive");
        inactiveStatus.setDisplayName("In Active");
        inactiveStatus.setTypeCode(2);
//        TicketAPI.addStatus(inactiveStatus, buildingModule);

        StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
        stateFlowRuleContext.setName("Default Stateflow");
        stateFlowRuleContext.setModuleId(buildingModule.getModuleId());
        stateFlowRuleContext.setModule(buildingModule);
        stateFlowRuleContext.setActivityType(EventType.CREATE);
        stateFlowRuleContext.setExecutionOrder(1);
        stateFlowRuleContext.setStatus(true);
        stateFlowRuleContext.setDefaltStateFlow(true);
        stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
        stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
//        WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

        StateflowTransitionContext activeToInactive = new StateflowTransitionContext();
        activeToInactive.setName("Mark as Inactive");
        activeToInactive.setModule(buildingModule);
        activeToInactive.setModuleId(buildingModule.getModuleId());
        activeToInactive.setActivityType(EventType.STATE_TRANSITION);
        activeToInactive.setExecutionOrder(1);
        activeToInactive.setButtonType(1);
        activeToInactive.setFromStateId(activeStatus.getId());
        activeToInactive.setToStateId(inactiveStatus.getId());
        activeToInactive.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        activeToInactive.setType(AbstractStateTransitionRuleContext.TransitionType.NORMAL);
        activeToInactive.setStateFlowId(stateFlowRuleContext.getId());
//        WorkflowRuleAPI.addWorkflowRule(activeToInactive);
        addSystemButtons();
    }

    private void addSystemButtons() throws Exception{
        SystemButtonRuleContext editBuilding = new SystemButtonRuleContext();
        editBuilding.setName("Edit");
        editBuilding.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editBuilding.setIdentifier("editBuilding");
        editBuilding.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editBuilding.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editBuilding.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.BUILDING,editBuilding);

        SystemButtonRuleContext addFloor = new SystemButtonRuleContext();
        addFloor.setName("Add Floor");
        addFloor.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        addFloor.setIdentifier("addFloor");
        addFloor.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        addFloor.setPermission(AccountConstants.ModulePermission.CREATE.name());
        addFloor.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.BUILDING,addFloor);

        SystemButtonRuleContext addSpace = new SystemButtonRuleContext();
        addSpace.setName("Add Space");
        addSpace.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        addSpace.setIdentifier("addBuildingSpace");
        addSpace.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        addSpace.setPermission(AccountConstants.ModulePermission.CREATE.name());
        addSpace.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.BUILDING,addSpace);

        SystemButtonRuleContext addPhoto = new SystemButtonRuleContext();
        addPhoto.setName("Add Photo");
        addPhoto.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        addPhoto.setIdentifier("addBuildingPhoto");
        addPhoto.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        addPhoto.setPermissionRequired(true);
        addPhoto.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.BUILDING,addPhoto);

        SystemButtonRuleContext downloadQR = new SystemButtonRuleContext();
        downloadQR.setName("Download QR");
        downloadQR.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        downloadQR.setIdentifier("downloadBuildingQR");
        downloadQR.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        downloadQR.setPermissionRequired(true);
        downloadQR.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.BUILDING,downloadQR);

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
        appNameList.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
        for (String appName : appNameList) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createFloorDefaultPage(app, module, true, false));
        }
        return appNameVsPage;
    }

    private List<PagesContext> createFloorDefaultPage(ApplicationContext app, FacilioModule module, boolean isDefault, boolean isTemplate) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule buildingModule = modBean.getModule("building");


        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.BUILDING_ACTIVITY);

        JSONObject floorParam = new JSONObject();
        floorParam.put("moduleName","floor");
        floorParam.put("parentName","building");

        JSONObject spaceParam = new JSONObject();
        spaceParam.put("moduleName","space");
        spaceParam.put("parentName","building");

        JSONObject notesModuleParam = new JSONObject();
        notesModuleParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME,"basespacenotes");

        JSONObject attachmentModuleParam = new JSONObject();
        attachmentModuleParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME,"basespaceattachments");


        return new ModulePages()
                .addPage("buildingDefaultPage","Default Building Page","",null,isTemplate,isDefault,true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Building details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("buildingInsights","",null)
                .addWidget("buildingInsights","Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_4_7",0,0,spaceParam,null)
                .widgetDone()
                .addWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS,"webOperatingHours_4_5",7,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false,notesModuleParam,attachmentModuleParam))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("floorsAndSpaces","Floors & Spaces", PageTabContext.TabType.SIMPLE,true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("floors","",null)
                .addWidget("floors","Floors", PageWidget.WidgetType.FLOORS,"flexibleWebFloors_7",0,0,floorParam,null)
                .widgetDone()
                .sectionDone()
                .addSection("spaces","",null)
                .addWidget("spaces","Spaces", PageWidget.WidgetType.SPACES,"flexibleWebSpaces_7",0,0,spaceParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("plannedmaintenance", "", null)
                .addWidget("buildingplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("unplannedmaintenance", "", null)
                .addWidget("buildingunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                .addSection("buildingworkorderdetails", null, null)
                .addWidget("buildingworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_3", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("recentlyclosedppm", null, null)
                .addWidget("buildingrecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_4", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("readings", "Readings", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("buildingreadings", null, null)
                .addWidget("buildingreadings", "Readings", PageWidget.WidgetType.READINGS, "flexiblewebreadings_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("buildingcommand", null, null)
                .addWidget("buildingcommand", "Commands", PageWidget.WidgetType.COMMANDS_WIDGET, "flexiblewebcommandswidget_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("buildingRelatedReadings", null, null)
                .addWidget("relatedReadings", "Related Readings", PageWidget.WidgetType.RELATED_READINGS, "flexiblewebrelatedreadings_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("safetyPlan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("hazards", "", null)
                .addWidget("buildinghazards", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_6", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .addSection("precautions", "", null)
                .addWidget("buildingPrecautions", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_6", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("buildingRelatedlist", "Related List", "List of related records across modules")
                .addWidget("buildingRelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("buildingActivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
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
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesModuleParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentModuleParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName,ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        List<FacilioField> buildingFields = moduleBean.getAllFields(moduleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(buildingFields);

        FacilioField descFields = fieldMap.get("description");
        FacilioField siteField = fieldMap.get("site");
        FacilioField managedByField = fieldMap.get("managedBy");
        FacilioField areaField=fieldMap.get("area");

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");

        addSummaryFieldInWidgetGroup(widgetGroup, descFields,1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, managedByField, 2 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,siteField,2,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup,areaField,2,3,1);

        widgetGroup.setColumns(4);

        FacilioField sysCreatedByField = fieldMap.get("sysCreatedBy");
        FacilioField createdTimeField =fieldMap.get("sysCreatedTime");
        FacilioField modifiedByField =fieldMap.get("sysModifiedBy");
        FacilioField modifiedTimeField =fieldMap.get("sysModifiedTime");

        SummaryWidgetGroup systemGroup = new SummaryWidgetGroup();
        systemGroup.setName("systemInformation");
        systemGroup.setDisplayName("System Information");

        addSummaryFieldInWidgetGroup(systemGroup, sysCreatedByField,1, 1, 1);
        addSummaryFieldInWidgetGroup(systemGroup, createdTimeField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(systemGroup,modifiedByField,1,3,1);
        addSummaryFieldInWidgetGroup(systemGroup,modifiedTimeField,1,4,1);

        systemGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(systemGroup);

        pageWidget.setDisplayName("Building details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> building = new ArrayList<FacilioView>();
        building.add(getAllBuildings().setOrder(order++));
        building.add(getSearchView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BUILDING);
        groupDetails.put("views", building);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    public static FacilioView getSearchView() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioView searchView = new FacilioView();
        searchView.setName("hidden-search");
        searchView.setDisplayName("Building search view");
        searchView.setHidden(true);
        FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
        List<FacilioField> allFields =  modBean.getAllFields(buildingModule.getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        String[] viewFieldNames = new String[]{"name","site","managedBy","maxOccupancy","createdBy","createdTime","modifiedBy","modifiedTime"};
        List<ViewField> viewFields = new ArrayList<>();
        for(String viewFieldName:viewFieldNames){
            if(fieldMap.containsKey(viewFieldName) && fieldMap.get(viewFieldName) != null) {
                FacilioField field = fieldMap.get(viewFieldName);
                ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
                viewField.setField(field);
                viewFields.add(viewField);
            }
        }
        searchView.setFields(viewFields);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        searchView.setAppLinkNames(appLinkNames);
        return searchView;
    }


    private static FacilioView getAllBuildings() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Buildings");
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
        FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING);

        FacilioForm defaultBuildingForm = new FacilioForm();
        defaultBuildingForm.setName("default_building_web");
        defaultBuildingForm.setModule(buildingModule);
        defaultBuildingForm.setDisplayName("Building");
        defaultBuildingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP, FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING,FacilioConstants.ApplicationLinkNames.FSM_APP));
        defaultBuildingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultBuildingForm.setShowInWeb(true);

        List<FormField> defaultBuildingFormfields = new ArrayList<>();
        defaultBuildingFormfields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultBuildingFormfields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultBuildingFormfields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 3, 1));
        defaultBuildingFormfields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site",4, 2));
        defaultBuildingFormfields.add(new FormField("managedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Managed By", FormField.Required.OPTIONAL, 4, 3));
        defaultBuildingFormfields.add(new FormField("grossFloorArea", FacilioField.FieldDisplayType.DECIMAL, "Gross Floor Area", FormField.Required.OPTIONAL, 5, 2));
        defaultBuildingFormfields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Total Built Area", FormField.Required.OPTIONAL, 5, 3));
        defaultBuildingFormfields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 6, 2));
        defaultBuildingFormfields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",7, 2));

//        defaultBuildingForm.setFields(defaultBuildingFormfields);

        FormSection Section = new FormSection("Default", 1, defaultBuildingFormfields, false);
        Section.setSectionType(FormSection.SectionType.FIELDS);
        defaultBuildingForm.setSections(Collections.singletonList(Section));
        defaultBuildingForm.setIsSystemForm(true);
        defaultBuildingForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(defaultBuildingForm);
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
