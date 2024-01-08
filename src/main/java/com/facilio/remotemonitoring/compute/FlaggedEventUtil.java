package com.facilio.remotemonitoring.compute;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.FieldChangeFieldContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
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
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.handlers.ticketmodulecreate.TicketModuleRecordCreationHandler;
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
import com.facilio.workflowlog.context.WorkflowLogContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class FlaggedEventUtil {
    public static FlaggedEventRuleContext getMatchingFlaggedEventRule(FilteredAlarmContext filteredAlarm) throws Exception {
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

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
                                    boolean workflowCriteriaMatched = true;
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
                                    if (flaggedEventRule.getWorkflowId() != null && flaggedEventRule.getWorkflowId() > 0) {
                                        workflowCriteriaMatched = false;
                                        WorkflowContext workflow = WorkflowUtil.getWorkflowContext(flaggedEventRule.getWorkflowId());
                                        if (workflow != null) {
                                            List<Object> params = new ArrayList<>();
                                            params.add(FieldUtil.getAsProperties(filteredAlarm));
                                            workflow.setParams(params);
                                            workflow.setParentId(filteredAlarm.getId());
                                            workflow.setRecordId(filteredAlarm.getId());
                                            workflow.setRecordModuleId(modBean.getModule(FilteredAlarmModule.MODULE_NAME).getModuleId());
                                            workflow.setLogType(WorkflowLogContext.WorkflowLogType.MODULE_RULE);
                                            Boolean result = (Boolean) workflow.executeWorkflow();
                                            if (result == null) {
                                                workflowCriteriaMatched = false;
                                            } else {
                                                workflowCriteriaMatched = result;
                                            }
                                        }
                                    }
                                    if (siteCriteriaMatched && controllerCriteriaMatched && workflowCriteriaMatched) {
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
        long controllerId = -1;
        if (filteredAlarm.getController() != null) {
            controllerId = filteredAlarm.getController().getId();
        }
        if (AccountUtil.getCurrentOrg() != null) {
            long orgId = AccountUtil.getCurrentOrg().getId();
            MessageQueue queue = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());
            JSONObject input = new JSONObject();
            input.put("orgId", orgId);
            input.put("filterAlarm", FacilioUtil.getAsJSON(filteredAlarm));
            queue.put(getMessageProcessingTopicName(), new FacilioRecord(orgId + "#" + controllerId, input));
        }
    }

    private static String getMessageProcessingTopicName() {
        return FacilioProperties.getStableEnvironment() + "-rm-flagged-event-queue";
    }

    public static FlaggedEventContext addFlaggedEventForFilteredAlarm(FilteredAlarmContext filteredAlarm, FlaggedEventRuleContext flaggedEventRule) throws Exception {
        FlaggedEventContext flaggedEventContext = new FlaggedEventContext();
        flaggedEventContext.setName(filteredAlarm.getMessage());
        flaggedEventContext.setController(filteredAlarm.getController());
        flaggedEventContext.setFlaggedAlarmProcess(flaggedEventRule);
        flaggedEventContext.setStatus(FlaggedEventContext.FlaggedEventStatus.OPEN);
        flaggedEventContext.setClient(filteredAlarm.getClient());
        flaggedEventContext.setSite(filteredAlarm.getSite());
        flaggedEventContext.setAsset(filteredAlarm.getAsset());
        flaggedEventContext.setSourceType(FlaggedEventContext.FlaggedEventSourceType.EVENT);
        FacilioContext createContext = V3Util.preCreateRecord(FlaggedEventModule.MODULE_NAME, Collections.singletonList(FieldUtil.getAsProperties(flaggedEventContext)), null, null);
        List<FlaggedEventContext> records = Constants.getRecordList(createContext);
        if (CollectionUtils.isNotEmpty(records)) {
            FlaggedEventContext event = records.get(0);
            return event;
        }
        return null;
    }

    public static void postCreateFlaggedEventIMS(FlaggedEventContext flaggedEvent) throws Exception {
        boolean overrideVersion = !FacilioProperties.isDevelopment();
        if (AccountUtil.getCurrentOrg() != null) {
            Messenger.getMessenger().sendMessage(new Message()
                    .setKey(Topics.FlaggedEventCreation.flaggedEventTopic + "/" + AccountUtil.getCurrentOrg().getId())
                    .setOrgId(AccountUtil.getCurrentOrg().getId())
                    .setContent(FieldUtil.getAsJSON(flaggedEvent)), overrideVersion);
        }
    }

    public static void checkAndCreateWorkorderForFlaggedEvent(Long flaggedEventId) throws Exception {
        Object record = V3Util.getRecord(FlaggedEventModule.MODULE_NAME, flaggedEventId, null);
        FlaggedEventContext flaggedEventContext = FieldUtil.getAsBeanFromJson(FieldUtil.getAsJSON(record), FlaggedEventContext.class);
        if (flaggedEventContext != null && flaggedEventContext.getFlaggedAlarmProcess() != null) {
            AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
            FlaggedEventRuleContext flaggedEventRule = alarmBean.getFlaggedEventRule(flaggedEventContext.getFlaggedAlarmProcess().getId());
            if(BooleanUtils.isTrue(flaggedEventRule.getAutoCreateWorkOrder())){
                checkAndCreateWorkorderForFlaggedEvent(flaggedEventId, false);
            } else {
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put(FacilioConstants.ContextNames.STATUS, FlaggedEventContext.FlaggedEventStatus.TIMEOUT.getIndex());
                V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, updateMap, Collections.singletonList(flaggedEventId), false);
            }
        }

    }

    public static void checkAndCreateWorkorderForFlaggedEvent(Long flaggedEventId, boolean manualAction) throws Exception {
        checkAndCreateWorkorderForFlaggedEvent(flaggedEventId, manualAction, false);
    }

    public static void checkAndCreateWorkorderForFlaggedEvent(Long flaggedEventId, boolean manualAction, boolean skipStatusCheck) throws Exception {
        if (flaggedEventId != null && flaggedEventId > -1) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Object record = V3Util.getRecord(FlaggedEventModule.MODULE_NAME, flaggedEventId, null);
            FlaggedEventContext flaggedEventContext = FieldUtil.getAsBeanFromJson(FieldUtil.getAsJSON(record), FlaggedEventContext.class);
            AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
            Map<String, Object> workorderProp = null;
            if (flaggedEventContext != null && flaggedEventContext.getFlaggedAlarmProcess() != null) {
                if (!skipStatusCheck) {
                    if (!(flaggedEventContext.getStatus() == null
                            || (flaggedEventContext.getStatus() == FlaggedEventContext.FlaggedEventStatus.OPEN)
                            || flaggedEventContext.getStatus() == FlaggedEventContext.FlaggedEventStatus.TIMEOUT
                            || flaggedEventContext.getStatus() == FlaggedEventContext.FlaggedEventStatus.SUSPENDED)) {
                        return;
                    }
                }
                FlaggedEventRuleContext flaggedEventRule = alarmBean.getFlaggedEventRule(flaggedEventContext.getFlaggedAlarmProcess().getId());
                if (flaggedEventRule != null && flaggedEventRule.shouldCreateWorkorder()) {
                    TicketModuleRecordCreationHandler handler = flaggedEventRule.getTicketModuleRecordCreationHandler();
                    workorderProp = handler.consructRecordPropsFromFieldMapping(flaggedEventRule, flaggedEventContext);
                    String ticketModuleName = RemoteMonitorUtils.getTicketModuleName(flaggedEventRule);
                    FacilioContext context = V3Util.createRecord(modBean.getModule(ticketModuleName), workorderProp);
                    if (context != null) {
                        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(Constants.RECORD_MAP);
                        if (MapUtils.isNotEmpty(recordMap)) {
                            List<ModuleBaseWithCustomFields> records = recordMap.get(ticketModuleName);
                            if (CollectionUtils.isNotEmpty(records)) {
                                ModuleBaseWithCustomFields woRecord = records.get(0);
                                Long workorderId = woRecord.getId();
                                handler.updateInitialRecordStatus(workorderProp, woRecord);
                                if (workorderId != null) {
                                    Map<String, Object> updateMap = new HashMap<>();
                                    updateMap.put(ticketModuleName, ImmutableMap.of(RemoteMonitorConstants.ID, workorderId));
                                    updateMap.put(FacilioConstants.ContextNames.STATUS, FlaggedEventContext.FlaggedEventStatus.WORKORDER_CREATED.getIndex());
                                    V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, updateMap, Collections.singletonList(flaggedEventId), false);
                                    if (flaggedEventContext.getCurrentBureauActionDetail() != null && manualAction) {
                                        changeFlaggedEventActionStatus(flaggedEventContext.getCurrentBureauActionDetail().getId(), FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.ACTION_TAKEN,true);
                                        updateTakeActionTimeStamp(flaggedEventContext.getCurrentBureauActionDetail().getId());
                                    }
                                    RemoteMonitorUtils.addFlaggedAlarmTimeLogForTicket(flaggedEventId);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void updateTakeActionTimeStamp(Long flaggedEventActionId) throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("takeActionTimestamp", System.currentTimeMillis());
        V3Util.updateBulkRecords(FlaggedEventBureauActionModule.MODULE_NAME, updateMap, Collections.singletonList(flaggedEventActionId), false);
    }

    public static void updateWorkorderStatus(ModuleBaseWithCustomFields workorder, FacilioStatus status) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioChain chain = TransactionChainFactory.getHistoryUpdateChain();
        FacilioContext facilioContext = chain.getContext();
        facilioContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
        StateFlowRulesAPI.updateState(workorder, modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER), status, false, facilioContext);
    }

    public static void updateServiceOrderStatus(ServiceOrderContext serviceOrder, ServiceOrderTicketStatusContext status) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ServiceOrderTicketStatusContext updatedStatus = new ServiceOrderTicketStatusContext();
        updatedStatus.setId(status.getId());
        serviceOrder.setStatus(updatedStatus);
        V3RecordAPI.updateRecord(serviceOrder, modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER), Collections.singletonList(modBean.getField("status", FacilioConstants.ContextNames.SERVICE_ORDER)));
    }
    private static void changeFlaggedEventActionStatus(Long actionId, FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus status) throws Exception {
        changeFlaggedEventActionStatus(actionId, status,false);
    }
    private static void changeFlaggedEventActionStatus(Long actionId, FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus status,boolean skipLog) throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("eventStatus", status.getIndex());
        V3Util.updateBulkRecords(FlaggedEventBureauActionModule.MODULE_NAME, updateMap, Collections.singletonList(actionId), false);
        if(!skipLog) {
            RemoteMonitorUtils.addFlaggedAlarmTimeLog(actionId);
        }
    }

    public static void changeFlaggedEventStatus(Long flaggedEventId, FlaggedEventContext.FlaggedEventStatus status) throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("status", status.getIndex());
        V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, updateMap, Collections.singletonList(flaggedEventId), false);
    }

    public static FlaggedEventContext getFlaggedEvent(Long id) throws Exception {
        return V3RecordAPI.getRecord(FlaggedEventModule.MODULE_NAME, id, FlaggedEventContext.class);
    }

    public static FlaggedEventContext getOpenFlaggedEvent(Long controllerId, Long flaggedEventRuleId, Long clientId, Long assetId) throws Exception {
        if (flaggedEventRuleId != null && clientId != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            if(controllerId != null) {
                criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", String.valueOf(controllerId), NumberOperators.EQUALS));
            } else {
                criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", StringUtils.EMPTY, CommonOperators.IS_EMPTY));
            }
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE", FlaggedEventModule.FLAGGED_EVENT_RULE_FIELD_NAME, String.valueOf(flaggedEventRuleId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", StringUtils.join(Arrays.asList(FlaggedEventContext.FlaggedEventStatus.CLEARED.name(), FlaggedEventContext.FlaggedEventStatus.AUTO_CLOSED.name(), FlaggedEventContext.FlaggedEventStatus.SUSPENDED.name()), ","), StringOperators.ISN_T));
            criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID", "client", String.valueOf(clientId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("SOURCE_TYPE", "sourceType", FlaggedEventContext.FlaggedEventSourceType.EVENT.getIndex(), StringOperators.IS));
            if (assetId != null && assetId > 0) {
                criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", String.valueOf(assetId), NumberOperators.EQUALS));
            } else {
                criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", StringUtils.EMPTY, CommonOperators.IS_EMPTY));
            }
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
        if (filteredAlarm != null && filteredAlarm.getClient() != null && filteredAlarm.getAlarmType() != null && filteredAlarm.getFlaggedAlarmProcess() != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
//            FilteredAlarmContext lastClearedFilterAlarm = getLastClearedAlarm(filteredAlarm);
//            if (lastClearedFilterAlarm != null) {
//                criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(lastClearedFilterAlarm.getId()), NumberOperators.GREATER_THAN_EQUAL));
//            }
            criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID", "clientId", String.valueOf(filteredAlarm.getClient().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "", CommonOperators.IS_EMPTY));
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE", "alarmType", String.valueOf(filteredAlarm.getAlarmType().getId()), NumberOperators.EQUALS));
            if(filteredAlarm.getController() != null) {
                criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", String.valueOf(filteredAlarm.getController().getId()), NumberOperators.EQUALS));
            }
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE", FilteredAlarmModule.FLAGGED_ALARM_PROCESS_FIELD_NAME, String.valueOf(filteredAlarm.getFlaggedAlarmProcess().getId()), NumberOperators.EQUALS));
            if (filteredAlarm.getAsset() != null && filteredAlarm.getAsset().getId() > 0) {
                criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", String.valueOf(filteredAlarm.getAsset().getId()), NumberOperators.EQUALS));
            } else {
                criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", StringUtils.EMPTY, CommonOperators.IS_EMPTY));
            }
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
        if (filteredAlarm != null && filteredAlarm.getClient() != null && filteredAlarm.getFlaggedAlarmProcess() != null && filteredAlarm.getAlarmType() != null && filteredAlarm.getController() != null && filteredAlarm.getFlaggedAlarmProcess() != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID", "clientId", String.valueOf(filteredAlarm.getClient().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "", CommonOperators.IS_NOT_EMPTY));
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE", "alarmType", String.valueOf(filteredAlarm.getAlarmType().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", String.valueOf(filteredAlarm.getController().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE", FilteredAlarmModule.FLAGGED_ALARM_PROCESS_FIELD_NAME, String.valueOf(filteredAlarm.getFlaggedAlarmProcess().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(filteredAlarm.getId()), NumberOperators.LESS_THAN));

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

    public static Long addFieldChangeEmailRule(WorkflowRuleContext emailRule, String ruleName, List<FieldChangeFieldContext> changingFields, Criteria criteria) throws Exception {
        emailRule.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        emailRule.setScheduleType(WorkflowRuleContext.ScheduledRuleType.AFTER);
        emailRule.setFields(changingFields);
        WorkflowEventContext workflowEventContext = new WorkflowEventContext();
        workflowEventContext.setActivityType(EventType.FIELD_CHANGE);
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
    public static Long addEmailRule(WorkflowRuleContext emailRule, String ruleName, Criteria criteria) throws Exception {
        return addEmailRule(emailRule, ruleName, criteria, EventType.CREATE);
    }
    public static Long addEmailRule(WorkflowRuleContext emailRule, String ruleName, Criteria criteria,EventType eventType) throws Exception {
        emailRule.setRuleType(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION);
        emailRule.setScheduleType(WorkflowRuleContext.ScheduledRuleType.AFTER);
        WorkflowEventContext workflowEventContext = new WorkflowEventContext();
        workflowEventContext.setActivityType(eventType);
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
        emailRule.setInterval(delay / 1000);
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
        if (CollectionUtils.isNotEmpty(actionsList)) {
            FlaggedEventUtil.changeFlaggedEventActionStatus(bureauAction.getId(),FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.PASSED_TO_NEXT_BUREAU);
        } else {
            FlaggedEventUtil.changeFlaggedEventActionStatus(bureauAction.getId(),FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.TIME_OUT);
        }
        if (CollectionUtils.isNotEmpty(actionsList)) {
            FlaggedEventBureauActionsContext action = actionsList.get(0);
            if (action != null) {
                FlaggedEventContext updateEvent = new FlaggedEventContext();

                Map<String,Object> prop = new HashMap<>();
                prop.put("assignedPeople",null);
                prop.put("currentBureauActionDetail",ImmutableMap.of("id",action.getId()));
                prop.put("team",ImmutableMap.of("id",action.getTeam().getId()));
                V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, prop,Collections.singletonList(event.getId()),false);
                FlaggedEventUtil.updateBureauActionStatus(action.getId(), FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.OPEN);
                if(action.getTakeCustodyPeriod() != null && action.getTakeCustodyPeriod() > 0) {
                    Long nextExecutionTime = (System.currentTimeMillis() + action.getTakeCustodyPeriod()) / 1000;
                    FacilioTimer.scheduleOneTimeJobWithTimestampInSec(action.getId(), RemoteMonitorConstants.FLAGGED_EVENT_BUREAU_TAKE_CUSTODY_JOB, nextExecutionTime, "priority");
                }
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
            supplementRecords.add((SupplementRecord) modBean.getField("serviceOrderStatuses", AddFlaggedEventClosureConfigModule.MODULE_NAME));
            supplementRecords.add((SupplementRecord) modBean.getField("serviceOrderCloseCommandCriteria", AddFlaggedEventClosureConfigModule.MODULE_NAME));
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
            if (status == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.OPEN) {
                actionsContext.setEvaluationOpenTimestamp(System.currentTimeMillis());
            }
            actionsContext.setId(id);
            V3RecordAPI.updateRecord(actionsContext, modBean.getModule(FlaggedEventBureauActionModule.MODULE_NAME), Arrays.asList(modBean.getField("evaluationOpenTimestamp", FlaggedEventBureauActionModule.MODULE_NAME)));
            changeFlaggedEventActionStatus(id,status);
        }
    }

    public static void updateFlaggedEvent(FlaggedEventContext flaggedEvent, FilteredAlarmContext filteredAlarmContext) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FilteredAlarmContext updateFilteredAlarm = new FilteredAlarmContext();
        updateFilteredAlarm.setFlaggedAlarm(flaggedEvent);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ID","id", String.valueOf(filteredAlarmContext.getId()), NumberOperators.EQUALS));
        UpdateRecordBuilder<FilteredAlarmContext> updateRecordBuilder = new UpdateRecordBuilder<FilteredAlarmContext>()
                .module(modBean.getModule(FilteredAlarmModule.MODULE_NAME))
                .fields(Arrays.asList(modBean.getField(FilteredAlarmModule.FLAGGED_ALARM_FIELD_NAME, FilteredAlarmModule.MODULE_NAME),modBean.getField(FilteredAlarmModule.FLAGGED_ALARM_FIELD_NAME, FilteredAlarmModule.MODULE_NAME)))
                .andCriteria(criteria);
        updateRecordBuilder.update(updateFilteredAlarm);
    }

    public static void closeFlaggedEvent(Long flaggedEventId) throws Exception {
        closeFlaggedEvent(flaggedEventId, false);
    }

    public static void sendWorkorderClosureCommand(Long flaggedEventId) throws Exception {
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        FlaggedEventContext flaggedEvent = FlaggedEventUtil.getFlaggedEvent(flaggedEventId);
        if (flaggedEvent != null && flaggedEvent.getFlaggedAlarmProcess() != null) {
            FlaggedEventRuleContext rule = alarmBean.getFlaggedEventRule(flaggedEvent.getFlaggedAlarmProcess().getId());
            TicketModuleRecordCreationHandler handler = rule.getTicketModuleRecordCreationHandler();
            handler.sendClosureCommandToTicketModuleRecord(rule, flaggedEvent);
        }
    }
    public static void closeFlaggedEvent(Long flaggedEventId,boolean isManualClose) throws Exception {
        closeFlaggedEvent(flaggedEventId,isManualClose,null);
    }
    public static void closeFlaggedEvent(Long flaggedEventId,boolean isManualClose,List<BureauCloseIssueReasonOptionContext> closeIssueReasons) throws Exception {
        if(!isManualClose) {
            FlaggedEventContext event = getFlaggedEvent(flaggedEventId);

            if(event != null && event.getStatus() != FlaggedEventContext.FlaggedEventStatus.OPEN
                    && event.getStatus() != FlaggedEventContext.FlaggedEventStatus.TIMEOUT) {
                return;
            }

            if(event != null && event.getCurrentBureauActionDetail() != null && event.getCurrentBureauActionDetail().getId() > 0){
                FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus[] restrictedStatus = {
                        FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.UNDER_CUSTODY,
                        FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.PASSED_TO_NEXT_BUREAU
                };
                FlaggedEventBureauActionsContext evaluationTeamAction = V3RecordAPI.getRecord(FlaggedEventBureauActionModule.MODULE_NAME, event.getCurrentBureauActionDetail().getId());
                FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus currentActionStatus = evaluationTeamAction.getEventStatus();
                if(currentActionStatus != null && Arrays.asList(restrictedStatus).contains(currentActionStatus)){
                    return;
                }
            }
        }
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
            RemoteMonitorUtils.addFlaggedAlarmTimeLogForTicket(flaggedEventId);
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
                changeFlaggedEventActionStatus(flaggedEvent.getCurrentBureauActionDetail().getId(), FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.INACTIVE,true);
            }
        }
    }
    public static void checkAndCloseAlarmFlaggedEvent(Long flaggedEventId) throws Exception {
        if(flaggedEventId != null && flaggedEventId > -1) {
            if(isAllFlaggedEventAlarmsClosed(flaggedEventId)) {
                closeFlaggedEvent(flaggedEventId);
            }
        }
    }
    public static boolean isAllFlaggedEventAlarmsClosed(long flaggedEventId) throws Exception {
        FlaggedEventContext event = V3RecordAPI.getRecord(FlaggedEventModule.MODULE_NAME,flaggedEventId,FlaggedEventContext.class);
        if(event != null && event.getFlaggedAlarmProcess() != null) {
            List<FlaggedEventRuleAlarmTypeRel> alarmTypes = RemoteMonitorUtils.getFlaggedEventRuleAlarmTypeRel(event.getFlaggedAlarmProcess().getId());
            if(CollectionUtils.isNotEmpty(alarmTypes)) {
                for(FlaggedEventRuleAlarmTypeRel alarmType : alarmTypes) {
                    if(alarmType != null && alarmType.getAlarmType() != null) {


                        FilteredAlarmContext lastFilteredAlarm = getLastAlarmForFlaggedEvent(flaggedEventId, alarmType.getAlarmType().getId());
                        if (lastFilteredAlarm != null) {
                            if (lastFilteredAlarm.getClearedTime() != null && lastFilteredAlarm.getClearedTime() > 0) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }


    private static FilteredAlarmContext getLastAlarmForFlaggedEvent(long flaggedEventId, Long alarmTypeId) throws Exception {
        if(flaggedEventId > -1 && alarmTypeId != null && alarmTypeId > -1) {
            FlaggedEventContext event = FlaggedEventUtil.getFlaggedEvent(flaggedEventId);
            if(event != null) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                Criteria criteria = new Criteria();
//                criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT", FilteredAlarmModule.FLAGGED_ALARM_FIELD_NAME, String.valueOf(flaggedEventId), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE", "alarmType", String.valueOf(alarmTypeId), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID", "clientId", String.valueOf(event.getClient().getId()), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition("SITE", "site", String.valueOf(event.getSite().getId()), NumberOperators.EQUALS));
                if(event.getController() != null && event.getController().getId() > 0) {
                    criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", String.valueOf(event.getController().getId()), NumberOperators.EQUALS));
                } else {
                    criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", StringUtils.EMPTY, CommonOperators.IS_EMPTY));
                }
                if(event.getAsset() != null && event.getAsset().getId() > 0) {
                    criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", String.valueOf(event.getAsset().getId()), NumberOperators.EQUALS));
                } else {
                    criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", StringUtils.EMPTY, CommonOperators.IS_EMPTY));
                }
                SelectRecordsBuilder<FilteredAlarmContext> selectRecordsBuilder = new SelectRecordsBuilder<FilteredAlarmContext>()
                        .select(modBean.getAllFields(FilteredAlarmModule.MODULE_NAME))
                        .module(modBean.getModule(FilteredAlarmModule.MODULE_NAME))
                        .beanClass(FilteredAlarmContext.class)
                        .andCriteria(criteria)
                        .orderBy("SYS_CREATED_TIME desc");
                return selectRecordsBuilder.fetchFirst();
            }
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

    public static void updateControlActionId(Long flaggedEventId,Long controlActionId) throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("controlAction", ImmutableMap.of(RemoteMonitorConstants.ID, controlActionId));
        V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, updateMap, Collections.singletonList(flaggedEventId), false);
    }


    public static void takeCustody(Long id,Long currentPeopleId,boolean isAssignPeople) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FlaggedEventContext flaggedEvent = FlaggedEventUtil.getFlaggedEvent(id);
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        FlaggedEventRuleContext flaggedAlarmProcess = alarmBean.getFlaggedEventRule(flaggedEvent.getFlaggedAlarmProcess().getId());
        if(flaggedEvent != null && flaggedEvent.getCurrentBureauActionDetail() != null) {
            FlaggedEventBureauActionsContext bureauAction = V3RecordAPI.getRecord(FlaggedEventBureauActionModule.MODULE_NAME, flaggedEvent.getCurrentBureauActionDetail().getId(), FlaggedEventBureauActionsContext.class);
            if(bureauAction != null) {
                boolean isTimeoutOnManualCreation = flaggedAlarmProcess.shouldCreateWorkorder() &&
                        BooleanUtils.isFalse(flaggedAlarmProcess.getAutoCreateWorkOrder()) &&
                        bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.TIME_OUT;
                boolean openEvent = bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.OPEN || isTimeoutOnManualCreation;

                if(!isAssignPeople && bureauAction.getEventStatus() != null && bureauAction.getEventStatus() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.UNDER_CUSTODY) {
                    FacilioUtil.throwIllegalArgumentException(true, "Already Under Custody");
                }
                if(bureauAction.getEventStatus() != null && bureauAction.getEventStatus().getState() == FlaggedEventBureauActionsContext.FlaggedEventBureauActionState.INACTIVE && !isTimeoutOnManualCreation) {
                    FacilioUtil.throwIllegalArgumentException(true, "Restricted action. Event already closed or passed to next bureau");
                }
                FlaggedEventBureauActionsContext updateAction = new FlaggedEventBureauActionsContext();
                updateAction.setId(bureauAction.getId());
                V3PeopleContext people = new V3PeopleContext();
                people.setId(currentPeopleId);
                updateAction.setAssignedPeople(people);
                updateAction.setTakeCustodyTimestamp(System.currentTimeMillis());
                List<FacilioField> updateFields = new ArrayList<>(Arrays.asList(modBean.getField("eventStatus",FlaggedEventBureauActionModule.MODULE_NAME),modBean.getField("assignedPeople",FlaggedEventBureauActionModule.MODULE_NAME)));
                if(openEvent) {
                    updateFields.add(modBean.getField("takeCustodyTimestamp",FlaggedEventBureauActionModule.MODULE_NAME));
                }
                V3RecordAPI.updateRecord(updateAction,modBean.getModule(FlaggedEventBureauActionModule.MODULE_NAME), updateFields);
                FlaggedEventUtil.updateBureauActionStatus(bureauAction.getId(),FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.UNDER_CUSTODY);
                if(openEvent && !bureauAction.getIsSLABreached()) {
                    startTakeActionJob(bureauAction);
                }
            }
            Map<String,Object> eventUpdateProp = new HashMap<>();
            Map<String,Object> peopleProp = new HashMap<>();
            peopleProp.put("id",currentPeopleId);
            eventUpdateProp.put("assignedPeople",peopleProp);
            if(flaggedEvent.getStatus() == FlaggedEventContext.FlaggedEventStatus.TIMEOUT){
                eventUpdateProp.put("status", FlaggedEventContext.FlaggedEventStatus.OPEN);
            }
            V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, eventUpdateProp,Collections.singletonList(id),false);
        }
    }

    private static void startTakeActionJob(FlaggedEventBureauActionsContext action) throws Exception {
        if(action.getTakeCustodyPeriod() != null && action.getTakeCustodyPeriod() > 0) {
            Long nextExecutionTime = (System.currentTimeMillis() + action.getTakeCustodyPeriod()) / 1000;
            FacilioTimer.scheduleOneTimeJobWithTimestampInSec(action.getId(), RemoteMonitorConstants.FLAGGED_EVENT_BUREAU_TAKE_ACTION_JOB, nextExecutionTime, "priority");
        }
    }
}