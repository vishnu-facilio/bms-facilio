package com.facilio.bmsconsole.commands;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.agent.controller.FacilioDataType;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.sensor.SensorAlarmContext;
import com.facilio.bmsconsole.context.sensor.SensorRollUpAlarmContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.report.context.ReportContext;
import com.facilio.time.DateRange;
import com.facilio.workflows.functions.FacilioDateFunction;
import com.facilio.workflows.functions.FacilioDateRangeFunctions;

public class FetchRelatedAlarmsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long alarmId = (long) context.get(FacilioConstants.ContextNames.ALARM_ID);
		SensorRollUpAlarmContext alarm = AlarmAPI.getSensorAlarms(alarmId);
		if (alarm != null) {
		List<SensorAlarmContext> childAlarms = AlarmAPI.getSensorChildAlarms(alarm, -1, -1);
		context.put(FacilioConstants.ContextNames.RELATED_ALARMS, childAlarms);

		if (childAlarms != null && !childAlarms.isEmpty()) {
			DateRange range = DateOperators.LAST_N_MONTHS.getRange("12");
		    System.out.println("range" + range);
	
			for (SensorAlarmContext childAlarm : childAlarms) {
				childAlarm.setTotalDuration(AlarmAPI.getOccurencesDuration(childAlarm.getId(), range));
				childAlarm.setAverageFrequencyFailure(AlarmAPI.getOccurencesAverageFrequencyFailure(childAlarm.getId(), range));
			}
		}
		}
		return false;
	}

	
}
