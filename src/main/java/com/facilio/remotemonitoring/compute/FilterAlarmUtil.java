package com.facilio.remotemonitoring.compute;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.controller.Controller;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.handlers.AlarmCriteriaHandler;
import com.facilio.remotemonitoring.signup.FilteredAlarmModule;
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
        if (rawAlarm != null && rawAlarm.getClient() != null) {
            AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
            List<AlarmFilterRuleContext> alarmFilterRules = alarmBean.getAlarmFilterRulesForClient(rawAlarm.getClient().getId());
            if (CollectionUtils.isNotEmpty(alarmFilterRules)) {
                for (AlarmFilterRuleContext alarmFilterRule : alarmFilterRules) {
                    if(alarmFilterRule.getAlarmApproach() != null && alarmFilterRule.getAlarmApproach() == rawAlarm.getAlarmApproach() && alarmFilterRule.getAlarmType() != null && rawAlarm.getAlarmType() != null && alarmFilterRule.getAlarmType().getId() == rawAlarm.getAlarmType().getId()) {
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
                            if (alarmFilterRule.getRuleType() == null || alarmFilterRule.getRuleType() != RuleType.ROLL_UP) {
                                List<FilterRuleCriteriaContext> filterRuleCriteriaList = alarmFilterRule.getFilterRuleCriteriaList();
                                if (CollectionUtils.isNotEmpty(filterRuleCriteriaList) && rawAlarm.getAlarmDefinition() != null && rawAlarm.getController() != null) {
                                    for (FilterRuleCriteriaContext filterRuleCriteria : filterRuleCriteriaList) {
                                        AlarmDefinitionContext alarmDefinition = filterRuleCriteria.getAlarmDefinition();
                                        Integer controllerType = filterRuleCriteria.getControllerTypeIndex();
                                        if (alarmDefinition != null && alarmDefinition.getId() == rawAlarm.getAlarmDefinition().getId() && controllerType != null && controllerType == rawAlarm.getController().getControllerType()) {
                                            return Pair.of(alarmFilterRule, filterRuleCriteria);
                                        }
                                    }
                                }
                            } else {
                                if(rawAlarm.getAsset() != null && rawAlarm.getAsset().getId() > 0) {
                                    V3AssetContext asset = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ASSET,rawAlarm.getAsset().getId(), V3AssetContext.class);
                                    if(asset != null && asset.getCategory() != null && asset.getCategory().getId() > 0) {
                                        List<FilterRuleCriteriaContext> filterRuleCriteriaList = alarmFilterRule.getFilterRuleCriteriaList();
                                        if (CollectionUtils.isNotEmpty(filterRuleCriteriaList)) {
                                            for (FilterRuleCriteriaContext filterRuleCriteria : filterRuleCriteriaList) {
                                                if(filterRuleCriteria.getAssetCategoryId() > 0) {
                                                    V3AssetCategoryContext cat = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ASSET_CATEGORY,asset.getCategory().getId(), V3AssetCategoryContext.class);
                                                    if(cat != null && filterRuleCriteria.getAssetCategoryId() == cat.getId()) {
                                                        return Pair.of(alarmFilterRule, filterRuleCriteria);
                                                    }
                                                }
                                            }
                                        }
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
            long controllerId = -1;
            if (rawAlarm.getController() != null && rawAlarm.getController().getId() > -1) {
                controllerId = rawAlarm.getController().getId();
            }
            long orgId = AccountUtil.getCurrentOrg().getId();
            String partitionKey = orgId + "#"+ controllerId;
            MessageQueue queue = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());
            JSONObject input = new JSONObject();
            input.put("orgId", orgId);
            input.put("rawAlarm", FacilioUtil.getAsJSON(rawAlarm));
            queue.put(getMessageProcessingTopicName(), new FacilioRecord(partitionKey, input));
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
        filteredAlarm.setAlarm(rawAlarm);
        filteredAlarm.setAsset(rawAlarm.getAsset());
        filteredAlarm.setMessage(rawAlarm.getMessage());
        return filteredAlarm;
    }

    public static List<Long> getNeighbourAssets(Long assetId,Long relationshipId,boolean excludeIncomingAsset) throws Exception {
        Long parenAssetId = FilterAlarmUtil.getRelatedParentAsset(assetId,relationshipId);
        if(parenAssetId != null) {
            List<Long> neighbourAssetIds = FilterAlarmUtil.getRelatedChildAssets(parenAssetId,relationshipId);
            if(CollectionUtils.isNotEmpty(neighbourAssetIds)) {
                if(excludeIncomingAsset) {
                    neighbourAssetIds.remove(assetId);
                }
                return neighbourAssetIds;
            }
        }
        return null;
    }
    private static List<Long> getRelatedChildAssets(Long parentId,Long relationshipId) throws Exception {
        List<Long> relatedAssets = getRelatedAssets(parentId,relationshipId,false);
        if(CollectionUtils.isNotEmpty(relatedAssets)) {
            return relatedAssets;
        }
        return null;
    }
    public static Long getRelatedParentAsset(Long childId,Long relationshipId) throws Exception {
        List<Long> relatedAssets = getRelatedAssets(childId,relationshipId,true);
        if(CollectionUtils.isNotEmpty(relatedAssets)) {
            return relatedAssets.get(0);
        }
        return null;
    }

    private static List<Long> getRelatedAssets(Long childId,Long relationshipId,boolean isForward) throws Exception {
        RelationContext relation = RelationUtil.getRelation(relationshipId,true);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        RelationMappingContext relationMapping = null;
        if(assetModule != null) {
            if (relation != null) {
                if(isForward) {
                    relationMapping = relation.getMapping2();
                } else {
                    relationMapping = relation.getMapping1();
                }
            }
        }
        if(relationMapping != null) {
            List<Long> ids = getRelatedRecords(childId,relationMapping.getId());
            if(CollectionUtils.isNotEmpty(ids)) {
                return ids;
            }
        }
        return null;
    }
    private static List<Long> getRelatedRecords(Long parentId,Long relMapId) throws Exception {
        RelationMappingContext relationMapping = RelationUtil.getRelationMapping(relMapId);
        JSONObject data = (JSONObject) RelationUtil.getRecordsWithRelationship(relMapId, parentId, 1, 500, null).get("data");
        List frmObjs = (List) data.get(relationMapping.getFromModule().getName());
        List<Long> resIds = new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(frmObjs)) {
            for (Object frmObj : frmObjs) {
                Map obj = (Map) frmObj;
                resIds.add((Long) obj.get("id"));
            }
        }
        return resIds;
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
        FilteredAlarmContext updateFilteredAlarm = new FilteredAlarmContext();
        FlaggedEventRuleContext rule = new FlaggedEventRuleContext();
        rule.setId(flaggedEventRuleId);
        updateFilteredAlarm.setFlaggedAlarmProcess(rule);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ID","id", String.valueOf(filterAlarmId), NumberOperators.EQUALS));
        UpdateRecordBuilder<FilteredAlarmContext> updateRecordBuilder = new UpdateRecordBuilder<FilteredAlarmContext>()
                .module(modBean.getModule(FilteredAlarmModule.MODULE_NAME))
                .fields(Arrays.asList(modBean.getField(FilteredAlarmModule.FLAGGED_ALARM_FIELD_NAME, FilteredAlarmModule.MODULE_NAME),modBean.getField(FilteredAlarmModule.FLAGGED_ALARM_PROCESS_FIELD_NAME, FilteredAlarmModule.MODULE_NAME)))
                .andCriteria(criteria);
        updateRecordBuilder.update(updateFilteredAlarm);
    }

    public static void clearAlarms(List<Long> rawAlarmIds,Long clearedTime) throws Exception {
        if(CollectionUtils.isNotEmpty(rawAlarmIds)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FilteredAlarmContext updateFilteredAlarm = new FilteredAlarmContext();
            updateFilteredAlarm.setClearedTime(clearedTime);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("RAW_ALARM","alarm", StringUtils.join(rawAlarmIds,","), NumberOperators.EQUALS));
            UpdateRecordBuilder<FilteredAlarmContext> updateRecordBuilder = new UpdateRecordBuilder<FilteredAlarmContext>()
                    .module(modBean.getModule(FilteredAlarmModule.MODULE_NAME))
                    .fields(Arrays.asList(modBean.getField("clearedTime", FilteredAlarmModule.MODULE_NAME),modBean.getField("alarm", FilteredAlarmModule.MODULE_NAME)))
                    .andCriteria(criteria);
            updateRecordBuilder.update(updateFilteredAlarm);
        }
    }

    public static void clearFlaggedEvent(List<Long> rawAlarmIds) throws Exception {
        if(CollectionUtils.isNotEmpty(rawAlarmIds)) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("RAW_ALARM","alarm", StringUtils.join(rawAlarmIds,","), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT","flaggedAlarm", StringUtils.EMPTY, CommonOperators.IS_NOT_EMPTY));
            List<FilteredAlarmContext> filteredAlarms = V3RecordAPI.getRecordsListWithSupplements (FilteredAlarmModule.MODULE_NAME, null, FilteredAlarmContext.class, criteria, null);
            if(CollectionUtils.isNotEmpty(filteredAlarms)) {
                for(FilteredAlarmContext filteredAlarm : filteredAlarms) {
                    if(filteredAlarm.getFlaggedAlarm() != null && filteredAlarm.getFlaggedAlarm().getId() > -1) {
                        FlaggedEventUtil.checkAndCloseAlarmFlaggedEvent(filteredAlarm.getFlaggedAlarm().getId());
                    }
                }
            }
        }
    }
}