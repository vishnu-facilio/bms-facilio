package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
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

public class FloorModule extends BaseModuleConfig {

    public FloorModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.FLOOR);
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
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
        for (String appName : appNameList) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            if(appName.equals(FacilioConstants.ApplicationLinkNames.ENERGY_APP)){
                appNameVsPage.put(appName, createEnergyAppFloorDefaultPage(app, module, true, false));
            }
            else {
                appNameVsPage.put(appName, createFloorDefaultPage(app, module, true, false));
            }
        }
        return appNameVsPage;
    }

    private List<PagesContext> createFloorDefaultPage(ApplicationContext app, FacilioModule module, boolean isDefault, boolean isTemplate) throws Exception{
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule floorModule = modBean.getModule("floor");


            JSONObject historyWidgetParam = new JSONObject();
            historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.FLOOR_ACTIVITY);

            JSONObject spaceParam = new JSONObject();
            spaceParam.put("moduleName","space");
            spaceParam.put("parentName","floor");

        JSONObject notesModuleParam = new JSONObject();
        notesModuleParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME,"basespacenotes");

        JSONObject attachmentModuleParam = new JSONObject();
        attachmentModuleParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME,"basespaceattachments");

        return new ModulePages()
                    .addPage("floorDefaultPage","Default Floor Page","",null,isTemplate,isDefault,true)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("floorDetails", "Floor details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("floorInsights","",null)
                    .addWidget("floorInsights","Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_4_7",0,0,spaceParam,null)
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

                    .addTab("spaces","Spaces", PageTabContext.TabType.SIMPLE,true,null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("spaces","",null)
                    .addWidget("spaces","Spaces", PageWidget.WidgetType.SPACES,"flexibleWebSpaces_7",0,0,spaceParam,null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("plannedmaintenance", "", null)
                    .addWidget("floorplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("unplannedmaintenance", "", null)
                    .addWidget("floorunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("floorworkorderdetails", null, null)
                    .addWidget("floorworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("recentlyclosedppm", null, null)
                    .addWidget("floorrecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("readings", "Readings", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("floorreadings", null, null)
                    .addWidget("floorreadings", "Readings", PageWidget.WidgetType.READINGS, "flexiblewebreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("floorcommand", null, null)
                    .addWidget("floorcommand", "Commands", PageWidget.WidgetType.COMMANDS_WIDGET, "flexiblewebcommandswidget_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("floorRelatedReadings", null, null)
                    .addWidget("relatedReadings", "Related Readings", PageWidget.WidgetType.RELATED_READINGS, "flexiblewebrelatedreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("safetyPlan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("hazards", "", null)
                    .addWidget("floorhazards", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_6", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("precautions", "", null)
                    .addWidget("floorPrecautions", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_6", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("floorRelatedlist", "Related List", "List of related records across modules")
                    .addWidget("floorrelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
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
                    .addWidget("flooractivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .layoutDone()
                    .pageDone().getCustomPages();
    }
    private List<PagesContext> createEnergyAppFloorDefaultPage(ApplicationContext app, FacilioModule module, boolean isDefault, boolean isTemplate) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule floorModule = modBean.getModule("floor");


        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.FLOOR_ACTIVITY);

        JSONObject spaceParam = new JSONObject();
        spaceParam.put("moduleName","space");
        spaceParam.put("parentName","floor");

        JSONObject notesModuleParam = new JSONObject();
        notesModuleParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME,"basespacenotes");

        JSONObject attachmentModuleParam = new JSONObject();
        attachmentModuleParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME,"basespaceattachments");

        return new ModulePages()
                .addPage("floorDefaultPage","Default Floor Page","",null,isTemplate,isDefault,true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("floorDetails", "Floor details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("floorInsights","",null)
                .addWidget("floorInsights","Insights", PageWidget.WidgetType.SPACE_INSIGHTS,"webSpaceInsights_4_7",0,0,spaceParam,null)
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

                .addTab("spaces","Spaces", PageTabContext.TabType.SIMPLE,true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("spaces","",null)
                .addWidget("spaces","Spaces", PageWidget.WidgetType.SPACES,"flexibleWebSpaces_7",0,0,spaceParam,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("floorRelatedlist", "Related List", "List of related records across modules")
                .addWidget("floorrelated", "Related", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
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
                .addWidget("flooractivity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
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
    private static JSONObject getSummaryWidgetDetails(String moduleName,ApplicationContext app) throws Exception{
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        List<FacilioField> buildingFields = moduleBean.getAllFields(moduleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(buildingFields);

        FacilioField descFields = fieldMap.get("description");
        FacilioField siteField = fieldMap.get("site");
        FacilioField managedByField = fieldMap.get("managedBy");
        FacilioField buildingField = fieldMap.get("building");

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");

        addSummaryFieldInWidgetGroup(widgetGroup, descFields,1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup,siteField,2,1,1);
        addSummaryFieldInWidgetGroup(widgetGroup,buildingField,2,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup, managedByField, 2 , 3, 1);

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

        pageWidget.setDisplayName("Floor details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    @Override
    public void addData() throws Exception {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule floorModule = modBean.getModule(FacilioConstants.ContextNames.FLOOR);

            FacilioStatus activeStatus = new FacilioStatus();
            activeStatus.setStatus("active");
            activeStatus.setDisplayName("Active");
            activeStatus.setTypeCode(1);
            TicketAPI.addStatus(activeStatus, floorModule);

            FacilioStatus inactiveStatus = new FacilioStatus();
            inactiveStatus.setStatus("inactive");
            inactiveStatus.setDisplayName("In Active");
            inactiveStatus.setTypeCode(2);
            TicketAPI.addStatus(inactiveStatus, floorModule);

            StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
            stateFlowRuleContext.setName("Default Stateflow");
            stateFlowRuleContext.setModuleId(floorModule.getModuleId());
            stateFlowRuleContext.setModule(floorModule);
            stateFlowRuleContext.setActivityType(EventType.CREATE);
            stateFlowRuleContext.setExecutionOrder(1);
            stateFlowRuleContext.setStatus(true);
            stateFlowRuleContext.setDefaltStateFlow(true);
            stateFlowRuleContext.setDefaultStateId(activeStatus.getId());
            stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
            WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

            StateflowTransitionContext activeToInactive = new StateflowTransitionContext();
            activeToInactive.setName("Mark as Inactive");
            activeToInactive.setModule(floorModule);
            activeToInactive.setModuleId(floorModule.getModuleId());
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
//            defaultForm.setModule(floorModule);
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
//            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(floorModule.getName()));
//            List<FormField> fields = new ArrayList<>();
//            fields.add(new FormField(fieldMap.get("name").getFieldId(), "name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
//            fields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
//            fields.add(new FormField(fieldMap.get("area").getFieldId(), "area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 3, 1));
//            fields.add(new FormField(fieldMap.get("maxOccupancy").getFieldId(), "maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 3, 1));
//            fields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, 4, 1));
//
//            fields.add(new FormField(fieldMap.get("building").getFieldId(), "building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL, 5, 1));
//
//            fields.add(new FormField(fieldMap.get("floorlevel").getFieldId(), "floorlevel", FacilioField.FieldDisplayType.NUMBER, "Floor Level", FormField.Required.OPTIONAL, 6, 1));
//
//            section.setFields(fields);
//            section.setSequenceNumber(1);
//
//            defaultForm.setSections(Collections.singletonList(section));
//            FormsAPI.createForm(defaultForm, floorModule);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void addSystemButtons() throws Exception {
        SystemButtonRuleContext editFloor = new SystemButtonRuleContext();
        editFloor.setName("Edit");
        editFloor.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editFloor.setIdentifier("editFloor");
        editFloor.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editFloor.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editFloor.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FLOOR,editFloor);

        SystemButtonRuleContext addSpace = new SystemButtonRuleContext();
        addSpace.setName("Add Space");
        addSpace.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        addSpace.setIdentifier("addFloorSpace");
        addSpace.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        addSpace.setPermission(AccountConstants.ModulePermission.CREATE.name());
        addSpace.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FLOOR,addSpace);

        SystemButtonRuleContext addPhoto = new SystemButtonRuleContext();
        addPhoto.setName("Add Photo");
        addPhoto.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        addPhoto.setIdentifier("addFloorPhoto");
        addPhoto.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        addPhoto.setPermissionRequired(true);
        addPhoto.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FLOOR,addPhoto);

        SystemButtonRuleContext floorPlan = new SystemButtonRuleContext();
        floorPlan.setName("Manage Floor Plan");
        floorPlan.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        floorPlan.setIdentifier("floorPlan");
        floorPlan.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        floorPlan.setPermissionRequired(true);
        floorPlan.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FLOOR,floorPlan);

        SystemButtonRuleContext downloadQR = new SystemButtonRuleContext();
        downloadQR.setName("Download QR");
        downloadQR.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        downloadQR.setIdentifier("downloadFloorQR");
        downloadQR.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        downloadQR.setPermissionRequired(true);
        downloadQR.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.FLOOR,downloadQR);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> floor = new ArrayList<FacilioView>();
        floor.add(getAllFloors().setOrder(order++));
        floor.add(getSearchView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.FLOOR);
        groupDetails.put("views", floor);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    public static FacilioView getSearchView() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioView searchView = new FacilioView();
        searchView.setName("hidden-search");
        searchView.setDisplayName("Floor search view");
        searchView.setHidden(true);
        FacilioModule floorModule = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
        List<FacilioField> allFields =  modBean.getAllFields(floorModule.getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        String[] viewFieldNames = new String[]{"name","site","building","maxOccupancy","sysCreatedBy","sysCreatedTime","sysModifiedBy","sysModifiedTime"};
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


    private static FacilioView getAllFloors() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Floors");
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule floorModule = modBean.getModule(FacilioConstants.ContextNames.FLOOR);

        FacilioForm defaultFloorForm = new FacilioForm();
        defaultFloorForm.setName("default_floor_web");
        defaultFloorForm.setModule(floorModule);
        defaultFloorForm.setDisplayName("Floor");
        defaultFloorForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP,FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING,FacilioConstants.ApplicationLinkNames.FSM_APP));
        defaultFloorForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultFloorForm.setShowInWeb(true);

        List<FormField> defaultFloorFormFields = new ArrayList<>();
        defaultFloorFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        defaultFloorFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        defaultFloorFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.REQUIRED,  "building",3, 2, true));
        defaultFloorFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site",3, 3, true));
        defaultFloorFormFields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 2));
        defaultFloorFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 4, 3));
        defaultFloorFormFields.add(new FormField("floorlevel", FacilioField.FieldDisplayType.NUMBER, "Floor Level", FormField.Required.OPTIONAL, 5, 2));
        defaultFloorFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",7, 2));

//        defaultFloorForm.setFields(defaultFloorFormFields);

        FormSection section = new FormSection("Default", 1, defaultFloorFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        defaultFloorForm.setSections(Collections.singletonList(section));
        defaultFloorForm.setIsSystemForm(true);
        defaultFloorForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(defaultFloorForm);
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
}
