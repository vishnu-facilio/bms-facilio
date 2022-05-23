package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fw.BeanFactory;
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

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);

        FacilioForm jobPlanModuleForm = new FacilioForm();
        jobPlanModuleForm.setDisplayName("Job Plan");
        jobPlanModuleForm.setName("default_jobplan_web");
        jobPlanModuleForm.setModule(jobPlanModule);
        jobPlanModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        jobPlanModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> jobPlanModuleFormDefaultFields = new ArrayList<>();
        jobPlanModuleFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        jobPlanModuleFormDefaultFields.add(new FormField("jobPlanCategory", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.REQUIRED, 2, 1));
        jobPlanModuleFormDefaultFields.add(new FormField("assetCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Asset Category", FormField.Required.OPTIONAL, "assetcategory", 3, 1));
        jobPlanModuleFormDefaultFields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space Category", FormField.Required.OPTIONAL, "spacecategory", 4, 1));

        List<FormField> taskFields = new ArrayList<>();
        taskFields.add(new FormField("jobplansection", FacilioField.FieldDisplayType.JP_TASK, "Tasks", FormField.Required.REQUIRED, 5, 1));

        List<FormField> jobPlanModuleFormFields = new ArrayList<>();
        jobPlanModuleFormFields.addAll(jobPlanModuleFormDefaultFields);
        jobPlanModuleFormFields.addAll(taskFields);

        jobPlanModuleForm.setFields(jobPlanModuleFormFields);

        FormSection defaultSection = new FormSection("Scope", 1, jobPlanModuleFormDefaultFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection taskSection = new FormSection("TASKS", 2, taskFields, true);
        taskSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(taskSection);

        jobPlanModuleForm.setSections(sections);

        return Collections.singletonList(jobPlanModuleForm);
    }
}
