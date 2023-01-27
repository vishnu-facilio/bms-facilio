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
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> surveyResponse = new ArrayList<FacilioView>();
        surveyResponse.add(getAllSurveyResponseViews().setOrder(order++));
        surveyResponse.add(getViewOne().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Survey.SURVEY_RESPONSE);
        groupDetails.put("views", surveyResponse);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private static FacilioView getViewOne() {

        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        SortField sortField = new SortField(createdTime, false);

        FacilioView viewOne = new FacilioView();
        viewOne.setName("viewOne");
        viewOne.setDisplayName("View One");
        viewOne.setFields(getViewOneColumns());
        viewOne.setSortFields(Collections.singletonList(sortField));
        viewOne.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));
        return viewOne;
    }

    private static List<ViewField> getViewOneColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();

        columns.add(new ViewField("id", "Id"));
        columns.add(new ViewField("name", "Name"));
        columns.add(new ViewField("resStatus", "Status"));
        columns.add(new ViewField("assignedTo", "Assigned To"));
        columns.add(new ViewField("totalScore", "Total Score"));
        columns.add(new ViewField("fullScore", "Full Score"));
        columns.add(new ViewField("createdTime", "Created Time"));

        return columns;
    }

    private static FacilioView getAllSurveyResponseViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Survey_Responses.ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Surveys");
        allView.setModuleName(FacilioConstants.Survey.SURVEY_RESPONSE);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
}

