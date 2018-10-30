package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	
	private static final List<String> EVENT_INTERNAL_PROPS = Collections.unmodifiableList(initDefaultEventProps());
	private static List<String> initDefaultEventProps() {
		List<String> defaultProps = new ArrayList<>();
		defaultProps.add("eventStateEnum");
		defaultProps.add("internalStateEnum");
		
		return defaultProps;
	}

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
		Set<String> defaultProps = new HashSet<>();
		defaultProps.add("severityString");
		defaultProps.add("orgId");
		defaultProps.add("siteId");
		
		String comment = (String) alarmInfo.remove("comment");
		
		Iterator<Map.Entry<String, Object>> itr = alarmInfo.entrySet().iterator();
		
		while (itr.hasNext()) {
			Map.Entry<String, Object> entry = itr.next();
			Object val = entry.getValue();
			if (val == null || val.toString().isEmpty()) {
				itr.remove();
			}
			else {
				if (!fieldNames.contains(entry.getKey()) && !defaultProps.contains(entry.getKey()) && !EVENT_INTERNAL_PROPS.contains(entry.getKey())) {
					additionalInfo.put(entry.getKey(), val);
				}
			}
		}
		
		alarm = FieldUtil.getAsBeanFromJson(alarmInfo, AlarmAPI.getAlarmClass(sourceType));
		alarm.setSourceType(sourceType);
		if (!additionalInfo.isEmpty()) {
			alarm.setAdditionInfo(additionalInfo);
		}
		context.put(FacilioConstants.ContextNames.ALARM, alarm);
		context.put(FacilioConstants.ContextNames.COMMENT, comment);
		
		return false;
	}
}
