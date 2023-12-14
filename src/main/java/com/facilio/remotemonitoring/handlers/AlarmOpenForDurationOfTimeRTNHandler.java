package com.facilio.remotemonitoring.handlers;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.compute.FilterAlarmUtil;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.context.FilteredAlarmContext;
import com.facilio.remotemonitoring.context.RawAlarmContext;
import com.facilio.remotemonitoring.signup.RawAlarmModule;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlarmOpenForDurationOfTimeRTNHandler implements AlarmCriteriaHandler<RawAlarmContext> {
    @Override
    public void compute(RawAlarmContext rawAlarm, FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        if (rawAlarm != null) {
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

            List<FacilioField> fetchFields = new ArrayList<>();
            fetchFields.add(modBean.getField("id", RawAlarmModule.MODULE_NAME));

            List<RawAlarmContext> rawAlarms = V3RecordAPI.getRecordsListWithSupplements(RawAlarmModule.MODULE_NAME, null, RawAlarmContext.class, fetchFields, criteria, null);
            if(CollectionUtils.isNotEmpty(rawAlarms)) {
                if(rawAlarm.getId() > -1) {
                    rawAlarms = rawAlarms.stream().filter(r -> r.getId() != rawAlarm.getId()).collect(Collectors.toList());
                }
                List<Long> ids = rawAlarms.stream().map(RawAlarmContext::getId).collect(Collectors.toList());
                Map<String,Object> map = new HashMap<>();
                map.put("clearedTime",System.currentTimeMillis());
                V3Util.updateBulkRecords(rawAlarmModule.getName(), map,ids,false);
//                V3RecordAPI.updateRecord(updateRecord,rawAlarmModule, Collections.singletonList(modBean.getField("clearedTime",rawAlarmModule.getName())),ids);
            }
            if(filterRuleCriteria != null) {
                RawAlarmUtil.updateFilterCriteriaId(rawAlarm, filterRuleCriteria);
                Long nextExecutionTime = (rawAlarm.getOccurredTime() + filterRuleCriteria.getAlarmDuration()) / 1000;
                FacilioTimer.scheduleOneTimeJobWithTimestampInSec(rawAlarm.getId(), RemoteMonitorConstants.ALARM_OPEN_FOR_DURATION_OF_TIME, nextExecutionTime, RemoteMonitorUtils.getExecutorName("priority"));
            }
        }
    }

    @Override
    public void createFilteredAlarm(RawAlarmContext rawAlarm,FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        if(rawAlarm != null && (rawAlarm.getClearedTime() == null || rawAlarm.getClearedTime() <= 0)) {
            if(!rawAlarm.isFiltered()) {
                FilteredAlarmContext filteredAlarm = FilterAlarmUtil.constructFilteredAlarm(rawAlarm);
                filteredAlarm.setAlarmCorrelationRule(filterRuleCriteria.getAlarmFilterRule());
                RawAlarmUtil.markAsFiltered(rawAlarm.getId());
                FilterAlarmUtil.addFilteredAlarm(filteredAlarm);
            }
        }
    }
}