package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;

public class HandleV2AlarmListLookupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<BaseAlarmContext> alarms =  (List<BaseAlarmContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(alarms)) {
			context.put(FacilioConstants.ContextNames.RECORD_LIST, alarms);
			
			NewAlarmAPI.loadAlarmLookups(alarms);
			
			Map<Long, AlarmOccurrenceContext> occurencesMap = NewAlarmAPI.getLatestAlarmOccuranceMap(alarms);
			for (AlarmOccurrenceContext occurrence : occurencesMap.values()) {
				occurrence.setAlarm(null);
				occurrence.setResource(null);
			}
			for(BaseAlarmContext alarm: alarms) {
				if (alarm instanceof ReadingAlarm) {
					ReadingAlarm readingAlarm = (ReadingAlarm) alarm;
//					readingAlarm.setRule(null);
					readingAlarm.setSubRule(null);
				}
				// AlarmOccurrenceContext occurrenceContext = occurencesMap.get(alarm.getLastOccurrenceId());
				alarm.setLastOccurrence(null);
			}
		}
		return false;
	}

}
