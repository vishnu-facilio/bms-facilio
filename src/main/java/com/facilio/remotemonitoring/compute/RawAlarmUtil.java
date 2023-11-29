package com.facilio.remotemonitoring.compute;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.controller.Controller;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.AlarmDefinitionModule;
import com.facilio.remotemonitoring.signup.AlarmDefinitionTaggingModule;
import com.facilio.remotemonitoring.signup.RawAlarmModule;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.logging.Filter;
import java.util.stream.Collectors;

public class RawAlarmUtil {
    private static boolean isValidRawAlarm(RawAlarmContext rawAlarm) {
        if (rawAlarm != null) {
            V3ClientContext client = rawAlarm.getClient();
            String message = rawAlarm.getMessage();
            if (client != null && StringUtils.isNotEmpty(message)) {
                return true;
            }
        }
        return false;
    }


    public static void pushControllerOfflineOpenAlarm(String message,Long occurredTime,Controller controller) throws Exception {
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        IncomingRawAlarmContext controllerOfflineAlarm = new IncomingRawAlarmContext();
        controllerOfflineAlarm.setMessage(message);
        controllerOfflineAlarm.setController(controller);
        controllerOfflineAlarm.setAlarmApproach(AlarmApproach.RETURN_TO_NORMAL.getIndex());
        controllerOfflineAlarm.setSourceType(IncomingRawAlarmContext.RawAlarmSourceType.CONTROLLER);
        controllerOfflineAlarm.setOccurredTime(occurredTime);
        controllerOfflineAlarm.setAlarmType(alarmBean.getAlarmType(RemoteMonitorConstants.SystemAlarmTypes.CONTROLLER_OFFLINE));
        pushToStormRawAlarmQueue(controllerOfflineAlarm);
    }

    public static void pushControllerOfflineClearAlarm(String message,Long clearedTime,Controller controller) throws Exception {
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        IncomingRawAlarmContext controllerOfflineAlarm = new IncomingRawAlarmContext();
        controllerOfflineAlarm.setMessage(message);
        controllerOfflineAlarm.setController(controller);
        controllerOfflineAlarm.setAlarmApproach(AlarmApproach.RETURN_TO_NORMAL.getIndex());
        controllerOfflineAlarm.setSourceType(IncomingRawAlarmContext.RawAlarmSourceType.CONTROLLER);
        controllerOfflineAlarm.setClearedTime(clearedTime);
        controllerOfflineAlarm.setAlarmType(alarmBean.getAlarmType(RemoteMonitorConstants.SystemAlarmTypes.CONTROLLER_OFFLINE));
        pushToStormRawAlarmQueue(controllerOfflineAlarm);
    }

    public static void pushToStormRawAlarmQueue(IncomingRawAlarmContext rawAlarm) throws Exception {
        long controllerId = -1;
        if (rawAlarm.getController() != null && rawAlarm.getController().getId() > -1) {
            controllerId = rawAlarm.getController().getId();
        }
        if (AccountUtil.getCurrentOrg() != null && rawAlarm != null) {
            long orgId = AccountUtil.getCurrentOrg().getId();
            MessageQueue queue = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());
            JSONObject input = new JSONObject();
            input.put("orgId", orgId);
            input.put("rawAlarm", FacilioUtil.getAsJSON(rawAlarm));
            queue.put(getMessageProcessingTopicName(), new FacilioRecord(orgId + "#" + controllerId, input));
        }
    }

    public static void pushToStormRawAlarmQueue(RawAlarmContext rawAlarm) throws Exception {
        long controllerId = -1;
        if (rawAlarm.getController() != null && rawAlarm.getController().getId() > -1) {
            controllerId = rawAlarm.getController().getId();
        }
        if (AccountUtil.getCurrentOrg() != null && rawAlarm != null) {
            long orgId = AccountUtil.getCurrentOrg().getId();
            MessageQueue queue = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());
            JSONObject input = new JSONObject();
            input.put("orgId", orgId);
            input.put("rawAlarm", FacilioUtil.getAsJSON(rawAlarm));
            queue.put(getMessageProcessingTopicName(), new FacilioRecord(orgId + "#" + controllerId, input));
        }
    }

    private static String getMessageProcessingTopicName() {
        return FacilioProperties.getStableEnvironment() + "-rm-raw-alarm-queue";
    }

    public static Pair<AlarmDefinitionMappingContext, RawAlarmContext> processMessage(RawAlarmContext rawAlarm) throws Exception {
        rawAlarm.setProcessed(true);
        AlarmDefinitionMappingContext matchedAlarmDefinitionMapping = null;
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        if (isValidRawAlarm(rawAlarm)) {
            String message = rawAlarm.getMessage();
            List<AlarmDefinitionMappingContext> alarmDefinitionMappings = alarmBean.getAlarmDefinitionMappingsForClient(rawAlarm.getClient().getId());
            if (CollectionUtils.isNotEmpty(alarmDefinitionMappings)) {
                for (AlarmDefinitionMappingContext alarmDefinitionMapping : alarmDefinitionMappings) {
                    String regularExpression = alarmDefinitionMapping.getRegularExpression();
                    if (StringUtils.isNotEmpty(regularExpression)) {
                        if (message.matches(regularExpression)) {
                            matchedAlarmDefinitionMapping = alarmDefinitionMapping;
                        }
                    }
                }
            }
        }
        return Pair.of(matchedAlarmDefinitionMapping, rawAlarm);
    }

    public static RawAlarmContext tagSiteAndClientRawAlarm(RawAlarmContext rawAlarm) throws Exception {
        if (rawAlarm.getController() != null) {
            Controller rawAlarmController = rawAlarm.getController();
            if (rawAlarmController.getId() > -1) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                Controller controller = V3RecordAPI.getRecord(FacilioConstants.ContextNames.CONTROLLER, rawAlarmController.getId(), Controller.class);
                if (controller != null) {
                    rawAlarm.setController(controller);
                }
                if (controller != null && controller.getId() > -1 && controller.getSiteId() > -1) {
                    FacilioField clientField = modBean.getField("client", FacilioConstants.ContextNames.SITE);
                    FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
                    SelectRecordsBuilder<V3SiteContext> builder = new SelectRecordsBuilder<V3SiteContext>()
                            .beanClass(V3SiteContext.class)
                            .module(siteModule)
                            .select(Collections.singletonList(clientField))
                            .fetchSupplements(Arrays.asList((SupplementRecord) clientField))
                            .andCondition(CriteriaAPI.getIdCondition(controller.getSiteId(), siteModule));
                    V3SiteContext site = builder.fetchFirst();
                    if (site != null) {
                        rawAlarm.setSite(site);
                        if (site.getClient() != null) {
                            rawAlarm.setClient(site.getClient());
                        }
                    }
                }
            }
        }
        return rawAlarm;
    }

    public static RawAlarmContext checkAndCreateAlarmDefinition(AlarmDefinitionMappingContext matchedAlarmDefinitionMapping, RawAlarmContext rawAlarm) throws Exception {
        if (matchedAlarmDefinitionMapping == null && rawAlarm != null) {
            AlarmDefinitionContext alarmDefinition = getNameMatchingDefinitionRecord(rawAlarm.getMessage(), rawAlarm.getClient());
            if (alarmDefinition == null) {
                alarmDefinition = createAlarmDefinition(rawAlarm.getMessage(), rawAlarm.getClient());
                if (alarmDefinition != null) {
                    createAlarmDefinitionTagging(alarmDefinition, rawAlarm);
                }
            }
            if (alarmDefinition != null) {
                rawAlarm.setAlarmDefinition(alarmDefinition);
            }
        } else {
            rawAlarm.setAlarmDefinition(matchedAlarmDefinitionMapping.getAlarmDefinition());
        }
        return rawAlarm;
    }

    private static AlarmDefinitionContext getNameMatchingDefinitionRecord(String message, V3ClientContext client) throws Exception {
        if (client != null && client.getId() > 0) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", message, StringOperators.IS));
            criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID", "client", String.valueOf(client.getId()), NumberOperators.EQUALS));
            List<AlarmDefinitionContext> alarmDefinitions = V3RecordAPI.getRecordsListWithSupplements(AlarmDefinitionModule.MODULE_NAME, null, AlarmDefinitionContext.class, criteria, null);
            if (CollectionUtils.isNotEmpty(alarmDefinitions)) {
                return alarmDefinitions.get(0);
            }
        }
        return null;
    }

    private static AlarmDefinitionTaggingContext createAlarmDefinitionTagging(AlarmDefinitionContext alarmDefinitionContext, RawAlarmContext rawAlarm) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        AlarmTypeContext alarmType;
        if(rawAlarm.getAlarmType() != null && StringUtils.isNotEmpty(rawAlarm.getAlarmType().getLinkName())) {
            alarmType = alarmBean.getAlarmType(rawAlarm.getAlarmType().getLinkName());
        } else {
            alarmType = alarmBean.getAlarmType(RemoteMonitorConstants.SystemAlarmTypes.UNDEFINED);
        }
        if (alarmType != null && alarmDefinitionContext != null) {
            AlarmDefinitionTaggingContext alarmDefinitionTagging = new AlarmDefinitionTaggingContext();
            alarmDefinitionTagging.setName(alarmDefinitionContext.getName());
            alarmDefinitionTagging.setClient(alarmDefinitionContext.getClient());
            alarmDefinitionTagging.setAlarmType(alarmType);
            alarmDefinitionTagging.setAlarmDefinition(alarmDefinitionContext);
            alarmDefinitionTagging.setControllerType(rawAlarm.getController().getControllerType());
            FacilioContext alarmDefinitionTaggingContext = V3Util.createRecord(modBean.getModule(AlarmDefinitionTaggingModule.MODULE_NAME), FieldUtil.getAsProperties(alarmDefinitionTagging));
            Map<String, List<ModuleBaseWithCustomFields>> recordsMap = (Map<String, List<ModuleBaseWithCustomFields>>) alarmDefinitionTaggingContext.get(Constants.RECORD_MAP);
            if (MapUtils.isNotEmpty(recordsMap)) {
                List<ModuleBaseWithCustomFields> recordsList = recordsMap.get(AlarmDefinitionTaggingModule.MODULE_NAME);
                if (CollectionUtils.isNotEmpty(recordsList)) {
                    ModuleBaseWithCustomFields addedAlarmDefinition = recordsList.get(0);
                    return V3RecordAPI.getRecord(AlarmDefinitionTaggingModule.MODULE_NAME, addedAlarmDefinition.getId());
                }
            }
        }
        return null;
    }

    private static AlarmDefinitionContext createAlarmDefinition(String message, V3ClientContext client) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        AlarmDefinitionContext alarmDefinition = new AlarmDefinitionContext();
        alarmDefinition.setName(message);
        alarmDefinition.setClient(client);
        FacilioContext alarmDefinitionContext = V3Util.createRecord(modBean.getModule(AlarmDefinitionModule.MODULE_NAME), FieldUtil.getAsProperties(alarmDefinition));
        Map<String, List<ModuleBaseWithCustomFields>> recordsMap = (Map<String, List<ModuleBaseWithCustomFields>>) alarmDefinitionContext.get(Constants.RECORD_MAP);
        if (MapUtils.isNotEmpty(recordsMap)) {
            List<ModuleBaseWithCustomFields> recordsList = recordsMap.get(AlarmDefinitionModule.MODULE_NAME);
            if (CollectionUtils.isNotEmpty(recordsList)) {
                ModuleBaseWithCustomFields addedAlarmDefinition = recordsList.get(0);
                return V3RecordAPI.getRecord(AlarmDefinitionModule.MODULE_NAME, addedAlarmDefinition.getId());
            }
        }
        return null;
    }

    public static RawAlarmContext tagRawAlarm(RawAlarmContext rawAlarm) throws Exception {
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        if (rawAlarm.getController() != null) {
            Controller controller = RemoteMonitorUtils.getControllerForAlarm(rawAlarm);
            if (controller != null && rawAlarm != null && rawAlarm.getAlarmDefinition() != null) {
                List<AlarmDefinitionTaggingContext> alarmDefinitionTaggings = alarmBean.getAlarmDefinitionTaggings(rawAlarm.getAlarmDefinition().getId(), ControllerType.valueOf(controller.getControllerType()));
                if (CollectionUtils.isNotEmpty(alarmDefinitionTaggings)) {
                    AlarmDefinitionTaggingContext tagging = alarmDefinitionTaggings.get(0);
                    if (tagging.getAlarmCategory() != null) {
                        rawAlarm.setAlarmCategory(tagging.getAlarmCategory());
                    }
                    if (tagging.getAlarmDefinition() != null) {
                        rawAlarm.setAlarmDefinition(tagging.getAlarmDefinition());
                    }
                    if (tagging.getAlarmType() != null) {
                        rawAlarm.setAlarmType(tagging.getAlarmType());
                    }
                }
            }
        }
        return rawAlarm;
    }

    public static void addRawAlarm(RawAlarmContext rawAlarm) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        V3Util.createRecord(modBean.getModule(RawAlarmModule.MODULE_NAME), Collections.singletonList(rawAlarm));
    }

    public static RawAlarmContext addAndGetRawAlarm(RawAlarmContext rawAlarm) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioContext rawAlarmContext = V3Util.createRecord(modBean.getModule(RawAlarmModule.MODULE_NAME), Collections.singletonList(rawAlarm));
        Map<String, List<ModuleBaseWithCustomFields>> recordsMap = (Map<String, List<ModuleBaseWithCustomFields>>) rawAlarmContext.get(Constants.RECORD_MAP);
        if (MapUtils.isNotEmpty(recordsMap)) {
            List<ModuleBaseWithCustomFields> recordsList = recordsMap.get(RawAlarmModule.MODULE_NAME);
            if (CollectionUtils.isNotEmpty(recordsList)) {
                ModuleBaseWithCustomFields createdRawAlarm = recordsList.get(0);
                return V3RecordAPI.getRecord(RawAlarmModule.MODULE_NAME, createdRawAlarm.getId());
            }
        }
        return null;
    }

    private static Map<Long, List<AlarmDefinitionMappingContext>> sortMappingsByPriority(List<AlarmDefinitionMappingContext> alarmDefinitionMappings) {
        Map<Long, List<AlarmDefinitionMappingContext>> priorityAlarmDefinitionMappings = new HashMap<>();
        if (CollectionUtils.isNotEmpty(alarmDefinitionMappings)) {
            for (AlarmDefinitionMappingContext alarmDefinitionMapping : alarmDefinitionMappings) {
                Long priority = alarmDefinitionMapping.getPriority();
                if (priority != null) {
                    List<AlarmDefinitionMappingContext> mappings = priorityAlarmDefinitionMappings.get(priority);
                    if (CollectionUtils.isEmpty(mappings)) {
                        mappings = new ArrayList<>();
                    }
                    mappings.add(alarmDefinitionMapping);
                    priorityAlarmDefinitionMappings.put(priority, mappings);
                }
            }
        }
        return priorityAlarmDefinitionMappings;
    }

    public static RawAlarmContext fetchRawAlarm(Long id) throws Exception {
        RawAlarmContext alarm = V3RecordAPI.getRecord(RawAlarmModule.MODULE_NAME, id);
        if (alarm != null && alarm.getSourceType() != null && alarm.getSourceType() == RawAlarmContext.RawAlarmSourceType.ROLLUP) {
            alarm.setController(RemoteMonitorUtils.getLogicalController());
        }
        return alarm;
    }

    public static void markAsFiltered(Long id) throws Exception {
        RawAlarmContext alarm = new RawAlarmContext();
        alarm.setFiltered(true);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(id), NumberOperators.EQUALS));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        UpdateRecordBuilder<RawAlarmContext> updateRecordBuilder = new UpdateRecordBuilder<RawAlarmContext>()
                .module(modBean.getModule(RawAlarmModule.MODULE_NAME))
                .fields(Arrays.asList(modBean.getField("filtered", RawAlarmModule.MODULE_NAME)))
                .andCriteria(criteria);
        updateRecordBuilder.update(alarm);
    }

    public static void updateParentAlarm(List<Long> ids, Long parentAlarmId) throws Exception {
        if (CollectionUtils.isNotEmpty(ids) && parentAlarmId != null) {
            RawAlarmContext parentAlarm = new RawAlarmContext();
            parentAlarm.setId(parentAlarmId);
            RawAlarmContext alarm = new RawAlarmContext();
            alarm.setFiltered(true);
            alarm.setParentAlarm(parentAlarm);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(ids, ","), NumberOperators.EQUALS));

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            UpdateRecordBuilder<RawAlarmContext> updateRecordBuilder = new UpdateRecordBuilder<RawAlarmContext>()
                    .module(modBean.getModule(RawAlarmModule.MODULE_NAME))
                    .fields(Arrays.asList(modBean.getField("parentAlarm", RawAlarmModule.MODULE_NAME)))
                    .andCriteria(criteria);
            updateRecordBuilder.update(alarm);
        }
    }
    public static void updateMarkAsFiltered(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            RawAlarmContext alarm = new RawAlarmContext();
            alarm.setFiltered(true);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(ids, ","), NumberOperators.EQUALS));

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            UpdateRecordBuilder<RawAlarmContext> updateRecordBuilder = new UpdateRecordBuilder<RawAlarmContext>()
                    .module(modBean.getModule(RawAlarmModule.MODULE_NAME))
                    .fields(Arrays.asList(modBean.getField("filtered", RawAlarmModule.MODULE_NAME)))
                    .andCriteria(criteria);
            updateRecordBuilder.update(alarm);
        }
    }

    public static void updateFilterCriteriaId(RawAlarmContext rawAlarm, FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        RawAlarmContext alarm = new RawAlarmContext();
        alarm.setFilterRuleCriteriaId(filterRuleCriteria.getId());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(rawAlarm.getId()), NumberOperators.EQUALS));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        UpdateRecordBuilder<RawAlarmContext> updateRecordBuilder = new UpdateRecordBuilder<RawAlarmContext>()
                .module(modBean.getModule(RawAlarmModule.MODULE_NAME))
                .fields(Arrays.asList(modBean.getField("filterRuleCriteriaId", RawAlarmModule.MODULE_NAME)))
                .andCriteria(criteria);
        updateRecordBuilder.update(alarm);
    }

    public static void markAsFiltered(List<RawAlarmContext> rawAlarms) throws Exception {
        if (CollectionUtils.isNotEmpty(rawAlarms)) {
            List<Long> ids = rawAlarms.stream().map(RawAlarmContext::getId).collect(Collectors.toList());
            Map<String, Object> prop = new HashMap<>();
            prop.put("filtered", true);
            V3Util.updateBulkRecords(RawAlarmModule.MODULE_NAME, prop, ids, false);
        }
    }
    public static void clearAlarms(List<Long> ids,Long clearTime) throws Exception {
        if(CollectionUtils.isNotEmpty(ids)) {
            if(clearTime == null) {
                clearTime = System.currentTimeMillis();
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            RawAlarmContext rawAlarmContext = new RawAlarmContext();
            rawAlarmContext.setClearedTime(clearTime);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("ID","id", StringUtils.join(ids,","), NumberOperators.EQUALS));
            UpdateRecordBuilder<RawAlarmContext> updateRecordBuilder = new UpdateRecordBuilder<RawAlarmContext>()
                    .module(modBean.getModule(RawAlarmModule.MODULE_NAME))
                    .fields(Arrays.asList(modBean.getField("clearedTime", RawAlarmModule.MODULE_NAME)))
                    .andCriteria(criteria);
            updateRecordBuilder.update(rawAlarmContext);
            FilterAlarmUtil.pushToStormFilterAlarmQueue(ids);
//            FilterAlarmUtil.clearAlarms(ids,clearTime);
        }
    }

    public static void clearAlarm(RawAlarmContext rawAlarm,Long clearTime) throws Exception {
        if(rawAlarm != null) {
            if(clearTime == null) {
                clearTime = System.currentTimeMillis();
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            RawAlarmContext rawAlarmContext = new RawAlarmContext();
            rawAlarmContext.setClearedTime(clearTime);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("ID","id", String.valueOf(rawAlarm.getId()), NumberOperators.EQUALS));
            UpdateRecordBuilder<RawAlarmContext> updateRecordBuilder = new UpdateRecordBuilder<RawAlarmContext>()
                    .module(modBean.getModule(RawAlarmModule.MODULE_NAME))
                    .fields(Arrays.asList(modBean.getField("clearedTime", RawAlarmModule.MODULE_NAME)))
                    .andCriteria(criteria);
            updateRecordBuilder.update(rawAlarmContext);
            FilterAlarmUtil.pushToStormFilterAlarmQueue(Collections.singletonList(rawAlarm.getId()));
//            FilterAlarmUtil.clearAlarms(Collections.singletonList(rawAlarm.getId()),clearTime);
            clearAllParentAlarms(rawAlarm,clearTime);
        }
    }
    private static void clearAllParentAlarms(List<RawAlarmContext> alarms,Long clearTime) throws Exception {
        if(CollectionUtils.isNotEmpty(alarms)) {
            for(RawAlarmContext alarm : alarms) {
                if(alarm != null && alarm.getParentAlarm() != null) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    List<SupplementRecord> supplements = new ArrayList<>();
                    supplements.add((SupplementRecord) modBean.getField("parentAlarm", RawAlarmModule.MODULE_NAME));
                    Criteria criteria = new Criteria();
                    criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ALARM", "parentAlarm", String.valueOf(alarm.getParentAlarm().getId()), NumberOperators.EQUALS));
                    criteria.addAndCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", StringUtils.EMPTY, CommonOperators.IS_EMPTY));
                    List<RawAlarmContext> rawAlarms = V3RecordAPI.getRecordsListWithSupplements(RawAlarmModule.MODULE_NAME, null, RawAlarmContext.class, criteria, supplements);
                    boolean allCleared = true;
                    if (CollectionUtils.isNotEmpty(rawAlarms)) {
                        allCleared = false;
                    }
                    if (allCleared) {
                        clearAlarm(alarm.getParentAlarm(),clearTime);
//                        FilterAlarmUtil.clearFlaggedEvent(Collections.singletonList(alarm.getParentAlarm().getId()));
                    }
                }
            }
        }
    }
    private static void clearAllParentAlarms(RawAlarmContext alarm,Long clearTime) throws Exception {
        if(alarm != null) {
            RawAlarmContext rawAlarm = fetchRawAlarm(alarm.getId());
            if(rawAlarm != null && rawAlarm.getParentAlarm() != null) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                List<SupplementRecord> supplements = new ArrayList<>();
                supplements.add((SupplementRecord) modBean.getField("parentAlarm", RawAlarmModule.MODULE_NAME));
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ALARM", "parentAlarm", String.valueOf(rawAlarm.getParentAlarm().getId()), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", StringUtils.EMPTY, CommonOperators.IS_EMPTY));
                List<RawAlarmContext> rawAlarms = V3RecordAPI.getRecordsListWithSupplements(RawAlarmModule.MODULE_NAME, null, RawAlarmContext.class, criteria, supplements);
                boolean allCleared = true;
                if (CollectionUtils.isNotEmpty(rawAlarms)) {
                    allCleared = false;
                }
                if (allCleared) {
                    clearAlarm(rawAlarm.getParentAlarm(),clearTime);
//                    FilterAlarmUtil.clearFlaggedEvent(Collections.singletonList(rawAlarm.getParentAlarm().getId()));
                }
            }
        }
    }
    public static List<RawAlarmContext> clearPreviousRawAlarmsForRTN(RawAlarmContext rawAlarm) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule rawAlarmModule = modBean.getModule(RawAlarmModule.MODULE_NAME);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_DEFINITION", "alarmDefinition", String.valueOf(rawAlarm.getAlarmDefinition().getId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE", "alarmType", String.valueOf(rawAlarm.getAlarmType().getId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", String.valueOf(rawAlarm.getController().getId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID", "clientId", String.valueOf(rawAlarm.getClient().getId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("SITE", "site", String.valueOf(rawAlarm.getSite().getId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", null, CommonOperators.IS_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition("STRATEGY", "alarmApproach", String.valueOf(rawAlarm.getAlarmApproach()), NumberOperators.EQUALS));
        if(rawAlarm.getAsset() != null && rawAlarm.getAsset().getId() > 0) {
            criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", String.valueOf(rawAlarm.getAsset().getId()), NumberOperators.EQUALS));
        } else {
            criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", StringUtils.EMPTY, CommonOperators.IS_EMPTY));
        }
        if(rawAlarm.getId() > -1) {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(rawAlarmModule), String.valueOf(rawAlarm.getId()), NumberOperators.LESS_THAN));
        }
//        if(rawAlarm.getClearedTime() != null && rawAlarm.getClearedTime() > -1) {
//            criteria.addAndCondition(CriteriaAPI.getCondition("OCCURRED_TIME", "occurredTime", String.valueOf(rawAlarm.getClearedTime()), NumberOperators.LESS_THAN_EQUAL));
//        } else {
//            criteria.addAndCondition(CriteriaAPI.getCondition("OCCURRED_TIME", "occurredTime", String.valueOf(rawAlarm.getOccurredTime()), NumberOperators.LESS_THAN_EQUAL));
//        }
        List<RawAlarmContext> rawAlarms = V3RecordAPI.getRecordsListWithSupplements(RawAlarmModule.MODULE_NAME, null, RawAlarmContext.class, criteria, null);
        if (CollectionUtils.isNotEmpty(rawAlarms)) {
            List<Long> ids = rawAlarms.stream().map(RawAlarmContext::getId).collect(Collectors.toList());
            Long clearTime = System.currentTimeMillis();
            if(rawAlarm.getClearedTime() != null && rawAlarm.getClearedTime() > -1) {
                clearTime = rawAlarm.getClearedTime();
            }
            if (CollectionUtils.isNotEmpty(ids)) {
                clearAlarms(ids,clearTime);
                clearAllParentAlarms(rawAlarms,clearTime);
            }
            return rawAlarms;
        }
        return null;
    }
}