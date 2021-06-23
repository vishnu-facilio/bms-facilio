package com.facilio.bmsconsole.commands;

import java.util.Collections;

import com.facilio.agent.alarms.AgentAlarmContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;

public class GetV2AlarmsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (id > 0) {
			BaseAlarmContext alarm = NewAlarmAPI.getAlarm(id);
			
			AlarmOccurrenceContext latestAlarmOccurance = NewAlarmAPI.getLatestAlarmOccurance(alarm);
			
			NewAlarmAPI.loadAlarmLookups(Collections.singletonList(alarm));
			if (alarm instanceof AgentAlarmContext) {
				AgentAlarmContext agentAlarm = (AgentAlarmContext) alarm;
				NewAlarmAPI.updateAgentData(Collections.singletonList(agentAlarm));
			}
			
			context.put(FacilioConstants.ContextNames.RECORD, alarm);
			context.put(FacilioConstants.ContextNames.LATEST_ALARM_OCCURRENCE, latestAlarmOccurance);
		}
		return false;
	}

}
