package com.facilio.remotemonitoring.handlers;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.remotemonitoring.compute.FilterAlarmUtil;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.ControllerAlarmInfoModule;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class NoAlarmReceivedForDurationOfTimeRURHandler implements AlarmCriteriaHandler {
    @Override
    public void compute(RawAlarmContext rawAlarm, FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        if(filterRuleCriteria != null) {
            RawAlarmUtil.updateFilterCriteriaId(rawAlarm, filterRuleCriteria);
        }
        updateAlarmLastReceivedTimeForController(rawAlarm);
    }

    @Override
    public void createFilteredAlarm(RawAlarmContext rawAlarm,FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        if (rawAlarm != null && rawAlarm.getClearedTime() == null || rawAlarm.getClearedTime() <= 0) {
            FilteredAlarmContext filteredAlarm = FilterAlarmUtil.constructFilteredAlarm(rawAlarm);
            filteredAlarm.setAlarmCorrelationRule(filterRuleCriteria.getAlarmFilterRule());
            RawAlarmUtil.markAsFiltered(rawAlarm.getId());
            FilterAlarmUtil.addFilteredAlarm(filteredAlarm);
        }
    }

    private static void updateAlarmLastReceivedTimeForController(RawAlarmContext rawAlarm) throws Exception {
        //add alarm last received time in controller info table
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule controllerInfoModule = modBean.getModule(ControllerAlarmInfoModule.MODULE_NAME);
        if (rawAlarm != null && rawAlarm.getController() != null && rawAlarm.getAlarmDefinition() != null && rawAlarm.getAlarmType() != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER", "controller", String.valueOf(rawAlarm.getController().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE", "alarmType", String.valueOf(rawAlarm.getAlarmType().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_DEFINITION", "alarmDefinition", String.valueOf(rawAlarm.getAlarmDefinition().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("STRATEGY", "alarmApproach", String.valueOf(rawAlarm.getAlarmApproach()), NumberOperators.EQUALS));
            if(rawAlarm.getAsset() != null && rawAlarm.getAsset().getId() > 0) {
                criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", String.valueOf(rawAlarm.getAsset().getId()), NumberOperators.EQUALS));
            } else {
                criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", StringUtils.EMPTY, CommonOperators.IS_EMPTY));
            }
            List<ControllerAlarmInfoContext> controllerAlarmInfoList = V3RecordAPI.getRecordsListWithSupplements(ControllerAlarmInfoModule.MODULE_NAME, null, ControllerAlarmInfoContext.class, criteria, null);
            if (CollectionUtils.isNotEmpty(controllerAlarmInfoList)) {
                ControllerAlarmInfoContext updateControllerAlarmInfo = new ControllerAlarmInfoContext();
                updateControllerAlarmInfo.setAlarmLastReceivedTime(System.currentTimeMillis());
                updateControllerAlarmInfo.setLastAlarmEvent(rawAlarm);
                updateControllerAlarmInfo.setFiltered(false);
                List<Long> controllerInfoIds = controllerAlarmInfoList.stream().map(ControllerAlarmInfoContext::getId).collect(Collectors.toList());
                V3RecordAPI.updateRecord(updateControllerAlarmInfo, controllerInfoModule, Arrays.asList(modBean.getField("alarmLastReceivedTime", ControllerAlarmInfoModule.MODULE_NAME),modBean.getField("filtered", ControllerAlarmInfoModule.MODULE_NAME),modBean.getField("lastAlarmEvent", ControllerAlarmInfoModule.MODULE_NAME)), controllerInfoIds);

            } else {
                ControllerAlarmInfoContext controllerAlarmInfo = new ControllerAlarmInfoContext();
                controllerAlarmInfo.setController(rawAlarm.getController());
                controllerAlarmInfo.setAsset(rawAlarm.getAsset());
                controllerAlarmInfo.setAlarmDefinition(rawAlarm.getAlarmDefinition());
                controllerAlarmInfo.setAlarmType(rawAlarm.getAlarmType());
                controllerAlarmInfo.setAlarmLastReceivedTime(System.currentTimeMillis());
                controllerAlarmInfo.setFiltered(false);
                controllerAlarmInfo.setAlarmApproach(AlarmApproach.REPEAT_UNTIL_RESOLVED.getIndex());
                controllerAlarmInfo.setLastAlarmEvent(rawAlarm);
                V3Util.createRecord(controllerInfoModule, Collections.singletonList(controllerAlarmInfo));
            }
        }
    }
}