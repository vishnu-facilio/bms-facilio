package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import org.apache.log4j.Logger;

public class HandleV2AlarmListLookupCommand extends FacilioCommand {


	private static final Logger LOGGER = Logger.getLogger(HandleV2AlarmListLookupCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<BaseAlarmContext> alarms =  (List<BaseAlarmContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 393l){
			LOGGER.info("HandleV2AlarmListLookupCommand: modulename : " + moduleName + " , alarms : " + alarms);
		}

		List<ReadingAlarm> readingAlarms = new ArrayList<>();
		List<Long> ruleIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(alarms)) {
			context.put(FacilioConstants.ContextNames.RECORD_LIST, alarms);
			
			NewAlarmAPI.loadAlarmLookups(alarms);
			if (moduleName.equals(FacilioConstants.ContextNames.AGENT_ALARM)) {
				NewAlarmAPI.updateAgentData(alarms);
			}

//			Map<Long, AlarmOccurrenceContext> occurencesMap = NewAlarmAPI.getLatestAlarmOccuranceMap(alarms);
//			for (AlarmOccurrenceContext occurrence : occurencesMap.values()) {
//				occurrence.setAlarm(null);
//				occurrence.setResource(null);
//			}

			for(BaseAlarmContext alarm: alarms) {
				if (alarm instanceof ReadingAlarm) {
					ReadingAlarm readingAlarm = (ReadingAlarm) alarm;
					readingAlarms.add(readingAlarm);
					if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 393l){
						LOGGER.info("HandleV2AlarmListLookupCommand : " + readingAlarm);
					}
					ruleIds.add(readingAlarm.getRule().getId());
//					readingAlarm.setRule(null);
					readingAlarm.setSubRule(null);
				}
				long alarmOccurrenceId = alarm.getLastOccurrenceId();
				alarm.setLastOccurrence(null);
				alarm.setLastOccurrenceId(alarmOccurrenceId);
				// AlarmOccurrenceContext occurrenceContext = occurencesMap.get(alarm.getLastOccurrenceId());

			}

			if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 393l){
					LOGGER.info("HandleV2AlarmListLookupCommand: reading alarms : " + readingAlarms);
				}
			
			if (!ruleIds.isEmpty()) {
				Map<Long, WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRulesAsMap(ruleIds, false, false);
				if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 393l){
					LOGGER.info("HandleV2AlarmListLookupCommand: ruleids : " + ruleIds + "  ,  rules : " + rules);
				}
				for (ReadingAlarm readingAlarm : readingAlarms) {
					if (readingAlarm.getRule() != null && readingAlarm.getRule().getId() > 0) {
						readingAlarm.getRule().setName(rules.get(readingAlarm.getRule().getId()).getName());
					}
				}
			}
			
			
		}
		return false;
	}

}
