package com.facilio.bmsconsole.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AlarmAPI {
	
	public static void loadExtendedAlarms(List<AlarmContext> alarms) throws Exception {
		if (alarms != null && !alarms.isEmpty()) {
			Map<SourceType, List<Long>> typeWiseIds = new HashMap<>();
			for (AlarmContext alarm : alarms) {
				List<Long> ids = typeWiseIds.get(alarm.getSourceTypeEnum());
				if (ids == null) {
					ids = new ArrayList<>();
					typeWiseIds.put(alarm.getSourceTypeEnum(), ids);
				}
				ids.add(alarm.getId());
			}
			Map<SourceType, Map<Long, ? extends AlarmContext>> typeWiseAlarms = getTypeWiseAlarms(typeWiseIds);
			
			for (int i = 0; i < alarms.size(); i++) {
				AlarmContext alarm = alarms.get(i);
				SourceType type = alarm.getSourceTypeEnum();
				if (type != null) {
					switch (type) {
						case THRESHOLD_ALARM:
							alarms.set(i, typeWiseAlarms.get(alarm.getSourceTypeEnum()).get(alarm.getId()));
							break;
						default:
							break;
					}
				}
			}
		}
	}
	
	private static Map<SourceType, Map<Long, ? extends AlarmContext>> getTypeWiseAlarms (Map<SourceType, List<Long>> typeWiseIds) throws Exception {
		Map<SourceType, Map<Long,? extends AlarmContext>> typewiseAlarms = new HashMap<>();
		
		if (typeWiseIds != null && !typeWiseIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for (Map.Entry<SourceType, List<Long>> entry : typeWiseIds.entrySet()) {
				SourceType type = entry.getKey();
				if (type != null) {
					switch (type) {
						case THRESHOLD_ALARM: {
							typewiseAlarms.put(entry.getKey(), fetchExtendedAlarms(modBean.getModule(FacilioConstants.ContextNames.READING_ALARM), modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM), entry.getValue(), ReadingAlarmContext.class));
						}break;
						default:
							break;
					}
				}
			}
		}
		
		return typewiseAlarms;
	}
	
	private static <E extends AlarmContext> Map<Long, E> fetchExtendedAlarms(FacilioModule module, List<FacilioField> fields, List<Long> ids, Class<E> alarmClass) throws Exception {
		SelectRecordsBuilder<E> selectBuilder = new SelectRecordsBuilder<E>()
																		.select(fields)
																		.module(module)
																		.beanClass(alarmClass)
																		.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		return selectBuilder.getAsMap();
	}
	
	public static void updateAlarmDetailsInTicket(AlarmContext sourceAlarm, AlarmContext destinationAlarm) throws Exception {
		
		TicketCategoryContext category =  null;
		switch (sourceAlarm.getAlarmTypeEnum()) {
			case FIRE:
				category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getOrgId(), "Fire Safety");
				break;
			case HVAC:
				category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getOrgId(), "HVAC");
				break;
			case ENERGY:
				category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getOrgId(), "Energy");
				break;
			default:
				break;
		}
		if(category != null) {
			destinationAlarm.setCategory(category);
		}
		
		if(sourceAlarm != destinationAlarm && destinationAlarm.getSeverity() != null) {
			destinationAlarm.setPreviousSeverity(sourceAlarm.getSeverity());
		}
		
		ResourceContext resource = sourceAlarm.getResource();
		if(resource != null && resource.getId() != -1) {
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
