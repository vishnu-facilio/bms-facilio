package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.wms.message.WmsEvent;
import com.facilio.wms.util.WmsApi;

public class AddAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		if(alarm != null) {
			
			if (alarm.getCreatedTime() == -1) {
				alarm.setCreatedTime(System.currentTimeMillis());
			}
			alarm.setModifiedTime(alarm.getCreatedTime());
			
			if(alarm.getEntityId() == -1)
			{
				long entityId = AlarmAPI.addAlarmEntity();
				alarm.setEntityId(entityId);
			}
			
			if((alarm.getSeverity() == null || alarm.getSeverity().getId() == -1) && alarm.getSeverityString() != null && !alarm.getSeverityString().isEmpty()) {
				alarm.setSeverity(AlarmAPI.getAlarmSeverity(alarm.getSeverityString()));
			}
			if(alarm.getSeverity() == null || alarm.getSeverity().getId() == -1) {
				alarm.setSeverity(AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.INFO_SEVERITY));
			}
			
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = null;
			String moduleName = null;
			if (alarm instanceof ReadingAlarmContext) {
				fields = bean.getAllFields(FacilioConstants.ContextNames.READING_ALARM);
				moduleName = FacilioConstants.ContextNames.READING_ALARM;
				alarm.setSourceType(TicketContext.SourceType.THRESHOLD_ALARM);
			}
			else {
				fields = bean.getAllFields(FacilioConstants.ContextNames.ALARM);
				moduleName = FacilioConstants.ContextNames.ALARM;
				alarm.setSourceType(TicketContext.SourceType.ALARM);
			}
			
			InsertRecordBuilder<AlarmContext> builder = new InsertRecordBuilder<AlarmContext>()
																.moduleName(moduleName)
																.fields(fields);
			
			AlarmAPI.updateAlarmDetailsInTicket(alarm, alarm);
			TicketAPI.updateTicketStatus(alarm);
			TicketAPI.updateTicketStatus(alarm);
			long alarmId = builder.insert(alarm);
			alarm.setId(alarmId);
			context.put(FacilioConstants.ContextNames.RECORD, alarm);
			context.put(FacilioConstants.ContextNames.RECORD_ID, alarmId);
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
			
			JSONObject record = new JSONObject();
			record.put("id", alarmId);
			
			WmsEvent event = new WmsEvent();
			event.setNamespace("alarm");
			event.setAction("newAlarm");
			event.setEventType(WmsEvent.WmsEventType.RECORD_UPDATE);
			event.addData("record", record);
			event.addData("sound", true);
			
			List<User> users = AccountUtil.getOrgBean().getOrgUsers(AccountUtil.getCurrentOrg().getId(), true);
			List<Long> recipients = new ArrayList<>();
			for (User user : users) {
				recipients.add(user.getId());
			}
			WmsApi.sendEvent(recipients, event);
		}
		else {
			throw new IllegalArgumentException("WorkOrder Object cannot be null");
		}
		return false;
	}
	
}
