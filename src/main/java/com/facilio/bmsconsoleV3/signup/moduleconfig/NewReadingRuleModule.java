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

public class NewReadingRuleModule extends BaseModuleConfig{
    public NewReadingRuleModule(){
        setModuleName(FacilioConstants.ContextNames.NEW_READING_RULE_MODULE);
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
        ArrayList<FacilioView> newReadingRule = new ArrayList<FacilioView>();
        newReadingRule.add(getRulesByStatusForNewRule("active", "Active", true).setOrder(order++));
        newReadingRule.add(getRulesByStatusForNewRule("inactive", "In Active", false).setOrder(order++));
        newReadingRule.add(getAllNewReadingRules().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.NEW_READING_RULE);
        groupDetails.put("views", newReadingRule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getRulesByStatusForNewRule(String name, String displayName, boolean status) {
        List<FacilioField> rulesFields = FieldFactory.getNewReadingRuleFields();
        FacilioField statusField = FieldFactory.getAsMap(rulesFields).get("status");

        Condition statusCondition = CriteriaAPI.getCondition(statusField, String.valueOf(status), BooleanOperators.IS);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusCondition);
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getNewReadingRuleModule());

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setCriteria(criteria);
        view.setSortFields(sortFields);

        return view;
    }

    private static FacilioView getAllNewReadingRules() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getNewReadingRuleModule());

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }
}
