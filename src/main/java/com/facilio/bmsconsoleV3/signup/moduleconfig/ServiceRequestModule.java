package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseFieldContext;
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
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;

import java.util.*;

public class ServiceRequestModule extends BaseModuleConfig{
    public ServiceRequestModule(){
        setModuleName(FacilioConstants.ContextNames.SERVICE_REQUEST);
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module=ModuleFactory.getServiceRequestModule();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,createServiceRequestPage(app, module, false,true));
        }
        return appNameVsPage;
    }

    public static List<PagesContext> createServiceRequestPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceRequest = modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST);
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SERVICE_REQUEST_ACTIVITY);

        return new ModulePages()
                .addPage("servicerequestdefaultpage","Default Service Request Page","",null,isTemplate,isDefault,true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("conversation", "Conversation", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("servicerequestconversationsection", null, null)
                .addWidget("servicerequestconversationwidget", "Conversation", PageWidget.WidgetType.SR_EMAIL_THREAD, "flexibleservicerequestemailthread_10", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                .addSection("servicerequestsitesection", null, null)
                .addWidget("servicerequestsitewidget", "Location Details", PageWidget.WidgetType.SR_SITE_WIDGET, "flexibleservicerequestsitewidget_3", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("servicerequestpropertiessection", null, null)
                .addWidget("servicerequestpropertieswidget", "Properties", PageWidget.WidgetType.SR_DETAILS_WIDGET, "flexibleservicerequestdetails_7", 0, 0, getPropertiesWidgetParams(), getPropertiesWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("properties", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicerequestsummaryfields", null, null)
                .addWidget("servicerequestsummaryfieldwidget", "Properties", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicerequestrelatedsection", "Relationships", "List of relationships and types between records across modules")
                .addWidget("servicerequestrelatedwidget", "Related", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("servicerequesttrelatedlist", "Related List", "List of all related records across modules")
                .addWidget("servicerequestbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("servicerequesthistory", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicerequesthistorysection", null, null)
                .addWidget("servicerequesthistorywidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_10", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }
    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.SERVICE_REQUEST_NOTES);
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.SERVICE_REQUEST_ATTACHMENTS);
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT,  "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Attachments", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT,  "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
    private static JSONObject getPropertiesWidgetParams(){
        JSONObject widgetParams=new JSONObject();
        widgetParams.put("visibleRowCount",4);
        return widgetParams;
    }
    private static JSONObject getPropertiesWidgetDetails(String moduleName,ApplicationContext app) throws Exception{
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        //general information

        FacilioField urgency = moduleBean.getField("urgency", moduleName);
        FacilioField assignedTo = moduleBean.getField("assignedTo", moduleName);
        FacilioField classificationType = moduleBean.getField("classificationType", moduleName);
        FacilioField dueDate = moduleBean.getField("dueDate", moduleName);




        SummaryWidgetGroup generalInformationwidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, urgency, 1, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, assignedTo, 2, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, classificationType, 3, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, dueDate, 4, 1, 1);



        // Requested By

        FacilioField requesterName = moduleBean.getField("name",FacilioConstants.ContextNames.PEOPLE);
        FacilioField requesterPhone = moduleBean.getField("phone",FacilioConstants.ContextNames.PEOPLE);
        FacilioField requesterEmail = moduleBean.getField("email",FacilioConstants.ContextNames.PEOPLE);
        FacilioField requester = moduleBean.getField("requester", moduleName);

        SummaryWidgetGroup requestedBywidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(requestedBywidgetGroup, requesterName, 1, 1, 1,requester);
        addSummaryFieldInWidgetGroup(requestedBywidgetGroup, requesterPhone, 2, 1, 1,requester);
        addSummaryFieldInWidgetGroup(requestedBywidgetGroup, requesterEmail, 3, 1, 1,requester);


        generalInformationwidgetGroup.setName("moduleDetails");
        generalInformationwidgetGroup.setDisplayName("General");
        generalInformationwidgetGroup.setColumns(1);


        requestedBywidgetGroup.setName("requestedBy");
        requestedBywidgetGroup.setDisplayName("Requested By");
        requestedBywidgetGroup.setColumns(1);


        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationwidgetGroup);
        widgetGroupList.add(requestedBywidgetGroup);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("properties");
        widgetGroup.setDisplayName("Properties");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        JSONObject jsonObject = FieldUtil.getAsJSON(pageWidget);

        return jsonObject;
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName,ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        //general information

//        FacilioField description = moduleBean.getField("description", moduleName);
        FacilioField assignedTo = moduleBean.getField("assignedTo", moduleName);
        FacilioField urgency = moduleBean.getField("urgency", moduleName);
        FacilioField actualStartDate = moduleBean.getField("actualStartDate", moduleName);
        FacilioField actualFinishDate = moduleBean.getField("actualFinishDate", moduleName);
        FacilioField dueDate = moduleBean.getField("dueDate", moduleName);

        SummaryWidgetGroup generalInformationwidgetGroup = new SummaryWidgetGroup();

//        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, description, 1, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, assignedTo, 1, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, urgency, 1, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, actualStartDate, 1, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, actualFinishDate, 1, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, dueDate, 2, 1, 1);


        //Reported By

//        FacilioField sysCreatedBy = moduleBean.getField("sysCreatedBy", moduleName);
//
//        SummaryWidgetGroup reportedBywidgetGroup = new SummaryWidgetGroup();
//
//        addSummaryFieldInWidgetGroup(reportedBywidgetGroup, sysCreatedBy, 1, 1, 1);

        // Requested By

        FacilioField requesterName = moduleBean.getField("name",FacilioConstants.ContextNames.PEOPLE);
        FacilioField requesterPhone = moduleBean.getField("phone",FacilioConstants.ContextNames.PEOPLE);
        FacilioField requesterEmail = moduleBean.getField("email",FacilioConstants.ContextNames.PEOPLE);
        FacilioField requester = moduleBean.getField("requester", moduleName);

        SummaryWidgetGroup requestedBywidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(requestedBywidgetGroup, requesterName, 1, 1, 1,requester);
        addSummaryFieldInWidgetGroup(requestedBywidgetGroup, requesterPhone, 1, 2, 1,requester);
        addSummaryFieldInWidgetGroup(requestedBywidgetGroup, requesterEmail, 1, 3, 1,requester);


        // Others


        // System Details
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidgetGroup systemDetailswidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysModifiedTimeField, 1, 4, 1);

        generalInformationwidgetGroup.setName("moduleDetails");
        generalInformationwidgetGroup.setDisplayName("General Information");
        generalInformationwidgetGroup.setColumns(4);

//        reportedBywidgetGroup.setName("reportedBy");
//        reportedBywidgetGroup.setDisplayName("Reported By");
//        reportedBywidgetGroup.setColumns(4);

        requestedBywidgetGroup.setName("requestedBy");
        requestedBywidgetGroup.setDisplayName("Requested By");
        requestedBywidgetGroup.setColumns(4);

        systemDetailswidgetGroup.setName("systemDetails");
        systemDetailswidgetGroup.setDisplayName("System Details");
        systemDetailswidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationwidgetGroup);
//        widgetGroupList.add(reportedBywidgetGroup);
        widgetGroupList.add(requestedBywidgetGroup);
        widgetGroupList.add(systemDetailswidgetGroup);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("properties");
        widgetGroup.setDisplayName("Properties");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan){
        addSummaryFieldInWidgetGroup(widgetGroup,field,rowIndex,colIndex,colSpan,null);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan,FacilioField lookupField) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if(lookupField!=null){
                summaryField.setParentLookupFieldId(lookupField.getFieldId());
            }

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> serviceRequest = new ArrayList<FacilioView>();
        serviceRequest.add(getAllServiceRequests().setOrder(order++));
        serviceRequest.add(getMyServiceRequets().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SERVICE_REQUEST);
        groupDetails.put("views", serviceRequest);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllServiceRequests() {

        FacilioModule serviceRequestsModule = ModuleFactory.getServiceRequestModule();

        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", serviceRequestsModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Service Requests");
        allView.setSortFields(sortFields);
        allView.setModuleName(FacilioConstants.ContextNames.SERVICE_REQUEST);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getMyServiceRequets() {

        Criteria criteria = new Criteria();
        FacilioModule serviceRequestsModule = ModuleFactory.getServiceRequestModule();
//		criteria.addAndCondition(getOpenStatusCondition());
        criteria.addAndCondition(getMyServiceRequestCondition());

        FacilioField createdTime = new FacilioField();
        createdTime.setName("dueDate");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("DUE_DATE");
        createdTime.setModule(serviceRequestsModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView openTicketsView = new FacilioView();
        openTicketsView.setName("myopenservicerequests");
        openTicketsView.setDisplayName("My Service Requests");
        openTicketsView.setCriteria(criteria);
        openTicketsView.setSortFields(sortFields);
        openTicketsView.setModuleName(FacilioConstants.ContextNames.SERVICE_REQUEST);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        openTicketsView.setAppLinkNames(appLinkNames);

        return openTicketsView;
    }

    public static Condition getMyServiceRequestCondition() {
        LookupField userField = new LookupField();
        userField.setName("assignedTo");
        userField.setColumnName("ASSIGNED_TO_ID");
        userField.setDataType(FieldType.LOOKUP);
        userField.setModule(ModuleFactory.getServiceRequestModule());
        userField.setSpecialType(FacilioConstants.ContextNames.USERS);

        Condition myUserCondition = new Condition();
        myUserCondition.setField(userField);
        myUserCondition.setOperator(PickListOperators.IS);
        myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

        return myUserCondition;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceRequestModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST);

        FacilioForm serviceRequestForm = new FacilioForm();
        serviceRequestForm.setDisplayName("SERVICE REQUEST");
        serviceRequestForm.setName("default_serviceRequest_web");
        serviceRequestForm.setModule(serviceRequestModule);
        serviceRequestForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        serviceRequestForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> serviceRequestFormFields = new ArrayList<>();
        serviceRequestFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        serviceRequestFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        serviceRequestFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 1));
        FormField requesterField = new FormField("requester", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requester", FormField.Required.REQUIRED, "people" , 4, 1);
        requesterField.setAllowCreateOptions(true);
        requesterField.addToConfig("canShowLookupWizard",true);
        requesterField.addToConfig("canShowQuickCreate",true);
        serviceRequestFormFields.add(requesterField);
        serviceRequestFormFields.add(new FormField("resource", FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", FormField.Required.OPTIONAL, 5, 1));
        serviceRequestFormFields.add(new FormField("assignment", FacilioField.FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", FormField.Required.OPTIONAL, 6, 1));
        serviceRequestFormFields.add(new FormField("urgency", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Urgency", FormField.Required.OPTIONAL, "servicerequestpriority", 7, 1));
        serviceRequestFormFields.add(new FormField("classificationType", FacilioField.FieldDisplayType.SELECTBOX, "Classification Type", FormField.Required.OPTIONAL, "classification" , 8, 1));
        serviceRequestFormFields.add(new FormField("attachedFiles", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, "attachment", 8, 1));
        serviceRequestForm.setFields(serviceRequestFormFields);

        FormSection section = new FormSection("Default", 1, serviceRequestFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        serviceRequestForm.setSections(Collections.singletonList(section));
        serviceRequestForm.setIsSystemForm(true);
        serviceRequestForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(serviceRequestForm);
    }


    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("site");
        fieldNames.add("resource");
        fieldNames.add("dueDate");
        fieldNames.add("assignedTo");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

}
