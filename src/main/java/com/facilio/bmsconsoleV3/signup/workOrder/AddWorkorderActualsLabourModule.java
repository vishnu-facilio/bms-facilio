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
import com.facilio.modules.fields.*;
import com.facilio.util.SummaryWidgetUtil;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.facilio.modules.fields.FacilioField.FieldDisplayType.SELECTBOX;

public class AddWorkorderActualsLabourModule extends BaseModuleConfig {

    public AddWorkorderActualsLabourModule(){
        setModuleName(FacilioConstants.ContextNames.WO_LABOUR);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        FacilioModule ticketModule = moduleBean.getModule("ticket");

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule workorderActualsLabourModule = addWorkorderActualsLabourModule();
        modules.add(workorderActualsLabourModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,ticketModule.getName());
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        addForms();
        addSummaryWidget(workorderActualsLabourModule);
    }

    public FacilioModule addWorkorderActualsLabourModule() throws Exception{

        ModuleBean moduleBean = Constants.getModBean();

        FacilioModule module = new FacilioModule("workorderLabour", "Workorder Labour", "Workorder_labour", FacilioModule.ModuleType.SUB_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField craft = FieldFactory.getDefaultField("craft","Craft","CRAFT_ID", FieldType.LOOKUP);
        craft.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.CraftAndSKills.CRAFT),"Craft module doesn't exists."));
        fields.add(craft);

        LookupField skill = FieldFactory.getDefaultField("skill","Skill","SKILL_ID",FieldType.LOOKUP);
        skill.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.CraftAndSKills.SKILLS),"Skill module doesn't exists."));
        fields.add(skill);

        NumberField cost = FieldFactory.getDefaultField("cost","Cost","COST", FieldType.DECIMAL);
        fields.add(cost);

        CurrencyField rate = FieldFactory.getDefaultField("rate","Rate per Hour",null, FieldType.CURRENCY_FIELD,FacilioField.FieldDisplayType.CURRENCY);
        rate.setRequired(true);
        fields.add(rate);

        CurrencyField totalAmount = FieldFactory.getDefaultField("totalAmount","Total Amount",null, FieldType.CURRENCY_FIELD,FacilioField.FieldDisplayType.CURRENCY);
        totalAmount.setRequired(true);
        fields.add(totalAmount);

        DateField startTime = FieldFactory.getDefaultField("startTime","Start Time","START_TIME", FieldType.DATE_TIME);
        startTime.setRequired(true);
        fields.add(startTime);

        DateField endTime = FieldFactory.getDefaultField("endTime","End Time","END_TIME", FieldType.DATE_TIME);
        endTime.setRequired(true);
        fields.add(endTime);

        NumberField duration = FieldFactory.getDefaultField("duration","duration","DURATION", FieldType.NUMBER,FacilioField.FieldDisplayType.DURATION);
        duration.setRequired(true);
        fields.add(duration);

        LookupField labour = FieldFactory.getDefaultField("labour","Labour","LABOUR", FieldType.LOOKUP,true);
        labour.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.LABOUR),"Labour module doesn't exists."));
        fields.add(labour);

        NumberField parentId = FieldFactory.getDefaultField("parentId","Parent","PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);


        LookupField parent = FieldFactory.getDefaultField("parent","Parent WO",
                "PARENT_WO", FieldType.LOOKUP);
        parent.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.WORK_ORDER),"Labour module doesn't exists."));
        fields.add(parent);

        EnumField typeField = FieldFactory.getDefaultField("type","Type","TYPE", FieldType.ENUM);
        typeField.setRequired(true);
        List<String> types = Arrays.asList(
                "Observation", "Setup",
                "Travel", "Work", "Waiting For Material", "Waiting For Access","Waiting For Other Department Assistance");

        List<EnumFieldValue<Integer>> typeValues = types.stream().map( val -> {
            int index = types.indexOf(val)+1;
            return new EnumFieldValue<>(index, val, index, true);
        }).collect(Collectors.toList());
        typeField.setValues(typeValues);


        fields.add(typeField);


        module.setFields(fields);
        return module;
    }

    public void addSummaryWidget(FacilioModule workorderActualsLabourModule) throws Exception {
        ArrayList<String> apps = new ArrayList<>();
        if (!SignupUtil.maintenanceAppSignup()) {
            apps.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        apps.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        apps.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);

        for (String app : apps) {
            ModuleBean moduleBean = Constants.getModBean();
            FacilioField labour = moduleBean.getField("labour", FacilioConstants.ContextNames.WO_LABOUR);
            FacilioField crafts = moduleBean.getField("craft", FacilioConstants.ContextNames.WO_LABOUR);
            FacilioField skills = moduleBean.getField("skill", FacilioConstants.ContextNames.WO_LABOUR);
            FacilioField type = moduleBean.getField("type", FacilioConstants.ContextNames.WO_LABOUR);
            FacilioField startTime = moduleBean.getField("startTime", FacilioConstants.ContextNames.WO_LABOUR);
            FacilioField endTime = moduleBean.getField("endTime", FacilioConstants.ContextNames.WO_LABOUR);
            FacilioField duration = moduleBean.getField("duration", FacilioConstants.ContextNames.WO_LABOUR);
            FacilioField rate = moduleBean.getField("rate", FacilioConstants.ContextNames.WO_LABOUR);
            FacilioField cost = moduleBean.getField("totalAmount", FacilioConstants.ContextNames.WO_LABOUR);

            FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", FacilioConstants.ContextNames.WO_LABOUR);
            FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", FacilioConstants.ContextNames.WO_LABOUR);
            FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", FacilioConstants.ContextNames.WO_LABOUR);
            FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", FacilioConstants.ContextNames.WO_LABOUR);

            CustomPageWidget widget = new CustomPageWidget();

            SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();
            SummaryWidgetGroup widgetGroup2 = new SummaryWidgetGroup();

            SummaryWidgetGroupFields groupField1 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField2 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField3 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField4 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField5 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField6 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField7 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField8 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField9 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField11 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField12 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField13 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField14 = new SummaryWidgetGroupFields();



            groupField1.setName(labour.getName());
            groupField1.setDisplayName(labour.getDisplayName());
            groupField1.setFieldId(labour.getId());
            groupField1.setRowIndex(1);
            groupField1.setColIndex(1);
            groupField1.setColSpan(1);

            groupField2.setName(crafts.getName());
            groupField2.setDisplayName(crafts.getDisplayName());
            groupField2.setFieldId(crafts.getId());
            groupField2.setRowIndex(1);
            groupField2.setColIndex(2);
            groupField2.setColSpan(1);

            groupField3.setName(skills.getName());
            groupField3.setDisplayName(skills.getDisplayName());
            groupField3.setFieldId(skills.getId());
            groupField3.setRowIndex(1);
            groupField3.setColIndex(3);
            groupField3.setColSpan(1);

            groupField4.setName(type.getName());
            groupField4.setDisplayName(type.getDisplayName());
            groupField4.setFieldId(type.getId());
            groupField4.setRowIndex(2);
            groupField4.setColIndex(1);
            groupField4.setColSpan(1);

            groupField5.setName(startTime.getName());
            groupField5.setDisplayName(startTime.getDisplayName());
            groupField5.setFieldId(startTime.getId());
            groupField5.setRowIndex(2);
            groupField5.setColIndex(2);
            groupField5.setColSpan(1);

            groupField6.setName(endTime.getName());
            groupField6.setDisplayName(endTime.getDisplayName());
            groupField6.setFieldId(endTime.getId());
            groupField6.setRowIndex(2);
            groupField6.setColIndex(3);
            groupField6.setColSpan(1);

            groupField7.setName(duration.getName());
            groupField7.setDisplayName(duration.getDisplayName());
            groupField7.setFieldId(duration.getId());
            groupField7.setRowIndex(3);
            groupField7.setColIndex(1);
            groupField7.setColSpan(1);

            groupField8.setName(rate.getName());
            groupField8.setDisplayName(rate.getDisplayName());
            groupField8.setFieldId(rate.getId());
            groupField8.setRowIndex(3);
            groupField8.setColIndex(2);
            groupField8.setColSpan(1);

            groupField9.setName(cost.getName());
            groupField9.setDisplayName(cost.getDisplayName());
            groupField9.setFieldId(cost.getId());
            groupField9.setRowIndex(3);
            groupField9.setColIndex(3);
            groupField9.setColSpan(1);


            groupField11.setName(sysCreatedByField.getName());
            groupField11.setDisplayName(sysCreatedByField.getDisplayName());
            groupField11.setFieldId(sysCreatedByField.getId());
            groupField11.setRowIndex(1);
            groupField11.setColIndex(1);
            groupField11.setColSpan(1);

            groupField12.setName(sysCreatedTimeField.getName());
            groupField12.setDisplayName(sysCreatedTimeField.getDisplayName());
            groupField12.setFieldId(sysCreatedTimeField.getId());
            groupField12.setRowIndex(1);
            groupField12.setColIndex(2);
            groupField12.setColSpan(1);

            groupField13.setName(sysModifiedByField.getName());
            groupField13.setDisplayName(sysModifiedByField.getDisplayName());
            groupField13.setFieldId(sysModifiedByField.getId());
            groupField13.setRowIndex(1);
            groupField13.setColIndex(3);
            groupField13.setColSpan(1);

            groupField14.setName(sysModifiedTimeField.getName());
            groupField14.setDisplayName(sysModifiedTimeField.getDisplayName());
            groupField14.setFieldId(sysModifiedTimeField.getId());
            groupField14.setRowIndex(2);
            groupField14.setColIndex(1);
            groupField14.setColSpan(1);

            List<SummaryWidgetGroupFields> groupOneFields = new ArrayList<>();
            List<SummaryWidgetGroupFields> groupTwoFields = new ArrayList<>();
            groupOneFields.add(groupField1);
            groupOneFields.add(groupField2);
            groupOneFields.add(groupField3);
            groupOneFields.add(groupField4);
            groupOneFields.add(groupField5);
            groupOneFields.add(groupField6);
            groupOneFields.add(groupField7);
            groupOneFields.add(groupField8);
            groupOneFields.add(groupField9);
            groupTwoFields.add(groupField11);
            groupTwoFields.add(groupField12);
            groupTwoFields.add(groupField13);
            groupTwoFields.add(groupField14);



            widgetGroup1.setName("section1");
            widgetGroup1.setDisplayName("Labor Details");
            widgetGroup1.setColumns(3);
            widgetGroup1.setFields(groupOneFields);

            widgetGroup2.setName("section2");
            widgetGroup2.setDisplayName("Additional Details");
            widgetGroup2.setColumns(3);
            widgetGroup2.setFields(groupTwoFields);

            List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
            widgetGroupList.add(widgetGroup1);
            widgetGroupList.add(widgetGroup2);

            widget.setName("actualsLabourWidget");
            widget.setDisplayName("Actuals Labour Widget");
            widget.setModuleId(workorderActualsLabourModule.getModuleId());
            widget.setAppId(ApplicationApi.getApplicationIdForLinkName(app));
            widget.setGroups(widgetGroupList);

            SummaryWidgetUtil.addPageWidget(widget);
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
            FacilioModule workOrderLabourModule = modBean.getModule(FacilioConstants.ContextNames.WO_LABOUR);

            FacilioForm defaultForm = new FacilioForm();
            defaultForm.setName("standard_" + app);
            defaultForm.setModule(workOrderLabourModule);
            defaultForm.setDisplayName("Standard_" + app);
            defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
            defaultForm.setShowInWeb(true);
            defaultForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));   //form needs to be created for multiple applications
            defaultForm.setIsSystemForm(true);
            defaultForm.setType(FacilioForm.Type.FORM);
            defaultForm.setAppLinkName(app);

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(workOrderLabourModule.getName()));
            List<FormSection> sections = new ArrayList<FormSection>();
            FormSection section = new FormSection();
            section.setName("WorkOrder Labour");
            section.setSectionType(FormSection.SectionType.FIELDS);
            section.setShowLabel(false);

            List<FormField> fields = new ArrayList<>();
            int seq = 0;
            fields.add(new FormField(fieldMap.get("labour").getFieldId(), "labour", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Labour", FormField.Required.REQUIRED, ++seq, 1));
            fields.add(new FormField(fieldMap.get("craft").getFieldId(), "craft", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Craft", FormField.Required.OPTIONAL, ++seq, 1));
            fields.add(new FormField(fieldMap.get("skill").getFieldId(), "skill", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Skill", FormField.Required.OPTIONAL, ++seq, 1));
            FormField formField= new FormField(fieldMap.get("type").getFieldId(), "type", FacilioField.FieldDisplayType.SELECTBOX, "Type", FormField.Required.OPTIONAL, ++seq, 1);
            formField.setValue("4");
            fields.add(formField);

            fields.add(new FormField(fieldMap.get("startTime").getFieldId(), "startTime", FacilioField.FieldDisplayType.DATETIME, "Start Time", FormField.Required.OPTIONAL, ++seq, 1));
            fields.add(new FormField(fieldMap.get("endTime").getFieldId(), "endTime", FacilioField.FieldDisplayType.DATETIME, "End Time", FormField.Required.OPTIONAL, ++seq, 1));
            fields.add(new FormField(fieldMap.get("duration").getFieldId(), "duration", FacilioField.FieldDisplayType.DURATION, "Duration", FormField.Required.REQUIRED, ++seq, 1));


            FormField rateField = new FormField(fieldMap.get("rate").getFieldId(), "rate", FacilioField.FieldDisplayType.CURRENCY, "Rate per Hour", FormField.Required.REQUIRED, ++seq, 1);
            rateField.setIsDisabled(true);
            fields.add(rateField);

            FormField totalField = new FormField(fieldMap.get("totalAmount").getFieldId(), "totalAmount", FacilioField.FieldDisplayType.CURRENCY, "Total Cost", FormField.Required.REQUIRED, ++seq, 1);
            totalField.setIsDisabled(true);
            totalField.setValue("0");
            fields.add(totalField);


            section.setFields(fields);
            section.setSequenceNumber(1);
            sections.add(section);

            defaultForm.setSections(sections);

//            for (FormSection sec : defaultForm.getSections()){
//                for(FormField field:sec.getFields()){
//                    if(field.getDisplayTypeEnum() == FacilioField.FieldDisplayType.SELECTBOX && field.getDisplayName() == "Type" ){
//                        field.setValue("3");
//                    }
//                }
//            }

            FormsAPI.createForm(defaultForm, workOrderLabourModule);

            Map<Long, FormField> formFieldMap = defaultForm.getSections().stream().map(FormSection::getFields).flatMap(List::stream).collect(Collectors.toMap(FormField::getFieldId, Function.identity()));
            addRuleForCraftOnUpdate(defaultForm, fieldMap, formFieldMap);
            addRuleForSkillOnUpdate(defaultForm, fieldMap, formFieldMap);
            addRuleForWorkOrderLabour(defaultForm, fieldMap, formFieldMap);
            addFieldDisableRule(defaultForm, fieldMap, formFieldMap);
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

        long labourFormFieldId = formFieldMap.get(fieldMap.get("labour").getId()).getId();
        long skillFormFieldId = formFieldMap.get(fieldMap.get("skill").getId()).getId();
        long rateFormFieldId = formFieldMap.get(fieldMap.get("rate").getId()).getId();

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.EXECUTE_SCRIPT.getVal());
        String workflowString = "List getActions(Map formData) {\n" +
                "resultList = [];\n" +
                "if(formData.craft != null && formData.skill == null) {\n" +
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
                "valueMap = {};\n"+
//                "if(formData.id != null ){\n" +
                "rates= formData.rate;\n"+
                "code = rates.currencyCode;\n"+
                "if(code != null ){\n" +
                "valueMap.currencyCode = code;\n" +
                "}\n" +
                "else if( code == null){\n" +
                "valueMap.currencyCode = null;\n" +
                "}\n" +
                "if(ratePerHour != null ){\n" +
                "valueMap.currencyValue = ratePerHour;\n" +
                "}\n" +
                "if(ratePerHour == null ){\n" +
                "valueMap.currencyValue = 0;\n" +
                "}\n" +
                "actionMap1 = {};\n" +
                "actionMap1.value = valueMap;\n" +
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
                "if(formData.craft != null && formData.skill != null){\n" +
                "craft = Module(\"craftSkill\").fetchFirst([id == formData.skill.id]);\n" +
                "ratePerHour = craft.standardRate;\n" +
                "valueMap = {};\n"+
                "rates= formData.rate;\n"+
                "code = rates.currencyCode;\n"+
                "if(code != null ){\n" +
                "valueMap.currencyCode = code;\n" +
                "}\n" +
                "else if( code == null){\n" +
                "valueMap.currencyCode = null;\n" +
                "}\n" +
                "if(ratePerHour != null ){\n" +
                "valueMap.currencyValue = ratePerHour;\n" +
                "}\n" +
                "if(ratePerHour == null ){\n" +
                "valueMap.currencyValue = 0;\n" +
                "}\n" +
                "actionMap1 = {};\n" +
                "actionMap1.value = valueMap;\n" +
                "actionMap1.actionName = \"set\" ;\n" +
                "result1 = {};\n" +
                "result1.action = actionMap1;\n" +
                "result1.fieldId ="+rateFormFieldId+";\n" +
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
        long labourFormFieldId = formFieldMap.get(fieldMap.get("labour").getId()).getId();
        long skillFormFieldId = formFieldMap.get(fieldMap.get("skill").getId()).getId();

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.EXECUTE_SCRIPT.getVal());
        String workflowString = "List getActions(Map formData) {\n" +
                "resultList = [];\n" +
                "if(formData.skill != null) {\n" +
                "craft = Module(\"craftSkill\").fetchFirst([id == formData.skill.id]);\n" +
                "ratePerHour = craft.standardRate;\n" +
                "valueMap = {};\n"+
                "rates= formData.rate;\n"+
                "code = rates.currencyCode;\n"+
                "if(code != null ){\n" +
                "valueMap.currencyCode = code;\n" +
                "}\n" +
                "else if( code == null){\n" +
                "valueMap.currencyCode = null;\n" +
                "}\n" +
                "if(ratePerHour != null ){\n" +
                "valueMap.currencyValue = ratePerHour;\n" +
                "}\n" +
                "if(ratePerHour == null ){\n" +
                "valueMap.currencyValue = 0;\n" +
                "}\n" +
                "actionMap1 = {};\n" +
                "actionMap1.value = valueMap;\n" +
                "actionMap1.actionName = \"set\" ;\n" +
                "result1 = {};\n" +
                "result1.action = actionMap1;\n" +
                "result1.fieldId ="+rateFormFieldId+";\n" +
                "resultList.add(result1);\n" +
                "}\n" +
                "if(formData.craft != null && formData.skill != null){\n" +
                "labourList = [];\n" +
                "labourCrafts = Module(\"labourCraftSkill\").fetch([skill == formData.skill.id && craft == formData.craft.id]);\n" +
                "if(labourCrafts != null ){\n" +
                "for each index,value in labourCrafts {\n"+
                "labourCraft= value;\n"+
                "labours = labourCraft.labour;\n" +
                "labour = labours.id+\"\";\n"+
                "labourList.add(labour);\n" +
                "}\n" +
                "}\n" +
                "conditionMap = {};\n" +
                "conditionMap.operatorId = 36;\n"+
                "conditionMap.fieldName = \"id\";\n" +
                "conditionMap.value = labourList.join(\",\");\n" +
                "valueMap = {};\n" +
                "conditionsMap = {};\n" +
                "conditionsMap.put(\"1\", conditionMap);\n" +
                "valueMap.put(\"conditions\", conditionsMap);\n" +
                "valueMap.put(\"pattern\", \"(1)\");\n" +
                "actionMap = {};\n" +
                "actionMap.value = valueMap;\n" +
                "actionMap.actionName = \"filter\" ;\n" +
                "result = {}; \n" +
                "result.action = actionMap;\n" +
                "result.fieldId ="+labourFormFieldId+";\n" +
                "resultList.add(result);\n" +
                "}\n" +

                "if(formData.skill == null && formData.craft != null){\n" +

                "craft = Module(\"crafts\").fetchFirst([id == formData.craft.id]);\n" +
                "ratePerHour = craft.standardRate;\n" +
                "valueMap = {};\n"+
                "rates= formData.rate;\n"+
                "code = rates.currencyCode;\n"+
                "if(code != null ){\n" +
                "valueMap.currencyCode = code;\n" +
                "}\n" +
                "else if( code == null){\n" +
                "valueMap.currencyCode = null;\n" +
                "}\n" +
                "if(ratePerHour != null ){\n" +
                "valueMap.currencyValue = ratePerHour;\n" +
                "}\n" +
                "if(ratePerHour == null ){\n" +
                "valueMap.currencyValue = 0;\n" +
                "}\n" +
                "actionMap1 = {};\n" +
                "actionMap1.value = valueMap;\n"+
                "actionMap1.actionName = \"set\" ;\n" +
                " result1 = {};\n" +
                "result1.action = actionMap1;\n" +
                " result1.fieldId = "+rateFormFieldId+";\n" +
                "resultList.add(result1);\n" +
                "valueMap = {};\n" +
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
                "labourList = [];\n" +
                "labourCrafts = Module(\"labourCraftSkill\").fetch([craft == formData.craft.id]);\n" +
                "if(labourCrafts != null ){\n" +
                "for each index,value in labourCrafts {\n"+
                "labourCraft= value;\n"+
                "labours = labourCraft.labour;\n" +
                "labour = labours.id+\"\";\n"+
                "labourList.add(labour);\n" +
                "}\n" +
                "}\n" +
                "conditionMap = {};\n" +
                "conditionMap.operatorId = 36;\n"+
                "conditionMap.fieldName = \"id\";\n" +
                "conditionMap.value = labourList.join(\",\");\n" +
                "valueMap = {};\n" +
                "conditionsMap = {};\n" +
                "conditionsMap.put(\"1\", conditionMap);\n" +
                "valueMap.put(\"conditions\", conditionsMap);\n" +
                "valueMap.put(\"pattern\", \"(1)\");\n" +
                "actionMap = {};\n" +
                "actionMap.value = valueMap;\n" +
                "actionMap.actionName = \"filter\" ;\n" +
                "result = {}; \n" +
                "result.action = actionMap;\n" +
                "result.fieldId ="+labourFormFieldId+";\n" +
                "resultList.add(result);\n" +

                "}\n" +
//                "if(formData.skill == null ){\n" +
//                "actionMap2 = {};\n" +
//                "actionMap2.value = null;\n" +
//                "actionMap2.actionName = \"set\" ;\n" +
//                "result2 = {};\n" +
//                "result2.action = actionMap2;\n" +
//                "result2.fieldId = "+labourFormFieldId+";\n" +
//                "resultList.add(result2);\n" +
//                "}\n" +
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
    public void addRuleForWorkOrderLabour(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {

        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Compute Cost");
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));
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


        singleRule.setTriggerFields(triggerFieldList);

        long costFormFieldId = formFieldMap.get(fieldMap.get("totalAmount").getId()).getId();

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.EXECUTE_SCRIPT.getVal());
        String workflowString = "List getActions(Map formData) {\n" +
                "result = [];\n" +
                "if(formData.duration != null ){\n" +
                "duration = formData.duration;\n" +
                "duartionInHours = new NameSpace(\"date\").secToHour(duration);\n" +
                "}\n" +
                "if(formData.duration == null ){\n" +
                "duartionInHours = 0;\n" +
                "}\n" +
                "rate= formData.rate;\n" +
                "currencyValue = rate.currencyValue;\n"+
                "ratePerHours = new NameSpace(\"number\").intValue(currencyValue);\n" +
                "if(ratePerHours != null ){\n" +
                "ratePerHour = ratePerHours;\n" +
                "}\n" +
                "if(ratePerHours == null ){\n" +
                "ratePerHour = 0;\n" +
                "}\n" +
                "cost = duartionInHours*ratePerHour;\n" +
                "valueMap = {};\n"+
                "rates= formData.rate;\n"+
                "code = rates.currencyCode;\n"+
                "if(code != null ){\n" +
                "valueMap.currencyCode = code;\n" +
                "}\n" +
                "else if( code == null){\n" +
                "valueMap.currencyCode = null;\n" +
                "}\n" +
                "if(cost != null ){\n" +
                "valueMap.currencyValue = cost;\n" +
                "}\n" +
                "else if( cost == null){\n" +
                "valueMap.currencyValue = 0;\n" +
                "}\n" +
                "temp = {};\n" +
                "action = {};\n" +
                "action.actionName = \"set\";\n" +
                "action.value = valueMap;\n" +
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
    private void addFieldDisableRule(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception{
        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Fields Disable");
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
        singleRule.setFormId(defaultForm.getId());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());


        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionContext disableAction = new FormRuleActionContext();
        disableAction.setActionType(FormActionType.DISABLE_FIELD.getVal());

        List<FormRuleActionFieldsContext> fieldList = new ArrayList<>();

        FormRuleActionFieldsContext craftField = new FormRuleActionFieldsContext();
        craftField.setFormFieldId(formFieldMap.get(fieldMap.get("craft").getId()).getId());
        fieldList.add(craftField);

        FormRuleActionFieldsContext skillField = new FormRuleActionFieldsContext();
        skillField.setFormFieldId(formFieldMap.get(fieldMap.get("skill").getId()).getId());
        fieldList.add(skillField);

        FormRuleActionFieldsContext rateField = new FormRuleActionFieldsContext();
        rateField.setFormFieldId(formFieldMap.get(fieldMap.get("rate").getId()).getId());
        fieldList.add(rateField);

        FormRuleActionFieldsContext totalAmount = new FormRuleActionFieldsContext();
        totalAmount.setFormFieldId(formFieldMap.get(fieldMap.get("totalAmount").getId()).getId());
        fieldList.add(totalAmount);


        disableAction.setFormRuleActionFieldsContext(fieldList);

        actions.add(disableAction);

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
        ArrayList<FacilioView> workOrderLabourModule = new ArrayList<FacilioView>();
        workOrderLabourModule.add(getWorkOrderActualLabourViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.WO_LABOUR);
        groupDetails.put("views", workOrderLabourModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getWorkOrderActualLabourViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "workorderLabour.ID", FieldType.NUMBER), true));

        FacilioView workorderActualLabourView = new FacilioView();
        workorderActualLabourView.setName("all");
        workorderActualLabourView.setDisplayName("WorkOrder Labour Actuals");

        workorderActualLabourView.setModuleName(FacilioConstants.ContextNames.WO_LABOUR);
        workorderActualLabourView.setSortFields(sortFields);

        List<ViewField> workorderLabourViewFields = new ArrayList<>();

        workorderLabourViewFields.add(new ViewField("labour","Labour"));
        workorderLabourViewFields.add(new ViewField("type","Type"));
        workorderLabourViewFields.add(new ViewField("startTime","Start Time"));
        workorderLabourViewFields.add(new ViewField("endTime","End TIme"));
        workorderLabourViewFields.add(new ViewField("duration","Duration"));
        workorderLabourViewFields.add(new ViewField("rate","Rate per Hour"));
        workorderLabourViewFields.add(new ViewField("totalAmount","Total Amount"));

        workorderActualLabourView.setFields(workorderLabourViewFields);

        return workorderActualLabourView;
    }
}
