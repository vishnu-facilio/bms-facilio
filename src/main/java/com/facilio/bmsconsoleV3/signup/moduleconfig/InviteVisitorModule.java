package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ClientContactModuleUtil;
import com.facilio.bmsconsole.util.VisitorManagementModulePageUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class InviteVisitorModule extends BaseModuleConfig{
    public InviteVisitorModule(){
        setModuleName(FacilioConstants.ContextNames.INVITE_VISITOR);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inviteVisitor = new ArrayList<FacilioView>();
        inviteVisitor.add(getUpcomingInviteVisitsView().setOrder(order++));
        inviteVisitor.add(getTodayInviteVisitorInvitesView().setOrder(order++));
        inviteVisitor.add(getPendingInviteVisitorInvitesView().setOrder(order++));
        inviteVisitor.add(getAllInviteVisitorInvitesView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVITE_VISITOR);
        groupDetails.put("views", inviteVisitor);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> vendorPortalInviteVisitor = new ArrayList<FacilioView>();
        vendorPortalInviteVisitor.add(getVendorUpcomingInviteVisitorLogsView().setOrder(order++));
        vendorPortalInviteVisitor.add(getVendorExpiredInviteVisitorInvites().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "vendorportal-systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVITE_VISITOR);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));
        groupDetails.put("views", vendorPortalInviteVisitor);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> tenantPortalInviteVisitor = new ArrayList<FacilioView>();
        tenantPortalInviteVisitor.add(getActiveInviteVisitorInvites().setOrder(order++));
        tenantPortalInviteVisitor.add(getExpiredInviteVisitorInvites().setOrder(order++));
        tenantPortalInviteVisitor.add(getMyExpiredInviteVisitorInvites().setOrder(order++));
        tenantPortalInviteVisitor.add(getMyActiveInviteVisitorInvites().setOrder(order++));
        tenantPortalInviteVisitor.add(getHiddenInviteVisitorView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "tenantportal-systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVITE_VISITOR);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        groupDetails.put("views", tenantPortalInviteVisitor);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getUpcomingInviteVisitsView() {

        FacilioView view = new FacilioView();
        view.setName("upcoming");
        view.setDisplayName("Upcoming Invites");
        Criteria criteria = new Criteria();
        FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();

        criteria.addAndCondition(getBaseVisitorLogStatusCriteria("Upcoming",inviteVisitorModule));
        view.setCriteria(criteria);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("expectedCheckInTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
        createdTime.setModule(inviteVisitorModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));
        view.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }
    public static FacilioView getHiddenInviteVisitorView() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioView baseSpaceView = new FacilioView();
        baseSpaceView.setName("InviteVisitorListView");
        baseSpaceView.setDisplayName("Invite Visitor List View");
        baseSpaceView.setHidden(true);
        FacilioModule visitorLogModule = modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR);
        List<FacilioField> allFields =  modBean.getAllFields(visitorLogModule.getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        String[] viewFieldNames = new String[]{"visitorName","visitorPhone","visitorEmail","host","expectedCheckInTime","expectedCheckOutTime"};
        List<ViewField> viewFields = new ArrayList<>();
        for(String viewFieldName:viewFieldNames){
            FacilioField field = fieldMap.get(viewFieldName);
            ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
            viewField.setField(field);
            viewField.setFieldName(field.getName());
            viewField.setFieldId(field.getFieldId());
            viewFields.add(viewField);
        }
        baseSpaceView.setFields(viewFields);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        baseSpaceView.setAppLinkNames(appLinkNames);
        return baseSpaceView;
    }

    private static FacilioView getPendingInviteVisitorInvitesView() {

        FacilioView view = new FacilioView();
        view.setName("invite_pending");
        view.setDisplayName("Pending Approval");
        Criteria criteria = new Criteria();
        FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();

        FacilioField hasCheckedInField = FieldFactory.getField("hasCheckedIn", "HAS_CHECKED_IN", inviteVisitorModule,FieldType.BOOLEAN);
        Criteria subCriteria = new Criteria();
        subCriteria.addOrCondition(CriteriaAPI.getCondition(hasCheckedInField, String.valueOf(false),BooleanOperators.IS));
        subCriteria.addOrCondition(CriteriaAPI.getCondition(hasCheckedInField, CommonOperators.IS_EMPTY));
        criteria.andCriteria(subCriteria);
        criteria.addAndCondition(getBaseVisitorLogStatusCriteria("InviteRequested",inviteVisitorModule));

        FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", inviteVisitorModule,FieldType.DATE_TIME);
        view.setSortFields(Arrays.asList(new SortField(expCheckInTime, false)));
        view.setCriteria(criteria);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
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

    private static FacilioView getTodayInviteVisitorInvitesView() {

        FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();
        FacilioView view = new FacilioView();
        view.setName("invite_today");
        view.setDisplayName("Today Invites");
        Criteria criteria = new Criteria();
        FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", inviteVisitorModule,FieldType.DATE_TIME);
        criteria.addAndCondition(CriteriaAPI.getCondition(expCheckInTime, DateOperators.TODAY));
        view.setCriteria(criteria);

        List<SortField> sortFields = Arrays.asList(new SortField(expCheckInTime, false));
        view.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }

    private static FacilioView getAllInviteVisitorInvitesView() {

        FacilioView allView = new FacilioView();
        allView.setName("invite_all");
        allView.setDisplayName("All Invites");

        FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();
        FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", inviteVisitorModule,FieldType.DATE_TIME);

        allView.setSortFields(Arrays.asList(new SortField(expCheckInTime, true)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getActiveInviteVisitorInvites() {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getActiveInviteVisitorInvitesCondition());

        FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();
        FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", inviteVisitorModule,FieldType.DATE_TIME);

        List<SortField> sortFields = Arrays.asList(new SortField(expCheckInTime, false));

        FacilioView myVisitorInvitesView = new FacilioView();
        myVisitorInvitesView.setName("invite_myInvites");
        myVisitorInvitesView.setDisplayName("Active");
        myVisitorInvitesView.setCriteria(criteria);
        myVisitorInvitesView.setSortFields(sortFields);

        myVisitorInvitesView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));

        return myVisitorInvitesView;
    }

    public static Condition getActiveInviteVisitorInvitesCondition() {
        FacilioModule module = ModuleFactory.getInviteVisitorLogModule();
        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition open = new Condition();
        open.setField(statusField);
        open.setOperator(LookupOperator.LOOKUP);
        open.setCriteriaValue(getOpenStatusCriteria());

        return open;
    }

    public static Criteria getOpenStatusCriteria() {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("typeCode");
        statusTypeField.setColumnName("STATUS_TYPE");
        statusTypeField.setDataType(FieldType.NUMBER);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition statusOpen = new Condition();
        statusOpen.setField(statusTypeField);
        statusOpen.setOperator(NumberOperators.EQUALS);
        statusOpen.setValue(String.valueOf(FacilioStatus.StatusType.OPEN.getIntVal()));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusOpen);

        return criteria;
    }

    private static FacilioView getExpiredInviteVisitorInvites() {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getMyBaseVisitorInvitesCondition());
        criteria.addAndCondition(getExpiredInviteVisitsCondition());

        FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();
//		FacilioField checkin = FieldFactory.getField("checkInTime","CHECKIN_TIME", FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkin, CommonOperators.IS_EMPTY));
        FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME",inviteVisitorModule, FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckin, DateOperators.TILL_NOW));

        List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

        FacilioView myVisitorInvitesView = new FacilioView();
        myVisitorInvitesView.setName("invite_myExpired");
        myVisitorInvitesView.setDisplayName("Expired");
        myVisitorInvitesView.setCriteria(criteria);
        myVisitorInvitesView.setSortFields(sortFields);

        myVisitorInvitesView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));

        return myVisitorInvitesView;
    }

    public static Condition getMyBaseVisitorInvitesCondition() {
        LookupField userField = new LookupField();
        userField.setName("requestedBy");
        userField.setColumnName("REQUESTED_BY");
        userField.setDataType(FieldType.LOOKUP);
        userField.setModule(ModuleFactory.getBaseVisitorLogCheckInModule());
        userField.setSpecialType(FacilioConstants.ContextNames.USERS);

        Condition myUserCondition = new Condition();
        myUserCondition.setField(userField);
        myUserCondition.setOperator(PickListOperators.IS);
        myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

        return myUserCondition;
    }

    public static Condition getExpiredInviteVisitsCondition() {
        FacilioModule module = ModuleFactory.getInviteVisitorLogModule();
        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition open = new Condition();
        open.setField(statusField);
        open.setOperator(LookupOperator.LOOKUP);
        open.setCriteriaValue(getCloseStatusCriteria());

        return open;
    }

    public static Criteria getCloseStatusCriteria() {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("typeCode");
        statusTypeField.setColumnName("STATUS_TYPE");
        statusTypeField.setDataType(FieldType.NUMBER);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition statusClose = new Condition();
        statusClose.setField(statusTypeField);
        statusClose.setOperator(NumberOperators.EQUALS);
        statusClose.setValue(String.valueOf(FacilioStatus.StatusType.CLOSED.getIntVal()));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusClose);

        return criteria;
    }

    private static FacilioView getVendorUpcomingInviteVisitorLogsView() {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getActiveInviteVisitorInvitesCondition());

        FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();
//		FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, CommonOperators.IS_EMPTY));

        FacilioField createdTime = new FacilioField();
        createdTime.setName("expectedCheckInTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
        createdTime.setModule(inviteVisitorModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("vendorActiveVisitors");
        allView.setDisplayName("All Visits");
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));

        return allView;
    }

    private static FacilioView getVendorExpiredInviteVisitorInvites() {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getExpiredInviteVisitsCondition());
//		FacilioField checkin = FieldFactory.getField("checkInTime","CHECKIN_TIME", FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkin, CommonOperators.IS_EMPTY));
        FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME", FieldType.DATE_TIME);
        criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckin, DateOperators.TILL_NOW));

        List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

        FacilioView myVisitorInvitesView = new FacilioView();
        myVisitorInvitesView.setName("vendorExpired");
        myVisitorInvitesView.setDisplayName("Vendor Expired Invites");
        myVisitorInvitesView.setCriteria(criteria);
        myVisitorInvitesView.setSortFields(sortFields);

        myVisitorInvitesView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));

        return myVisitorInvitesView;
    }

    private static FacilioView getMyExpiredInviteVisitorInvites() {

        Criteria criteria = new Criteria();
        //ß	criteria.addAndCondition(getMyVistorInvitesCondition());
        criteria.addAndCondition(getExpiredInviteVisitsCondition());
//		FacilioField checkin = FieldFactory.getField("checkInTime","CHECKIN_TIME", FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkin, CommonOperators.IS_EMPTY));
        FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME", FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckin, DateOperators.TILL_NOW));

        List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

        FacilioView myVisitorInvitesView = new FacilioView();
        myVisitorInvitesView.setName("myExpired");
        myVisitorInvitesView.setDisplayName("Expired");
        myVisitorInvitesView.setCriteria(criteria);
        myVisitorInvitesView.setSortFields(sortFields);
        myVisitorInvitesView.setHidden(true);

        myVisitorInvitesView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));

        return myVisitorInvitesView;
    }

    private static FacilioView getMyActiveInviteVisitorInvites() {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getMyBaseVisitorInvitesCondition());
        criteria.addAndCondition(getActiveInviteVisitorInvitesCondition());

        FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("expectedCheckInTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
        createdTime.setModule(inviteVisitorModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView myVisitorInvitesView = new FacilioView();
        myVisitorInvitesView.setName("myActive");
        myVisitorInvitesView.setDisplayName("Active");
        myVisitorInvitesView.setCriteria(criteria);
        myVisitorInvitesView.setSortFields(sortFields);
        myVisitorInvitesView.setHidden(true);

        myVisitorInvitesView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));

        return myVisitorInvitesView;
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
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR);
        for(String appName:appNameList) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
                appNameVsPage.put(appName, buildInviteVisitorPage(app, module, false, true));
        }
        return appNameVsPage;
    }
    private List<PagesContext> buildInviteVisitorPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName() + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";
        return new ModulePages()
                .addPage(pageName, pageDisplayName, "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("inviteVisitorDetails", "Invite Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, VisitorManagementModulePageUtil.getSummaryWidgetDetailsForIniviteVisitorModule(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("inivitevisitorlogcommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, VisitorManagementModulePageUtil.getWidgetGroup(false,module.getName()))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }
}
