package com.facilio.remotemonitoring.handlers;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.compute.FilterAlarmUtil;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.context.RawAlarmContext;
import com.facilio.remotemonitoring.signup.RawAlarmModule;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class RollupAlarmHandler implements AlarmCriteriaHandler<RawAlarmContext> {
    @Override
    public void compute(RawAlarmContext rawAlarm, FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        if (rawAlarm != null && rawAlarm.getAsset() != null && rawAlarm.getAsset().getId() > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Long deltaTime = System.currentTimeMillis() - filterRuleCriteria.getAlarmDuration();
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE", "alarmType", String.valueOf(rawAlarm.getAlarmType().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("SITE", "site", String.valueOf(rawAlarm.getSite().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID", "clientId", String.valueOf(rawAlarm.getClient().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", null, CommonOperators.IS_EMPTY));
            criteria.addAndCondition(CriteriaAPI.getCondition("FILTERED", "filtered", "false", BooleanOperators.IS));
            criteria.addAndCondition(CriteriaAPI.getCondition("STRATEGY", "alarmApproach", String.valueOf(rawAlarm.getAlarmApproach()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("FILTER_RULE_CRITERIA_ID", "filterRuleCriteriaId", String.valueOf(filterRuleCriteria.getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("OCCURRED_TIME", "occurredTime", String.valueOf(deltaTime), NumberOperators.GREATER_THAN_EQUAL));
            criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", StringUtils.EMPTY, CommonOperators.IS_NOT_EMPTY));
            criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(rawAlarm.getId()), NumberOperators.LESS_THAN));
            List<Long> neighbourAssetIds = FilterAlarmUtil.getNeighbourAssets(rawAlarm.getAsset().getId(),filterRuleCriteria.getRelationshipId(),false);
            if(CollectionUtils.isNotEmpty(neighbourAssetIds)) {
                criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", StringUtils.join(neighbourAssetIds, ","), NumberOperators.EQUALS));
                criteria.addOrCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(rawAlarm.getId()), NumberOperators.EQUALS));
                List<RawAlarmContext> rawAlarms = V3RecordAPI.getRecordsListWithSupplements(RawAlarmModule.MODULE_NAME, null, RawAlarmContext.class, criteria, null);
                if (CollectionUtils.isNotEmpty(rawAlarms)) {
                    List<Long> uniqueAssetIds = rawAlarms.stream().map(RawAlarmContext::getAsset).map(asset -> asset.getId()).distinct().collect(Collectors.toList());
                    if (filterRuleCriteria.getPercentage() != null) {
                        Float issueAssetPercentage = (uniqueAssetIds.size() * 100f) / neighbourAssetIds.size();
                        if (CollectionUtils.isNotEmpty(uniqueAssetIds) && issueAssetPercentage >= filterRuleCriteria.getPercentage()) {
                            constructAndGenerateRawAlarm(rawAlarm, rawAlarms, filterRuleCriteria);
                        }
                    } else {
                        if (CollectionUtils.isNotEmpty(uniqueAssetIds) && uniqueAssetIds.size() >= filterRuleCriteria.getAlarmCount()) {
                            constructAndGenerateRawAlarm(rawAlarm, rawAlarms, filterRuleCriteria);
                        }
                    }
                }
            }
                if (filterRuleCriteria != null) {
                    RawAlarmUtil.updateFilterCriteriaId(rawAlarm, filterRuleCriteria);
//                  Create filter alarm directly when roll up alarm is not matched with any of the filter criteria
                    Long nextExecutionTime = (rawAlarm.getOccurredTime() + filterRuleCriteria.getAlarmDuration()) / 1000;
                    FacilioTimer.scheduleOneTimeJobWithTimestampInSec(rawAlarm.getId(), RemoteMonitorConstants.ALARM_OPEN_FOR_DURATION_OF_TIME, nextExecutionTime, RemoteMonitorUtils.getExecutorName("priority"));
                }
        }
    }

    public static void constructAndGenerateRawAlarm(RawAlarmContext rawAlarm,List<RawAlarmContext> involvedAlarms,FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        RawAlarmContext alarm = new RawAlarmContext();
        alarm.setMessage(filterRuleCriteria.getMessage());
        alarm.setClient(rawAlarm.getClient());
        alarm.setSite(rawAlarm.getSite());
        alarm.setAlarmApproach(rawAlarm.getAlarmApproach());
        alarm.setSourceType(RawAlarmContext.RawAlarmSourceType.ROLLUP);
        alarm.setOccurredTime(System.currentTimeMillis());
        alarm.setController(RemoteMonitorUtils.getLogicalController());
        Long parentAssetId = FilterAlarmUtil.getRelatedParentAsset(rawAlarm.getAsset().getId(),filterRuleCriteria.getRelationshipId());
        V3AssetContext parentAsset = new V3AssetContext();
        parentAsset.setId(parentAssetId);
        alarm.setAsset(parentAsset);
        if(CollectionUtils.isNotEmpty(involvedAlarms)) {
            List<Long> involvedAlarmIds = involvedAlarms.stream().map(RawAlarmContext::getId).collect(Collectors.toList());
            alarm.setRelatedAlarmIds(involvedAlarmIds);
            RawAlarmUtil.updateMarkAsFiltered(involvedAlarmIds);
        }
        RawAlarmUtil.pushToStormRawAlarmQueue(alarm);
    }

    @Override
    public void createFilteredAlarm(RawAlarmContext rawAlarm, FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        if (rawAlarm != null) {
            RawAlarmUtil.updateParentAlarm(rawAlarm.getRelatedAlarmIds(), rawAlarm.getId());
        }
    }
}