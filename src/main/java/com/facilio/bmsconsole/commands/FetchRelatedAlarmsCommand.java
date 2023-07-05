package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.alarms.sensor.context.sensoralarm.SensorAlarmContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpAlarmContext;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.time.DateRange;

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
		    
		    List<Long> sensorRuleIds = childAlarms.stream().map(childAlarm -> childAlarm.getSensorRule().getId()).collect(Collectors.toList());	
		    HashMap<Long, JSONObject> sensorRuleValidatorPropsMap = SensorRuleUtil.getSensorRuleValidatorPropsByParentRuleIds(sensorRuleIds);
	
			for (SensorAlarmContext childAlarm : childAlarms) {
				
				if (sensorRuleValidatorPropsMap != null && sensorRuleValidatorPropsMap.get(childAlarm.getSensorRule().getId()) != null) {
					childAlarm.getSensorRule().setRulePropInfo(sensorRuleValidatorPropsMap.get(childAlarm.getSensorRule().getId()));
				}
				
				childAlarm.setTotalDuration(AlarmAPI.getOccurencesDuration(childAlarm.getId(), range));
				childAlarm.setAverageFrequencyFailure(AlarmAPI.getOccurencesAverageFrequencyFailure(childAlarm.getId(), range));
			}
		}
		}
		return false;
	}

	
}
