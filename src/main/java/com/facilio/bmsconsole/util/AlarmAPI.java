package com.facilio.bmsconsole.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AlarmAPI {
	public static void updateAlarmDetailsInTicket(AlarmContext sourceAlarm, AlarmContext destinationAlarm) throws Exception {
		//if(alarm.getType() == AlarmContext.AlarmType.LIFE_SAFETY.getIntVal()) 
		{
			TicketCategoryContext category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getOrgId(), "Fire Safety");
			if(category != null) {
				destinationAlarm.setCategory(category);
			}
		}
		if(sourceAlarm != destinationAlarm && destinationAlarm.getSeverity() != null) {
			destinationAlarm.setPreviousSeverity(sourceAlarm.getSeverity());
		}
		
		ResourceContext resource = sourceAlarm.getResource();
		if(resource != null) {
			resource = ResourceAPI.getResource(resource.getId());
			switch (resource.getResourceTypeEnum()) {
				case SPACE:
					if(sourceAlarm.getNode() != null) {
						String description;
						if(sourceAlarm.isAcknowledged()) {
							description = MessageFormat.format("A {0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal(),sourceAlarm.getNode(), resource.getName());
						}
						else {
							description = MessageFormat.format("A {0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal(),sourceAlarm.getNode(), resource.getName());
						}
						destinationAlarm.setDescription(description);
					}
					break;
				case ASSET:
					String description;
					BaseSpaceContext space = SpaceAPI.getBaseSpace(resource.getSpaceId());
					
					if(sourceAlarm.isAcknowledged()) {
						if(space != null) {
							description = MessageFormat.format("A{0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName(), space.getName());
						}
						else {
							description = MessageFormat.format("A{0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName());
						}
					}
					else {
						if(space != null) {
							description = MessageFormat.format("A{0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName(), space.getName());
						}
						else {
							description = MessageFormat.format("A{0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName());
						}
					}
					destinationAlarm.setDescription(description);
					break;
			}
		}
		else {
			if(sourceAlarm.isAcknowledged()) {
				destinationAlarm.setDescription(MessageFormat.format("A {0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getAlarmTypeVal(),sourceAlarm.getNode()));
			}
			else {
				destinationAlarm.setDescription(MessageFormat.format("A {0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getAlarmTypeVal(),sourceAlarm.getNode()));
			}
		}
	}
	
	public static String sendAlarmSMS(AlarmContext alarm, String to, String message) throws Exception {
		ResourceContext resource = alarm.getResource();
		if(resource != null) {
			resource = ResourceAPI.getResource(resource.getId());
			String spaceName = null;
			switch (resource.getResourceTypeEnum()) {
				case SPACE:
					spaceName = resource.getName();
					break;
				case ASSET:
					spaceName = SpaceAPI.getBaseSpace(resource.getSpaceId()).getName();
					break;
			}
			String sms = null;
			if(message != null && !message.isEmpty()) {
				if(spaceName != null) {
					sms = MessageFormat.format("[ALARM] [{0}] {1} @ {2}, {3}", alarm.getAlarmTypeVal(), alarm.getSubject(), spaceName, message);
				}
				else {
					sms = MessageFormat.format("[ALARM] [{0}] {1}, {2}", alarm.getAlarmTypeVal(), alarm.getSubject(), message);
				}
			}
			else {
				if(spaceName != null) {
					sms = MessageFormat.format("[ALARM] [{0}] {1} @ {2}", alarm.getAlarmTypeVal(), alarm.getSubject(), spaceName);
				}
				else {
					sms = MessageFormat.format("[ALARM] [{0}] {1}", alarm.getAlarmTypeVal(), alarm.getSubject());
				}
			}
			JSONObject json = new JSONObject();
			json.put("to", to);
			json.put("message", sms);
			return SMSUtil.sendSMS(json);
		}
		return null;
	}
	
	public static long addAlarmEntity() throws Exception {
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", AccountUtil.getCurrentAccount().getOrg().getId());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table("Alarm_Entity")
				.fields(FieldFactory.getAlarmEntityFields())
				.addRecord(prop);
		insertBuilder.save();
		return (long) prop.get("id");
	}
	
	public static AlarmSeverityContext getAlarmSeverity(String severity) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AlarmSeverityContext> selectBuilder = new SelectRecordsBuilder<AlarmSeverityContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_SEVERITY))
																		.moduleName(FacilioConstants.ContextNames.ALARM_SEVERITY)
																		.beanClass(AlarmSeverityContext.class)
																		.andCustomWhere("SEVERITY = ?", severity);
		
		List<AlarmSeverityContext> severities = selectBuilder.get();
		if(severities != null && !severities.isEmpty()) {
			return severities.get(0);
		}
		return null;
	}
	
	public static AlarmSeverityContext getAlarmSeverity(long severityId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AlarmSeverityContext> selectBuilder = new SelectRecordsBuilder<AlarmSeverityContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_SEVERITY))
																		.moduleName(FacilioConstants.ContextNames.ALARM_SEVERITY)
																		.beanClass(AlarmSeverityContext.class)
																		.andCustomWhere("ID = ?", severityId);
		
		List<AlarmSeverityContext> severities = selectBuilder.get();
		if(severities != null && !severities.isEmpty()) {
			return severities.get(0);
		}
		return null;
	}
	
	public static List<AlarmSeverityContext> getAlarmSeverityList() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AlarmSeverityContext> selectBuilder = new SelectRecordsBuilder<AlarmSeverityContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_SEVERITY))
																		.moduleName(FacilioConstants.ContextNames.ALARM_SEVERITY)
																		.beanClass(AlarmSeverityContext.class);
		
		return selectBuilder.get();
	}
}
