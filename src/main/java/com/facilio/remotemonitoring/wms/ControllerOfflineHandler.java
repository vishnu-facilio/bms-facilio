package com.facilio.remotemonitoring.wms;

import com.facilio.agentv2.controller.Controller;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringSystemEnumOperators;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.handler.ImsHandler;
import com.facilio.modules.FieldUtil;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleCriteriaModule;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleModule;
import com.facilio.remotemonitoring.signup.ControllerAlarmInfoModule;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import java.util.Collections;
import java.util.List;

@Log4j
public class ControllerOfflineHandler extends ImsHandler {
    public void processMessage(Message message) {

        try {
            JSONObject json = message.getContent();
            ControllerOfflineJobInfoContext controllerOfflineJobInfo = FieldUtil.getAsBeanFromJson(json, ControllerOfflineJobInfoContext.class);
            if(controllerOfflineJobInfo != null) {
                Criteria filterRuleBuilderCriteria = new Criteria();
                filterRuleBuilderCriteria.addAndCondition(CriteriaAPI.getCondition("ALARM_FILTER_CRITERIA_TYPE", "alarmFilterCriteriaType", AlarmFilterCriteriaType.NO_ALARM_RECEIVED_FOR_SPECIFIC_PERIOD.getIndex(), StringSystemEnumOperators.IS));
                List<FilterRuleCriteriaContext> filterRuleCriteriaList = V3RecordAPI.getRecordsListWithSupplements(AlarmFilterRuleCriteriaModule.MODULE_NAME, null, FilterRuleCriteriaContext.class, filterRuleBuilderCriteria, null);
                if (CollectionUtils.isNotEmpty(filterRuleCriteriaList)) {
                    for (FilterRuleCriteriaContext filterRuleCriteria : filterRuleCriteriaList) {
                        if (filterRuleCriteria != null && filterRuleCriteria.getControllerType() != null && filterRuleCriteria.getAlarmFilterRule() != null) {
                            AlarmFilterRuleContext alarmFilterRule = V3RecordAPI.getRecord(AlarmFilterRuleModule.MODULE_NAME, filterRuleCriteria.getAlarmFilterRule().getId(), AlarmFilterRuleContext.class);
                            if (alarmFilterRule != null && alarmFilterRule.isEnabled()) {
                                filterRuleCriteria.setAlarmFilterRule(alarmFilterRule);
                                Criteria criteria = new Criteria();
                                criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER_TYPE", "controllerType", String.valueOf(filterRuleCriteria.getControllerTypeIndex()), NumberOperators.EQUALS));
                                List<Controller> controllerListForTypes = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.CONTROLLER, null, Controller.class, criteria, null);
                                if (CollectionUtils.isNotEmpty(controllerListForTypes)) {
                                    for (Controller controller : controllerListForTypes) {
                                        checkAndFilterAlarm(controller, filterRuleCriteria, controllerOfflineJobInfo);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error Occured while adding Job: " + e);
        }
    }




    private static void checkAndFilterAlarm(Controller controller, FilterRuleCriteriaContext filterRuleCriteria, ControllerOfflineJobInfoContext controllerOfflineJobInfo) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        if(filterRuleCriteria != null && filterRuleCriteria.getControllerType() != null && filterRuleCriteria.getAlarmDefinition() != null && filterRuleCriteria.getAlarmFilterRule() != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER","controller",String.valueOf(controller.getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_TYPE","alarmType",String.valueOf(filterRuleCriteria.getAlarmFilterRule().getAlarmType().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_DEFINITION","alarmDefinition",String.valueOf(filterRuleCriteria.getAlarmDefinition().getId()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("STRATEGY","alarmApproach",String.valueOf(filterRuleCriteria.getAlarmFilterRule().getAlarmApproach()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("FILTERED","filtered",String.valueOf(false), BooleanOperators.IS));
            List<ControllerAlarmInfoContext> controllersAlarmInfo = V3RecordAPI.getRecordsListWithSupplements(ControllerAlarmInfoModule.MODULE_NAME, null, ControllerAlarmInfoContext.class, criteria, null);
            if(CollectionUtils.isNotEmpty(controllersAlarmInfo)) {
                for(ControllerAlarmInfoContext controllerAlarmInfo : controllersAlarmInfo) {
                    Long timeStamp = controllerOfflineJobInfo.getTimeStamp();
                    Long timeDelta = timeStamp - controllerAlarmInfo.getAlarmLastReceivedTime();
                    if(timeDelta > filterRuleCriteria.getAlarmDuration()) {
                        ControllerAlarmInfoContext updateControllerAlarmInfo = new ControllerAlarmInfoContext();
                        updateControllerAlarmInfo.setFiltered(true);
                        V3RecordAPI.updateRecord(updateControllerAlarmInfo,modBean.getModule(ControllerAlarmInfoModule.MODULE_NAME), Collections.singletonList(modBean.getField("filtered", ControllerAlarmInfoModule.MODULE_NAME)),Collections.singletonList(controllerAlarmInfo.getId()));
                        RawAlarmContext rawAlarm = constructRawAlarm(filterRuleCriteria,controller,controllerAlarmInfo);
                        Pair<AlarmDefinitionMappingContext,RawAlarmContext> pair = RawAlarmUtil.processMessage(rawAlarm);
                        if(pair != null) {
                            RawAlarmContext processedRawAlarm = RawAlarmUtil.checkAndCreateAlarmDefinition(pair.getLeft(),pair.getRight());
                            if(processedRawAlarm.getAlarmDefinition() != null) {
                                processedRawAlarm = RawAlarmUtil.tagRawAlarm(processedRawAlarm);
                                if(processedRawAlarm.getAlarmApproachEnum() == AlarmApproach.RETURN_TO_NORMAL) {
                                    RawAlarmUtil.clearPreviousRawAlarmsForRTN(processedRawAlarm);
                                }
                                processedRawAlarm.setAlarmType(alarmBean.getAlarmType(RemoteMonitorConstants.SystemAlarmTypes.CONTROLLER_OFFLINE));
                                RawAlarmContext addedAlarm = RawAlarmUtil.addAndGetRawAlarm(processedRawAlarm);
                                AlarmFilterCriteriaType.NO_ALARM_RECEIVED_FOR_SPECIFIC_PERIOD.getHandler(rawAlarm).createFilteredAlarm(addedAlarm,filterRuleCriteria);
                            }
                        }
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
            rawAlarm.setAlarmApproach(filterRuleCriteria.getAlarmFilterRule().getAlarmApproach());
            V3SiteContext site = new V3SiteContext();
            site.setId(controller.getSiteId());
            rawAlarm.setSite(site);
            rawAlarm.setOccurredTime(System.currentTimeMillis());
            rawAlarm.setFilterRuleCriteriaId(filterRuleCriteria.getId());
            rawAlarm.setClient(filterRuleCriteria.getAlarmFilterRule().getClient());
            rawAlarm.setAsset(controllerAlarmInfo.getAsset());
            rawAlarm.setProcessed(true);
            rawAlarm.setMessage(filterRuleCriteria.getMessage());
            rawAlarm.setSourceType(RawAlarmContext.RawAlarmSourceType.SYSTEM);
            return rawAlarm;
        }
        return null;
    }
}