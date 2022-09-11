package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class VisitorLoggingModule extends BaseModuleConfig{
    public VisitorLoggingModule(){
        setModuleName(FacilioConstants.ContextNames.VISITOR_LOGGING);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> visitorLogging = new ArrayList<FacilioView>();
        visitorLogging.add(getVisitorsCheckedInTodayView().setOrder(order++));
        visitorLogging.add(getVisitorsCheckedInCurrentlyView().setOrder(order++));
        visitorLogging.add(getPendingVisitsView().setOrder(order++));
        visitorLogging.add(getUpcomingVisitsView().setOrder(order++));
        visitorLogging.add(getAllVisitorLogsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VISITOR_LOGGING);
        groupDetails.put("views", visitorLogging);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> visitorLoggingVendorPortal = new ArrayList<FacilioView>();
        visitorLoggingVendorPortal.add(getVendorUpcomingVisitorLogsView().setOrder(order++));
        visitorLoggingVendorPortal.add(getVendorExpiredVisitorInvites().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "vendorportal-systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VISITOR_LOGGING);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));
        groupDetails.put("views", visitorLoggingVendorPortal);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> visitorLoggingTenantPortal = new ArrayList<FacilioView>();
        visitorLoggingTenantPortal.add(getTenantPortalAllVisitorLogsView().setOrder(order++));
        visitorLoggingTenantPortal.add(getMyExpiredVisitorInvites().setOrder(order++));
        visitorLoggingTenantPortal.add(getMyActiveVisitorInvites().setOrder(order++));
        visitorLoggingTenantPortal.add(getMyPendingVisitsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "tenantportal-systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VISITOR_LOGGING);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        groupDetails.put("views", visitorLoggingTenantPortal);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private static FacilioView getVisitorsCheckedInTodayView() {

        FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLoggingModule();
        FacilioView view = new FacilioView();
        view.setName("todayvisits");
        view.setDisplayName("Today Visits");
        Criteria criteria = new Criteria();
        FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
        criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, DateOperators.TODAY));
        view.setCriteria(criteria);
        view.setSortFields(Arrays.asList(new SortField(checkInTime, false)));

        return view;
    }

    private static FacilioView  getVisitorsCheckedInCurrentlyView() {

        FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLoggingModule();
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
        return view;
    }

    private static FacilioView  getPendingVisitsView() {

        FacilioView view = new FacilioView();
        view.setName("pending");
        view.setDisplayName("Pending Approval");
        Criteria criteria = new Criteria();
        FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLoggingModule();
        FacilioField hostApprovalField = FieldFactory.getField("approvalNeeded", "IS_APPROVAL_NEEDED", visitorLoggingModule,FieldType.BOOLEAN);
        criteria.addAndCondition(CriteriaAPI.getCondition(hostApprovalField, String.valueOf(true),BooleanOperators.IS));
        FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
        criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, CommonOperators.IS_EMPTY));
        view.setCriteria(criteria);

        List<SortField> sortFields = Arrays.asList(new SortField(checkInTime, false));
        view.setSortFields(sortFields);

        return view;
    }

    private static FacilioView getUpcomingVisitsView() {

        FacilioView view = new FacilioView();
        view.setName("upcoming");
        view.setDisplayName("Upcoming Visits");
        Criteria criteria = new Criteria();
        FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLoggingModule();

        FacilioField preRegisterField = FieldFactory.getField("isPreregistered", "IS_PREREGISTERED", visitorLoggingModule,FieldType.BOOLEAN);
        criteria.addAndCondition(CriteriaAPI.getCondition(preRegisterField, String.valueOf(true),BooleanOperators.IS));
        criteria.addAndCondition(getVisitorLogStatusCriteria("Upcoming"));
        view.setCriteria(criteria);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("expectedCheckInTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
        createdTime.setModule(visitorLoggingModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));
        view.setSortFields(sortFields);

        return view;
    }

    public static Condition getVisitorLogStatusCriteria(String status) {

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
        statusField.setModule(ModuleFactory.getVisitorLogCheckInModule());
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition condition = new Condition();
        condition.setField(statusField);
        condition.setOperator(LookupOperator.LOOKUP);
        condition.setCriteriaValue(statusCriteria);

        return condition;
    }

    private static FacilioView getAllVisitorLogsView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Visits");

        FacilioModule visitorLogging = ModuleFactory.getVisitorLoggingModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("checkInTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("CHECKIN_TIME");
        createdTime.setModule(visitorLogging);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getTenantPortalAllVisitorLogsView() {

        FacilioView allView = new FacilioView();
        allView.setName("tenantAll");
        allView.setDisplayName("My Visitors");

        FacilioModule visitorLogging = ModuleFactory.getVisitorLoggingModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("checkInTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("CHECKIN_TIME");
        createdTime.setModule(visitorLogging);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));

        return allView;
    }

    private static FacilioView getVendorUpcomingVisitorLogsView() {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getActiveInvitesCondition());
        FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLoggingModule();
//		FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, CommonOperators.IS_EMPTY));

        FacilioField createdTime = new FacilioField();
        createdTime.setName("expectedCheckInTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
        createdTime.setModule(visitorLoggingModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("invite_vendorActiveVisitors");
        allView.setDisplayName("Active");
        allView.setCriteria(criteria);
        allView.setSortFields(sortFields);

        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));

        return allView;
    }

    public static Condition getActiveInvitesCondition() {
        FacilioModule module = ModuleFactory.getVisitorLoggingModule();
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

    private static FacilioView getVendorExpiredVisitorInvites() {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getExpiredInvitesCondition());
//		FacilioField checkin = FieldFactory.getField("checkInTime","CHECKIN_TIME", FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkin, CommonOperators.IS_EMPTY));
        FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME", FieldType.DATE_TIME);
        criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckin, DateOperators.TILL_NOW));

        List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

        FacilioView myVisitorInvitesView = new FacilioView();
        myVisitorInvitesView.setName("invite_vendorExpired");
        myVisitorInvitesView.setDisplayName("Expired");
        myVisitorInvitesView.setCriteria(criteria);
        myVisitorInvitesView.setSortFields(sortFields);

        myVisitorInvitesView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));

        return myVisitorInvitesView;
    }

    public static Condition getExpiredInvitesCondition() {
        FacilioModule module = ModuleFactory.getVisitorLoggingModule();
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

    private static FacilioView getMyExpiredVisitorInvites() {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getExpiredInvitesCondition());
        FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME", FieldType.DATE_TIME);

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

    private static FacilioView getMyActiveVisitorInvites() {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getMyVistorInvitesCondition());
        criteria.addAndCondition(getActiveInvitesCondition());

        FacilioModule visitorInvitesModule = ModuleFactory.getVisitorLoggingModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("expectedCheckInTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
        createdTime.setModule(visitorInvitesModule);

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

    public static Condition getMyVistorInvitesCondition() {
        LookupField userField = new LookupField();
        userField.setName("requestedBy");
        userField.setColumnName("REQUESTED_BY");
        userField.setDataType(FieldType.LOOKUP);
        userField.setModule(ModuleFactory.getVisitorLoggingModule());
        userField.setSpecialType(FacilioConstants.ContextNames.USERS);

        Condition myUserCondition = new Condition();
        myUserCondition.setField(userField);
        myUserCondition.setOperator(PickListOperators.IS);
        myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

        return myUserCondition;
    }

    private static FacilioView  getMyPendingVisitsView() {

        Criteria criteria = new Criteria();
        criteria.addAndCondition(getVisitorLogStatusCriteria("Requested"));
        FacilioView view = new FacilioView();
        view.setName("myPendingVisits");
        view.setDisplayName("My Approvals");
        view.setCriteria(criteria);

        FacilioModule visitorLogging = ModuleFactory.getVisitorLoggingModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("checkInTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("CHECKIN_TIME");
        createdTime.setModule(visitorLogging);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        view.setSortFields(sortFields);
        view.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));

        return view;
    }
}
