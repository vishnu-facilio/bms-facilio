package com.facilio.remotemonitoring.compute;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.controller.Controller;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.tasker.FacilioTimer;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.wmsv2.constants.Topics;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class FlaggedEventUtil {
    public static FlaggedEventRuleContext getMatchingFlaggedEventRule(FilteredAlarmContext filteredAlarm) throws Exception {
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        if (filteredAlarm != null && filteredAlarm.getClient() != null) {
            List<FlaggedEventRuleContext> flaggedEventRuleList = alarmBean.getFlaggedEventRulesForClient(filteredAlarm.getClient().getId());
            if (CollectionUtils.isNotEmpty(flaggedEventRuleList)) {
                for (FlaggedEventRuleContext flaggedEventRule : flaggedEventRuleList) {
                    List<FlaggedEventRuleAlarmTypeRel> flaggedEventRuleAlarmTypeRelList = flaggedEventRule.getFlaggedEventRuleAlarmTypeRel();
                    if (CollectionUtils.isNotEmpty(flaggedEventRuleAlarmTypeRelList)) {
                        for (FlaggedEventRuleAlarmTypeRel flaggedEventRuleAlarmTypeRel : flaggedEventRuleAlarmTypeRelList) {
                            AlarmTypeContext alarmType = flaggedEventRuleAlarmTypeRel.getAlarmType();
                            if (alarmType != null && filteredAlarm.getAlarmType() != null) {
                                if (alarmType.getId() == filteredAlarm.getAlarmType().getId()) {
                                    Criteria siteCriteria = flaggedEventRule.getSiteCriteria();
                                    Criteria controllerCriteria = flaggedEventRule.getControllerCriteria();
                                    boolean siteCriteriaMatched = true;
                                    boolean controllerCriteriaMatched = true;
                                    if (siteCriteria != null) {
                                        siteCriteriaMatched = false;
                                        if (filteredAlarm.getSite() != null) {
                                            Map<String, Object> siteProp = FieldUtil.getAsProperties(filteredAlarm.getSite());
                                            siteCriteriaMatched = siteCriteria.computePredicate(siteProp).evaluate(siteProp);
                                        }
                                    }
                                    if (controllerCriteria != null) {
                                        controllerCriteriaMatched = false;
                                        if (filteredAlarm.getSite() != null) {
                                            Map<String, Object> controllerProp = FieldUtil.getAsProperties(filteredAlarm.getController());
                                            controllerCriteriaMatched = controllerCriteria.computePredicate(controllerProp).evaluate(controllerProp);
                                        }
                                    }
                                    if (siteCriteriaMatched && controllerCriteriaMatched) {
                                        return flaggedEventRule;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void pushToStormFlaggedEventQueue(FilteredAlarmContext filteredAlarm) throws Exception {
        if (AccountUtil.getCurrentOrg() != null) {
            long orgId = AccountUtil.getCurrentOrg().getId();
            MessageQueue queue = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());
            JSONObject input = new JSONObject();
            input.put("orgId", orgId);
            input.put("filterAlarm", FacilioUtil.getAsJSON(filteredAlarm));
            queue.put(getMessageProcessingTopicName(), new FacilioRecord(String.valueOf(orgId), input));
        }
    }

    private static String getMessageProcessingTopicName() {
        return FacilioProperties.getStableEnvironment() + "-rm-flagged-event-queue";
    }

    public static FlaggedEventContext addFlaggedEventForFilteredAlarm(FilteredAlarmContext filteredAlarm, FlaggedEventRuleContext flaggedEventRule) throws Exception {
        FlaggedEventContext flaggedEventContext = new FlaggedEventContext();
        flaggedEventContext.setName(filteredAlarm.getMessage());
        flaggedEventContext.setController(filteredAlarm.getController());
        flaggedEventContext.setFlaggedEventRule(flaggedEventRule);
        flaggedEventContext.setStatus(FlaggedEventContext.FlaggedEventStatus.OPEN);
        flaggedEventContext.setClient(filteredAlarm.getClient());
        FacilioContext createContext = V3Util.preCreateRecord(FlaggedEventModule.MODULE_NAME, Collections.singletonList(FieldUtil.getAsProperties(flaggedEventContext)), null, null);
        List<FlaggedEventContext> records = Constants.getRecordList(createContext);
        if (CollectionUtils.isNotEmpty(records)) {
            FlaggedEventContext event = records.get(0);
            postCreateFlaggedEventIMS(event);
            return event;
        }
        return null;
    }

    public static void postCreateFlaggedEventIMS(FlaggedEventContext flaggedEvent) throws Exception {
        if (AccountUtil.getCurrentOrg() != null) {
            Messenger.getMessenger().sendMessage(new Message()
                    .setKey(Topics.FlaggedEventCreation.flaggedEventTopic + "/" + AccountUtil.getCurrentOrg().getId())
                    .setOrgId(AccountUtil.getCurrentOrg().getId())
                    .setContent(FieldUtil.getAsJSON(flaggedEvent)));
        }
    }

    public static void checkAndCreateWorkorderForFlaggedEvent(Long flaggedEventId) throws Exception {
        checkAndCreateWorkorderForFlaggedEvent(flaggedEventId, false);
    }

    public static void checkAndCreateWorkorderForFlaggedEvent(Long flaggedEventId, boolean manualAction) throws Exception {
        if (flaggedEventId != null && flaggedEventId > -1) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Object record = V3Util.getRecord(FlaggedEventModule.MODULE_NAME, flaggedEventId, null);
            FlaggedEventContext flaggedEventContext = FieldUtil.getAsBeanFromJson(FieldUtil.getAsJSON(record), FlaggedEventContext.class);
            AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
            Map<String, Object> workorderProp = null;
            boolean createWO = false;
            if (flaggedEventContext != null && flaggedEventContext.getFlaggedEventRule() != null) {
                if (!(flaggedEventContext.getStatus() == null || (flaggedEventContext.getStatus() != null && flaggedEventContext.getStatus() == FlaggedEventContext.FlaggedEventStatus.OPEN))) {
                    return;
                }
                FlaggedEventRuleContext flaggedEventRule = alarmBean.getFlaggedEventRule(flaggedEventContext.getFlaggedEventRule().getId());
                if (flaggedEventRule != null && flaggedEventRule.shouldCreateWorkorder()) {
                    createWO = true;
                    List<FlaggedEventWorkorderFieldMappingContext> fieldMapping = flaggedEventRule.getFieldMapping();
                    Map<String, Object> flaggedEventProp = FieldUtil.getAsProperties(flaggedEventContext);
                    if (CollectionUtils.isNotEmpty(fieldMapping)) {
                        workorderProp = new HashMap<>();
                        for (FlaggedEventWorkorderFieldMappingContext mapping : fieldMapping) {
                            FacilioField woField = modBean.getField(mapping.getLeftFieldId(), FacilioConstants.ContextNames.WORK_ORDER);
                            if (
                                    woField.getDataTypeEnum() == FieldType.STRING ||
                                            woField.getDataTypeEnum() == FieldType.NUMBER ||
                                            woField.getDataTypeEnum() == FieldType.BIG_STRING ||
                                            woField.getDataTypeEnum() == FieldType.DECIMAL ||
                                            woField.getDataTypeEnum() == FieldType.LARGE_TEXT) {
                                String replacedString = WorkflowRuleAPI.replacePlaceholders(FlaggedEventModule.MODULE_NAME, flaggedEventContext, mapping.getValueText());
                                workorderProp.put(woField.getName(), replacedString);
                            } else {
                                if (woField.getDataTypeEnum() == FieldType.LOOKUP) {
                                    if (mapping.getRightFieldId() != null) {
                                        FacilioField flaggedEventField = modBean.getField(mapping.getRightFieldId(), FlaggedEventModule.MODULE_NAME);
                                        if (flaggedEventField != null) {
                                            Map<String, Object> prop = (Map<String, Object>) flaggedEventProp.get(flaggedEventField.getName());
                                            if (MapUtils.isNotEmpty(prop) && prop.containsKey("id")) {
                                                Map<String, Object> insertProp = new HashMap<>();
                                                insertProp.put("id", prop.get("id"));
                                                workorderProp.put(woField.getName(), insertProp);
                                            }
                                        }
                                    }
                                } else if (StringUtils.isNotEmpty(mapping.getValueText())) {
                                    Long value = Long.parseLong(mapping.getValueText());
                                    workorderProp.put(woField.getName(), value);
                                }
                            }
                        }
                    }
                    if (flaggedEventRule.getWorkorderTemplateId() != null) {
                        if (workorderProp == null) {
                            workorderProp = new HashMap<>();
                        }
                        workorderProp.put("formId", flaggedEventRule.getWorkorderTemplateId());
                    }
                }
            }
            if (createWO) {
                if (workorderProp == null) {
                    workorderProp = new HashMap<>();
                }
                Controller controller = V3RecordAPI.getRecord(FacilioConstants.ContextNames.CONTROLLER, flaggedEventContext.getController().getId());
                if (controller != null) {
                    workorderProp.put("siteId", controller.getSiteId());
                    ResourceContext resource = new ResourceContext();
                    resource.setId(controller.getSiteId());
                    workorderProp.put("resource", FieldUtil.getAsProperties(resource));
                }
                if (!workorderProp.containsKey("subject")) {
                    workorderProp.put("subject", flaggedEventContext.getName());
                }
                workorderProp.put("flaggedEvent", ImmutableMap.of("id", flaggedEventId));
                FacilioContext context = V3Util.createRecord(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER), workorderProp);
                if (context != null) {
                    Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(Constants.RECORD_MAP);
                    if (MapUtils.isNotEmpty(recordMap)) {
                        List<ModuleBaseWithCustomFields> records = recordMap.get(FacilioConstants.ContextNames.WORK_ORDER);
                        if (CollectionUtils.isNotEmpty(records)) {
                            ModuleBaseWithCustomFields woRecord = records.get(0);
                            Long workorderId = woRecord.getId();
                            if (workorderId != null) {
                                Map<String, Object> updateMap = new HashMap<>();
                                updateMap.put("workorder", ImmutableMap.of("id", workorderId));
                                updateMap.put("status", FlaggedEventContext.FlaggedEventStatus.WORKORDER_CREATED.getIndex());
                                V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, updateMap, Collections.singletonList(flaggedEventId), false);
                                if (flaggedEventContext.getCurrentBureauActionDetail() != null && manualAction) {
                                    changeFlaggedEventActionStatus(flaggedEventContext.getCurrentBureauActionDetail().getId(), FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.ACTION_TAKEN);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void changeFlaggedEventActionStatus(Long actionId, FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus status) throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("eventStatus", status.getIndex());
        V3Util.updateBulkRecords(FlaggedEventBureauActionModule.MODULE_NAME, updateMap, Collections.singletonList(actionId), false);
    }

    public static void changeFlaggedEventStatus(Long flaggedEventId, FlaggedEventContext.FlaggedEventStatus status) throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("status", status.getIndex());
        V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, updateMap, Collections.singletonList(flaggedEventId), false);
    }

    public static FlaggedEventContext getFlaggedEvent(Long id) throws Exception {
        return V3RecordAPI.getRecord(FlaggedEventModule.MODULE_NAME, id, FlaggedEventContext.class);
    }

    public static FlaggedEventContext getOpenFlaggedEvent(Long controllerId, Long flaggedEventRuleId, Long clientId) throws Exception {
        if (controllerId != null && flaggedEventRuleId != null && clientId != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", String.valueOf(controllerId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE", "flaggedEventRule", String.valueOf(flaggedEventRuleId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", StringUtils.join(Arrays.asList(FlaggedEventContext.FlaggedEventStatus.CLEARED.name(), FlaggedEventContext.FlaggedEventStatus.AUTO_CLOSED.name()), ","), StringOperators.ISN_T));
            criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID", "client", String.valueOf(clientId), NumberOperators.EQUALS));
            SelectRecordsBuilder<FlaggedEventContext> selectRecordsBuilder = new SelectRecordsBuilder<FlaggedEventContext>()
                    .select(modBean.getAllFields(FlaggedEventModule.MODULE_NAME))
                    .module(modBean.getModule(FlaggedEventModule.MODULE_NAME))
                    .beanClass(FlaggedEventContext.class)
                    .andCriteria(criteria)
                    .orderBy("SYS_CREATED_TIME desc");
            return selectRecordsBuilder.fetchFirst();
        }
        return null;
    }

    public static boolean shouldCreateFlaggedEvent(FilteredAlarmContext filteredAlarm, FlaggedEventRuleContext flaggedEventRule) throws Exception {
        List<Long> alarmTypeIds = null;
        List<FlaggedEventRuleAlarmTypeRel> flaggedEventRuleAlarmTypeRels = flaggedEventRule.getFlaggedEventRuleAlarmTypeRel();
        if (CollectionUtils.isNotEmpty(flaggedEventRuleAlarmTypeRels)) {
            List<AlarmTypeContext> alarmTypes = flaggedEventRuleAlarmTypeRels.stream().map(FlaggedEventRuleAlarmTypeRel::getAlarmType).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(alarmTypes)) {
                alarmTypeIds = alarmTypes.stream().map(AlarmTypeContext::getId).collect(Collectors.toList());
            }
        }
        List<FilteredAlarmContext> openFilteredAlarms = getOpenFilteredAlarmsForFlaggedRule(filteredAlarm);
        if (CollectionUtils.isNotEmpty(openFilteredAlarms)) {
            for (FilteredAlarmContext filterAlarm : openFilteredAlarms) {
                if (filterAlarm.getAlarmType() != null && alarmTypeIds != null && alarmTypeIds.contains(filterAlarm.getAlarmType().getId())) {
                    alarmTypeIds.remove(filterAlarm.getAlarmType().getId());
                }
            }
        }
        if (CollectionUtils.isEmpty(alarmTypeIds)) {
            return true;
        }
        return false;
    }

    private static List<FilteredAlarmContext> getOpenFilteredAlarmsForFlaggedRule(FilteredAlarmContext filteredAlarm) throws Exception {
        if (filteredAlarm != null && filteredAlarm.getClient() != null && filteredAlarm.getAlarmType() != null && filteredAlarm.getController() != null && filteredAlarm.getFlaggedEventRule() != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            FilteredAlarmContext lastClearedFilterAlarm = getLastClearedAlarm(filteredAlarm);
            if (lastClearedFilterAlarm != null) {
                criteria.addAndCondition(CriteriaAPI.getCondition("OCCURRED_TIME", "occurredTime", String.valueOf(lastClearedFilterAlarm.getClearedTime()), NumberOperators.GREATER_THAN));
            }
            criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID", "clientId", String.valueOf(filteredAlarm.getClient().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE", "alarmType", String.valueOf(filteredAlarm.getAlarmType().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", String.valueOf(filteredAlarm.getController().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE", "flaggedEventRule", String.valueOf(filteredAlarm.getFlaggedEventRule().getId()), NumberOperators.EQUALS));

            SelectRecordsBuilder<FilteredAlarmContext> selectRecordsBuilder = new SelectRecordsBuilder<FilteredAlarmContext>()
                    .select(modBean.getAllFields(FilteredAlarmModule.MODULE_NAME))
                    .module(modBean.getModule(FilteredAlarmModule.MODULE_NAME))
                    .beanClass(FilteredAlarmContext.class)
                    .andCriteria(criteria);
            return selectRecordsBuilder.get();
        }
        return null;
    }

    private static FilteredAlarmContext getLastClearedAlarm(FilteredAlarmContext filteredAlarm) throws Exception {
        if (filteredAlarm != null && filteredAlarm.getClient() != null && filteredAlarm.getFlaggedEventRule() != null && filteredAlarm.getAlarmType() != null && filteredAlarm.getController() != null && filteredAlarm.getFlaggedEventRule() != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID", "clientId", String.valueOf(filteredAlarm.getClient().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "", CommonOperators.IS_NOT_EMPTY));
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE", "alarmType", String.valueOf(filteredAlarm.getAlarmType().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", String.valueOf(filteredAlarm.getController().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE", "flaggedEventRule", String.valueOf(filteredAlarm.getFlaggedEventRule().getId()), NumberOperators.EQUALS));

            SelectRecordsBuilder<FilteredAlarmContext> selectRecordsBuilder = new SelectRecordsBuilder<FilteredAlarmContext>()
                    .select(modBean.getAllFields(FilteredAlarmModule.MODULE_NAME))
                    .module(modBean.getModule(FilteredAlarmModule.MODULE_NAME))
                    .beanClass(FilteredAlarmContext.class)
                    .andCriteria(criteria)
                    .orderBy("CLEARED_TIME desc");
            FilteredAlarmContext lastClearedFilteredAlarm = selectRecordsBuilder.fetchFirst();
            return lastClearedFilteredAlarm;
        }
        return null;
    }


    public static Long addEmailRule(WorkflowRuleContext emailRule, String ruleName, Criteria criteria) throws Exception {
        emailRule.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        emailRule.setScheduleType(WorkflowRuleContext.ScheduledRuleType.AFTER);
        WorkflowEventContext workflowEventContext = new WorkflowEventContext();
        workflowEventContext.setActivityType(EventType.CREATE);
        workflowEventContext.setModuleName(FlaggedEventModule.MODULE_NAME);
        emailRule.setEvent(workflowEventContext);
        emailRule.setCriteria(criteria);
        emailRule.setName(ruleName);
        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext ruleContext = chain.getContext();
        ruleContext.put(FacilioConstants.ContextNames.MODULE_NAME, FlaggedEventModule.MODULE_NAME);
        ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, emailRule);
        chain.execute();
        Long ruleId = (Long) ruleContext.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
        return ruleId;
    }

    public static Long addScheduledEmailRule(WorkflowRuleContext emailRule, String ruleName, Criteria criteria, Long delay) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        emailRule.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        emailRule.setScheduleType(WorkflowRuleContext.ScheduledRuleType.AFTER);
        WorkflowEventContext workflowEventContext = new WorkflowEventContext();
        workflowEventContext.setActivityType(EventType.SCHEDULED);
        workflowEventContext.setModuleName(FlaggedEventModule.MODULE_NAME);
        emailRule.setEvent(workflowEventContext);
        emailRule.setCriteria(criteria);
        emailRule.setName(ruleName);
        FacilioField dateField = modBean.getField("sysCreatedTime", FlaggedEventModule.MODULE_NAME);
        emailRule.setDateField(dateField);
        emailRule.setDateFieldId(dateField.getId());
        emailRule.setInterval(delay);
        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext ruleContext = chain.getContext();
        ruleContext.put(FacilioConstants.ContextNames.MODULE_NAME, FlaggedEventModule.MODULE_NAME);
        ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, emailRule);
        chain.execute();
        Long ruleId = (Long) ruleContext.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
        return ruleId;
    }

    public static boolean passToNextBureau(FlaggedEventContext event, FlaggedEventBureauActionsContext bureauAction) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT", "flaggedEvent", String.valueOf(event.getId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(FlaggedEventBureauActionModule.MODULE_NAME)), String.valueOf(bureauAction.getId()), NumberOperators.GREATER_THAN));
        List<FlaggedEventBureauActionsContext> actionsList = V3RecordAPI.getRecordsListWithSupplements(FlaggedEventBureauActionModule.MODULE_NAME, null, FlaggedEventBureauActionsContext.class, criteria, null);
        FlaggedEventBureauActionsContext changeActionStatus = new FlaggedEventBureauActionsContext();
        changeActionStatus.setId(bureauAction.getId());
        if (CollectionUtils.isNotEmpty(actionsList)) {
            changeActionStatus.setEventStatus(FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.PASSED_TO_NEXT_BUREAU);
        } else {
            changeActionStatus.setEventStatus(FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.TIME_OUT);
        }
        V3RecordAPI.updateRecord(changeActionStatus, modBean.getModule(FlaggedEventBureauActionModule.MODULE_NAME), Collections.singletonList(modBean.getField("eventStatus", FlaggedEventBureauActionModule.MODULE_NAME)));
        if (CollectionUtils.isNotEmpty(actionsList)) {
            FlaggedEventBureauActionsContext action = actionsList.get(0);
            if (action != null) {
                FlaggedEventContext updateEvent = new FlaggedEventContext();
                updateEvent.setTeam(action.getTeam());
                updateEvent.setCurrentBureauActionDetail(action);
                updateEvent.setAssignedPeople(null);
                updateEvent.setId(event.getId());
                V3RecordAPI.updateRecord(updateEvent, modBean.getModule(FlaggedEventModule.MODULE_NAME), Arrays.asList(modBean.getField("team", FlaggedEventModule.MODULE_NAME), modBean.getField("currentBureauActionDetail", FlaggedEventModule.MODULE_NAME),modBean.getField("assignedPeople", FlaggedEventModule.MODULE_NAME)));
                FlaggedEventUtil.updateBureauActionStatus(action.getId(), FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.OPEN);
                Long nextExecutionTime = (System.currentTimeMillis() + action.getTakeCustodyPeriod()) / 1000;
                FacilioTimer.scheduleOneTimeJobWithTimestampInSec(action.getId(), RemoteMonitorConstants.FLAGGED_EVENT_BUREAU_TAKE_CUSTODY_JOB, nextExecutionTime, "priority");
                return true;
            }
        }
        return false;
    }

    public static FlaggedEventRuleBureauEvaluationContext getLiveBureauEvaluation(Long flaggedEventId) throws Exception {
        if (flaggedEventId != null) {
            List<FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus> activeStatus = FlaggedEventBureauActionsContext.FlaggedEventBureauActionState.ACTIVE.getStatuses();
            List<String> values = activeStatus.stream().map(FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus::name).collect(Collectors.toList());
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule flaggedEventActionModule = modBean.getModule(FlaggedEventBureauActionModule.MODULE_NAME);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE", "flaggedEventRule", String.valueOf(flaggedEventId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("EVENT_STATUS", "eventStatus", StringUtils.join(values, ","), NumberOperators.EQUALS));
            List<FlaggedEventBureauActionsContext> bureauEvaluationList = V3RecordAPI.getRecordsListWithSupplements(flaggedEventActionModule.getName(), null, FlaggedEventBureauActionsContext.class, criteria, null, FieldFactory.getIdField(flaggedEventActionModule).getCompleteColumnName(), "asc");
            if (CollectionUtils.isNotEmpty(bureauEvaluationList)) {
                return bureauEvaluationList.get(0);
            }
        }
        return null;
    }

    public static FlaggedEventBureauActionsContext getNextBureauEvaluation(Long flaggedEventId, Long currentBureauActionId) throws Exception {
        if (flaggedEventId != null) {
            List<FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus> activeStatus = FlaggedEventBureauActionsContext.FlaggedEventBureauActionState.ACTIVE.getStatuses();
            List<String> values = activeStatus.stream().map(FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus::name).collect(Collectors.toList());
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule flaggedEventActionModule = modBean.getModule(FlaggedEventBureauActionModule.MODULE_NAME);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT", "flaggedEvent", String.valueOf(flaggedEventId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("EVENT_STATUS", "eventStatus", StringUtils.join(values, ","), StringOperators.IS));
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(flaggedEventActionModule), String.valueOf(currentBureauActionId), NumberOperators.GREATER_THAN));
            List<FlaggedEventBureauActionsContext> bureauEvaluationList = V3RecordAPI.getRecordsListWithSupplements(flaggedEventActionModule.getName(), null, FlaggedEventBureauActionsContext.class, criteria, null, FieldFactory.getIdField(flaggedEventActionModule).getCompleteColumnName(), "asc");
            if (CollectionUtils.isNotEmpty(bureauEvaluationList)) {
                return bureauEvaluationList.get(0);
            }
        }
        return null;
    }

    public static FlaggedEventRuleClosureConfigContext getFlaggedEventClosureConfig(Long id) throws Exception {
        if (id != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<SupplementRecord> supplementRecords = new ArrayList<>();
            supplementRecords.add((SupplementRecord) modBean.getField("workorderStatuses", AddFlaggedEventClosureConfigModule.MODULE_NAME));
            supplementRecords.add((SupplementRecord) modBean.getField("workorderCloseCommandCriteria", AddFlaggedEventClosureConfigModule.MODULE_NAME));
            List<FlaggedEventRuleClosureConfigContext> closureConfigs = V3RecordAPI.getRecordsListWithSupplements(AddFlaggedEventClosureConfigModule.MODULE_NAME, Collections.singletonList(id), FlaggedEventRuleClosureConfigContext.class, null, supplementRecords, null, null);
            if (CollectionUtils.isNotEmpty(closureConfigs)) {
                FlaggedEventRuleClosureConfigContext closure = closureConfigs.get(0);
                closure.setFlaggedEventStatuses(getClosureFlaggedEventStatus(closure.getId()));
                return closure;
            }
        }
        return null;
    }

    public static void updateBureauActionStatus(Long id, FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus status) throws Exception {
        if (id != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FlaggedEventBureauActionsContext actionsContext = new FlaggedEventBureauActionsContext();
            actionsContext.setEventStatus(status);
            if (status == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.OPEN) {
                actionsContext.setEvaluationOpenTimestamp(System.currentTimeMillis());
            }
            actionsContext.setId(id);
            V3RecordAPI.updateRecord(actionsContext, modBean.getModule(FlaggedEventBureauActionModule.MODULE_NAME), Arrays.asList(modBean.getField("evaluationOpenTimestamp", FlaggedEventBureauActionModule.MODULE_NAME), modBean.getField("eventStatus", FlaggedEventBureauActionModule.MODULE_NAME)));
        }
    }

    public static void updateFlaggedEvent(FlaggedEventContext flaggedEvent, FilteredAlarmContext filteredAlarmContext) throws Exception {
        Map<String, Object> prop = new HashMap();
        prop.put("flaggedEvent", FieldUtil.getAsProperties(flaggedEvent));
        V3Util.updateBulkRecords("filteredAlarm", prop, Collections.singletonList(filteredAlarmContext.getId()), false);
    }

    public static void closeFlaggedEvent(Long flaggedEventId) throws Exception {
        closeFlaggedEvent(flaggedEventId, false);
    }

    public static void sendWorkorderClosureCommand(Long flaggedEventId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        FlaggedEventContext flaggedEvent = FlaggedEventUtil.getFlaggedEvent(flaggedEventId);
        if (flaggedEvent != null && flaggedEvent.getWorkorder() != null) {
            V3WorkOrderContext workorder = V3RecordAPI.getRecord(FacilioConstants.ContextNames.WORK_ORDER, flaggedEvent.getWorkorder().getId(), V3WorkOrderContext.class);
            if (workorder != null && workorder.getStatus() != null) {
                if (flaggedEvent.getFlaggedEventRule() != null) {
                    FlaggedEventRuleContext rule = alarmBean.getFlaggedEventRule(flaggedEvent.getFlaggedEventRule().getId());
                    if (rule != null && rule.getFlaggedEventRuleClosureConfig() != null && rule.getFlaggedEventRuleClosureConfig().getWorkorderCloseStatus() != null) {
                        List<FacilioStatus> workorderCloseCommandStatuses = rule.getFlaggedEventRuleClosureConfig().getWorkorderCloseCommandCriteria();
                        if (CollectionUtils.isNotEmpty(workorderCloseCommandStatuses)) {
                            for (FacilioStatus criteriaStatus : workorderCloseCommandStatuses) {
                                if (criteriaStatus != null && criteriaStatus.getId() == workorder.getStatus().getId()) {
                                    FacilioChain chain = TransactionChainFactory.getHistoryUpdateChain();
                                    FacilioContext facilioContext = chain.getContext();
                                    facilioContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
                                    StateFlowRulesAPI.updateState(workorder, modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER), rule.getFlaggedEventRuleClosureConfig().getWorkorderCloseStatus(), false, facilioContext);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public static void closeFlaggedEvent(Long flaggedEventId,boolean isManualClose) throws Exception {
        closeFlaggedEvent(flaggedEventId,isManualClose,null);
    }
    public static void closeFlaggedEvent(Long flaggedEventId,boolean isManualClose,List<BureauCloseIssueReasonOptionContext> closeIssueReasons) throws Exception {
        if(flaggedEventId != null) {
            Map<String, Object> updateMap = new HashMap<>();
            if(isManualClose) {
                updateMap.put("status", FlaggedEventContext.FlaggedEventStatus.CLEARED.getIndex());
            } else {
                updateMap.put("status", FlaggedEventContext.FlaggedEventStatus.AUTO_CLOSED.getIndex());
            }
            updateMap.put("bureauCloseIssues",getCloseOptionProp(closeIssueReasons));
//            updateMap.put("currentBureauActionDetail", ImmutableMap.of("id", -1));
            V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, updateMap, Collections.singletonList(flaggedEventId), false);
            sendWorkorderClosureCommand(flaggedEventId);
            updateCurrentActingBureau(flaggedEventId);
        }
    }

    private static List<Map<String,Object>> getCloseOptionProp(List<BureauCloseIssueReasonOptionContext> closeOptionList) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<Map<String,Object>> props = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(closeOptionList)) {
            for(BureauCloseIssueReasonOptionContext closeOption : closeOptionList) {
                Map<String,Object> prop = ImmutableMap.of("id",closeOption.getId());
                props.add(prop);
            }
        }
        return props;
    }
    private static void updateCurrentActingBureau(Long flaggedEventId) throws Exception {
        if(flaggedEventId != null) {
            FlaggedEventContext flaggedEvent = V3RecordAPI.getRecord(FlaggedEventModule.MODULE_NAME,flaggedEventId,FlaggedEventContext.class);
            if(flaggedEvent != null && flaggedEvent.getCurrentBureauActionDetail() != null) {
                updateBureauActionStatus(flaggedEvent.getCurrentBureauActionDetail().getId(), FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.INACTIVE);
            }
        }
    }
    public static boolean isAllFlaggedEventAlarmsClosed(long flaggedEventId) throws Exception {
        FlaggedEventContext event = V3RecordAPI.getRecord(FlaggedEventModule.MODULE_NAME,flaggedEventId,FlaggedEventContext.class);
        if(event != null && event.getFlaggedEventRule() != null) {
            List<FlaggedEventRuleAlarmTypeRel> alarmTypes = RemoteMonitorUtils.getFlaggedEventRuleAlarmTypeRel(event.getFlaggedEventRule().getId());
            if(CollectionUtils.isNotEmpty(alarmTypes)) {
                for(FlaggedEventRuleAlarmTypeRel alarmType : alarmTypes) {
                    if(alarmType != null && alarmType.getAlarmType() != null) {
                        FilteredAlarmContext lastFilteredAlarm = getLastAlarmForFlaggedEvent(flaggedEventId, alarmType.getAlarmType().getId());
                        if (lastFilteredAlarm != null) {
                            if (lastFilteredAlarm.getClearedTime() != null && lastFilteredAlarm.getClearedTime() > 0) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
        return true;
    }


    private static FilteredAlarmContext getLastAlarmForFlaggedEvent(long flaggedEventId, Long alarmTypeId) throws Exception {
        if(flaggedEventId > -1 && alarmTypeId != null && alarmTypeId > -1) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT", "flaggedEvent",String.valueOf(flaggedEventId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE", "alarmType",String.valueOf(alarmTypeId), NumberOperators.EQUALS));
            SelectRecordsBuilder<FilteredAlarmContext> selectRecordsBuilder = new SelectRecordsBuilder<FilteredAlarmContext>()
                    .select(modBean.getAllFields(FilteredAlarmModule.MODULE_NAME))
                    .module(modBean.getModule(FilteredAlarmModule.MODULE_NAME))
                    .beanClass(FilteredAlarmContext.class)
                    .andCriteria(criteria)
                    .orderBy("SYS_CREATED_TIME desc");
            return selectRecordsBuilder.fetchFirst();
        }
        return null;
    }


    public static boolean allowClose(FlaggedEventContext event, FlaggedEventRuleClosureConfigContext closure) throws Exception {
        if(event != null) {
            if(event.getStatus() == FlaggedEventContext.FlaggedEventStatus.AUTO_CLOSED || event.getStatus() == FlaggedEventContext.FlaggedEventStatus.CLEARED) {
                return false;
            }
            if(event.getAssignedPeople() == null || event.getAssignedPeople().getId() != AccountUtil.getCurrentUser().getPeopleId()) {
                return false;
            }
            boolean clear = FlaggedEventUtil.isAllFlaggedEventAlarmsClosed(event.getId());
            if(closure.getClosureRestriction() == FlaggedEventRuleClosureConfigContext.ClosureRestriction.RESTRICT) {
                boolean allow = false;
                if(clear) {
                    allow = true;
                }
                return allow;
            } else {
                return true;
            }
        }
        return false;
    }

    public static List<FlaggedEventContext.FlaggedEventStatus> getClosureFlaggedEventStatus(Long parentId) throws Exception{
        if(parentId != null && parentId > -1) {
//            FlaggedEventContext.FlaggedEventStatus status = FlaggedEventContext.FlaggedEventStatus.valueOf("");
            List<FacilioField> fields = FieldFactory.getFlaggedEventClosureStatusFields();
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getFlaggedEventClosureStatusFields())
                    .table(ModuleFactory.getFlaggedEventClosureStatusModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(parentId),NumberOperators.EQUALS));
            List<Map<String, Object>> prop = selectRecordBuilder.get();
            List<FlaggedEventContext.FlaggedEventStatus> closureFlaggedEventStatus = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(prop)){
                prop.stream().forEach(i->{
                    String value =  (String) i.get("value");
                    FlaggedEventContext.FlaggedEventStatus status = FlaggedEventContext.FlaggedEventStatus.valueOf(value);
                    closureFlaggedEventStatus.add(status);
                });
                return closureFlaggedEventStatus;
            }
        }
        return null;
    }
}