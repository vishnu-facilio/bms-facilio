package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandleV2AlarmListLookupCommand extends FacilioCommand {


    private static final Logger LOGGER = Logger.getLogger(HandleV2AlarmListLookupCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        HashMap alarmData = (HashMap) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<BaseAlarmContext> alarms = (List<BaseAlarmContext>) alarmData.get(moduleName);


        List<ReadingAlarm> readingAlarms = new ArrayList<>();
        List<ReadingAlarm> newReadingAlarms = new ArrayList<>();
        List<Long> oldRuleIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(alarms)) {

            NewAlarmAPI.loadAlarmLookups(alarms);
            if (moduleName.equals(FacilioConstants.ContextNames.AGENT_ALARM)) {
                NewAlarmAPI.updateAgentData(alarms);
            }

//			Map<Long, AlarmOccurrenceContext> occurencesMap = NewAlarmAPI.getLatestAlarmOccuranceMap(alarms);
//			for (AlarmOccurrenceContext occurrence : occurencesMap.values()) {
//				occurrence.setAlarm(null);
//				occurrence.setResource(null);
//			}

            for (BaseAlarmContext alarm : alarms) {
                if (alarm instanceof ReadingAlarm) {
                    ReadingAlarm readingAlarm = (ReadingAlarm) alarm;
                    if (readingAlarm.getIsNewReadingRule()) {
                        newReadingAlarms.add(readingAlarm);
                    } else {
                        readingAlarms.add(readingAlarm);
                        oldRuleIds.add(readingAlarm.getRule().getId());
                    }
//					readingAlarm.setRule(null);
                    readingAlarm.setSubRule(null);
                }
                long alarmOccurrenceId = alarm.getLastOccurrenceId();
                alarm.setLastOccurrence(null);
                alarm.setLastOccurrenceId(alarmOccurrenceId);
                // AlarmOccurrenceContext occurrenceContext = occurencesMap.get(alarm.getLastOccurrenceId());

            }

            List<Long> newRuleIds = newReadingAlarms.stream().map(el -> el.getRule().getId()).collect(Collectors.toList());
            if (!newRuleIds.isEmpty()) {
                Map<Long, String> ruleNameMap = NewReadingRuleAPI.getReadingRuleNamesByIds(newRuleIds);
                for (ReadingAlarm alarm : newReadingAlarms) {
                    if (alarm.getIsNewReadingRule()) {
                        alarm.getRule().setName(ruleNameMap.get(alarm.getRule().getId()));
                    }
                }
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
        context.put(FacilioConstants.ContextNames.RECORD_MAP,  recordMap);
        return false;
    }

}
