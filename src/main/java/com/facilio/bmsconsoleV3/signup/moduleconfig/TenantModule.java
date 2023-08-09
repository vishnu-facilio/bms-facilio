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
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class TenantModule extends BaseModuleConfig{
    public TenantModule(){
        setModuleName(FacilioConstants.ContextNames.TENANT);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tenant = new ArrayList<FacilioView>();
        tenant.add(getAllTenantsView().setOrder(order++));
        tenant.add(getActiveTenantsView().setOrder(order++));

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
                FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP};

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
        return new ModulePages()
                .addPage(pageName, pageDisplayName,"", null, isTemplate, isDefault, true)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("tenantdetailcontact", null, null)
                .addWidget("tenantdetailcontactwidget", "Primary Contact", PageWidget.WidgetType.TENANT_DETAIL_CONTACT, "webtenantdetailcontactwidget_13_6", 0, 0,null,null )
                .widgetDone()
                .addWidget("tenantspecialwidget", "Details", PageWidget.WidgetType.TENANT_DETAIL_OVERVIEW, "webtenantdetailoverviewwidget_13_6", 6, 0,null,null )
                .widgetDone()
                .sectionDone()
                .addSection("occupyingunitsvaccatedunits",null,null)
                .addWidget("occupyingunitsvaccatedunitswidget", null, PageWidget.WidgetType.TENANT_SPECIAL_WIDGET, "webtenantspecialwidget_31_9", 0, 0,null,null )
                .widgetDone()
                .addWidget("tenantworkorders", "Workorders", PageWidget.WidgetType.TENANT_WORKORDERS, "webtenantworkorders_13_3", 9, 0,null,null )
                .widgetDone()
                .addWidget("tenantrecentlyclosedworkorder", "Recently Closed Work order", PageWidget.WidgetType.TENANT_RECENTLY_CLOSED_WORKORDER, "webtenantrecentlyclosedworkorder_23_3", 9, 13,null,null )
                .widgetDone()
                .addWidget("tenantfacilitybookingwidget", "Facility Bookings", PageWidget.WidgetType.TENANT_BOOKINGS, "webtenantfacilitybookingwidget_13_3", 9, 36,null,null )
                .widgetDone()
                .addWidget("tenantupcomingfacilitybooking", "Upcoming Facility Booking", PageWidget.WidgetType.TENANT_UPCOMING_BOOKING, "webtenantupcomingfacilitybookingwidget_10_3", 9, 49,null,null )
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("tenantcontact", "Tenant Contacts", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("tenantcontactrelatedlist", "", "")
                .addWidget("tenantcontactrelatedlistWidget", "Tenant Contact", PageWidget.WidgetType.RELATED_LIST, "flexiblewebrelatedlist_29", 0, 0, null, RelatedListWidgetUtil.getSingleRelatedListForModule(module,FacilioConstants.ContextNames.TENANT_CONTACT,"tenant"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("information", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryfieldswidget", "Tenant Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
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
        generalInformationWidgetGroup.setDisplayName("General Information");
        generalInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, siteField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, categoryField, 1, 2, 1,null,"Category");

        SummaryWidgetGroup systemInformationWidgetGroup = new SummaryWidgetGroup();
        systemInformationWidgetGroup.setName("systemInformation");
        systemInformationWidgetGroup.setDisplayName("System Information");
        systemInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedTimeField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedTimeField, 1, 2, 1);

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
        widgetGroupList.add(systemInformationWidgetGroup);
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
                .addSection("comments", "Comments", "")
                .addWidget("commentwidget", "Comments", PageWidget.WidgetType.COMMENT, "flexiblewebcomment_27", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, "flexiblewebattachment_27", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }
}
