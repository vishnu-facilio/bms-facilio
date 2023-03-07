package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ReadingRuleModule extends BaseModuleConfig{
    public ReadingRuleModule(){
        setModuleName(FacilioConstants.ContextNames.READING_RULE_MODULE);
    }

    @Override
    protected void addTriggers() throws Exception {
        return;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> readingRule = new ArrayList<FacilioView>();
        readingRule.add(getRulesByStatus("active", "Active", true).setOrder(order++));
        readingRule.add(getRulesByStatus("inactive", "In Active", false).setOrder(order++));
        readingRule.add(getAllRules().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.READING_RULE_MODULE);
        groupDetails.put("views", readingRule);
        groupDetails.put("appLinkNames",Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getRulesByStatus(String name, String displayName, boolean status) {
        Condition statusCondition = getRulesStateCondition(status);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusCondition);
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkflowRuleModule());

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setCriteria(criteria);
        view.setSortFields(sortFields);
        view.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        return view;
    }

    private static Condition getRulesStateCondition(Boolean status) {
        List<FacilioField> rulesFields = FieldFactory.getWorkflowRuleFields();
        Map<String, FacilioField> fieldProps = FieldFactory.getAsMap(rulesFields);

        FacilioField statusField = fieldProps.get("status");

        Condition statusCondition = CriteriaAPI.getCondition(statusField, String.valueOf(status), BooleanOperators.IS);
        return statusCondition;
    }

    private static FacilioView getAllRules() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkflowRuleModule());

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        return allView;
    }
}

