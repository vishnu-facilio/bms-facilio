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

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Survey.SURVEY_RESPONSE);
        groupDetails.put("views", surveyResponse);
        groupVsViews.add(groupDetails);

        return groupVsViews;
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

