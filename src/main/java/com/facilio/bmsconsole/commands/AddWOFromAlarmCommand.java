package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class AddWOFromAlarmCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		AlarmContext oldAlarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		if (oldAlarm == null || oldAlarm.getId() == -1) {
			Long alarmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			oldAlarm = AlarmAPI.getAlarm(alarmId);
		}
		else {
			context.put(FacilioConstants.ContextNames.RECORD_ID, oldAlarm.getId());
		}
		Object newObj = context.get(FacilioConstants.ContextNames.RECORD);
		
		if(oldAlarm != null && newObj != null) {
			if (oldAlarm.getWoId() != -1) {
				throw new IllegalArgumentException("Workorder is already created for the alarm");
			}
			
			JSONObject woJson = FieldUtil.mergeBean(oldAlarm, newObj);
			
			WorkOrderContext wo = FieldUtil.getAsBeanFromJson(woJson, WorkOrderContext.class);
			wo.setScheduledStart(oldAlarm.getModifiedTime());
			wo.setId(-1);
			
			context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
		}
		return false;
	}
}
