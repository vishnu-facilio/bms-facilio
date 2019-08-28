package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
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
			for(BaseAlarmContext alarm: alarms) {
				alarm.setLastOccurrence(occurencesMap.get(alarm.getLastOccurrenceId()));
			}
		}
		return false;
	}

}
