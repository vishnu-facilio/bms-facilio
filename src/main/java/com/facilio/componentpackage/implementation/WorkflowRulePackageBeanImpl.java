package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.modules.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.lang3.StringUtils;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.facilio.chain.FacilioChain;
import com.facilio.beans.ModuleBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class WorkflowRulePackageBeanImpl implements PackageBean<WorkflowRuleContext> {
    private final List<Integer> SUPPORTED_ACTION_TYPES = new ArrayList<Integer>(){{
        add(ActionType.FIELD_CHANGE.getVal());
        add(ActionType.CHANGE_STATE.getVal());
        add(ActionType.WORKFLOW_ACTION.getVal());
    }};

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return WorkflowRuleAPI.getAllRuleIdVsModuleId(WorkflowRuleContext.RuleType.MODULE_RULE);
    }

    @Override
    public Map<Long, WorkflowRuleContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, WorkflowRuleContext> workFlowRules = WorkflowRuleAPI.getWorkFlowRules(WorkflowRuleContext.RuleType.MODULE_RULE, ids);
        // TODO - Filtering UnSupported ActionTypes - Do handle all ActionTypes
        Map<Long, WorkflowRuleContext> workFlowRulesToPackage = new HashMap<>();
        for (WorkflowRuleContext workflowRule : workFlowRules.values()) {
            List<ActionContext> actionContexts = workflowRule.getActions();
            if (CollectionUtils.isNotEmpty(actionContexts)) {
                List<ActionContext> actionContextsToPackage = actionContexts.stream().filter(actionContext -> SUPPORTED_ACTION_TYPES.contains(actionContext.getActionType())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(actionContextsToPackage)) {
                    workflowRule.setActions(actionContextsToPackage);
                    workFlowRulesToPackage.put(workflowRule.getId(), workflowRule);
                }
            }
        }
        return workFlowRulesToPackage;
    }

    @Override
    public void convertToXMLComponent(WorkflowRuleContext workflowRule, XMLBuilder builder) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        // TODO - Handle WORKFLOW_ID (no use), PARENT_RULE_ID, VERSION_GROUP_ID
        builder.element(PackageConstants.NAME).text(workflowRule.getName());
        builder.element(PackageConstants.DESCRIPTION).text(workflowRule.getDescription());
        builder.element(PackageConstants.MODULENAME).text(workflowRule.getModuleName());
        builder.element(PackageConstants.WorkFlowRuleConstants.ACTIVITY_TYPE)
                .text(workflowRule.getActivityTypeEnum() != null ? workflowRule.getActivityTypeEnum().name() : null);
        builder.element(PackageConstants.WorkFlowRuleConstants.EXECUTION_ORDER).text(String.valueOf(workflowRule.getExecutionOrder()));
        builder.element(PackageConstants.WorkFlowRuleConstants.STATUS).text(String.valueOf(workflowRule.isActive()));           // status
        builder.element(PackageConstants.WorkFlowRuleConstants.RULE_TYPE).text(workflowRule.getRuleTypeEnum().name());
        builder.element(PackageConstants.WorkFlowRuleConstants.ON_SUCCESS).text(String.valueOf(workflowRule.isOnSuccess()));    // onSuccess
        builder.element(PackageConstants.WorkFlowRuleConstants.VERSION_GROUP_ID).text(String.valueOf(workflowRule.getVersionGroupId()));
        builder.element(PackageConstants.WorkFlowRuleConstants.VERSION_NUMBER).text(String.valueOf(workflowRule.getVersionNumber()));
        builder.element(PackageConstants.WorkFlowRuleConstants.IS_LATEST_VERSION).text(String.valueOf(workflowRule.isLatestVersion()));
        builder.element(PackageConstants.WorkFlowRuleConstants.SCHEDULE_INFO).text(workflowRule.getScheduleJson());
        builder.element(PackageConstants.WorkFlowRuleConstants.SCHEDULE_TYPE)
                .text(workflowRule.getScheduleTypeEnum() != null ? workflowRule.getScheduleTypeEnum().name() : null);
        builder.element(PackageConstants.WorkFlowRuleConstants.TIME_INTERVAL).text(String.valueOf(workflowRule.getInterval()));
        builder.element(PackageConstants.WorkFlowRuleConstants.JOB_TIME).text(workflowRule.getTime());
        builder.element(PackageConstants.WorkFlowRuleConstants.LAST_SCHEDULE_RULE_EXECUTED_TIME).text(String.valueOf(workflowRule.getLastScheduleRuleExecutedTime()));

        if (workflowRule.getDateFieldId() > 0 && workflowRule.getDateField() != null) {
            builder.element(PackageConstants.WorkFlowRuleConstants.DATE_FIELD_NAME).text(workflowRule.getDateField().getName());
            builder.element(PackageConstants.WorkFlowRuleConstants.DATE_FIELD_MODULE_NAME).text(workflowRule.getDateField().getModule().getName());
        }

        // criteria
        if (workflowRule.getCriteria() != null) {
            LOGGER.info("####Sandbox Tracking - Parsing Criteria - ModuleName - " + workflowRule.getModuleName() + " WorkFlowRule - " + workflowRule.getName());
            builder.addElement(PackageBeanUtil.constructBuilderFromCriteria(workflowRule.getCriteria(), builder.element(PackageConstants.CriteriaConstants.CRITERIA), workflowRule.getModuleName()));
        }

        // actions
        if (CollectionUtils.isNotEmpty(workflowRule.getActions())) {
            PackageBeanUtil.constructBuilderFromActionsList(workflowRule.getActions(), builder.element(PackageConstants.WorkFlowRuleConstants.ACTIONS_LIST));
        }

        // EventType.FIELD_CHANGE (move to common handling)
        if (EventType.FIELD_CHANGE.isPresent(workflowRule.getActivityType()) && CollectionUtils.isNotEmpty(workflowRule.getFields())) {
            List<Long> fieldIds = workflowRule.getFields().stream().map(FieldChangeFieldContext::getFieldId).collect(Collectors.toList());
            List<FacilioField> fieldList = moduleBean.getFields(fieldIds);

            XMLBuilder fieldChangeFieldsElement = builder.element(PackageConstants.WorkFlowRuleConstants.FIELD_CHANGE_FIELDS);
            for (FacilioField field : fieldList) {
                fieldChangeFieldsElement.element(PackageConstants.WorkFlowRuleConstants.CHANGE_FIELD_NAME).text(field.getName());
            }
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        // no validation (since no linkName)
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        // no system WorkflowRules
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        // TODO - Handle ActivityType, EventType...
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();
        WorkflowRuleContext workflowRuleContext;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder builder = idVsData.getValue();
            workflowRuleContext = constructWorkflowRuleFromBuilder(builder, moduleBean);

            long ruleId = addRule(workflowRuleContext);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), ruleId);
        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        WorkflowRuleContext workflowRuleContext;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long ruleId = idVsData.getKey();
            if (ruleId == null || ruleId < 0) {
                continue;
            }

            XMLBuilder builder = idVsData.getValue();

            workflowRuleContext = constructWorkflowRuleFromBuilder(builder, moduleBean);
            workflowRuleContext.setId(ruleId);

            updateRule(workflowRuleContext);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            WorkflowRuleAPI.deleteWorkFlowRules(ids);
        }
    }

    private WorkflowRuleContext constructWorkflowRuleFromBuilder(XMLBuilder builder, ModuleBean moduleBean) throws Exception {
        String name = builder.getElement(PackageConstants.NAME).getText();
        String description = builder.getElement(PackageConstants.DESCRIPTION).getText();
        String moduleName = builder.getElement(PackageConstants.MODULENAME).getText();
        String activityTypeStr = builder.getElement(PackageConstants.WorkFlowRuleConstants.ACTIVITY_TYPE).getText();
        int executionOrder = Integer.parseInt(builder.getElement(PackageConstants.WorkFlowRuleConstants.EXECUTION_ORDER).getText());
        boolean status = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.STATUS).getText());
        String ruleTypeStr = builder.getElement(PackageConstants.WorkFlowRuleConstants.RULE_TYPE).getText();
        boolean onSuccess = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.ON_SUCCESS).getText());
        long versionGroupId = Long.parseLong(builder.getElement(PackageConstants.WorkFlowRuleConstants.VERSION_GROUP_ID).getText());
        int versionNumber = Integer.parseInt(builder.getElement(PackageConstants.WorkFlowRuleConstants.VERSION_NUMBER).getText());
        boolean isLatestVersion = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.IS_LATEST_VERSION).getText());
        String scheduleInfoJson = builder.getElement(PackageConstants.WorkFlowRuleConstants.SCHEDULE_INFO).getText();
        String scheduleTypeStr = builder.getElement(PackageConstants.WorkFlowRuleConstants.SCHEDULE_TYPE).getText();
        long interval = Long.parseLong(builder.getElement(PackageConstants.WorkFlowRuleConstants.TIME_INTERVAL).getText());
        String jobTime = builder.getElement(PackageConstants.WorkFlowRuleConstants.JOB_TIME).getText();
        long lastScheduleRuleExecutedTime = Long.parseLong(builder.getElement(PackageConstants.WorkFlowRuleConstants.LAST_SCHEDULE_RULE_EXECUTED_TIME).getText());

        FacilioModule module = StringUtils.isNotEmpty(moduleName) ? moduleBean.getModule(moduleName) : null;
        long moduleId = module != null ? module.getModuleId() : -1;
        EventType activityType = StringUtils.isNotEmpty(activityTypeStr) ? EventType.valueOf(activityTypeStr) : null;
        WorkflowRuleContext.RuleType ruleType = StringUtils.isNotEmpty(ruleTypeStr) ? WorkflowRuleContext.RuleType.valueOf(ruleTypeStr) : null;
        WorkflowRuleContext.ScheduledRuleType scheduleType = StringUtils.isNotEmpty(scheduleTypeStr) ? WorkflowRuleContext.ScheduledRuleType.valueOf(scheduleTypeStr) : null;

        WorkflowEventContext event = new WorkflowEventContext();
        event.setActivityType(activityType);
        event.setModuleName(moduleName);
        event.setModule(module);

        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext(name, description, moduleName, moduleId, executionOrder, status, onSuccess, versionGroupId, versionNumber, isLatestVersion,
                scheduleInfoJson, interval, jobTime, lastScheduleRuleExecutedTime, event, ruleType, scheduleType);

        if (builder.getElement(PackageConstants.WorkFlowRuleConstants.DATE_FIELD_NAME) != null) {
            String dateFieldName = builder.getElement(PackageConstants.WorkFlowRuleConstants.DATE_FIELD_NAME).getText();
            String dateFieldModuleName = builder.getElement(PackageConstants.WorkFlowRuleConstants.DATE_FIELD_MODULE_NAME).getText();
            FacilioField dateField = moduleBean.getField(dateFieldName, dateFieldModuleName);
            if (dateField != null) {
                workflowRuleContext.setDateField(dateField);
                workflowRuleContext.setDateFieldId(dateField.getFieldId());
            }
        }

        // criteria
        XMLBuilder criteriaElement = builder.getElement(PackageConstants.CriteriaConstants.CRITERIA);
        if (criteriaElement != null) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            workflowRuleContext.setCriteria(criteria);
        }


        // actions
        XMLBuilder actionsList = builder.getElement(PackageConstants.WorkFlowRuleConstants.ACTIONS_LIST);
        if (actionsList != null) {
            List<ActionContext> actionContextList = PackageBeanUtil.constructActionContextsFromBuilder(actionsList);
            workflowRuleContext.setActions(actionContextList);
        }

        // Field_Change_Fields
        XMLBuilder fieldChangeFieldsElement = builder.getElement(PackageConstants.WorkFlowRuleConstants.FIELD_CHANGE_FIELDS);
        if (EventType.FIELD_CHANGE.isPresent(workflowRuleContext.getActivityType()) && fieldChangeFieldsElement != null) {
            List<XMLBuilder> changeFieldNamesBuilder = fieldChangeFieldsElement.getFirstLevelElementListForTagName(PackageConstants.WorkFlowRuleConstants.CHANGE_FIELD_NAME);
            List<String> changeFieldNames = new ArrayList<>();
            for (XMLBuilder changeFieldBuilder : changeFieldNamesBuilder) {
                String fieldName = changeFieldBuilder.getText();
                if (StringUtils.isNotEmpty(fieldName)) {
                    changeFieldNames.add(fieldName);
                }
            }

            Criteria fieldNamCriteria = new Criteria();
            FacilioField fieldNameField = FieldFactory.getStringField("NAME", "name", ModuleFactory.getFieldsModule());
            fieldNamCriteria.addAndCondition(CriteriaAPI.getCondition(fieldNameField, StringUtils.join(changeFieldNames, ","), StringOperators.IS));
            List<FacilioField> moduleFields = moduleBean.getAllFields(moduleName, null, null, fieldNamCriteria);

            if (CollectionUtils.isNotEmpty(moduleFields)) {
                List<FieldChangeFieldContext> changeFieldContexts = new ArrayList<>();
                for (FacilioField field : moduleFields) {
                    FieldChangeFieldContext fieldChangeFieldContext = new FieldChangeFieldContext();
                    fieldChangeFieldContext.setFieldId(field.getFieldId());
                    changeFieldContexts.add(fieldChangeFieldContext);
                }
                workflowRuleContext.setFields(changeFieldContexts);
            } else {
                LOGGER.info("####Sandbox Tracking - FieldChangeFieldContext is empty - WorkflowRule - + " + name + " FieldObj not found moduleName - " + moduleName + " for fieldNames" + changeFieldNames);
            }
        }

        return workflowRuleContext;
    }

    private long addRule(WorkflowRuleContext workflowRuleContext) throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, workflowRuleContext.getModuleName());
        chain.execute();

        workflowRuleContext = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        return workflowRuleContext.getId();
    }

    private void updateRule(WorkflowRuleContext workflowRuleContext) throws Exception {
        FacilioChain chain = TransactionChainFactory.getUpdateModuleWorkflowRuleChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, workflowRuleContext.getModuleName());
        chain.execute();
    }
}
