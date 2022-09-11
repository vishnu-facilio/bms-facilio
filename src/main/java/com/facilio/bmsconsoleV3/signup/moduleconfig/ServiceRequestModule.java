package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class ServiceRequestModule extends BaseModuleConfig{
    public ServiceRequestModule(){
        setModuleName(FacilioConstants.ContextNames.SERVICE_REQUEST);
    }

    @Override
    protected void addForms() throws Exception {

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
}
