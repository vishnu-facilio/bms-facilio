package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.wms.message.WmsEvent;
import com.facilio.wms.util.WmsApi;

public class UpdateAlarmCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(UpdateAlarmCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(alarm != null && recordIds != null && !recordIds.isEmpty()) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = AlarmAPI.getAlarmFields(alarm.getSourceTypeEnum());
			String moduleName = AlarmAPI.getAlarmModuleName(alarm.getSourceTypeEnum());
			
			FacilioModule module = modBean.getModule(moduleName);
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
				
				if (alarm.getModifiedTime() == -1) {
					alarm.setModifiedTime(System.currentTimeMillis());
				}
			}
			if(isCleared || (alarm.getSeverity() != null && alarm.getSeverity().getId() == AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CLEAR_SEVERITY).getId())) {
				alarm.setClearedTime(alarm.getModifiedTime());
				alarm.setClearedBy(AccountUtil.getCurrentUser());
				isCleared = true;
			}
			
			if(recordIds.size() == 1) {
				AlarmContext alarmObj = getAlarmObj(idCondition, moduleName, fields, false);
				if(alarmObj != null) {
					AlarmAPI.updateAlarmDetailsInTicket(alarmObj, alarm);
					TicketAPI.updateTicketStatus(null, alarm, alarmObj, false);
					TicketAPI.updateTicketAssignedBy(alarm);
				}
			}
			
			if(alarm.getSeverity() != null)
			{
				if(isCleared) {
					context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ALARM_CLEARED);
				}
				else if (alarm.getPreviousSeverity() == null || (alarm.getPreviousSeverity().getId() != alarm.getSeverity().getId())) {
					context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.UPDATED_ALARM_SEVERITY);
				}
			}
			
			TicketAPI.updateTicketAssignedBy(alarm);
			TicketAPI.updateTicketStatus(alarm);
			
			UpdateRecordBuilder<AlarmContext> updateBuilder = new UpdateRecordBuilder<AlarmContext>()
																		.module(module)
																		.fields(fields)
																		.andCondition(idCondition);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(alarm));
			if(recordIds.size() == 1) {
				AlarmContext alarmObj = getAlarmObj(idCondition, moduleName, fields, true);
				if(alarmObj != null) {
					context.put(FacilioConstants.ContextNames.RECORD, alarmObj);
					
					if (isCleared && AlarmAPI.isReadingAlarm(alarmObj.getSourceTypeEnum())) {
						if (AccountUtil.getCurrentOrg().getId() == 135) {
							LOGGER.info("Updating meta as clear alarm : "+alarmObj.getId()+" for resource : "+(alarmObj.getResource() == null ? "" : alarmObj.getResource().getId()));
						}
						ReadingRuleAPI.markAlarmMetaAsClear(alarmObj.getId());
					}
				}
			}
			
			JSONObject record = new JSONObject();
			record.put("id", recordIds.get(0));
			
			if(ActivityType.UPDATED_ALARM_SEVERITY.equals(context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE)) && 
					(AccountUtil.getCurrentOrg().getOrgId() != 88 || alarm.getSeverity().getId() == AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CRITICAL_SEVERITY).getId())) {
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
		return false;
	}
	
	private AlarmContext getAlarmObj(Condition idCondition, String moduleName, List<FacilioField> fields, boolean fetchExtended) throws Exception {
		SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
																.moduleName(moduleName)
																.beanClass(AlarmContext.class)
																.select(fields)
																.andCondition(idCondition);
		
		List<AlarmContext> alarms = builder.get();
		if(alarms != null && !alarms.isEmpty()) {
			if (fetchExtended) {
				AlarmAPI.loadExtendedAlarms(alarms);
			}
			return alarms.get(0);
		}
		return null;
	}

}
