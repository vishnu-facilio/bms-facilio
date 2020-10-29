package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.sensor.SensorAlarmContext;
import com.facilio.bmsconsole.context.sensor.SensorRollUpAlarmContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;

public class FetchRelatedAlarmsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long alarmId = (long) context.get(FacilioConstants.ContextNames.ALARM_ID);
		SensorRollUpAlarmContext alarm = AlarmAPI.getSensorAlarms(alarmId);
		if (alarm != null) {
		List<SensorAlarmContext> childAlarms = AlarmAPI.getSensorChildAlarms(alarm, -1, -1);
		context.put(FacilioConstants.ContextNames.RELATED_ALARMS, childAlarms);
		}
		return false;
	}

	
}
