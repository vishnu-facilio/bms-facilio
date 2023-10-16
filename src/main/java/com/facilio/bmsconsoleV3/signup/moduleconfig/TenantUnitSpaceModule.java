package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
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
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;

import java.util.*;

public class TenantUnitSpaceModule extends BaseModuleConfig{
    public TenantUnitSpaceModule(){
        setModuleName(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
    }
    @Override
    public void addData() throws Exception {
        addSystemButtons();
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tenantUnitSpace = new ArrayList<FacilioView>();
        tenantUnitSpace.add(getAllTenantUnitSpace().setOrder(order++));
        tenantUnitSpace.add(getAllTenantUnitSpaceDetailsView().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
        groupDetails.put("views", tenantUnitSpace);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTenantUnitSpace() {

        FacilioModule tenantUnitSpaceModule = ModuleFactory.getTenantUnitSpaceModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tenant Unit");
        allView.setModuleName(tenantUnitSpaceModule.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getAllTenantUnitSpaceDetailsView() {

        FacilioModule tenantUnitSpaceModule = ModuleFactory.getTenantUnitSpaceModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("details");
        allView.setDisplayName("All Tenant Units");
        allView.setModuleName(tenantUnitSpaceModule.getName());
        allView.setSortFields(sortFields);
        allView.setHidden(true);

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
        FacilioModule tenantUnitModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);

        FacilioForm tenantUnitSpaceForm = new FacilioForm();
        tenantUnitSpaceForm.setDisplayName("NEW TENANT UNIT");
        tenantUnitSpaceForm.setName("default_tenantunit_web");
        tenantUnitSpaceForm.setModule(tenantUnitModule);
        tenantUnitSpaceForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        tenantUnitSpaceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> tenantUnitSpaceFormFields = new ArrayList<>();
        tenantUnitSpaceFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        tenantUnitSpaceFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        tenantUnitSpaceFormFields.add(new FormField("area", FacilioField.FieldDisplayType.NUMBER, "Area", FormField.Required.OPTIONAL, 3, 2));
        tenantUnitSpaceFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 3, 3));
        tenantUnitSpaceFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 4, 2));
        tenantUnitSpaceFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL,"building", 4, 3));
        FormField tenant = new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.OPTIONAL,"tenant", 5, 1);
        tenant.setHideField(true);
        tenantUnitSpaceFormFields.add(tenant);
        FormField isOccupied = new FormField("isOccupied", FacilioField.FieldDisplayType.DECISION_BOX, "Occupancy Status", FormField.Required.OPTIONAL, 5, 1);
        isOccupied.setHideField(true);
        tenantUnitSpaceFormFields.add(isOccupied);
        tenantUnitSpaceFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL,"floor", 5, 1));
        tenantUnitSpaceFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 4, 1));
        FormField spaceCategory = new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1,false);
        spaceCategory.setHideField(true);
        tenantUnitSpaceFormFields.add(spaceCategory);

        tenantUnitSpaceForm.setFields(tenantUnitSpaceFormFields);

        FormSection section = new FormSection("Default", 1, tenantUnitSpaceFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        tenantUnitSpaceForm.setSections(Collections.singletonList(section));
        tenantUnitSpaceForm.setIsSystemForm(true);
        tenantUnitSpaceForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(tenantUnitSpaceForm);
    }


    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("tenant");
        fieldNames.add("space");
        fieldNames.add("building");
        fieldNames.add("floor");
        fieldNames.add("site");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();

        String[] appNames=new String[]{
                FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,
                FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,
                FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP
        };

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        for(String appName:appNames){
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,buildTenantUnitPage(app,module,false,true));
        }

        return appNameVsPage;
    }
    public List<PagesContext> buildTenantUnitPage(ApplicationContext app, FacilioModule module,boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName()+ "defaultpage";
        pageDisplayName = "Default "+module.getDisplayName()+" Page ";

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.TENANT_ACTIVITY);
        return new ModulePages().addPage(pageName, pageDisplayName, "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("occupant", null, null)
                .addWidget("tenantdetailcontactwidget", "Occupant", PageWidget.WidgetType.TENANT_UNIT_TENANT, "webtenantunitoccupantwidget_3", 0, 0,null,null )
                .widgetDone()
                .addWidget("tenanthistorywidget", "Tenant History", PageWidget.WidgetType.TENANT_UNIT_SPECIAL_WIDGET, "webtenantunithistorywidget_6", 0, 0,getTenantHistoryWidgetConfig(),null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                .addSection("locationdetails",null,null)
                .addWidget("locationdetailswidget", "Location Details", PageWidget.WidgetType.TENANT_UNIT_LOCATION, "webtenantunitlocationwidget_3", 0, 0,null,null )
                .widgetDone()
                .addWidget("insights", "Insights", PageWidget.WidgetType.TENANT_UNIT_OVERVIEW, "webtenantunitinsightswidget_6", 0, 0,null,null )
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
                .addSection("tenantunitworkorderdetails", null, null)
                .addWidget("tenantunitworkorderdetail", "Workorders", PageWidget.WidgetType.TENANT_UNIT_WORKORDER, "webtenantunitworkorderswidget_3", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("tenantunitrecentlyclosedworkorder", null, null)
                .addWidget("tenantunitrecentlyclosedworkorderwidget", "Recently Closed Work order", PageWidget.WidgetType.TENANT_UNIT_RECENTLY_CLOSED_WORKORDER, "webtenantunitrecentlyclosedworkorderwidget_4", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("information", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryfieldswidget", "Tenant Unit Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("related", "Related", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("relatedlist", "Related List", "List of related records across modules")
                .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_6", 0, 0,  null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField descriptionField = moduleBean.getField("description", moduleName);
        FacilioField isOccupiedField = moduleBean.getField("isOccupied", moduleName);
        FacilioField maxOccupancyField = moduleBean.getField("maxOccupancy", moduleName);
        FacilioField spaceCategoryField = moduleBean.getField("spaceCategory", moduleName);

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup generalInformationWidgetGroup = new SummaryWidgetGroup();
        generalInformationWidgetGroup.setName("generalInformation");
        generalInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, descriptionField, 1, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, isOccupiedField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, maxOccupancyField, 2, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, spaceCategoryField, 2, 3, 1);

        SummaryWidgetGroup systemInformationWidgetGroup = new SummaryWidgetGroup();
        systemInformationWidgetGroup.setName("systemInformation");
        systemInformationWidgetGroup.setDisplayName("System Information");
        systemInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedTimeField, 1, 4, 1);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationWidgetGroup);
        widgetGroupList.add(systemInformationWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, colSpan, null);
    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan, FacilioField lookupField) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if (lookupField != null) {
                summaryField.setParentLookupFieldId(lookupField.getFieldId());
            }

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "basespacenotes");

        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put("attachmentsModuleName", "basespaceattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("comments", "Notes", "")
                .addWidget("commentwidget", "Notes", PageWidget.WidgetType.COMMENT, "flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, "flexiblewebattachment_5", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }
    public static JSONObject getTenantHistoryWidgetConfig(){
        JSONObject tenantHistory = new JSONObject();
        tenantHistory.put("viewName","tenanthistory");
        tenantHistory.put("viewModuleName","tenant");
        return tenantHistory;
    }
    public static void addSystemButtons() throws Exception {

       for(SystemButtonRuleContext btn:getSystemButtons()) {
           SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TENANT_UNIT_SPACE, btn);
       }

    }
    public static List<SystemButtonRuleContext> getSystemButtons(){
        List<SystemButtonRuleContext> btnList = new ArrayList<>();

        SystemButtonRuleContext editButton = new SystemButtonRuleContext();
        editButton.setName("Edit");
        editButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editButton.setIdentifier("edit");
        editButton.setPermissionRequired(true);
        editButton.setPermission("UPDATE");

        SystemButtonRuleContext createWorkorderButton = new SystemButtonRuleContext();
        createWorkorderButton.setName("Create Work order");
        createWorkorderButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createWorkorderButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        createWorkorderButton.setIdentifier("createWorkorder");
        createWorkorderButton.setPermissionRequired(true);
        createWorkorderButton.setPermission("CREATE");

        btnList.add(editButton);
        btnList.add(createWorkorderButton);

        return btnList;
    }
}
