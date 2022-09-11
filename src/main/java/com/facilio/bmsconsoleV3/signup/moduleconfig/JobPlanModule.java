package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class JobPlanModule extends BaseModuleConfig{
    public JobPlanModule(){
        setModuleName(FacilioConstants.ContextNames.JOB_PLAN);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> jobPlan = new ArrayList<FacilioView>();
        jobPlan.add(getAllJobPlanView().setOrder(order++));
        jobPlan.add(getActiveJobPlanView().setOrder(order++));
        jobPlan.add(getInActiveJobPlanView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.JOB_PLAN);
        groupDetails.put("views", jobPlan);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllJobPlanView() {

        FacilioModule module = ModuleFactory.getJobPlanModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Job Plans");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }

    private static FacilioView getActiveJobPlanView() {
        Criteria criteria = getActiveJobPlanCriteria();
        FacilioModule jobPlanModule = ModuleFactory.getJobPlanModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(jobPlanModule);
        FacilioView allView = new FacilioView();
        allView.setName("active");
        allView.setDisplayName("Active");
        allView.setCriteria(criteria);
        return allView;
    }

    private static FacilioView getInActiveJobPlanView() {
        Criteria criteria = getInActiveJobPlanCriteria();
        FacilioModule jobPlanModule = ModuleFactory.getJobPlanModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(jobPlanModule);
        FacilioView allView = new FacilioView();
        allView.setName("inactive");
        allView.setDisplayName("Inactive");
        allView.setCriteria(criteria);
        return allView;
    }

    public static Criteria getActiveJobPlanCriteria() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getJobPlanBooleanCondition("isActive", "IS_ACTIVE", "true"));
        criteria.addAndCondition(getJobPlanBooleanCondition("isDisabled", "IS_DISABLED", "false"));
        return criteria;
    }

    public static Criteria getInActiveJobPlanCriteria() {
        Criteria criteria = new Criteria();

        criteria.addCondition("1", getJobPlanBooleanCondition("isActive", "IS_ACTIVE", "false"));
        criteria.addCondition("2", getJobPlanBooleanCondition("isDisabled", "IS_DISABLED", "false"));
        criteria.setPattern("(1 and 2)");

        return criteria;
    }

    public static Condition getJobPlanBooleanCondition(String fieldName, String columnName, String conditionValue) {
        FacilioModule module = ModuleFactory.getJobPlanModule();
        FacilioField field = new FacilioField();
        field.setName(fieldName);
        field.setColumnName(columnName);
        field.setDataType(FieldType.BOOLEAN);
        field.setModule(module);

        Condition condition = new Condition();
        condition.setField(field);
        condition.setOperator(BooleanOperators.IS);
        condition.setValue(conditionValue);

        return condition;
    }

}
