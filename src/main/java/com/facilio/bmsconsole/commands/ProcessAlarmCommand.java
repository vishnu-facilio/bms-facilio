package com.facilio.bmsconsole.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ProcessAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject alarmInfo = (JSONObject) context.get(FacilioConstants.ContextNames.ALARM);
		AlarmContext alarm = null;
		List<FacilioField> fields = null;
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		int sourceType = -1;
		if (alarmInfo.get("sourceType") != null) {
			sourceType = Integer.parseInt(alarmInfo.get("sourceType").toString());
		}
		
		if (sourceType == SourceType.THRESHOLD_ALARM.getIntVal()) {
			fields = bean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
		}
		else {
			fields = bean.getAllFields(FacilioConstants.ContextNames.ALARM);
		}
		JSONObject additionalInfo = new JSONObject();
		Set<String> fieldNames = fields.stream().map(FacilioField::getName).collect(Collectors.toSet());
		Set<String> propNames = alarmInfo.keySet();
		Set<String> defaultProps = new HashSet<>();
		defaultProps.add("severityString");
		defaultProps.add("orgId");
		
		for (String propName : propNames) {
			if (!fieldNames.contains(propName) && !defaultProps.contains(propName)) {
				additionalInfo.put(propName, alarmInfo.get(propName));
			}
		}
		
		if (sourceType == 6) {
			alarm = FieldUtil.getAsBeanFromJson(alarmInfo, ReadingAlarmContext.class);
		}
		else {
			alarm = FieldUtil.getAsBeanFromJson(alarmInfo, AlarmContext.class);
		}
		
		if (!additionalInfo.isEmpty()) {
			alarm.setAdditionInfo(additionalInfo);
		}
		context.put(FacilioConstants.ContextNames.ALARM, alarm);
		
		return false;
	}

}
