package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.*;
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

public class SpaceModule extends BaseModuleConfig {
    public SpaceModule(){
        setModuleName(FacilioConstants.ContextNames.SPACE);
    }

    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);

            FacilioStatus activeStatus = new FacilioStatus();
            activeStatus.setStatus("active");
            activeStatus.setDisplayName("Active");
            activeStatus.setTypeCode(1);
            TicketAPI.addStatus(activeStatus, spaceModule);

            FacilioStatus inactiveStatus = new FacilioStatus();
            inactiveStatus.setStatus("inactive");
            inactiveStatus.setDisplayName("In Active");
            inactiveStatus.setTypeCode(2);
            TicketAPI.addStatus(inactiveStatus, spaceModule);

            StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
            stateFlowRuleContext.setName("Default Stateflow");
            stateFlowRuleContext.setModuleId(spaceModule.getModuleId());
            stateFlowRuleContext.setModule(spaceModule);
            stateFlowRuleContext.setActivityType(EventType.CREATE);
            stateFlowRuleContext.setExecutionOrder(1);
            stateFlowRuleContext.setStatus(true);
            stateFlowRuleContext.setDefaltStateFlow(true);
            stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
            stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
            WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

            StateflowTransitionContext activeToInactive = new StateflowTransitionContext();
            activeToInactive.setName("Mark as Inactive");
            activeToInactive.setModule(spaceModule);
            activeToInactive.setModuleId(spaceModule.getModuleId());
            activeToInactive.setActivityType(EventType.STATE_TRANSITION);
            activeToInactive.setExecutionOrder(1);
            activeToInactive.setButtonType(1);
            activeToInactive.setFromStateId(activeStatus.getId());
            activeToInactive.setToStateId(inactiveStatus.getId());
            activeToInactive.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
            activeToInactive.setType(AbstractStateTransitionRuleContext.TransitionType.NORMAL);
            activeToInactive.setStateFlowId(stateFlowRuleContext.getId());
            WorkflowRuleAPI.addWorkflowRule(activeToInactive);
            addSystemButtons();

            // adding form
//            FacilioForm defaultForm = new FacilioForm();
//            defaultForm.setName("standard");
//            defaultForm.setModule(spaceModule);
//            defaultForm.setDisplayName("Standard");
//            defaultForm.setFormType(FacilioForm.FormType.WEB);
//            defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
//            defaultForm.setShowInWeb(true);
//
//            FormSection section = new FormSection();
//            section.setName("Default Section");
//            section.setSectionType(FormSection.SectionType.FIELDS);
//            section.setShowLabel(true);
//
//            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(spaceModule.getName()));
//            List<FormField> fields = new ArrayList<>();
//            fields.add(new FormField(fieldMap.get("name").getFieldId(), "name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
//            fields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
//            fields.add(new FormField(fieldMap.get("area").getFieldId(), "area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 3, 1));
//            fields.add(new FormField(fieldMap.get("maxOccupancy").getFieldId(), "maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 3, 1));
//            fields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, 4, 1));
//
//            fields.add(new FormField(fieldMap.get("building").getFieldId(), "building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL, 5, 1));
//            fields.add(new FormField(fieldMap.get("floor").getFieldId(), "floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL, 6, 1));
//
//            fields.add(new FormField(fieldMap.get("spaceCategory").getFieldId(), "spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space Category", FormField.Required.OPTIONAL, 7, 1));
//
//            section.setFields(fields);
//            section.setSequenceNumber(1);
//
//            defaultForm.setSections(Collections.singletonList(section));
//            FormsAPI.createForm(defaultForm, spaceModule);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void addSystemButtons() throws Exception{
        //List buttons
        SystemButtonApi.addCreateButton(FacilioConstants.ContextNames.SPACE);
        SystemButtonApi.addListDeleteButton(FacilioConstants.ContextNames.SPACE);
        SystemButtonApi.addListEditButton(FacilioConstants.ContextNames.SPACE);
        SystemButtonApi.addExportAsCSV(FacilioConstants.ContextNames.SPACE);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.SPACE);

        //summary buttons
        SystemButtonRuleContext editSpace = new SystemButtonRuleContext();
        editSpace.setName("Edit");
        editSpace.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editSpace.setIdentifier("editSpace");
        editSpace.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editSpace.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editSpace.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SPACE,editSpace);

        SystemButtonRuleContext addSpace = new SystemButtonRuleContext();
        addSpace.setName("Add Sub Space");
        addSpace.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        addSpace.setIdentifier("addSubSpace");
        addSpace.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        addSpace.setPermission(AccountConstants.ModulePermission.CREATE.name());
        addSpace.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SPACE,addSpace);

        SystemButtonRuleContext addPhoto = new SystemButtonRuleContext();
        addPhoto.setName("Add Photo");
        addPhoto.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        addPhoto.setIdentifier("addSpacePhoto");
        addPhoto.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        addPhoto.setPermissionRequired(true);
        addPhoto.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SPACE,addPhoto);

        SystemButtonRuleContext downloadQR = new SystemButtonRuleContext();
        downloadQR.setName("Download QR");
        downloadQR.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        downloadQR.setIdentifier("downloadSpaceQR");
        downloadQR.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        downloadQR.setPermissionRequired(true);
        downloadQR.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SPACE,downloadQR);

        SystemButtonRuleContext addMeterRelationShip = new SystemButtonRuleContext();
        addMeterRelationShip.setName("Add Meter Relationship");
        addMeterRelationShip.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        addMeterRelationShip.setIdentifier("addMeterRelationShip");
        addMeterRelationShip.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        List<SystemButtonAppRelContext> systemButtonAppRels = new ArrayList<>();
        SystemButtonAppRelContext energyAppRelationshipButton = new SystemButtonAppRelContext();
        energyAppRelationshipButton.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        systemButtonAppRels.add(energyAppRelationshipButton);
        addMeterRelationShip.setSystemButtonAppRels(systemButtonAppRels);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SPACE,addMeterRelationShip);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> people = new ArrayList<FacilioView>();
        people.add(getAllSpaces().setOrder(order++));
        people.add(getSearchView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SPACE);
        groupDetails.put("views", people);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    public static FacilioView getSearchView() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioView searchView = new FacilioView();
        searchView.setName("hidden-search");
        searchView.setDisplayName("Space search view");
        searchView.setHidden(true);
        FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);
        List<FacilioField> allFields =  modBean.getAllFields(spaceModule.getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        String[] viewFieldNames = new String[]{"name","site","building","floor","category","maxOccupancy","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime"};
        List<ViewField> viewFields = new ArrayList<>();
        for(String viewFieldName:viewFieldNames){
            if(fieldMap.containsKey(viewFieldName) && fieldMap.get(viewFieldName) != null) {
                FacilioField field = fieldMap.get(viewFieldName);
                ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
                viewField.setField(field);
                viewField.setFieldId(field.getFieldId());
                viewField.setFieldName(field.getName());
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

    private static FacilioView getAllSpaces() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Spaces");
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
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
        for (String appName : appNameList) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            if(appName.equals(FacilioConstants.ApplicationLinkNames.ENERGY_APP)){
                appNameVsPage.put(appName, createEnergyAppSpaceDefaultPage(app, module, true, false));
            }else  if(appName.equals(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)){
                appNameVsPage.put(appName, createRemoteMonitoringAppSpaceDefaultPage(app, module, true, false));
            }else if(appName.equalsIgnoreCase(FacilioConstants.ApplicationLinkNames.FSM_APP)){
                appNameVsPage.put(appName, createFSMAppSpaceDefaultPage(app, module, true, false));
            }else {
                appNameVsPage.put(appName, createSpaceDefaultPage(app, module, true, false));
            }
        }
        return appNameVsPage;
    }
    private List<PagesContext> createSpaceDefaultPage(ApplicationContext app,FacilioModule module,boolean isDefault,boolean isTemplate) throws Exception{
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SPACE_ACTIVITY);

        JSONObject spaceParam = new JSONObject();
        spaceParam.put("moduleName","space");
        spaceParam.put("parentName","space");

        JSONObject notesModuleParam = new JSONObject();
        notesModuleParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME,"basespacenotes");

        JSONObject attachmentModuleParam = new JSONObject();
        attachmentModuleParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME,"basespaceattachments");

        return new ModulePages()
                .addPage("spaceDefaultPage","Default Space Page","",null,isTemplate,isDefault,true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Space details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("spaceInsights","",null)
                .addWidget("spaceLocationWidgets","Location Details", PageWidget.WidgetType.SPACE_LOCATION,"webSpaceLocation_4_3",0,0,null,null)
                .widgetDone()
                .addWidget("spaceInsights","Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_4_6",3,0,spaceParam,null)
                .widgetDone()
                .addWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS,"webOperatingHours_4_3",9,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false,notesModuleParam,attachmentModuleParam))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("subSpaces","Sub Spaces", PageTabContext.TabType.SIMPLE,true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("subspaces","",null)
                .addWidget("subspaces","Sub Spaces", PageWidget.WidgetType.SUB_SPACES,"flexibleWebSubSpaces_7",0,0,spaceParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("plannedmaintenance", "", null)
                .addWidget("spaceplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("unplannedmaintenance", "", null)
                .addWidget("spaceunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                .addSection("spaceworkorderdetails", null, null)
                .addWidget("spaceworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_3", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("recentlyclosedppm", null, null)
                .addWidget("spacerecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_4", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("safetyPlan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("hazards", "", null)
                .addWidget("spacehazards", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_6", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .addSection("precautions", "", null)
                .addWidget("spacePrecautions", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_6", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("classification", null, null)
                .addWidget("classification", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("spaceRelatedlist", "Related List", "List of related records across modules")
                .addWidget("spacerelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
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
                .addWidget("spaceActivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .layoutDone()
                .pageDone().getCustomPages();
    }
    private List<PagesContext> createRemoteMonitoringAppSpaceDefaultPage(ApplicationContext app,FacilioModule module,boolean isDefault,boolean isTemplate) throws Exception{
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SPACE_ACTIVITY);

        JSONObject spaceParam = new JSONObject();
        spaceParam.put("moduleName","space");
        spaceParam.put("parentName","space");

        JSONObject notesModuleParam = new JSONObject();
        notesModuleParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME,"basespacenotes");

        JSONObject attachmentModuleParam = new JSONObject();
        attachmentModuleParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME,"basespaceattachments");

        return new ModulePages()
                .addPage("spaceDefaultPage","Default Space Page","",null,isTemplate,isDefault,true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Space details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("spaceInsights","",null)
                .addWidget("spaceLocationWidgets","Location Details", PageWidget.WidgetType.SPACE_LOCATION,"webSpaceLocation_4_3",0,0,null,null)
                .widgetDone()
                .addWidget("spaceInsights","Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_4_6",3,0,spaceParam,null)
                .widgetDone()
                .addWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS,"webOperatingHours_4_3",9,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false,notesModuleParam,attachmentModuleParam))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("subSpaces","Sub Spaces", PageTabContext.TabType.SIMPLE,true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("subspaces","",null)
                .addWidget("subspaces","Sub Spaces", PageWidget.WidgetType.SUB_SPACES,"flexibleWebSubSpaces_7",0,0,spaceParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("classification", null, null)
                .addWidget("classification", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("spaceRelatedlist", "Related List", "List of related records across modules")
                .addWidget("spacerelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
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
                .addWidget("spaceActivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .layoutDone()
                .pageDone().getCustomPages();
    }
    private List<PagesContext> createFSMAppSpaceDefaultPage(ApplicationContext app,FacilioModule module,boolean isDefault,boolean isTemplate) throws Exception{
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SPACE_ACTIVITY);

        JSONObject spaceParam = new JSONObject();
        spaceParam.put("moduleName","space");
        spaceParam.put("parentName","space");

        JSONObject notesModuleParam = new JSONObject();
        notesModuleParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME,"basespacenotes");

        JSONObject attachmentModuleParam = new JSONObject();
        attachmentModuleParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME,"basespaceattachments");

        return new ModulePages()
                .addPage("spaceDefaultPage","Default Space Page","",null,isTemplate,isDefault,true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Space details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("spaceInsights","",null)
                .addWidget("spaceLocationWidgets","Location Details", PageWidget.WidgetType.SPACE_LOCATION,"webSpaceLocation_4_3",0,0,null,null)
                .widgetDone()
                .addWidget("spaceInsights","Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_4_6",3,0,spaceParam,null)
                .widgetDone()
                .addWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS,"webOperatingHours_4_3",9,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false,notesModuleParam,attachmentModuleParam))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("subSpaces","Sub Spaces", PageTabContext.TabType.SIMPLE,true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("subspaces","",null)
                .addWidget("subspaces","Sub Spaces", PageWidget.WidgetType.SUB_SPACES,"flexibleWebSubSpaces_7",0,0,spaceParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("spaceRelatedlist", "Related List", "List of related records across modules")
                .addWidget("spacerelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
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
                .addWidget("spaceActivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .layoutDone()
                .pageDone().getCustomPages();
    }
    private List<PagesContext> createEnergyAppSpaceDefaultPage(ApplicationContext app,FacilioModule module,boolean isDefault,boolean isTemplate) throws Exception{
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SPACE_ACTIVITY);

        JSONObject spaceParam = new JSONObject();
        spaceParam.put("moduleName","space");
        spaceParam.put("parentName","space");

        JSONObject notesModuleParam = new JSONObject();
        notesModuleParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME,"basespacenotes");

        JSONObject attachmentModuleParam = new JSONObject();
        attachmentModuleParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME,"basespaceattachments");

        return new ModulePages()
                .addPage("spaceDefaultPage","Default Space Page","",null,isTemplate,isDefault,true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Space details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("spaceInsights","",null)
                .addWidget("spaceLocationWidgets","Location Details", PageWidget.WidgetType.SPACE_LOCATION,"webSpaceLocation_4_3",0,0,null,null)
                .widgetDone()
                .addWidget("spaceInsights","Insights", PageWidget.WidgetType.ENERGY_SPACE_INSIGHTS,"webEnergySpaceInsights_4_6",3,0,spaceParam,null)
                .widgetDone()
                .addWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS,"webOperatingHours_4_3",9,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false,notesModuleParam,attachmentModuleParam))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("subSpaces","Sub Spaces", PageTabContext.TabType.SIMPLE,true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("subspaces","",null)
                .addWidget("subspaces","Sub Spaces", PageWidget.WidgetType.SUB_SPACES,"flexibleWebSubSpaces_7",0,0,spaceParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("spaceRelatedlist", "Related List", "List of related records across modules")
                .addWidget("spacerelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET,"flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("meters", "Meters", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterRelations", "", "")
                .addWidget("meterRelationsWidget", "Relationships", PageWidget.WidgetType.METER_RELATIONSHIPS,"flexiblewebmeterrelationshipwidget_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("spaceActivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
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
        FacilioField buildingField = fieldMap.get("building");
        FacilioField floorField = fieldMap.get("floor");

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");

        addSummaryFieldInWidgetGroup(widgetGroup, descFields,1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, siteField, 2 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,buildingField,2,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup,floorField,2,3,1);

        widgetGroup.setColumns(4);

        FacilioField sysCreatedByField = fieldMap.get("sysCreatedBy");
        FacilioField createdTimeField =fieldMap.get("sysCreatedTime");
        FacilioField modifiedByField =fieldMap.get("sysModifiedBy");
        FacilioField modifiedTimeField =fieldMap.get("sysModifiedTime");

        SummaryWidgetGroup systemGroup = new SummaryWidgetGroup();
        systemGroup.setName("secondaryDetails");
        systemGroup.setDisplayName("System Details");

        addSummaryFieldInWidgetGroup(systemGroup, sysCreatedByField,1, 1, 1);
        addSummaryFieldInWidgetGroup(systemGroup, createdTimeField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(systemGroup,modifiedByField,1,3,1);
        addSummaryFieldInWidgetGroup(systemGroup,modifiedTimeField,1,4,1);

        systemGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(systemGroup);

        pageWidget.setDisplayName("Space Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }


    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);

        FacilioForm defaultSpaceWebSiteForm = new FacilioForm();
        defaultSpaceWebSiteForm.setName("default_space_web_site");
        defaultSpaceWebSiteForm.setModule(spaceModule);
        defaultSpaceWebSiteForm.setDisplayName("Standard Form From Site");
        defaultSpaceWebSiteForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP,FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING,FacilioConstants.ApplicationLinkNames.FSM_APP));
        defaultSpaceWebSiteForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultSpaceWebSiteForm.setShowInWeb(true);

        List<FormField> defaultSpaceWebSiteFormFields = new ArrayList<>();
        defaultSpaceWebSiteFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site Associated", FormField.Required.REQUIRED,"site", 5, 1,true));
        defaultSpaceWebSiteFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 7, 1));
        defaultSpaceWebSiteFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));
        defaultSpaceWebSiteFormFields.add(new FormField("amenities",FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Amenities",FormField.Required.OPTIONAL,"amenity",9,2));


        try {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.RESOURCE_BOOKING)) {
                defaultSpaceWebSiteFormFields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
                defaultSpaceWebSiteFormFields.add(new FormField("unitReservationCost", FacilioField.FieldDisplayType.TEXTBOX, "Unit Reservation Cost", FormField.Required.OPTIONAL, 9, 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FormSection defaultSpaceWebSiteFormSection = new FormSection("Default", 1, defaultSpaceWebSiteFormFields, false);
        defaultSpaceWebSiteFormSection.setSectionType(FormSection.SectionType.FIELDS);
        defaultSpaceWebSiteForm.setSections(Collections.singletonList(defaultSpaceWebSiteFormSection));
        defaultSpaceWebSiteForm.setIsSystemForm(true);
        defaultSpaceWebSiteForm.setType(FacilioForm.Type.FORM);


        FacilioForm defaultSpaceWebBuildingForm = new FacilioForm();
        defaultSpaceWebBuildingForm.setName("default_space_web_building");
        defaultSpaceWebBuildingForm.setModule(spaceModule);
        defaultSpaceWebBuildingForm.setDisplayName("Standard Form From Building");
        defaultSpaceWebBuildingForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING,FacilioConstants.ApplicationLinkNames.FSM_APP));
        defaultSpaceWebBuildingForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultSpaceWebBuildingForm.setShowInWeb(true);

        List<FormField> defaultSpaceWebBuildingFormFields = new ArrayList<>();
        defaultSpaceWebBuildingFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building Associated", FormField.Required.REQUIRED,"building", 5, 1,true));
        defaultSpaceWebBuildingFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 7, 1));
        defaultSpaceWebBuildingFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));
        defaultSpaceWebBuildingFormFields.add(new FormField("amenities",FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Amenities",FormField.Required.OPTIONAL,"amenity",9,2));


        try {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.RESOURCE_BOOKING)) {
                defaultSpaceWebBuildingFormFields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
                defaultSpaceWebBuildingFormFields.add(new FormField("unitReservationCost", FacilioField.FieldDisplayType.TEXTBOX, "Unit Reservation Cost", FormField.Required.OPTIONAL, 9, 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FormSection defaultSpaceWebBuildingFormSection = new FormSection("Default", 1, defaultSpaceWebBuildingFormFields, false);
        defaultSpaceWebBuildingFormSection.setSectionType(FormSection.SectionType.FIELDS);
        defaultSpaceWebBuildingForm.setSections(Collections.singletonList(defaultSpaceWebBuildingFormSection));
        defaultSpaceWebBuildingForm.setIsSystemForm(true);
        defaultSpaceWebBuildingForm.setType(FacilioForm.Type.FORM);


        FacilioForm defaultSpaceWebFloorForm = new FacilioForm();
        defaultSpaceWebFloorForm.setName("default_space_web_floor");
        defaultSpaceWebFloorForm.setModule(spaceModule);
        defaultSpaceWebFloorForm.setDisplayName("Standard Form From Floor");
        defaultSpaceWebFloorForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING,FacilioConstants.ApplicationLinkNames.FSM_APP));
        defaultSpaceWebFloorForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultSpaceWebFloorForm.setShowInWeb(true);

        List<FormField> defaultSpaceWebFloorFormFields = new ArrayList<>();
        defaultSpaceWebFloorFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor Associated", FormField.Required.REQUIRED,"floor", 5, 1,true));
        defaultSpaceWebFloorFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 7, 1));
        defaultSpaceWebFloorFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));
        defaultSpaceWebFloorFormFields.add(new FormField("amenities",FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Amenities",FormField.Required.OPTIONAL,"amenity",9,2));

        try {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.RESOURCE_BOOKING)) {
                defaultSpaceWebFloorFormFields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
                defaultSpaceWebFloorFormFields.add(new FormField("unitReservationCost", FacilioField.FieldDisplayType.TEXTBOX, "Unit Reservation Cost", FormField.Required.OPTIONAL, 9, 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FormSection defaultSpaceWebFloorFormSection = new FormSection("Default", 1, defaultSpaceWebFloorFormFields, false);
        defaultSpaceWebFloorFormSection.setSectionType(FormSection.SectionType.FIELDS);
        defaultSpaceWebFloorForm.setSections(Collections.singletonList(defaultSpaceWebFloorFormSection));
        defaultSpaceWebFloorForm.setIsSystemForm(true);
        defaultSpaceWebFloorForm.setType(FacilioForm.Type.FORM);

        FacilioForm defaultSpaceWebSpaceForm = new FacilioForm();
        defaultSpaceWebSpaceForm.setName("default_space_web_space");
        defaultSpaceWebSpaceForm.setModule(spaceModule);
        defaultSpaceWebSpaceForm.setDisplayName("Standard Form From Space");
        defaultSpaceWebSpaceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING,FacilioConstants.ApplicationLinkNames.FSM_APP));
        defaultSpaceWebSpaceForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultSpaceWebSpaceForm.setShowInWeb(true);

        List<FormField> defaultSpaceWebSpaceFormFields = new ArrayList<>();
        defaultSpaceWebSpaceFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultSpaceWebSpaceFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultSpaceWebSpaceFormFields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
        defaultSpaceWebSpaceFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
        defaultSpaceWebSpaceFormFields.add(getSpaceAssociatedField());
        defaultSpaceWebSpaceFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
        defaultSpaceWebSpaceFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 7, 1));
        defaultSpaceWebSpaceFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));
        defaultSpaceWebSpaceFormFields.add(new FormField("amenities",FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Amenities",FormField.Required.OPTIONAL,"amenity",9,2));

        try {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.RESOURCE_BOOKING)) {
                defaultSpaceWebSpaceFormFields.add(new FormField("reservable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Reservable", FormField.Required.OPTIONAL, 8, 1));
                defaultSpaceWebSpaceFormFields.add(new FormField("unitReservationCost", FacilioField.FieldDisplayType.TEXTBOX, "Unit Reservation Cost", FormField.Required.OPTIONAL, 9, 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        FormSection defaultSpaceWebSpaceFormSection = new FormSection("Default", 1, defaultSpaceWebSpaceFormFields, false);
        defaultSpaceWebSpaceFormSection.setSectionType(FormSection.SectionType.FIELDS);
        defaultSpaceWebSpaceForm.setSections(Collections.singletonList(defaultSpaceWebSpaceFormSection));
        defaultSpaceWebSpaceForm.setIsSystemForm(true);
        defaultSpaceWebSpaceForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> spaceModuleForms = new ArrayList<>();
        spaceModuleForms.add(defaultSpaceWebSiteForm);
        spaceModuleForms.add(defaultSpaceWebBuildingForm);
        spaceModuleForms.add(defaultSpaceWebFloorForm);
        spaceModuleForms.add(defaultSpaceWebSpaceForm);

        return spaceModuleForms;
    }

    public static FormField getSpaceAssociatedField() {
        FormField parentSpace = new FormField("parentSpace", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space Associated", FormField.Required.REQUIRED,"space", 5, 1,true);
        parentSpace.addToConfig("isFiltersEnabled", true); // Adding this as parent space is a special case with no field object
        parentSpace.addToConfig("lookupModuleName", "space");
        return parentSpace;
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

        ScopeVariableModulesFields energyApp = new ScopeVariableModulesFields();
        energyApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_energy_site"));
        energyApp.setModuleId(module.getModuleId());
        energyApp.setFieldName("siteId");

        scopeConfigList = Arrays.asList(maintenanceApp,energyApp);
        return scopeConfigList;
    }

    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("site");
        fieldNames.add("building");
        fieldNames.add("floor");
        fieldNames.add("sysCreatedTime");
        fieldNames.add("sysCreatedBy");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

}
