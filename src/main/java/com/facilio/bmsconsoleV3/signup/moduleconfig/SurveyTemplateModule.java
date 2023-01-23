package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class SurveyTemplateModule extends BaseModuleConfig{
    public SurveyTemplateModule(){
        setModuleName(FacilioConstants.Survey.SURVEY_TEMPLATE);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> surveyTemplate = new ArrayList<FacilioView>();
        surveyTemplate.add(getAllSurveyTemplateViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Survey.SURVEY_TEMPLATE);
        groupDetails.put("views", surveyTemplate);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllSurveyTemplateViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Survey_Templates.ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Survey Templates");
        allView.setModuleName(FacilioConstants.Survey.SURVEY_TEMPLATE);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
}

