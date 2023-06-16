package com.facilio.faults.newreadingalarm;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleInterface;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class HandleV3AlarmListLookupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        HashMap alarmData = (HashMap) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<BaseAlarmContext> alarms = (List<BaseAlarmContext>) alarmData.get(moduleName);
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");


        List<ReadingAlarm> readingAlarms = new ArrayList<>();
        List<ReadingAlarm> newReadingAlarms = new ArrayList<>();
        List<Long> oldRuleIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(alarms)) {

            NewAlarmAPI.loadAlarmLookups(alarms);
            if (moduleName.equals(FacilioConstants.ContextNames.AGENT_ALARM)) {
                NewAlarmAPI.updateAgentData(alarms);
            }

            for (BaseAlarmContext alarm : alarms) {
                if (alarm instanceof ReadingAlarm) {
                    ReadingAlarm readingAlarm = (ReadingAlarm) alarm;
                    if (readingAlarm.getIsNewReadingRule()) {
                        newReadingAlarms.add(readingAlarm);
                    } else {
                        readingAlarms.add(readingAlarm);
                        ReadingRuleInterface rule = readingAlarm.getRule();
                        Long ruleId = (rule != null) ? rule.getId() : -1;
                        oldRuleIds.add(ruleId);
                    }
                    readingAlarm.setSubRule(null);
                    readingAlarm.setReadingFieldName(bean.getField(readingAlarm.getReadingFieldId()).getDisplayName());
                }
                long alarmOccurrenceId = alarm.getLastOccurrenceId();
                alarm.setLastOccurrence(null);
                alarm.setLastOccurrenceId(alarmOccurrenceId);
            }

            List<Long> newRuleIds = new ArrayList<>();

            for(ReadingAlarm newReadingAlarm:newReadingAlarms){
                if(newReadingAlarm.getRule()!=null) {
                    if(newReadingAlarm.getRule() instanceof NewReadingRuleContext) {
                        newRuleIds.add(newReadingAlarm.getRule().getId());
                    } else {
                        LOGGER.info("NewAlarm with old rule: NEW ALARM ID " + newReadingAlarm.getId() + "Old Rule Id: "+  newReadingAlarm.getRule().getId());
                    }
                } else {
                    LOGGER.info("Id of New Alarm that doesn't have a Rule " + newReadingAlarm.getId());
                }
            }
            if (CollectionUtils.isNotEmpty(newRuleIds)) {
                Map<Long, Map<String, Object>> ruleMap = NewReadingRuleAPI.getReadingRuleNameAndImpactByIds(newRuleIds);
                newReadingAlarms.stream().filter(readingAlarm -> readingAlarm.getIsNewReadingRule() && readingAlarm.getRule()!=null).forEach(alarm -> {
                    LOGGER.info("alarmId"+alarm.getId()+"alarmRule"+alarm.getRule());
                    NewReadingRuleContext rule = (NewReadingRuleContext) alarm.getRule();
                    Map<String, Object> prop = ruleMap.get(rule.getId());
                    rule.setName((String) prop.get("name"));
                    rule.setImpact((FaultImpactContext) prop.get("impact"));
                    alarm.setNewRule(rule);
                });
            }
            if (!oldRuleIds.isEmpty()) {
                Map<Long, WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRulesAsMap(oldRuleIds, false, false);
                if (MapUtils.isNotEmpty(rules)) {
                    for (ReadingAlarm readingAlarm : readingAlarms) {
                        if (readingAlarm.getRule() != null && readingAlarm.getRule().getId() > 0) {
                            WorkflowRuleContext r = rules.get(readingAlarm.getRule().getId());
                            String ruleName = r != null ? r.getName() : "";
                            readingAlarm.getRule().setName(ruleName);
                        }
                    }
                }
            }


        }
        Map<String, List<? extends ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        recordMap.put(moduleName, alarms);
        context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
        return false;
    }
}
