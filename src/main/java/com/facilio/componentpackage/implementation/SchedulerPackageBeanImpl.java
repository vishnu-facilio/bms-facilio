package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkflowTemplate;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.context.WorkflowFieldType;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.workflowlog.context.WorkflowLogContext;
import com.facilio.workflows.command.SchedulerAPI;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class SchedulerPackageBeanImpl implements PackageBean<ScheduledWorkflowContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getSchedulerIdVsParentIds();
    }

    @Override
    public void convertToXMLComponent(ScheduledWorkflowContext component, XMLBuilder schedulerElement) throws Exception {
        WorkflowContext workflowContext = component.getWorkflowContext();
        schedulerElement.element(PackageConstants.NAME).text(component.getName());
        schedulerElement.element(PackageConstants.WorkFlowRuleConstants.LINK_NAME).text(component.getLinkName());
        schedulerElement.element(PackageConstants.OrgConstants.TIMEZONE).text(component.getTimeZone());
        schedulerElement.element(PackageConstants.GroupConstants.IS_ACTIVE).text(String.valueOf(component.getIsActive()));
        schedulerElement.element(PackageConstants.WorkFlowRuleConstants.START_TIME).text(String.valueOf(component.getStartTime()));
        schedulerElement.element(PackageConstants.WorkFlowRuleConstants.SCHEDULE_INFO).text(component.getScheduleJson());

        PackageBeanUtil.constructBuilderFromActionsList(component.getActions(),schedulerElement);

        XMLBuilder workflowElement = schedulerElement.element(PackageConstants.EmailConstants.WORKFLOW);
        workflowElement.element(PackageConstants.FunctionConstants.WORKFLOW_STRING).cData(workflowContext.getWorkflowV2String());
        workflowElement.element(PackageConstants.FunctionConstants.WORKFLOW_XML_STRING).cData(workflowContext.getWorkflowString());
        workflowElement.element(PackageConstants.FunctionConstants.UI_MODE)
                .text(workflowContext.getWorkflowUIMode() > -1 ? String.valueOf(workflowContext.getWorkflowUIMode()) : String.valueOf(-1));
        workflowElement.element(PackageConstants.FunctionConstants.TYPE)
                .text(workflowContext.getLogType() != null ? workflowContext.getLogType().name() : null);
        workflowElement.element(PackageConstants.FunctionConstants.RETURN_TYPE)
                .text(workflowContext.getReturnTypeEnum() != null ? workflowContext.getReturnTypeEnum().name() : null);
        workflowElement.element(PackageConstants.FunctionConstants.IS_LOG_NEEDED).text(String.valueOf(workflowContext.isLogNeeded()));
        workflowElement.element(PackageConstants.FunctionConstants.IS_V2).text(String.valueOf(workflowContext.isV2Script()));
        workflowElement.element(PackageConstants.FunctionConstants.RUN_AS_ADMIN).text(String.valueOf(workflowContext.isRunAsAdmin()));
    }

    @Override
    public void deleteComponentFromXML(List ids) throws Exception {

    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            XMLBuilder schedulerElement = idVsData.getValue();
            if (schedulerElement != null) {
                ScheduledWorkflowContext scheduledWorkflowContext = getSchedulerContextFromXML(schedulerElement);
                updateScheduler(scheduledWorkflowContext);
            }
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder schedulerElement = idVsData.getValue();
            if (schedulerElement != null) {
                ScheduledWorkflowContext scheduledWorkflowContext = getSchedulerContextFromXML(schedulerElement);

                Long newModuleId = addScheduler(scheduledWorkflowContext);
                uniqueIdentifierVsComponentId.put(idVsData.getKey(), newModuleId);
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map uniqueIdVsXMLData) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, String> validateComponentToCreate(List components) throws Exception {
        return null;
    }

    @Override
    public Map<Long, ScheduledWorkflowContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, ScheduledWorkflowContext> scheduleIdVsScheduleMap = new HashMap<>();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);

        List<Long> idsSubList;
        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);
            Criteria scheduleIdCriteria = new Criteria();
            FacilioField scheduleIdField = FieldFactory.getIdField(ModuleFactory.getScheduledWorkflowModule());
            scheduleIdCriteria.addAndCondition(CriteriaAPI.getCondition(scheduleIdField, idsSubList, NumberOperators.EQUALS));

            List<ScheduledWorkflowContext> schedulerList = getScheduledWorkflowContext(idsSubList);
            if (CollectionUtils.isNotEmpty(schedulerList)) {
                schedulerList.forEach(schedule -> scheduleIdVsScheduleMap.put(schedule.getId(), schedule));
            }

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());
        }

        return scheduleIdVsScheduleMap;
    }

    public static Map<Long,Long> getSchedulerIdVsParentIds() throws Exception {
        FacilioModule schedulerModule = ModuleFactory.getScheduledWorkflowModule();
        FacilioField schedulerField = FieldFactory.getIdField(schedulerModule);
        Map<Long,Long> schedulerIdVsParentIds = new HashMap<>();

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singletonList(schedulerField))
                .table(schedulerModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("IS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));
        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                schedulerIdVsParentIds.put((Long) prop.get("id"), -1L);
            }
        }
        return schedulerIdVsParentIds;
    }

    public static ScheduledWorkflowContext getSchedulerContextFromXML(XMLBuilder schedulerElement) throws Exception{
        ScheduledWorkflowContext scheduledWorkflowContext = new ScheduledWorkflowContext();
        scheduledWorkflowContext.setName(schedulerElement.getElement(PackageConstants.NAME).getText());
        scheduledWorkflowContext.setLinkName(schedulerElement.getElement(PackageConstants.WorkFlowRuleConstants.LINK_NAME).getText());
        scheduledWorkflowContext.setTimeZone(schedulerElement.getElement(PackageConstants.OrgConstants.TIMEZONE).getText());
        scheduledWorkflowContext.setIsActive(false);

        scheduledWorkflowContext.setStartTime(Long.parseLong(schedulerElement.getElement(PackageConstants.WorkFlowRuleConstants.START_TIME).getText()));
        String scheduleInfoString = schedulerElement.getElement(PackageConstants.WorkFlowRuleConstants.SCHEDULE_INFO).getText();
        scheduledWorkflowContext.setScheduleJson(scheduleInfoString);

        JSONParser parser = new JSONParser();
        ScheduleInfo scheduleInfo = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(scheduleInfoString),ScheduleInfo.class);
        scheduledWorkflowContext.setSchedule(scheduleInfo);

        List<ActionContext> actionList = PackageBeanUtil.constructActionContextsFromBuilder(schedulerElement);
        scheduledWorkflowContext.setActions(actionList);

        scheduledWorkflowContext.setWorkflowContext(constructWorkflowFromXML(schedulerElement));

        return scheduledWorkflowContext;
    }

    public static Long addScheduler(ScheduledWorkflowContext scheduledWorkflowContext) throws Exception {
        Map<String, Object> prop = FieldUtil.getAsProperties(scheduledWorkflowContext);
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getScheduledWorkflowModule().getTableName())
                .fields(FieldFactory.getScheduledWorkflowFields())
                .addRecord(prop);

        insertRecordBuilder.save();

        if(prop.containsKey("id")) {
            scheduledWorkflowContext.setId((Long)prop.get("id"));
        }

        addActionsForScheduler(scheduledWorkflowContext);

        return scheduledWorkflowContext.getId();
    }

    public static Long updateScheduler(ScheduledWorkflowContext scheduledWorkflowContext) throws Exception {
        FacilioTimer.deleteJob(scheduledWorkflowContext.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME);
        SchedulerAPI.deleteAllActions(scheduledWorkflowContext.getId());

        if (scheduledWorkflowContext.getStartTime() < DateTimeUtil.getCurrenTime()) {
            scheduledWorkflowContext.setStartTime(DateTimeUtil.getCurrenTime());
        }

        GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
        update.table(ModuleFactory.getScheduledWorkflowModule().getTableName());
        update.fields(FieldFactory.getScheduledWorkflowFields())
                .andCondition(CriteriaAPI.getIdCondition(scheduledWorkflowContext.getId(), ModuleFactory.getScheduledWorkflowModule()));

        Map<String, Object> prop = FieldUtil.getAsProperties(scheduledWorkflowContext);
        update.update(prop);

        Boolean isActive = scheduledWorkflowContext.getIsActive();
        if(isActive != null && isActive){
            FacilioTimer.scheduleCalendarJob(scheduledWorkflowContext.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME, scheduledWorkflowContext.getStartTime(), scheduledWorkflowContext.getSchedule(), "facilio");
        }

        addActionsForScheduler(scheduledWorkflowContext);

        return scheduledWorkflowContext.getId();
    }

    public static void addActionsForScheduler(ScheduledWorkflowContext scheduledWorkflowContext) throws Exception{
        if (scheduledWorkflowContext.getWorkflowContext() != null) {
            ActionContext executeAction = new ActionContext();
            executeAction.setActionType(ActionType.WORKFLOW_ACTION);
            JSONObject templateJson = new JSONObject();
            templateJson.put("resultWorkflowContext", FieldUtil.getAsJSON(scheduledWorkflowContext.getWorkflowContext()));
            executeAction.setTemplateJson(templateJson);

            List<ActionContext> actions = new ArrayList<>();
            actions.add(executeAction);
            scheduledWorkflowContext.setActions(actions);
        }

        SchedulerAPI.addActions(scheduledWorkflowContext, true);
    }

    public static List<ScheduledWorkflowContext> getScheduledWorkflowContext(List<Long> scheduledWorkflowIds) throws Exception {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(FieldFactory.getScheduledWorkflowFields())
                .table(ModuleFactory.getScheduledWorkflowModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("id", "ID", StringUtils.join(scheduledWorkflowIds,","), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("IS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));

        List<Map<String, Object>> props = select.get();

        List<ScheduledWorkflowContext> scheduledWorkflowList = new ArrayList<>();
        if(props != null && !props.isEmpty()) {
            for(Map<String, Object> prop : props){
                ScheduledWorkflowContext scheduledWorkflowContext = FieldUtil.getAsBeanFromMap(prop, ScheduledWorkflowContext.class);
                JobContext job = FacilioTimer.getJob(scheduledWorkflowContext.getId(), "ScheduledWorkflow");

                if(job != null) {
                    long nextExecutionTime = job.getExecutionTime()*1000;
                    if(nextExecutionTime > DateTimeUtil.getCurrenTime()) {
                        scheduledWorkflowContext.setNextExecutionTime(nextExecutionTime);
                    }
                }

                SchedulerAPI.getSchedulerActions(Collections.singletonList(scheduledWorkflowContext));

                ActionContext scriptAction = scheduledWorkflowContext.getScriptAction();
                if (scriptAction != null) {
                    Template template = scriptAction.getTemplate();
                    if (template instanceof WorkflowTemplate) {
                        WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(((WorkflowTemplate) template).getResultWorkflowId());
                        scheduledWorkflowContext.setWorkflowContext(workflowContext);
                    }
                }

                scheduledWorkflowList.add(scheduledWorkflowContext);
            }
        }
        return scheduledWorkflowList;
    }

    public static WorkflowContext constructWorkflowFromXML(XMLBuilder schedulerElement){
        XMLBuilder workflowElement = schedulerElement.getElement(PackageConstants.EmailConstants.WORKFLOW);
        WorkflowContext workflowContext = new WorkflowContext();

        workflowContext.setWorkflowV2String(workflowElement.getElement(PackageConstants.FunctionConstants.WORKFLOW_STRING).getCData());
        workflowContext.setWorkflowString(workflowElement.getElement(PackageConstants.FunctionConstants.WORKFLOW_XML_STRING).getCData());
        workflowContext.setLogNeeded(Boolean.parseBoolean(workflowElement.getElement(PackageConstants.FunctionConstants.IS_LOG_NEEDED).getText()));
        workflowContext.setIsV2Script(Boolean.parseBoolean(workflowElement.getElement(PackageConstants.FunctionConstants.IS_V2).getText()));
        workflowContext.setRunAsAdmin(Boolean.parseBoolean(workflowElement.getElement(PackageConstants.FunctionConstants.RUN_AS_ADMIN).getText()));

        int uiModeStr = Integer.parseInt(workflowElement.getElement(PackageConstants.FunctionConstants.UI_MODE).getText());
        String typeStr = workflowElement.getElement(PackageConstants.FunctionConstants.TYPE).getText();
        String returnTypeStr = workflowElement.getElement(PackageConstants.FunctionConstants.RETURN_TYPE).getText();

        ScriptContext.WorkflowUIMode workflowUIMode = uiModeStr > 0 ? ScriptContext.WorkflowUIMode.valueOf(uiModeStr) : null;
        WorkflowLogContext.WorkflowLogType workflowLogType = StringUtils.isNotEmpty(typeStr) ? WorkflowLogContext.WorkflowLogType.valueOf(typeStr) : null;
        WorkflowFieldType returnType = StringUtils.isNotEmpty(returnTypeStr) ? WorkflowFieldType.valueOf(returnTypeStr) : null;

        workflowContext.setWorkflowUIMode(workflowUIMode);
        workflowContext.setLogType(workflowLogType);
        workflowContext.setReturnTypeEnum(returnType);

        return workflowContext;
    }
}
