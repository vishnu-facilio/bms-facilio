package com.facilio.remotemonitoring.compute;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.controller.Controller;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.handlers.AlarmCriteriaHandler;
import com.facilio.remotemonitoring.signup.FilteredAlarmModule;
import com.facilio.remotemonitoring.signup.RawAlarmModule;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;

public class FilterAlarmUtil {
    public static void pushRawAlarmToFilterProcessQueue(RawAlarmContext rawAlarm) throws Exception {
        if (AccountUtil.getCurrentOrg() != null) {
            Messenger.getMessenger().sendMessage(new Message()
                    .setKey("__filter_alarm__/" + AccountUtil.getCurrentOrg().getOrgId())
                    .setOrgId(AccountUtil.getCurrentOrg().getOrgId())
                    .setContent(FieldUtil.getAsJSON(rawAlarm)));
        }
    }

    public static Pair<AlarmFilterRuleContext, FilterRuleCriteriaContext> getRulesMatchingCriteria(RawAlarmContext rawAlarm) throws Exception {
        if (rawAlarm != null && rawAlarm.getClient() != null && rawAlarm.getAlarmDefinition() != null && rawAlarm.getController() != null) {
            AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
            List<AlarmFilterRuleContext> alarmFilterRules = alarmBean.getAlarmFilterRulesForClient(rawAlarm.getClient().getId());
            if (CollectionUtils.isNotEmpty(alarmFilterRules)) {
                for (AlarmFilterRuleContext alarmFilterRule : alarmFilterRules) {
                    if(alarmFilterRule.getStrategy() != null && alarmFilterRule.getStrategy() == rawAlarm.getStrategy() && alarmFilterRule.getAlarmType() != null && rawAlarm.getAlarmType() != null && alarmFilterRule.getAlarmType().getId() == rawAlarm.getAlarmType().getId()) {
                        boolean siteCriteriaMatched = true;
                        boolean controllerCriteriaMatched = true;
                        Criteria siteCriteria = alarmFilterRule.getSiteCriteria();
                        Criteria controllerCriteria = alarmFilterRule.getControllerCrtieria();
                        if (siteCriteria != null) {
                            siteCriteriaMatched = false;
                            V3SiteContext site = rawAlarm.getSite();
                            if(site != null) {
                                Map<String,Object> prop = FieldUtil.getAsProperties(site);
                                siteCriteriaMatched = siteCriteria.computePredicate(prop).evaluate(prop);
                            }
                        }
                        if (controllerCriteria != null) {
                            controllerCriteriaMatched = false;
                            Controller controller = rawAlarm.getController();
                            if(controller != null) {
                                Map<String,Object> prop = FieldUtil.getAsProperties(controller);
                                controllerCriteriaMatched = controllerCriteria.computePredicate(prop).evaluate(prop);
                            }
                        }

                        if (siteCriteriaMatched && controllerCriteriaMatched) {
                            List<FilterRuleCriteriaContext> filterRuleCriteriaList = alarmFilterRule.getFilterRuleCriteriaList();
                            if (CollectionUtils.isNotEmpty(filterRuleCriteriaList)) {
                                for (FilterRuleCriteriaContext filterRuleCriteria : filterRuleCriteriaList) {
                                    AlarmDefinitionContext alarmDefinition = filterRuleCriteria.getAlarmDefinition();
                                    Integer controllerType = filterRuleCriteria.getControllerTypeIndex();
                                    if (alarmDefinition != null && alarmDefinition.getId() == rawAlarm.getAlarmDefinition().getId() && controllerType != null && controllerType == rawAlarm.getController().getControllerType()) {
                                        return Pair.of(alarmFilterRule, filterRuleCriteria);
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

    public static void filterRawAlarm(RawAlarmContext rawAlarm, Pair<AlarmFilterRuleContext, FilterRuleCriteriaContext> filterRuleAndCriteriaPair) throws Exception {
        if (filterRuleAndCriteriaPair != null && rawAlarm != null) {
            if(filterRuleAndCriteriaPair.getRight() != null && filterRuleAndCriteriaPair.getRight().getFilterCriteria() != null) {
                AlarmCriteriaHandler alarmCriteriaHandler = filterRuleAndCriteriaPair.getRight().getFilterCriteria().getHandler(rawAlarm);
                if (alarmCriteriaHandler != null) {
                    alarmCriteriaHandler.compute(rawAlarm,filterRuleAndCriteriaPair.getRight());
                }
            }
        }
    }

    public static void pushToStormFilterAlarmQueue(RawAlarmContext rawAlarm) throws Exception {
        if (AccountUtil.getCurrentOrg() != null) {
            long orgId = AccountUtil.getCurrentOrg().getId();
            MessageQueue queue = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());
            JSONObject input = new JSONObject();
            input.put("orgId", orgId);
            input.put("rawAlarm", FacilioUtil.getAsJSON(rawAlarm));
            queue.put(getMessageProcessingTopicName(), new FacilioRecord(orgId + "#"+ rawAlarm.getController().getId(), input));
        }
    }

    private static String getMessageProcessingTopicName() {
        return FacilioProperties.getStableEnvironment() + "-rm-filter-alarm-queue";
    }

    public static FilteredAlarmContext constructFilteredAlarm(RawAlarmContext rawAlarm) {
        FilteredAlarmContext filteredAlarm = new FilteredAlarmContext();
        filteredAlarm.setAlarmCategory(rawAlarm.getAlarmCategory());
        filteredAlarm.setAlarmType(rawAlarm.getAlarmType());
        filteredAlarm.setOccurredTime(rawAlarm.getOccurredTime());
        filteredAlarm.setClient(rawAlarm.getClient());
        filteredAlarm.setSite(rawAlarm.getSite());
        filteredAlarm.setController(rawAlarm.getController());
        filteredAlarm.setRawAlarm(rawAlarm);
        filteredAlarm.setAsset(rawAlarm.getAsset());
        filteredAlarm.setMessage(rawAlarm.getMessage());
        return filteredAlarm;
    }
    public static void addFilteredAlarm(FilteredAlarmContext filteredAlarm) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
                .module(modBean.getModule(FilteredAlarmModule.MODULE_NAME))
                .fields(modBean.getAllFields(FilteredAlarmModule.MODULE_NAME));
        long id = insertRecordBuilder.insert(filteredAlarm);
        Collection<SupplementRecord> lookUpfields = new ArrayList<>();
        FacilioField controllerField = modBean.getField("controller", FilteredAlarmModule.MODULE_NAME);
        FacilioField siteField = modBean.getField("site", FilteredAlarmModule.MODULE_NAME);
        lookUpfields.add((LookupField) controllerField);
        lookUpfields.add((LookupField) siteField);
        FilteredAlarmContext fetchFilteredAlarm = V3RecordAPI.getRecord(FilteredAlarmModule.MODULE_NAME,id,FilteredAlarmContext.class,lookUpfields);
        updateFlaggedEventRuleInFilteredAlarm(fetchFilteredAlarm);
    }

    private static void updateFlaggedEventRuleInFilteredAlarm(FilteredAlarmContext filteredAlarm) throws Exception {
        FlaggedEventRuleContext flaggedEventRule = FlaggedEventUtil.getMatchingFlaggedEventRule(filteredAlarm);
        if(flaggedEventRule != null) {
            FilterAlarmUtil.updateFlaggedEventRuleIdToFilteredAlarm(filteredAlarm.getId(), flaggedEventRule.getId());
            if (flaggedEventRule.getExecutionType() == null || (flaggedEventRule.getExecutionType() != null && flaggedEventRule.getExecutionType().getPeriod() <= 0l)) {
                FlaggedEventUtil.pushToStormFlaggedEventQueue(filteredAlarm);
            }
        }
    }

    private static void updateSentToProcessingStatus(FilteredAlarmContext filteredAlarm) throws Exception {
        if(filteredAlarm != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule filteredAlarmModule = modBean.getModule(FilteredAlarmModule.MODULE_NAME);
            FacilioField field = FieldFactory.getBooleanField("sentToProcessing", "SENT_TO_PROCESSING", filteredAlarmModule);
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(filteredAlarmModule.getTableName())
                    .fields(Collections.singletonList(field))
                    .andCondition(CriteriaAPI.getIdCondition(filteredAlarm.getId(), filteredAlarmModule));
            Map<String, Object> prop = new HashMap<>();
            prop.put("sentToProcessing", true);
            updateRecordBuilder.update(prop);
        }
    }

    public static void updateFlaggedEventRuleIdToFilteredAlarm(Long filterAlarmId,Long flaggedEventRuleId) throws Exception {
        if(filterAlarmId == null || flaggedEventRuleId == null){
            return;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String,Object> prop = new HashMap<>();
        Map<String,Object> flaggedEventRuleProp = new HashMap<>();
        flaggedEventRuleProp.put("id",flaggedEventRuleId);
        prop.put("flaggedEventRule",flaggedEventRuleProp);
        V3Util.updateBulkRecords(FilteredAlarmModule.MODULE_NAME, prop,Collections.singletonList(filterAlarmId),false);
    }

    public static void clearAlarms(List<Long> rawAlarmIds,Long clearedTime) throws Exception {
        if(CollectionUtils.isNotEmpty(rawAlarmIds)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FilteredAlarmContext updateFilteredAlarm = new FilteredAlarmContext();
            updateFilteredAlarm.setClearedTime(clearedTime);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("RAW_ALARM","rawAlarm", StringUtils.join(rawAlarmIds,","), NumberOperators.EQUALS));
            UpdateRecordBuilder<FilteredAlarmContext> updateRecordBuilder = new UpdateRecordBuilder<FilteredAlarmContext>()
                    .module(modBean.getModule(FilteredAlarmModule.MODULE_NAME))
                    .fields(Arrays.asList(modBean.getField("clearedTime", FilteredAlarmModule.MODULE_NAME),modBean.getField("rawAlarm", FilteredAlarmModule.MODULE_NAME)))
                    .andCriteria(criteria);
            updateRecordBuilder.update(updateFilteredAlarm);
        }
    }
}