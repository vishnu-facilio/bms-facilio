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
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class TenantModule extends BaseModuleConfig{
    public TenantModule(){
        setModuleName(FacilioConstants.ContextNames.TENANT);
    }

    @Override
    public void addData() throws Exception {
        addSystemButtons();
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tenant = new ArrayList<FacilioView>();
        tenant.add(getAllTenantsView().setOrder(order++));
        tenant.add(getActiveTenantsView().setOrder(order++));
        tenant.add(getHiddenTenantsHistoryView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TENANT);
        groupDetails.put("views", tenant);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTenantsView() {
        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getTenantsModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tenants");
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getActiveTenantsView() {
        FacilioField localId = new FacilioField();

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getTenantStateCondition("Active"));


        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getTenantsModule());

        FacilioView activeTenantsView = new FacilioView();
        activeTenantsView.setName("active");
        activeTenantsView.setDisplayName("Residing Tenants");
        activeTenantsView.setSortFields(Arrays.asList(new SortField(localId, false)));
        activeTenantsView.setCriteria(criteria);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        activeTenantsView.setAppLinkNames(appLinkNames);

        return activeTenantsView;
    }
    public static FacilioView getHiddenTenantsHistoryView() throws Exception {
        FacilioView tenantHistoryView = new FacilioView();
        tenantHistoryView.setName("tenanthistory");
        tenantHistoryView.setDisplayName("Tenant History");
        tenantHistoryView.setHidden(true);


        FacilioModule tenantModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.TENANT);
        List<FacilioField> allFields =  Constants.getModBean().getAllFields(tenantModule.getName());

        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        String[] viewFieldNames = new String[]{"name","primaryContactName","primaryContactPhone","inTime","outTime"};
        List<ViewField> viewFields = new ArrayList<>();

        for(String viewFieldName:viewFieldNames){
            FacilioField field = fieldMap.get(viewFieldName);
            ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
            viewField.setFieldId(field.getFieldId());
            viewField.setFieldName(field.getName());
            viewFields.add(viewField);

        }

        tenantHistoryView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        tenantHistoryView.setAppLinkNames(appLinkNames);
        return tenantHistoryView;
    }

    private static Condition getTenantStateCondition(String state) {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("status");
        statusTypeField.setColumnName("STATUS");
        statusTypeField.setDataType(FieldType.STRING);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition statusCondition = new Condition();
        statusCondition.setField(statusTypeField);
        statusCondition.setOperator(StringOperators.IS);
        statusCondition.setValue(state);

        Criteria statusCriteria = new Criteria() ;
        statusCriteria.addAndCondition(statusCondition);

        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(ModuleFactory.getTenantsModule());
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition condition = new Condition();
        condition.setField(statusField);
        condition.setOperator(LookupOperator.LOOKUP);
        condition.setCriteriaValue(statusCriteria);

        return condition;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule tenantModule = modBean.getModule(FacilioConstants.ContextNames.TENANT);

        FacilioForm tenantForm = new FacilioForm();
        tenantForm.setDisplayName("Tenant");
        tenantForm.setName("default_tenant_web");
        tenantForm.setModule(tenantModule);
        tenantForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        tenantForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> tenantFormFields = new ArrayList<>();
        tenantFormFields.add(new FormField("avatar", FacilioField.FieldDisplayType.IMAGE,"Tenant Logo", FormField.Required.OPTIONAL,1,1));
        tenantFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        tenantFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        tenantFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 4, 1));
        tenantFormFields.add(new FormField("primaryContactName", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Name", FormField.Required.REQUIRED, 6, 1));
        tenantFormFields.add(new FormField("primaryContactEmail", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact E-mail", FormField.Required.OPTIONAL, 7, 1));
        tenantFormFields.add(new FormField("primaryContactPhone", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Phone", FormField.Required.REQUIRED, 8, 1));
        tenantFormFields.add(new FormField("tenantType", FacilioField.FieldDisplayType.SELECTBOX, "Tenant Type", FormField.Required.OPTIONAL, 9, 1));
        tenantFormFields.add(new FormField("inTime", FacilioField.FieldDisplayType.DATE, "Lease Start Date", FormField.Required.OPTIONAL, 10, 1));
        tenantFormFields.add(new FormField("outTime", FacilioField.FieldDisplayType.DATE, "Lease End Date", FormField.Required.OPTIONAL, 11, 1));
        tenantFormFields.add(new FormField("address", FacilioField.FieldDisplayType.ADDRESS, "Address", FormField.Required.OPTIONAL, 12, 1));

        FormSection tenantFormSection = new FormSection("Default", 1, tenantFormFields, false);
        tenantFormSection.setSectionType(FormSection.SectionType.FIELDS);
        tenantForm.setSections(Collections.singletonList(tenantFormSection));
        tenantForm.setIsSystemForm(true);
        tenantForm.setType(FacilioForm.Type.FORM);


        List<FacilioForm> tenantModuleForms = new ArrayList<>();
        tenantModuleForms.add(tenantForm);

        return tenantModuleForms;
    }


    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("primaryContactName");
        fieldNames.add("primaryContactEmail");

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
                FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP,
                FacilioConstants.ApplicationLinkNames.FSM_APP,
                FacilioConstants.ApplicationLinkNames.ENERGY_APP};

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        for(String appName:appNames){
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,buildTenantPage(app,module,false,true));
        }

        return appNameVsPage;
    }
    public List<PagesContext> buildTenantPage(ApplicationContext app, FacilioModule module,boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName()+ "defaultpage";
        pageDisplayName = "Default "+module.getDisplayName()+" Page ";

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.TENANT_ACTIVITY);

        JSONObject tenantSpaceViewParam = new JSONObject();
        tenantSpaceViewParam.put("viewName", "TenantSpaceListView");
        tenantSpaceViewParam.put("viewModuleName","basespace");

        return new ModulePages()
                .addPage(pageName, pageDisplayName,"", null, isTemplate, isDefault, true)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("tenantdetailcontact", null, null)
                .addWidget("tenantdetailcontactwidget", "Primary Contact", PageWidget.WidgetType.TENANT_DETAIL_CONTACT, "webtenantdetailcontactwidget_3_6", 0, 0,null,null )
                .widgetDone()
                .addWidget("tenantspecialwidget", "Details", PageWidget.WidgetType.TENANT_DETAIL_OVERVIEW, "webtenantdetailoverviewwidget_3_6", 6, 0,null,null )
                .widgetDone()
                .sectionDone()
                .addSection("occupyingunitsvaccatedunits",null,null)
                .addWidget("occupyingunitsvaccatedunitswidget", null, PageWidget.WidgetType.TENANT_SPECIAL_WIDGET, "webtenantspecialwidget_6_12", 0, 0,tenantSpaceViewParam,null )
                .widgetDone()
                .sectionDone()

                .addSection("tenantwosrbookings",null,null)
                .addWidget("tenantsr", "Service Requests", PageWidget.WidgetType.TENANT_SERVICE_REQUEST, "webtenantservicerequestswidget_3_4", 0, 0,null,null )
                .widgetDone()
                .addWidget("tenantworkorders", "Workorders", PageWidget.WidgetType.TENANT_WORKORDERS, "webtenantworkorders_3_4", 0, 3,null,null )
                .widgetDone()
                .addWidget("tenantrecentlyclosedworkorder", "Recently Closed Work order", PageWidget.WidgetType.TENANT_RECENTLY_CLOSED_WORKORDER, "webtenantrecentlyclosedworkorder_6_4", 4, 0,null,null )
                .widgetDone()
                .addWidget("tenantupcomingfacilitybooking", "Upcoming Facility Booking", PageWidget.WidgetType.TENANT_UPCOMING_BOOKING, "webtenantupcomingfacilitybookingwidget_3_4", 8, 0,null,null )
                .widgetDone()
                .addWidget("tenantfacilitybookingwidget", "Facility Bookings", PageWidget.WidgetType.TENANT_BOOKINGS, "webtenantfacilitybookingwidget_3_4", 8, 3,null,null )
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("tenantcontact", "Contacts", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("tenantcontactrelatedlist", "", "")
                .addWidget("tenantcontactrelatedlistWidget", "Contacts", PageWidget.WidgetType.TENANT_CONTACT_RELATED_LIST, "webtenantcontactrelatedlistwidget_6", 0, 0, getTenantContactRelatedListWidgetConfig(), RelatedListWidgetUtil.getSingleRelatedListForModule(module,FacilioConstants.ContextNames.TENANT_CONTACT,"tenant"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("information", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryfieldswidget", "Tenant Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
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
                .addSection("tenantRelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("tenantBulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("tenantRelatedlist", "Related List", "List of related records across modules")
                .addWidget("tenantBulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST,"flexiblewebbulkrelatedlist_6", 0, 0,  null, fetchAllRelatedListForModule(module,false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
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

        FacilioField siteField = moduleBean.getField("siteId", moduleName);
        FacilioField categoryField = moduleBean.getField("tenantType", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField streetField = moduleBean.getField("street", FacilioConstants.ContextNames.LOCATION);
        FacilioField cityField = moduleBean.getField("city", FacilioConstants.ContextNames.LOCATION);
        FacilioField stateField = moduleBean.getField("state", FacilioConstants.ContextNames.LOCATION);
        FacilioField zipField = moduleBean.getField("zip", FacilioConstants.ContextNames.LOCATION);
        FacilioField countryField = moduleBean.getField("country", FacilioConstants.ContextNames.LOCATION);
        FacilioField addressField = moduleBean.getField("address", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup generalInformationWidgetGroup = new SummaryWidgetGroup();
        generalInformationWidgetGroup.setName("generalInformation");
        generalInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, siteField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, categoryField, 1, 2, 1,null,"Category");
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, sysCreatedTimeField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, sysModifiedTimeField, 1, 4, 1,null);


        SummaryWidgetGroup addressWidgetGroup = new SummaryWidgetGroup();
        addressWidgetGroup.setName("address");
        addressWidgetGroup.setDisplayName("Address");
        addressWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(addressWidgetGroup, streetField, 1, 1, 1, addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, cityField, 1, 2, 1, addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, stateField, 1, 3, 1, addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, zipField, 1, 4, 1, addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, countryField, 2, 1, 1, addressField);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationWidgetGroup);
        widgetGroupList.add(addressWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, colSpan, null,null);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan,FacilioField lookupField) {
        addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, colSpan, lookupField,null);
    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan, FacilioField lookupField,String displayName) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            if(StringUtils.isNotEmpty(displayName)){
                summaryField.setDisplayName(displayName);
            }else {
                summaryField.setDisplayName(field.getDisplayName());
            }
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
        notesWidgetParam.put("notesModuleName", "tenantnotes");

        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put("attachmentsModuleName", "tenantattachments");

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
    private static JSONObject getColumnWidth(FacilioField field){
        JSONObject jsonObject = new JSONObject();
        Integer columnWidth = null;
        switch (field.getName()){
            case "name":
                columnWidth = 132;
                break;
            case "primaryContactName":
                columnWidth = 174;
                break;
            case "primaryContactPhone":
                columnWidth = 146;
                break;
            case "inTime":
            case "outTime":
                columnWidth = 140;
                break;
        }
        jsonObject.put("columnWidth",columnWidth);
        return jsonObject;

    }
    public static void addSystemButtons() throws Exception {
        for(SystemButtonRuleContext btn : getSystemButtons()){
            SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TENANT, btn);
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

        SystemButtonRuleContext createButton = new SystemButtonRuleContext();
        createButton.setName("Create");
        createButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        createButton.setIdentifier("create");
        createButton.setPermissionRequired(true);
        createButton.setPermission("CREATE");
        btnList.add(createButton);


        SystemButtonRuleContext listEditButton = new SystemButtonRuleContext();
        listEditButton.setName("Edit");
        listEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listEditButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listEditButton.setIdentifier("edit_list");
        listEditButton.setPermissionRequired(true);
        listEditButton.setPermission("UPDATE");
        btnList.add(listEditButton);


        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        btnList.add(listDeleteButton);



        SystemButtonRuleContext bulkDeleteButton = new SystemButtonRuleContext();
        bulkDeleteButton.setName("Delete");
        bulkDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        bulkDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        bulkDeleteButton.setIdentifier("delete_bulk");
        bulkDeleteButton.setPermissionRequired(true);
        bulkDeleteButton.setPermission("DELETE");
        btnList.add(bulkDeleteButton);



        SystemButtonRuleContext exportAsCSVButton = new SystemButtonRuleContext();
        exportAsCSVButton.setName("Export As CSV");
        exportAsCSVButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsCSVButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCSVButton.setIdentifier("export_as_csv");
        exportAsCSVButton.setPermissionRequired(true);
        exportAsCSVButton.setPermission("EXPORT");
        btnList.add(exportAsCSVButton);



        SystemButtonRuleContext exportAsExcelButton = new SystemButtonRuleContext();
        exportAsExcelButton.setName("Export As Excel");
        exportAsExcelButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsExcelButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButton.setIdentifier("export_as_excel");
        exportAsExcelButton.setPermissionRequired(true);
        exportAsExcelButton.setPermission("EXPORT");
        btnList.add(exportAsExcelButton);

        btnList.add(editButton);
        btnList.add(createWorkorderButton);

        return btnList;
    }
    public static JSONObject getTenantContactRelatedListWidgetConfig(){
        JSONObject tenantHistory = new JSONObject();
        tenantHistory.put("viewName","tenantContactRelatedList");
        tenantHistory.put("viewModuleName","tenantcontact");
        return tenantHistory;
    }
    private static JSONObject fetchAllRelatedListForModule(FacilioModule module, boolean checkPermission) throws Exception {
        List<String> moduleToRemove=new ArrayList<>();
        moduleToRemove.add(FacilioConstants.ContextNames.TENANT_CONTACT);
        moduleToRemove.add("contact");
        moduleToRemove.add(FacilioConstants.ContextNames.SERVICE_ORDER);
        moduleToRemove.add("serviceAppointment");
        moduleToRemove.add("inspectionResponse");
        moduleToRemove.add("inspectionTemplate");
        moduleToRemove.add("inspectionTemplate");

        List<RelatedListWidgetContext> relatedLists = RelatedListWidgetUtil.fetchAllRelatedList(module, checkPermission, null, moduleToRemove);
        if (CollectionUtils.isNotEmpty(relatedLists)) {
            RelatedListWidgetContext woRelated = relatedLists.stream().filter(p->p.getSubModuleName()!=null &&
                    p.getSubModuleName().equals(FacilioConstants.ContextNames.WORK_ORDER))
                    .findFirst().orElse(null);
            if(woRelated!=null){
                woRelated.setDisplayName("Workorders");
            }
            BulkRelatedListContext bulkRelatedListWidget = new BulkRelatedListContext();
            bulkRelatedListWidget.setRelatedList(relatedLists);
            return FieldUtil.getAsJSON(bulkRelatedListWidget);
        }

        return null;
    }
}
