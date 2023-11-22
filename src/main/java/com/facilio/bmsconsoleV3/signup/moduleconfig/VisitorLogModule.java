package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.util.VisitorManagementModulePageUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class VisitorLogModule extends BaseModuleConfig{
    public VisitorLogModule(){
        setModuleName(FacilioConstants.ContextNames.VISITOR_LOG);
    }
    @Override
    public void addData() throws Exception {
        addSystemButton(getModuleName());
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> visitorLog = new ArrayList<FacilioView>();
        visitorLog.add(getVisitorsLogCheckedInTodayView().setOrder(order++));
        visitorLog.add(getVisitorsLogCheckedInCurrentlyView().setOrder(order++));
        visitorLog.add(getPendingVisitLogsView().setOrder(order++));
        visitorLog.add(getAllVisitorCheckInLogsView().setOrder(order++));
        visitorLog.add(getHiddenVisitorLogView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VISITOR_LOG);
        groupDetails.put("views", visitorLog);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> visitorLogTenantPortal = new ArrayList<FacilioView>();
        visitorLogTenantPortal.add(getMyPendingVisitsCheckInView().setOrder(order++));
        visitorLogTenantPortal.add(getTenantPortalAllVisitorCheckInLogsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "tenantportal-systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VISITOR_LOG);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        groupDetails.put("views", visitorLogTenantPortal);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    public static FacilioView getHiddenVisitorLogView() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioView view = new FacilioView();
        view.setName("VisitorLogListView");
        view.setDisplayName("Visitor Log List View");
        view.setHidden(true);
        FacilioModule visitorLogModule = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOG);
        List<FacilioField> allFields =  modBean.getAllFields(visitorLogModule.getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        String[] viewFieldNames = new String[]{"host","checkInTime","checkOutTime","purposeOfVisit"};

        List<ViewField> viewFields = new ArrayList<>();
        for(String viewFieldName:viewFieldNames){
            FacilioField field = fieldMap.get(viewFieldName);
            ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
            viewField.setField(field);
            viewField.setFieldName(field.getName());
            viewField.setFieldId(field.getFieldId());
            viewFields.add(viewField);
        }
        view.setFields(viewFields);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        view.setAppLinkNames(appLinkNames);
        return view;
    }

    private static FacilioView getVisitorsLogCheckedInTodayView() {

        FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLogCheckInModule();
        FacilioView view = new FacilioView();
        view.setName("todayvisits");
        view.setDisplayName("Today Visits");
        Criteria criteria = new Criteria();
        FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule, FieldType.DATE_TIME);
        criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, DateOperators.TODAY));
        view.setCriteria(criteria);
        view.setSortFields(Arrays.asList(new SortField(checkInTime, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        view.setAppLinkNames(appLinkNames);
        return view;
    }

    private static FacilioView getVisitorsLogCheckedInCurrentlyView() {

        FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLogCheckInModule();
        FacilioView view = new FacilioView();
        view.setName("current");
        view.setDisplayName("Currently Checked-in");
        Criteria criteria = new Criteria();
        FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
        FacilioField checkOutTime = FieldFactory.getField("checkOutTime", "CHECKOUT_TIME", visitorLoggingModule,FieldType.DATE_TIME);
        criteria.addAndCondition(CriteriaAPI.getCondition(checkOutTime, CommonOperators.IS_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, CommonOperators.IS_NOT_EMPTY));

        view.setCriteria(criteria);
        view.setSortFields(Arrays.asList(new SortField(checkInTime, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }

    private static FacilioView getPendingVisitLogsView() {

        FacilioView view = new FacilioView();
        view.setName("pending");
        view.setDisplayName("Pending Approval");
        Criteria criteria = new Criteria();
        FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLogCheckInModule();
        FacilioField hostApprovalField = FieldFactory.getField("approvalNeeded", "IS_APPROVAL_NEEDED", visitorLoggingModule,FieldType.BOOLEAN);
        criteria.addAndCondition(CriteriaAPI.getCondition(hostApprovalField, String.valueOf(true), BooleanOperators.IS));
        FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
        criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, CommonOperators.IS_EMPTY));
        view.setCriteria(criteria);

        List<SortField> sortFields = Arrays.asList(new SortField(checkInTime, false));
        view.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }

    private static FacilioView getAllVisitorCheckInLogsView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Visits");

        FacilioModule visitorLogging = ModuleFactory.getVisitorLogCheckInModule();

        FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLogging,FieldType.DATE_TIME);
        List<SortField> sortFields = Arrays.asList(new SortField(checkInTime, false));
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView  getMyPendingVisitsCheckInView() {

        Criteria criteria = new Criteria();
        FacilioModule visitorLogModule = ModuleFactory.getVisitorLogCheckInModule();

        criteria.addAndCondition(getBaseVisitorLogStatusCriteria("Requested",visitorLogModule));
        FacilioView view = new FacilioView();
        view.setName("myPendingVisits");
        view.setDisplayName("My Approvals");
        view.setCriteria(criteria);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("checkInTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("CHECKIN_TIME");
        createdTime.setModule(visitorLogModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        view.setSortFields(sortFields);
        view.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));

        return view;
    }

    private static FacilioView getTenantPortalAllVisitorCheckInLogsView() {

        FacilioView allView = new FacilioView();
        allView.setName("tenantAll");
        allView.setDisplayName("My Visitors");

        FacilioModule visitorLogModule = ModuleFactory.getVisitorLogCheckInModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("checkInTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("CHECKIN_TIME");
        createdTime.setModule(visitorLogModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));

        return allView;
    }

    public static Condition getBaseVisitorLogStatusCriteria(String status, FacilioModule module) {

        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("status");
        statusTypeField.setColumnName("STATUS");
        statusTypeField.setDataType(FieldType.STRING);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition statusCondition = new Condition();
        statusCondition.setField(statusTypeField);
        statusCondition.setOperator(StringOperators.IS);
        statusCondition.setValue(status);

        Criteria statusCriteria = new Criteria() ;
        statusCriteria.addAndCondition(statusCondition);

        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition condition = new Condition();
        condition.setField(statusField);
        condition.setOperator(LookupOperator.LOOKUP);
        condition.setCriteriaValue(statusCriteria);

        return condition;
    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String>  appNameList=new ArrayList<>();
        appNameList.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOG);
        for(String appName:appNameList) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            appNameVsPage.put(appName, buildVisitorPage(app, module, false, true));
        }
        return appNameVsPage;
    }
    private List<PagesContext> buildVisitorPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName() + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";
        return new ModulePages()
                .addPage(pageName, pageDisplayName, "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("visitorLogDetails", "Visit Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, VisitorManagementModulePageUtil.getSummaryWidgetDetailsForVisitorLogModule(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("visitorlogcommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, VisitorManagementModulePageUtil.getWidgetGroup(false,module.getName()))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }
    private static void addSystemButton(String moduleName) throws Exception{

        SystemButtonRuleContext vipButton = new SystemButtonRuleContext();
        vipButton.setName("Mark As Vip");
        vipButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        vipButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        vipButton.setIdentifier("mark_as_vip");
        vipButton.setPermissionRequired(true);
        vipButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(moduleName,vipButton);

        SystemButtonRuleContext blockButton = new SystemButtonRuleContext();
        blockButton.setName("Mark As Blocked");
        blockButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        blockButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        blockButton.setIdentifier("mark_as_blocked");
        blockButton.setPermissionRequired(true);
        blockButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(moduleName,blockButton);

        SystemButtonRuleContext createButton = new SystemButtonRuleContext();
        createButton.setName("Create");
        createButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        createButton.setIdentifier("create");
        createButton.setPermissionRequired(true);
        createButton.setPermission("CREATE");
        SystemButtonApi.addSystemButton(moduleName,createButton);


        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        SystemButtonApi.addSystemButton(moduleName,listDeleteButton);


        SystemButtonRuleContext exportAsCSVButton = new SystemButtonRuleContext();
        exportAsCSVButton.setName("Export As CSV");
        exportAsCSVButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsCSVButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCSVButton.setIdentifier("export_as_csv");
        exportAsCSVButton.setPermissionRequired(true);
        exportAsCSVButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(moduleName,exportAsCSVButton);

        SystemButtonRuleContext exportAsExcelButton = new SystemButtonRuleContext();
        exportAsExcelButton.setName("Export As Excel");
        exportAsExcelButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsExcelButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButton.setIdentifier("export_as_excel");
        exportAsExcelButton.setPermissionRequired(true);
        exportAsExcelButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(moduleName,exportAsExcelButton);

    }

/*    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule visitorLogModule = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);

        FacilioForm defaultVisitorLogCheckinForm = new FacilioForm();
        defaultVisitorLogCheckinForm.setName("default_visitor_log_checkin_form");
        defaultVisitorLogCheckinForm.setModule(visitorLogModule);
        defaultVisitorLogCheckinForm.setDisplayName("Visitor Log Form");
        defaultVisitorLogCheckinForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultVisitorLogCheckinForm.setShowInWeb(true);
        defaultVisitorLogCheckinForm.setShowInMobile(true);
        defaultVisitorLogCheckinForm.setHideInList(false);
        defaultVisitorLogCheckinForm.setAppId(-1);


        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(visitorLogModule.getName()));

        List<FormSection> sections = new ArrayList<FormSection>();

        FormSection configSection1 = new FormSection();
        configSection1.setName("visitor");
        configSection1.setSectionType(FormSection.SectionType.FIELDS);
        configSection1.setShowLabel(false);

        List<FormField> configFields1 = new ArrayList<>();

        int seq = 0;

        configFields1.add(new FormField(fieldMap.get("avatar").getFieldId(), "avatar", FacilioField.FieldDisplayType.IMAGE, "Avatar", FormField.Required.OPTIONAL, ++seq, 1));
        configFields1.add(new FormField(fieldMap.get("visitorPhone").getFieldId(), "visitorPhone", FacilioField.FieldDisplayType.TEXTBOX, "Visitor Phone", FormField.Required.REQUIRED, ++seq, 1));
        configFields1.add(new FormField(fieldMap.get("visitorName").getFieldId(), "visitorName", FacilioField.FieldDisplayType.TEXTBOX, "Visitor Name", FormField.Required.REQUIRED, ++seq, 1));
        configFields1.add(new FormField(fieldMap.get("visitorEmail").getFieldId(), "visitorEmail", FacilioField.FieldDisplayType.TEXTBOX, "Visitor Email", FormField.Required.REQUIRED, ++seq, 1));
        configFields1.add(new FormField(fieldMap.get("visitedSpace").getFieldId(), "visitorSpace", FacilioField.FieldDisplayType.SPACECHOOSER, "Visited Space", FormField.Required.OPTIONAL, ++seq, 1));
        configFields1.add(new FormField(fieldMap.get("host").getFieldId(), "host", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Host", FormField.Required.OPTIONAL, ++seq, 1));
        configFields1.add(new FormField(fieldMap.get("nda").getFieldId(), "nda", FacilioField.FieldDisplayType.IMAGE, "NDA", FormField.Required.OPTIONAL, ++seq, 1));

        configSection1.setFields(configFields1);

        configSection1.setSequenceNumber(1);


        FormSection configSection2 = new FormSection();
        configSection2.setName("summary");
        configSection2.setSectionType(FormSection.SectionType.FIELDS);
        configSection2.setShowLabel(false);

        List<FormField> configFields2 = new ArrayList<>();
        int seq1 = 0;

        configFields2.add(new FormField(fieldMap.get("purposeOfVisit").getFieldId(), "purposeOfVisit", FacilioField.FieldDisplayType.SELECTBOX, "Purpose of Visit", FormField.Required.OPTIONAL, ++seq1, 1));

        configSection2.setFields(configFields2);

        configSection2.setSequenceNumber(1);

        sections.add(configSection1);
        sections.add(configSection2);

        defaultVisitorLogCheckinForm.setSections(sections);

        return Collections.singletonList(defaultVisitorLogCheckinForm);
    }*/
}
