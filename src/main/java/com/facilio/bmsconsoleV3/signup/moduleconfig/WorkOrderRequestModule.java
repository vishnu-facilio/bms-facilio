package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class WorkOrderRequestModule extends BaseModuleConfig{
    public WorkOrderRequestModule(){
        setModuleName(FacilioConstants.ContextNames.WORK_ORDER_REQUEST);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workorderRequest = new ArrayList<FacilioView>();
        workorderRequest.add(getOpenWorkorderRequests().setOrder(order++));
        workorderRequest.add(getAllWorkRequests().setOrder(order++));
        workorderRequest.add(getRejectedWorkorderRequests().setOrder(order++));
        workorderRequest.add(getMyWorkorderRequests().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.WORK_ORDER_REQUEST);
        groupDetails.put("views", workorderRequest);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getOpenWorkorderRequests() {

        Criteria criteria = getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus.OPEN);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrderRequestsModule());

        FacilioView allRequestsView = new FacilioView();
        allRequestsView.setName("open");
        allRequestsView.setDisplayName("Work Requests");
        allRequestsView.setCriteria(criteria);
        allRequestsView.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return allRequestsView;
    }

    private static FacilioView getRejectedWorkorderRequests() {

        Criteria criteria = getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus.REJECTED);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrderRequestsModule());

        FacilioView allRequestsView = new FacilioView();
        allRequestsView.setName("rejected");
        allRequestsView.setDisplayName("Rejected Work Requests");
        allRequestsView.setCriteria(criteria);
        allRequestsView.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return allRequestsView;
    }

    private static FacilioView getAllWorkRequests() {

        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrderRequestsModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Requests");
        allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return allView;
    }

    private static FacilioView getMyWorkorderRequests() {

        Criteria criteria = new Criteria();
        FacilioModule workOrderRequestsModule = ModuleFactory.getWorkOrderRequestsModule();
        criteria.addAndCondition(getMyRequestCondition());

        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrderRequestsModule);

        FacilioView myRequestsView = new FacilioView();
        myRequestsView.setName("myrequests");
        myRequestsView.setDisplayName("My Work Requests");
        myRequestsView.setCriteria(criteria);
        myRequestsView.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return myRequestsView;
    }

    public static Criteria getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus status) {

        FacilioField field = new FacilioField();
        field.setName("requestStatus");
        field.setColumnName("STATUS");
        field.setDataType(FieldType.NUMBER);
        FacilioModule workOrderRequestsModule = ModuleFactory.getWorkOrderRequestsModule();
        field.setModule(workOrderRequestsModule);

        Condition condition = new Condition();
        condition.setField(field);
        condition.setOperator(NumberOperators.EQUALS);
        condition.setValue(String.valueOf(status.getIntVal()));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);

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
