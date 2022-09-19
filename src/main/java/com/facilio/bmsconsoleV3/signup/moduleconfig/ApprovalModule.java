package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class ApprovalModule extends BaseModuleConfig{
    public ApprovalModule(){
        setModuleName(FacilioConstants.ContextNames.APPROVAL);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> approval = new ArrayList<FacilioView>();
        approval.add(getMyRequestWorkorders("myrequests").setOrder(order++));
        approval.add(getRequestedStateApproval("requested", false).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.APPROVAL);
        groupDetails.put("views", approval);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getMyRequestWorkorders(String viewName) {

        Criteria criteria = new Criteria();
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        criteria.addAndCondition(getMyRequestCondition());

        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView myAllWorkordersView = new FacilioView();
        myAllWorkordersView.setName(viewName);
        myAllWorkordersView.setDisplayName("My Work Orders");
        myAllWorkordersView.setCriteria(criteria);
        myAllWorkordersView.setSortFields(sortFields);
        myAllWorkordersView.setHidden(true);

        return myAllWorkordersView;
    }

    public static FacilioView getRequestedStateApproval(String viewName, boolean isHidden) {

        FacilioModule module = ModuleFactory.getTicketsModule();
        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Criteria requestedStateCriteria = getRequestedStateCriteria(true);
        Condition requested = new Condition();
        requested.setField(statusField);
        requested.setOperator(LookupOperator.LOOKUP);
        requested.setCriteriaValue(requestedStateCriteria);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(requested);

        LookupField approvalStatusField = new LookupField();
        approvalStatusField.setName("approvalStatus");
        approvalStatusField.setColumnName("ARRPOVAL_STATE");
        approvalStatusField.setDataType(FieldType.LOOKUP);
        approvalStatusField.setModule(module);
        approvalStatusField.setLookupModule(ModuleFactory.getTicketStatusModule());
        Criteria c = new Criteria();
        c.addAndCondition(CriteriaAPI.getCondition(approvalStatusField, CommonOperators.IS_NOT_EMPTY));
        c.addAndCondition(CriteriaAPI.getCondition(approvalStatusField, requestedStateCriteria, LookupOperator.LOOKUP));
        criteria.orCriteria(c);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());

        FacilioView rejectedApproval = new FacilioView();
        rejectedApproval.setName(viewName);
        rejectedApproval.setDisplayName("Pending Approval");
        rejectedApproval.setCriteria(criteria);
        rejectedApproval.setHidden(isHidden);
        rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return rejectedApproval;
    }

    public static Criteria getRequestedStateCriteria(boolean isRequested) {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("requestedState");
        statusTypeField.setColumnName("REQUESTED_STATE");
        statusTypeField.setDataType(FieldType.BOOLEAN);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition status = new Condition();
        status.setField(statusTypeField);
        status.setOperator(BooleanOperators.IS);
        status.setValue(String.valueOf(isRequested));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(status);

        return criteria;
    }

    private static Condition getMyRequestCondition() {
        FacilioModule workOrderRequestsModule = ModuleFactory.getWorkOrdersModule();
        LookupField userField = new LookupField();
        userField.setName("requester");
        userField.setColumnName("REQUESTER_ID");
        userField.setDataType(FieldType.LOOKUP);
        userField.setModule(workOrderRequestsModule);
        userField.setSpecialType(FacilioConstants.ContextNames.REQUESTER);

        Condition myUserCondition = new Condition();
        myUserCondition.setField(userField);
        myUserCondition.setOperator(PickListOperators.IS);
        myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

        return myUserCondition;
    }
}
