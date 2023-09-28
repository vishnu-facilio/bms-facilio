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
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class AlarmCountsInAPeriodOfTimeRTNHandler implements AlarmCriteriaHandler {
    @Override
    public void compute(RawAlarmContext rawAlarm, FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        if (rawAlarm != null) {
            if(filterRuleCriteria != null) {
                RawAlarmUtil.updateFilterCriteriaId(rawAlarm, filterRuleCriteria);
                Long startTime = (rawAlarm.getOccurredTime() - filterRuleCriteria.getAlarmCountPeriod());
                Long endTime = rawAlarm.getOccurredTime();
                Long count = alarmCountWithInInterval(rawAlarm, startTime, endTime);
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
            filteredAlarm.setAlarmFilterRule(filterRuleCriteria.getAlarmFilterRule());
            FilterAlarmUtil.addFilteredAlarm(filteredAlarm);
            RawAlarmUtil.markAsFiltered(rawAlarm.getId());
        }
    }

    private static Long alarmCountWithInInterval(RawAlarmContext rawAlarm,Long startTime,Long endTime) throws Exception {
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
        criteria.addAndCondition(CriteriaAPI.getCondition("STRATEGY", "strategy", String.valueOf(rawAlarm.getStrategy()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(rawAlarm.getId()), NumberOperators.LESS_THAN));

        if(rawAlarm.getAsset() != null && rawAlarm.getAsset().getId() > 0) {
            criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", String.valueOf(rawAlarm.getAsset().getId()), NumberOperators.EQUALS));
        } else {
            criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", StringUtils.EMPTY, CommonOperators.IS_EMPTY));
        }

        FacilioField aggregateField = FieldFactory.getIdField(rawAlarmModule);
        List<Map<String, Object>> props = V3RecordAPI.getRecordsAggregateValue(rawAlarmModule.getName(), null, RawAlarmContext.class,criteria, BmsAggregateOperators.CommonAggregateOperator.COUNT, aggregateField, null);
        if(props != null) {
            Long count = (Long) props.get(0).get(aggregateField.getName());
            if(count != null) {
                return count;
            }
        }
        return 0l;
    }
}
