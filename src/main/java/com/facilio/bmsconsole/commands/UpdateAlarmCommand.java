package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.wms.message.WmsEvent;
import com.facilio.wms.util.WmsApi;

public class UpdateAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(alarm != null && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(module));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			if(alarm.isAcknowledged()) {
				alarm.setAcknowledgedTime(System.currentTimeMillis());
				
				alarm.setAcknowledgedBy(AccountUtil.getCurrentUser());
			}
			
			boolean isCleared = false;
			if((alarm.getSeverity() == null || alarm.getSeverity().getId() == -1) && alarm.getSeverityString() != null && !alarm.getSeverityString().isEmpty()) {
				AlarmSeverityContext severity = AlarmAPI.getAlarmSeverity(alarm.getSeverityString());
				if(severity == null) {
					severity = AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.INFO_SEVERITY);
				}
				alarm.setSeverity(severity);
				if(alarm.getSeverityString().equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
					isCleared = true;
				}
				alarm.setModifiedTime(System.currentTimeMillis());
			}
			if(isCleared || (alarm.getSeverity() != null && alarm.getSeverity().getId() == AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY).getId())) {
				alarm.setClearedTime(System.currentTimeMillis());
				alarm.setClearedBy(AccountUtil.getCurrentUser());
				isCleared = true;
			}
			
			if(alarm.getSeverity() != null)
			{
				if(isCleared) {
					context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ALARM_CLEARED);
				}
				else {
					context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.UPDATED_ALARM_SEVERITY);
				}
			}
			if(recordIds.size() == 1) {
				AlarmContext alarmObj = getAlarmObj(idCondition, moduleName, fields);
				if(alarmObj != null) {
					AlarmAPI.updateAlarmDetailsInTicket(alarmObj, alarm);
				}
			}
			
			UpdateRecordBuilder<AlarmContext> updateBuilder = new UpdateRecordBuilder<AlarmContext>()
																		.moduleName(moduleName)
																		.table(dataTableName)
																		.fields(fields)
																		.andCondition(idCondition);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(alarm));
			if(recordIds.size() == 1) {
				AlarmContext alarmObj = getAlarmObj(idCondition, moduleName, fields);
				if(alarmObj != null) {
					context.put(FacilioConstants.ContextNames.RECORD, alarmObj);
				}
			}
			
			JSONObject record = new JSONObject();
			record.put("id", recordIds.get(0));
			
			WmsEvent event = new WmsEvent();
			event.setNamespace("alarm");
			event.setAction("newAlarm");
			event.setEventType(WmsEvent.WmsEventType.RECORD_UPDATE);
			event.addData("record", record);
			if(ActivityType.UPDATED_ALARM_SEVERITY.equals(context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE))) {
				event.addData("sound", true);
			}
			
			List<User> users = AccountUtil.getOrgBean().getOrgUsers(AccountUtil.getCurrentOrg().getId(), true);
			List<Long> recipients = new ArrayList<>();
			for (User user : users) {
				recipients.add(user.getId());
			}
			WmsApi.sendEvent(recipients, event);
		}
		return false;
	}
	
	private AlarmContext getAlarmObj(Condition idCondition, String moduleName, List<FacilioField> fields) throws Exception {
		SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
																.moduleName(moduleName)
																.beanClass(AlarmContext.class)
																.select(fields)
																.andCondition(idCondition);
		
		List<AlarmContext> alarms = builder.get();
		if(alarms != null && !alarms.isEmpty()) {
			return alarms.get(0);
		}
		return null;
	}

}
