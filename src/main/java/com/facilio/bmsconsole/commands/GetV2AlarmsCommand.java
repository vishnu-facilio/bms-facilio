package com.facilio.bmsconsole.commands;

import com.facilio.agent.alarms.AgentAlarmContext;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.google.common.collect.Lists;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.Map;

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
            } else if (alarm instanceof ReadingAlarm) {
                ReadingAlarm readingAlarm = (ReadingAlarm) alarm;
                if(readingAlarm.getIsNewReadingRule()) {
                    Map<Long, String> ruleNameMap = NewReadingRuleAPI.getReadingRuleNamesByIds(Lists.newArrayList(readingAlarm.getRule().getId()));
                    String name = ruleNameMap.get(readingAlarm.getRule().getId());
                    readingAlarm.getRule().setName(name);
                }
            }
            context.put(FacilioConstants.ContextNames.RECORD, alarm);
            context.put(FacilioConstants.ContextNames.LATEST_ALARM_OCCURRENCE, latestAlarmOccurance);
        }
        return false;
    }

}
