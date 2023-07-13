package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class SurveyResponseModule extends BaseModuleConfig{
    public SurveyResponseModule(){
        setModuleName(FacilioConstants.Survey.SURVEY_RESPONSE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();

        Map<String, Object> serviceRequestGroupDetails;

        int srViewOrder = 1;
        ArrayList<FacilioView> serviceRequestViewFields = new ArrayList<FacilioView>();
        serviceRequestViewFields.add(getServiceRequestViewOne().setOrder(srViewOrder++));

        serviceRequestGroupDetails = new HashMap<>();
        serviceRequestGroupDetails.put("name", "ServiceRequest");
        serviceRequestGroupDetails.put("displayName", "Service Request");
        serviceRequestGroupDetails.put("moduleName", FacilioConstants.Survey.SURVEY_RESPONSE);
        serviceRequestGroupDetails.put("views", serviceRequestViewFields);
        groupVsViews.add(serviceRequestGroupDetails);

        Map<String, Object> workOrderGroupDetails;

        int woViewOrder = 1;
        ArrayList<FacilioView> workOrderViewFields = new ArrayList<FacilioView>();
        workOrderViewFields.add(getWorkOrderViewOne().setOrder(woViewOrder++));

        workOrderGroupDetails = new HashMap<>();
        workOrderGroupDetails.put("name", "WorkOrder");
        workOrderGroupDetails.put("displayName", "Work Order");
        workOrderGroupDetails.put("moduleName", FacilioConstants.Survey.SURVEY_RESPONSE);
        workOrderGroupDetails.put("views", workOrderViewFields);
        groupVsViews.add(workOrderGroupDetails);

        return groupVsViews;
    }
    private static FacilioView getWorkOrderViewOne() throws Exception{

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Survey_Responses.ID", FieldType.NUMBER), true));

        FacilioView workOrderSurveyListView = new FacilioView();
        workOrderSurveyListView.setName("workorder");

        workOrderSurveyListView.setModuleName(FacilioConstants.Survey.SURVEY_RESPONSE);
        workOrderSurveyListView.setDisplayName("Work Order Surveys");
        workOrderSurveyListView.setSortFields(sortFields);

        List<ViewField> columns = new ArrayList<ViewField>();

        columns.add(new ViewField("name", "Survey"));
        columns.add(new ViewField("workOrderId", "WorkOrder"));
        columns.add(new ViewField("responseStatus", "Completion Status"));
        columns.add(new ViewField("assignedTo", "Survey Respondent"));

        workOrderSurveyListView.setFields(columns);
        return workOrderSurveyListView;
    }

    private static FacilioView getServiceRequestViewOne() throws Exception{

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Survey_Responses.ID", FieldType.NUMBER), true));

        FacilioView serviceRequestSurveyListView = new FacilioView();
        serviceRequestSurveyListView.setName("serviceRequest");

        serviceRequestSurveyListView.setModuleName(FacilioConstants.Survey.SURVEY_RESPONSE);
        serviceRequestSurveyListView.setDisplayName("Service Request Surveys");
        serviceRequestSurveyListView.setSortFields(sortFields);

        List<ViewField> columns = new ArrayList<ViewField>();

        columns.add(new ViewField("name", "Survey"));
        columns.add(new ViewField("serviceRequestId", "Service Request"));
        columns.add(new ViewField("responseStatus", "Completion Status"));
        columns.add(new ViewField("assignedTo", "Survey Respondent"));

        serviceRequestSurveyListView.setFields(columns);
        return serviceRequestSurveyListView;
    }
}

