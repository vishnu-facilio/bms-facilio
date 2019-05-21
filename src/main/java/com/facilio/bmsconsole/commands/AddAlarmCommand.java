package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.wms.message.WmsEvent;
import com.facilio.wms.util.WmsApi;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class AddAlarmCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(AddAlarmCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		if(alarm != null) {
			alarm.setCreatedBy(AccountUtil.getCurrentUser());
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
			
			List<FacilioField> fields = AlarmAPI.getAlarmFields(alarm.getSourceTypeEnum());
			String moduleName = AlarmAPI.getAlarmModuleName(alarm.getSourceTypeEnum());
			
			InsertRecordBuilder<AlarmContext> builder = new InsertRecordBuilder<AlarmContext>()
																.moduleName(moduleName)
																.fields(fields);
			
			builder.withLocalId();
//			LOGGER.info("Alarm Obj in insert : "+FieldUtil.getAsJSON(alarm).toJSONString());
			AlarmAPI.updateAlarmDetailsInTicket(alarm, alarm);
//			LOGGER.info("Alarm Obj after ticket details : "+FieldUtil.getAsJSON(alarm).toJSONString());
			TicketAPI.updateTicketStatus(alarm);
//			LOGGER.info("Alarm Obj after status  : "+FieldUtil.getAsJSON(alarm).toJSONString());
			long alarmId = builder.insert(alarm);
			alarm.setId(alarmId);
			context.put(FacilioConstants.ContextNames.RECORD, alarm);
			context.put(FacilioConstants.ContextNames.RECORD_ID, alarmId);
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
			
			JSONObject record = new JSONObject();
			record.put("id", alarmId);
			
			try {
				if (AccountUtil.getCurrentOrg().getOrgId() != 88 || (alarm.getSeverity() != null && alarm.getSeverity().getId() == AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CRITICAL_SEVERITY).getId())) {
					WmsEvent event = new WmsEvent();
					event.setNamespace("alarm");
					event.setAction("newAlarm");
					event.setEventType(WmsEvent.WmsEventType.RECORD_UPDATE);
					event.addData("record", record);
					event.addData("sound", true);
					
					List<User> users = AccountUtil.getOrgBean().getActiveOrgUsers(AccountUtil.getCurrentOrg().getId());
					List<Long> recipients = users.stream().map(user -> user.getId()).collect(Collectors.toList());
					
					WmsApi.sendEvent(recipients, event);
				}
			}
			catch (Exception e) {
				LOGGER.info("Exception occcurred while pushing Web notification during alarm creation ", e);
			}
		}
		else {
			throw new IllegalArgumentException("WorkOrder Object cannot be null");
		}
		return false;
	}
	
}
