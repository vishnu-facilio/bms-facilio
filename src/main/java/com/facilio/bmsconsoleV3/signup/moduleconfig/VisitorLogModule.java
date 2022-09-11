package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
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
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> visitorLog = new ArrayList<FacilioView>();
        visitorLog.add(getVisitorsLogCheckedInTodayView().setOrder(order++));
        visitorLog.add(getVisitorsLogCheckedInCurrentlyView().setOrder(order++));
        visitorLog.add(getPendingVisitLogsView().setOrder(order++));
        visitorLog.add(getAllVisitorCheckInLogsView().setOrder(order++));

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
}
