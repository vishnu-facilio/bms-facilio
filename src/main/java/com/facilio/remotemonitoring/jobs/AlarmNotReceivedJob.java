package com.facilio.remotemonitoring.jobs;

import com.facilio.agentv2.controller.Controller;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleCriteriaModule;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleModule;
import com.facilio.remotemonitoring.signup.ControllerAlarmInfoModule;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Log4j
public class AlarmNotReceivedJob extends FacilioJob {
    @Override
    public void execute(JobContext jc) throws Exception {
        // TODO Auto-generated method stub

        long filterCriteriaId = jc.getJobId();
        FilterRuleCriteriaContext filterRuleCriteria = V3RecordAPI.getRecord(AlarmFilterRuleCriteriaModule.MODULE_NAME,filterCriteriaId,FilterRuleCriteriaContext.class);
        if(filterRuleCriteria != null && filterRuleCriteria.getControllerType() != null && filterRuleCriteria.getAlarmFilterRule() != null) {
            AlarmFilterRuleContext alarmFilterRule = V3RecordAPI.getRecord(AlarmFilterRuleModule.MODULE_NAME,filterRuleCriteria.getAlarmFilterRule().getId(),AlarmFilterRuleContext.class);
            filterRuleCriteria.setAlarmFilterRule(alarmFilterRule);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER_TYPE","controllerType",String.valueOf(filterRuleCriteria.getControllerTypeIndex()), NumberOperators.EQUALS));
            List<Controller> controllerListForTypes = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.CONTROLLER, null, Controller.class, criteria, null);
            if(CollectionUtils.isNotEmpty(controllerListForTypes)) {
                for(Controller controller : controllerListForTypes) {
                    checkAndFilterAlarm(controller,filterRuleCriteria);
                }
            }
        }
    }

    private static void checkAndFilterAlarm(Controller controller,FilterRuleCriteriaContext filterRuleCriteria) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(filterRuleCriteria != null && filterRuleCriteria.getControllerType() != null && filterRuleCriteria.getAlarmDefinition() != null && filterRuleCriteria.getAlarmFilterRule() != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER","controller",String.valueOf(controller.getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE","alarmType",String.valueOf(filterRuleCriteria.getAlarmFilterRule().getAlarmType().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_DEFINITION","alarmDefinition",String.valueOf(filterRuleCriteria.getAlarmDefinition().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("STRATEGY","strategy",String.valueOf(filterRuleCriteria.getAlarmFilterRule().getStrategy()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("FILTERED","filtered",String.valueOf(false), BooleanOperators.IS));
            List<ControllerAlarmInfoContext> controllersAlarmInfo = V3RecordAPI.getRecordsListWithSupplements(ControllerAlarmInfoModule.MODULE_NAME, null, ControllerAlarmInfoContext.class, criteria, null);
            if(CollectionUtils.isNotEmpty(controllersAlarmInfo)) {
                for(ControllerAlarmInfoContext controllerAlarmInfo : controllersAlarmInfo) {
                    Long currentTimeInMillis = System.currentTimeMillis();
                    Long timeDelta = currentTimeInMillis - controllerAlarmInfo.getAlarmLastReceivedTime();
                    if(timeDelta > filterRuleCriteria.getAlarmDuration()) {
                        ControllerAlarmInfoContext updateControllerAlarmInfo = new ControllerAlarmInfoContext();
                        updateControllerAlarmInfo.setFiltered(true);
                        V3RecordAPI.updateRecord(updateControllerAlarmInfo,modBean.getModule(ControllerAlarmInfoModule.MODULE_NAME), Collections.singletonList(modBean.getField("filtered", ControllerAlarmInfoModule.MODULE_NAME)),Collections.singletonList(controllerAlarmInfo.getId()));
                        RawAlarmContext rawAlarm = constructRawAlarm(filterRuleCriteria,controller,controllerAlarmInfo);
                        RawAlarmUtil.addRawAlarm(rawAlarm);
                        AlarmFilterCriteriaType.NO_ALARM_RECEIVED_FOR_SPECIFIC_PERIOD.getHandler(rawAlarm).createFilteredAlarm(rawAlarm,filterRuleCriteria);
                    }
                }
            }
        }
    }

    private static RawAlarmContext constructRawAlarm(FilterRuleCriteriaContext filterRuleCriteria,Controller controller,ControllerAlarmInfoContext controllerAlarmInfo) throws Exception {
        if(filterRuleCriteria.getAlarmFilterRule() != null && controllerAlarmInfo != null) {
            RawAlarmContext rawAlarm = new RawAlarmContext();
            rawAlarm.setFiltered(true);
            rawAlarm.setController(controller);
            rawAlarm.setAsset(controllerAlarmInfo.getAsset());
            rawAlarm.setAlarmType(filterRuleCriteria.getAlarmFilterRule().getAlarmType());
            rawAlarm.setStrategy(filterRuleCriteria.getAlarmFilterRule().getStrategy());
            rawAlarm.setAlarmDefinition(filterRuleCriteria.getAlarmDefinition());
            V3SiteContext site = new V3SiteContext();
            site.setId(controller.getSiteId());
            rawAlarm.setSite(site);
            rawAlarm.setOccurredTime(System.currentTimeMillis());
            rawAlarm.setFilterRuleCriteriaId(filterRuleCriteria.getId());
            rawAlarm.setClient(filterRuleCriteria.getAlarmFilterRule().getClient());
            rawAlarm.setProcessed(true);
            rawAlarm.setMessage("System Generated Alarm");
            rawAlarm.setSourceType(RawAlarmContext.RawAlarmSourceType.SYSTEM);
            return rawAlarm;
        }
        return null;
    }
}