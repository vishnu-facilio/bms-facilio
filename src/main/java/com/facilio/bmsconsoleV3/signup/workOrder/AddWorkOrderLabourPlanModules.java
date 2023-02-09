package com.facilio.bmsconsoleV3.signup.workOrder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.CustomPageWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.SummaryWidgetUtil;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddWorkOrderLabourPlanModules extends BaseModuleConfig {

    public AddWorkOrderLabourPlanModules(){
        setModuleName(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
    }

    @Override
    public void addData() throws Exception {

        ModuleBean bean = Constants.getModBean();
        FacilioModule parentModule = bean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

        Objects.requireNonNull(parentModule,"WorkOrder module doesn't exists.");

        FacilioModule workOrderJobPlanModule = constructWorkOrderJobPlanModule(parentModule,bean);

        List<FacilioModule> modules = new ArrayList<>();
        modules.add(workOrderJobPlanModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,parentModule.getName());
        addModuleChain.getContext().put(FacilioConstants.ContextNames.DELETE_TYPE , 2);
        addModuleChain.execute();

        addForms();
        addSummaryWidget(workOrderJobPlanModule);

    }


    private FacilioModule constructWorkOrderJobPlanModule(FacilioModule workOrderModule, ModuleBean bean) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN, "WorkOrder Labour Plan", "WorkOrder_Labour_Plan", FacilioModule.ModuleType.SUB_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField parent = FieldFactory.getDefaultField("parent","Parent","PARENT_ID", FieldType.LOOKUP,true);
        parent.setLookupModule(workOrderModule);
        fields.add(parent);

        LookupField craft = FieldFactory.getDefaultField("craft","Craft","CRAFT_ID",FieldType.LOOKUP);
        craft.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.CraftAndSKills.CRAFT),"Craft module doesn't exists."));
        fields.add(craft);

        LookupField skill = FieldFactory.getDefaultField("skill","Skill","SKILL_ID",FieldType.LOOKUP);
        skill.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.CraftAndSKills.SKILLS),"Skill module doesn't exists."));
        fields.add(skill);

        fields.add(FieldFactory.getDefaultField("rate","Rate per Hour","RATE",FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("totalPrice","Total Amount","TOTAL_PRICE",FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("duration","Duration","DURATION",FieldType.NUMBER,FacilioField.FieldDisplayType.DURATION));

        module.setFields(fields);

        return module;
    }
    public void addSummaryWidget(FacilioModule workOrderJobPlanModule) throws Exception {
        ArrayList<String> apps = new ArrayList<>();
        if (!SignupUtil.maintenanceAppSignup()) {
            apps.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        apps.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        apps.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);

        for (String app : apps) {
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
            FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
            FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
            FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);


            CustomPageWidget pageWidget1 = new CustomPageWidget();
            SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();

            SummaryWidgetGroupFields groupField11 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField12 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField13 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField14 = new SummaryWidgetGroupFields();

            groupField11.setName(sysCreatedByField.getName());
            groupField11.setDisplayName(sysCreatedByField.getDisplayName());
            groupField11.setFieldId(sysCreatedByField.getId());
            groupField11.setRowIndex(1);
            groupField11.setColIndex(1);
            groupField11.setColSpan(2);

            groupField12.setName(sysCreatedTimeField.getName());
            groupField12.setDisplayName(sysCreatedTimeField.getDisplayName());
            groupField12.setFieldId(sysCreatedTimeField.getId());
            groupField12.setRowIndex(1);
            groupField12.setColIndex(3);
            groupField12.setColSpan(2);

            groupField13.setName(sysModifiedByField.getName());
            groupField13.setDisplayName(sysModifiedByField.getDisplayName());
            groupField13.setFieldId(sysModifiedByField.getId());
            groupField13.setRowIndex(2);
            groupField13.setColIndex(1);
            groupField13.setColSpan(2);

            groupField14.setName(sysModifiedTimeField.getName());
            groupField14.setDisplayName(sysModifiedTimeField.getDisplayName());
            groupField14.setFieldId(sysModifiedTimeField.getId());
            groupField14.setRowIndex(2);
            groupField14.setColIndex(3);
            groupField14.setColSpan(2);


            List<SummaryWidgetGroupFields> groupOneFields = new ArrayList<>();
            groupOneFields.add(groupField11);
            groupOneFields.add(groupField12);
            groupOneFields.add(groupField13);
            groupOneFields.add(groupField14);


            widgetGroup1.setName("moreDetails");
            widgetGroup1.setDisplayName("More Details");
            widgetGroup1.setColumns(4);
            widgetGroup1.setFields(groupOneFields);

            List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
            widgetGroupList.add(widgetGroup1);

            pageWidget1.setName("plansWidget");
            pageWidget1.setDisplayName("Plans Widget");
            pageWidget1.setModuleId(workOrderJobPlanModule.getModuleId());
            pageWidget1.setAppId(ApplicationApi.getApplicationIdForLinkName(app));
            pageWidget1.setGroups(widgetGroupList);

            SummaryWidgetUtil.addPageWidget(pageWidget1);
        }
    }

    public void addForms() throws Exception {
        ArrayList<String> apps = new ArrayList<>();
        if (!SignupUtil.maintenanceAppSignup()) {
            apps.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        apps.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        apps.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);

        for (String app : apps) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule workOrderPlansLabourModule = modBean.getModule(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);

            FacilioForm defaultForm = new FacilioForm();
            defaultForm.setName("standard_" + app);
            defaultForm.setModule(workOrderPlansLabourModule);
            defaultForm.setDisplayName("Standard_" + app);
            defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
            defaultForm.setShowInWeb(true);
            defaultForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));   //form needs to be created for multiple applications
            defaultForm.setIsSystemForm(true);
            defaultForm.setType(FacilioForm.Type.FORM);
            defaultForm.setAppLinkName(app);

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(workOrderPlansLabourModule.getName()));
            List<FormSection> sections = new ArrayList<FormSection>();
            FormSection section = new FormSection();
            section.setName("WorkOrder Labour Plans");
            section.setSectionType(FormSection.SectionType.FIELDS);
            section.setShowLabel(false);

            List<FormField> fields = new ArrayList<>();
            int seq = 0;
            fields.add(new FormField(fieldMap.get("craft").getFieldId(), "craft", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Craft", FormField.Required.REQUIRED, ++seq, 1));
            fields.add(new FormField(fieldMap.get("skill").getFieldId(), "skill", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Skill", FormField.Required.OPTIONAL, ++seq, 1));
            fields.add(new FormField(fieldMap.get("quantity").getFieldId(), "quantity", FacilioField.FieldDisplayType.NUMBER, "Quantity", FormField.Required.OPTIONAL, ++seq, 1));
            fields.add(new FormField(fieldMap.get("duration").getFieldId(), "duration", FacilioField.FieldDisplayType.DURATION, "Duration", FormField.Required.OPTIONAL, ++seq, 1));
            fields.add(new FormField(fieldMap.get("rate").getFieldId(), "rate", FacilioField.FieldDisplayType.NUMBER, "Rate per Hour", FormField.Required.OPTIONAL, ++seq, 1));
            fields.add(new FormField(fieldMap.get("totalPrice").getFieldId(), "totalPrice", FacilioField.FieldDisplayType.NUMBER, "Total Amount", FormField.Required.OPTIONAL, ++seq, 1));


            section.setFields(fields);
            section.setSequenceNumber(1);
            sections.add(section);

            defaultForm.setSections(sections);

            FormsAPI.createForm(defaultForm, workOrderPlansLabourModule);
            Map<Long, FormField> formFieldMap = defaultForm.getSections().stream().map(FormSection::getFields).flatMap(List::stream).collect(Collectors.toMap(FormField::getFieldId, Function.identity()));
            addRuleForCraftOnUpdate(defaultForm, fieldMap, formFieldMap);
            addRuleForSkillOnUpdate(defaultForm, fieldMap, formFieldMap);
            addRuleForWorkOrderPlannedLabour(defaultForm, fieldMap, formFieldMap);
        }
    }
    public void addRuleForCraftOnUpdate(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {

        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Craft on update");
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        singleRule.setFormId(defaultForm.getId());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

        List<FormRuleTriggerFieldContext> triggerFieldList= new ArrayList<>();
        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldId(formFieldMap.get(fieldMap.get("craft").getId()).getId());
        triggerFieldList.add(triggerField);
        singleRule.setTriggerFields(triggerFieldList);

        long skillFormFieldId = formFieldMap.get(fieldMap.get("skill").getId()).getId();
        long rateFormFieldId = formFieldMap.get(fieldMap.get("rate").getId()).getId();

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.EXECUTE_SCRIPT.getVal());
        String workflowString = "List getActions(Map formData) {\n" +
                "resultList = [];\n" +
                "if(formData.craft != null) {\n" +
               " valueMap = {};\n" +
                "conditionMap = {};\n" +
                "conditionMap.operatorId = 36;\n"+
                "conditionMap.fieldName = \"parentId\";\n" +
                "conditionMap.value = formData.craft.id;\n" +
                "conditionsMap = {};\n" +
                "conditionsMap.put(\"1\", conditionMap);\n" +
                "valueMap.put(\"conditions\", conditionsMap);\n" +
                "valueMap.put(\"pattern\", \"(1)\");\n" +
                "actionMap = {};\n" +
                "actionMap.value = valueMap;\n" +
                "actionMap.actionName = \"filter\" ;\n" +
                "result = {}; \n" +
                "result.action = actionMap;\n" +
                "result.fieldId ="+skillFormFieldId+";\n" +
                "resultList.add(result);\n" +
                "craft = Module(\"crafts\").fetchFirst([id == formData.craft.id]);\n" +
                "ratePerHour = craft.standardRate;\n" +
                "actionMap1 = {};\n" +
                "actionMap1.value = ratePerHour;\n" +
                "actionMap1.actionName = \"set\" ;\n" +
                "result1 = {};\n" +
                "result1.action = actionMap1;\n" +
                "result1.fieldId = "+rateFormFieldId+";\n" +
                "resultList.add(result1);\n" +
                "actionMap2 = {};\n" +
                "actionMap2.value = null;\n" +
                "actionMap2.actionName = \"set\" ;\n" +
                "result2 = {};\n" +
                "result2.action = actionMap2;\n" +
                "result2.fieldId = "+skillFormFieldId+";\n" +
                "resultList.add(result2);\n" +
                "}\n" +
                "return resultList;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "}";

        WorkflowContext workflowScript = new WorkflowContext();
        workflowScript.setWorkflowV2String(workflowString);
        workflowScript.setIsV2Script(true);
        showAction.setWorkflow(workflowScript);
        actions.add(showAction);
        singleRule.setActions(actions);

        FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
        Context context = chain.getContext();
        context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);
        chain.execute();

    }
    public void addRuleForSkillOnUpdate(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Skill on update");
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        singleRule.setFormId(defaultForm.getId());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

        List<FormRuleTriggerFieldContext> triggerFieldList= new ArrayList<>();
        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldId(formFieldMap.get(fieldMap.get("skill").getId()).getId());
        triggerFieldList.add(triggerField);
        singleRule.setTriggerFields(triggerFieldList);

        long rateFormFieldId = formFieldMap.get(fieldMap.get("rate").getId()).getId();

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.EXECUTE_SCRIPT.getVal());
        String workflowString = "List getActions(Map formData) {\n" +
                "resultList = [];\n" +
                "if(formData.skill != null) {\n" +
                "craft = Module(\"craftSkill\").fetchFirst([id == formData.skill.id]);\n" +
                "ratePerHour = craft.standardRate;\n" +
                "actionMap1 = {};\n" +
                "actionMap1.value = ratePerHour;\n" +
                "actionMap1.actionName = \"set\" ;\n" +
                "result1 = {};\n" +
                "result1.action = actionMap1;\n" +
                "result1.fieldId ="+rateFormFieldId+";\n" +
                "resultList.add(result1);\n" +
                "}\n" +
                "if(formData.skill == null){\n" +
                "craft = Module(\"crafts\").fetchFirst([id == formData.craft.id]);\n" +
                "ratePerHour = craft.standardRate;\n" +
                "actionMap1 = {};\n" +
                        "actionMap1.value = ratePerHour;\n"+
                        "actionMap1.actionName = \"set\" ;\n" +
                        " result1 = {};\n" +
                        "result1.action = actionMap1;\n" +
                "  result1.fieldId = "+rateFormFieldId+";\n" +
                "resultList.add(result1);\n" +
                "}\n" +
                "return resultList;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "}";
        WorkflowContext workflowScript = new WorkflowContext();
        workflowScript.setWorkflowV2String(workflowString);
        workflowScript.setIsV2Script(true);
        showAction.setWorkflow(workflowScript);
        actions.add(showAction);
        singleRule.setActions(actions);

        FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
        Context context = chain.getContext();
        context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);
        chain.execute();




    }
    public void addRuleForWorkOrderPlannedLabour(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {

        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Compute Cost");
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        singleRule.setFormId(defaultForm.getId());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("duration"), CommonOperators.IS_NOT_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("rate"), CommonOperators.IS_NOT_EMPTY));
        singleRule.setCriteria(criteria);

        List<FormRuleTriggerFieldContext> triggerFieldList= new ArrayList<>();
        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldId(formFieldMap.get(fieldMap.get("duration").getId()).getId());
        triggerFieldList.add(triggerField);
        FormRuleTriggerFieldContext triggerField2 = new FormRuleTriggerFieldContext();
        triggerField2.setFieldId(formFieldMap.get(fieldMap.get("rate").getId()).getId());
        triggerFieldList.add(triggerField2);
        FormRuleTriggerFieldContext triggerField3 = new FormRuleTriggerFieldContext();
        triggerField3.setFieldId(formFieldMap.get(fieldMap.get("quantity").getId()).getId());
        triggerFieldList.add(triggerField3);

        singleRule.setTriggerFields(triggerFieldList);

        long costFormFieldId = formFieldMap.get(fieldMap.get("totalPrice").getId()).getId();

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.EXECUTE_SCRIPT.getVal());
        String workflowString = "List getActions(Map formData) {\n" +
                "result = [];\n" +
                "duration = formData.duration;\n" +
                "duartionInHours = new NameSpace(\"date\").secToHour(duration);\n" +
                "ratePerHour= formData.rate;\n" +
                "cost = formData.quantity*duartionInHours*ratePerHour;\n" +
                "temp = {};\n" +
                "action = {};\n" +
                "action.actionName = \"set\";\n" +
                "action.value = cost;\n" +
                "temp.action = action;  \n" +
                "temp.fieldId ="+costFormFieldId+";\n" +
                "result.push(temp);\n" +
                "return result;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "}";

        WorkflowContext workflowScript = new WorkflowContext();
        workflowScript.setWorkflowV2String(workflowString);
        workflowScript.setIsV2Script(true);
        showAction.setWorkflow(workflowScript);
        actions.add(showAction);
        singleRule.setActions(actions);

        FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
        Context context = chain.getContext();
        context.put(FormRuleAPI.FORM_RULE_CONTEXT,singleRule);
        chain.execute();

    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderPlansLabourModule = new ArrayList<FacilioView>();
        workOrderPlansLabourModule.add(getJobPlanCraftViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
        groupDetails.put("views", workOrderPlansLabourModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getJobPlanCraftViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "workorderLabourPlan.ID", FieldType.NUMBER), true));

        FacilioView workorderPlanLabourView = new FacilioView();
        workorderPlanLabourView.setName("all");
        workorderPlanLabourView.setDisplayName("Job Plan Crafts");

        workorderPlanLabourView.setModuleName(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
        workorderPlanLabourView.setSortFields(sortFields);

        List<ViewField> workorderPlanLabourViewFields = new ArrayList<>();

        workorderPlanLabourViewFields.add(new ViewField("craft","Craft"));
        workorderPlanLabourViewFields.add(new ViewField("skill","Skill"));
        workorderPlanLabourViewFields.add(new ViewField("quantity","Quantity"));
        workorderPlanLabourViewFields.add(new ViewField("duration","Duration"));
        workorderPlanLabourViewFields.add(new ViewField("rate","Rate per Hour"));
        workorderPlanLabourViewFields.add(new ViewField("totalPrice","Total Amount"));

        workorderPlanLabourView.setFields(workorderPlanLabourViewFields);

        return workorderPlanLabourView;
    }
}
