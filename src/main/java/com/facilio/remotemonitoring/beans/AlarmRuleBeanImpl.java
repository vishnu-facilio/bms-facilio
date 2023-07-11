package com.facilio.remotemonitoring.beans;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.v3.util.V3Util;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AlarmRuleBeanImpl implements AlarmRuleBean {

    @Override
    public List<AlarmDefinitionMappingContext> getAlarmDefinitionMappingsForClient(@NonNull Long clientId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID","client",String.valueOf(clientId), NumberOperators.EQUALS));
        List<AlarmDefinitionMappingContext> alarmDefinitionMappings = V3RecordAPI.getRecordsListWithSupplements(AlarmDefinitionMappingModule.MODULE_NAME, null, AlarmDefinitionMappingContext.class, criteria, null, null,null);
        if(CollectionUtils.isNotEmpty(alarmDefinitionMappings)) {
            alarmDefinitionMappings.sort(Comparator.comparing(definition -> definition.getPriority()));
            return alarmDefinitionMappings;
        }
        return null;
    }

    @Override
    public AlarmTypeContext getUncategorisedAlarmType() throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("UNCATEGORISED_ALARM","uncategorisedAlarm",String.valueOf(true), BooleanOperators.IS));
        List<AlarmTypeContext> alarmTypes = V3RecordAPI.getRecordsListWithSupplements(AlarmTypeModule.MODULE_NAME, null, AlarmTypeContext.class, criteria, null, null,null);
        if(CollectionUtils.isNotEmpty(alarmTypes)) {
            return alarmTypes.get(0);
        }
        return null;
    }

    @Override
    public List<AlarmDefinitionTaggingContext> getAlarmDefinitionTaggings(@NonNull Long alarmDefinitionId,@NonNull ControllerType controllerType) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_DEFINITION","alarmDefinition",String.valueOf(alarmDefinitionId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER_TYPE","controllerType",String.valueOf(controllerType.getIndex()), NumberOperators.EQUALS));
        List<AlarmDefinitionTaggingContext> alarmDefinitionTaggings = V3RecordAPI.getRecordsListWithSupplements(AlarmDefinitionTaggingModule.MODULE_NAME, null, AlarmDefinitionTaggingContext.class, criteria, null, null,null);
        if(CollectionUtils.isNotEmpty(alarmDefinitionTaggings)) {
            return alarmDefinitionTaggings;
        }
        return null;
    }

    @Override
    public List<AlarmFilterRuleContext> getAlarmFilterRulesForClient(@NonNull Long clientId) throws Exception {
        Criteria clientCriteria = new Criteria();
        clientCriteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID","client",String.valueOf(clientId), NumberOperators.EQUALS));
        clientCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status",String.valueOf(Boolean.TRUE), BooleanOperators.IS));
        List<AlarmFilterRuleContext> alarmFilterRules = V3RecordAPI.getRecordsListWithSupplements(AlarmFilterRuleModule.MODULE_NAME, null, AlarmFilterRuleContext.class, clientCriteria, null);
        if(CollectionUtils.isNotEmpty(alarmFilterRules)) {
            for(AlarmFilterRuleContext alarmFilterRule : alarmFilterRules) {
                Criteria filterRuleCriteria = new Criteria();
                filterRuleCriteria.addAndCondition(CriteriaAPI.getCondition("ALARM_FILTER_RULE_ID","alarmFilterRule",String.valueOf(alarmFilterRule.getId()), NumberOperators.EQUALS));
                List<FilterRuleCriteriaContext> filterRuleCriteriaList = V3RecordAPI.getRecordsListWithSupplements(AlarmFilterRuleCriteriaModule.MODULE_NAME, null, FilterRuleCriteriaContext.class, filterRuleCriteria, null);
                alarmFilterRule.setFilterRuleCriteriaList(filterRuleCriteriaList);
                if(alarmFilterRule.getSiteCriteriaId() != null && alarmFilterRule.getSiteCriteriaId() > 0) {
                    alarmFilterRule.setSiteCriteria(CriteriaAPI.getCriteria(alarmFilterRule.getSiteCriteriaId()));
                }
                if(alarmFilterRule.getControllerCriteriaId() != null && alarmFilterRule.getControllerCriteriaId() > 0) {
                    alarmFilterRule.setControllerCrtieria(CriteriaAPI.getCriteria(alarmFilterRule.getControllerCriteriaId()));
                }
            }
            return alarmFilterRules;
         }
        return null;
    }

    @Override
    public List<FlaggedEventRuleContext> getFlaggedEventRulesForClient(@NonNull Long clientId) throws Exception {
        Criteria clientCriteria = new Criteria();
        clientCriteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID","client",String.valueOf(clientId), NumberOperators.EQUALS));
        clientCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status",String.valueOf(Boolean.TRUE), BooleanOperators.IS));
        List<FlaggedEventRuleContext> flaggedEventRuleList = V3RecordAPI.getRecordsListWithSupplements(FlaggedEventRuleModule.MODULE_NAME, null, FlaggedEventRuleContext.class, clientCriteria, null);
        if(CollectionUtils.isNotEmpty(flaggedEventRuleList)) {
            for(FlaggedEventRuleContext flaggedEventRule : flaggedEventRuleList) {
                Criteria flaggedEventAlarmTypeRelCriteria = new Criteria();
                flaggedEventAlarmTypeRelCriteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE","flaggedEventRule",String.valueOf(flaggedEventRule.getId()), NumberOperators.EQUALS));
                List<FlaggedEventRuleAlarmTypeRel> flaggedEventRuleAlarmTypeRels = V3RecordAPI.getRecordsListWithSupplements(FlaggedEventAlarmTypeRelModule.MODULE_NAME, null, FlaggedEventRuleAlarmTypeRel.class, flaggedEventAlarmTypeRelCriteria, null);
                flaggedEventRule.setFlaggedEventRuleAlarmTypeRel(flaggedEventRuleAlarmTypeRels);
                if(flaggedEventRule.getSiteCriteriaId() != null && flaggedEventRule.getSiteCriteriaId() > 0) {
                    flaggedEventRule.setSiteCriteria(CriteriaAPI.getCriteria(flaggedEventRule.getSiteCriteriaId()));
                }
                if(flaggedEventRule.getControllerCriteriaId() != null && flaggedEventRule.getControllerCriteriaId() > 0) {
                    flaggedEventRule.setControllerCriteria(CriteriaAPI.getCriteria(flaggedEventRule.getControllerCriteriaId()));
                }
            }
            flaggedEventRuleList.sort(Comparator.comparing(rule -> rule.getPriority()));
            return flaggedEventRuleList;
        }
        return null;
    }

    @Override
    public FlaggedEventRuleContext getFlaggedEventRule(@NonNull Long id) throws Exception {
        Criteria clientCriteria = new Criteria();
        clientCriteria.addAndCondition(CriteriaAPI.getCondition("ID","id",String.valueOf(id), NumberOperators.EQUALS));
        clientCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS","status",String.valueOf(Boolean.TRUE), BooleanOperators.IS));
        List<FlaggedEventRuleContext> flaggedEventRuleList = V3RecordAPI.getRecordsListWithSupplements(FlaggedEventRuleModule.MODULE_NAME, null, FlaggedEventRuleContext.class, clientCriteria, null);
        if(CollectionUtils.isNotEmpty(flaggedEventRuleList)) {
            for(FlaggedEventRuleContext flaggedEventRule : flaggedEventRuleList) {
                Criteria flaggedEventAlarmTypeRelCriteria = new Criteria();
                flaggedEventAlarmTypeRelCriteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE","flaggedEventRule",String.valueOf(flaggedEventRule.getId()), NumberOperators.EQUALS));
                List<FlaggedEventRuleAlarmTypeRel> flaggedEventRuleAlarmTypeRels = V3RecordAPI.getRecordsListWithSupplements(FlaggedEventAlarmTypeRelModule.MODULE_NAME, null, FlaggedEventRuleAlarmTypeRel.class, flaggedEventAlarmTypeRelCriteria, null);
                flaggedEventRule.setFlaggedEventRuleAlarmTypeRel(flaggedEventRuleAlarmTypeRels);
                if(flaggedEventRule.getSiteCriteriaId() != null && flaggedEventRule.getSiteCriteriaId() > 0) {
                    flaggedEventRule.setSiteCriteria(CriteriaAPI.getCriteria(flaggedEventRule.getSiteCriteriaId()));
                }
                if(flaggedEventRule.getControllerCriteriaId() != null && flaggedEventRule.getControllerCriteriaId() > 0) {
                    flaggedEventRule.setControllerCriteria(CriteriaAPI.getCriteria(flaggedEventRule.getControllerCriteriaId()));
                }
                flaggedEventRule.setFlaggedEventRuleBureauEvaluationContexts(RemoteMonitorUtils.getFlaggedEventBureauEval(flaggedEventRule.getId()));
                flaggedEventRule.setFieldMapping(RemoteMonitorUtils.getFlaggedEventRuleWOFieldMapping(flaggedEventRule.getId()));
                flaggedEventRule.setFlaggedEventRuleClosureConfig(RemoteMonitorUtils.getFlaggedEventRuleClosureConfig(flaggedEventRule.getId()));
            }
            flaggedEventRuleList.sort(Comparator.comparing(rule -> rule.getPriority()));
            return flaggedEventRuleList.get(0);
        }
        return null;
    }
}