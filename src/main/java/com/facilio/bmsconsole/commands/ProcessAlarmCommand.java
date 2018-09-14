package com.facilio.bmsconsole.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;

public class ProcessAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject alarmInfo = (JSONObject) context.get(FacilioConstants.ContextNames.ALARM);
		AlarmContext alarm = null;
		SourceType sourceType = null;
		if (alarmInfo.get("sourceType") != null) {
			sourceType = SourceType.getType(Integer.parseInt(alarmInfo.get("sourceType").toString()));
		}
		if (sourceType == null) {
			sourceType = SourceType.ALARM;
		}
		
		List<FacilioField> fields = AlarmAPI.getAlarmFields(sourceType);
		JSONObject additionalInfo = new JSONObject();
		Set<String> fieldNames = fields.stream().map(FacilioField::getName).collect(Collectors.toSet());
		Set<String> propNames = alarmInfo.keySet();
		Set<String> defaultProps = new HashSet<>();
		defaultProps.add("severityString");
		defaultProps.add("orgId");
		
		for (String propName : propNames) {
			Object val = alarmInfo.get(propName);
			if (val == null) {
				alarmInfo.remove(propName);
			}
			else {
				if (!fieldNames.contains(propName) && !defaultProps.contains(propName)) {
					additionalInfo.put(propName, val);
				}
			}
		}
		
		alarm = FieldUtil.getAsBeanFromJson(alarmInfo, AlarmAPI.getAlarmClass(sourceType));
		alarm.setSourceType(sourceType);
		if (!additionalInfo.isEmpty()) {
			alarm.setAdditionInfo(additionalInfo);
		}
		context.put(FacilioConstants.ContextNames.ALARM, alarm);
		
		return false;
	}
}
