package com.facilio.remotemonitoring.handlers;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.compute.FilterAlarmUtil;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.context.FilteredAlarmContext;
import com.facilio.remotemonitoring.context.RawAlarmContext;
import com.facilio.remotemonitoring.signup.RawAlarmModule;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlarmCountsInAPeriodOfTimeRTNHandler implements AlarmCriteriaHandler {
    @Override
    public void compute(RawAlarmContext rawAlarm, FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        if (rawAlarm != null) {
            if(filterRuleCriteria != null) {
                RawAlarmUtil.updateFilterCriteriaId(rawAlarm, filterRuleCriteria);
                Long startTime = (rawAlarm.getOccurredTime() - filterRuleCriteria.getAlarmCountPeriod());
                Long endTime = rawAlarm.getOccurredTime();
                int count = alarmCountWithInInterval(rawAlarm, startTime, endTime);
                if((count+1) >= filterRuleCriteria.getAlarmCount()) {
                    createFilteredAlarm(rawAlarm,filterRuleCriteria);
                }
            }
        }
    }

    @Override
    public void createFilteredAlarm(RawAlarmContext rawAlarm,FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        if(rawAlarm != null && !rawAlarm.isFiltered()) {
            FilteredAlarmContext filteredAlarm = FilterAlarmUtil.constructFilteredAlarm(rawAlarm);
            filteredAlarm.setAlarmCorrelationRule(filterRuleCriteria.getAlarmFilterRule());
            RawAlarmUtil.markAsFiltered(rawAlarm.getId());
            FilterAlarmUtil.addFilteredAlarm(filteredAlarm);
        }
    }

    private static int alarmCountWithInInterval(RawAlarmContext rawAlarm,Long startTime,Long endTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule rawAlarmModule = modBean.getModule(RawAlarmModule.MODULE_NAME);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_DEFINITION", "alarmDefinition", String.valueOf(rawAlarm.getAlarmDefinition().getId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE", "alarmType", String.valueOf(rawAlarm.getAlarmType().getId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", String.valueOf(rawAlarm.getController().getId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID", "clientId", String.valueOf(rawAlarm.getClient().getId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("SITE", "site", String.valueOf(rawAlarm.getSite().getId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("OCCURRED_TIME", "occurredTime", String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition("OCCURRED_TIME", "occurredTime", String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition("STRATEGY", "alarmApproach", String.valueOf(rawAlarm.getAlarmApproach()), NumberOperators.EQUALS));

        if(rawAlarm.getAsset() != null && rawAlarm.getAsset().getId() > 0) {
            criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", String.valueOf(rawAlarm.getAsset().getId()), NumberOperators.EQUALS));
        } else {
            criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", StringUtils.EMPTY, CommonOperators.IS_EMPTY));
        }

        List<FacilioField> fetchFields = new ArrayList<>();
        fetchFields.add(modBean.getField("id", RawAlarmModule.MODULE_NAME));

        List<RawAlarmContext> rawAlarms = V3RecordAPI.getRecordsListWithSupplements(rawAlarmModule.getName(), null, RawAlarmContext.class, fetchFields, criteria, null);
        int count = 0;
        if(CollectionUtils.isNotEmpty(rawAlarms)) {
            if(rawAlarm.getId() > -1) {
                rawAlarms = rawAlarms.stream().filter(r -> r.getId() != rawAlarm.getId()).collect(Collectors.toList());
            }
            count = rawAlarms.size();
        }
        return count;
    }
}
