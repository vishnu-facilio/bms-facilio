package com.facilio.bmsconsole.util;

import java.text.MessageFormat;
import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

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
		AssetContext asset = sourceAlarm.getAsset();
		if(asset != null) {
			String description;
			BaseSpaceContext space = sourceAlarm.getSpace();
			
			if(space == null && asset.getSpace() != null) {
				space = asset.getSpace();
				destinationAlarm.setSpace(space);
			}
			
			if(sourceAlarm.isAcknowledged()) {
				if(space != null) {
					description = MessageFormat.format("A{0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getTypeVal() != null? " "+sourceAlarm.getTypeVal():"n",asset.getName(), space.getName());
				}
				else {
					description = MessageFormat.format("A{0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getTypeVal() != null? " "+sourceAlarm.getTypeVal():"n",asset.getName());
				}
			}
			else {
				if(space != null) {
					description = MessageFormat.format("A{0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getTypeVal() != null? " "+sourceAlarm.getTypeVal():"n",asset.getName(), space.getName());
				}
				else {
					description = MessageFormat.format("A{0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getTypeVal() != null? " "+sourceAlarm.getTypeVal():"n",asset.getName());
				}
			}
			destinationAlarm.setDescription(description);
		}
		else if(sourceAlarm.getNode() != null) {
			String description;
			BaseSpaceContext space = sourceAlarm.getSpace();
			if(sourceAlarm.isAcknowledged()) {
				if(space != null) {
					description = MessageFormat.format("A {0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getTypeVal(),sourceAlarm.getNode(), space.getName());
				}
				else {
					description = MessageFormat.format("A {0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getTypeVal(),sourceAlarm.getNode());
				}
			}
			else {
				if(space != null) {
					description = MessageFormat.format("A {0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getTypeVal(),sourceAlarm.getNode(), space.getName());
				}
				else {
					description = MessageFormat.format("A {0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getTypeVal(),sourceAlarm.getNode());
				}
			}
			destinationAlarm.setDescription(description);
		}
	}
	
	public static String sendAlarmSMS(AlarmContext alarm, String to, String message) throws Exception {
		AssetContext asset = alarm.getAsset();
		if(asset != null) {
			asset = AssetsAPI.getAssetInfo(asset.getId());
			BaseSpaceContext space = asset.getSpace();
			String spaceName = null;
			if(space != null) {
				spaceName = space.getName();
			}
			
			String sms = null;
			if(message != null && !message.isEmpty()) {
				if(spaceName != null) {
					sms = MessageFormat.format("[ALARM] [{0}] {1} @ {2}, {3}", alarm.getTypeVal(), alarm.getSubject(), spaceName, message);
				}
				else {
					sms = MessageFormat.format("[ALARM] [{0}] {1}, {2}", alarm.getTypeVal(), alarm.getSubject(), message);
				}
			}
			else {
				if(spaceName != null) {
					sms = MessageFormat.format("[ALARM] [{0}] {1} @ {2}", alarm.getTypeVal(), alarm.getSubject(), spaceName);
				}
				else {
					sms = MessageFormat.format("[ALARM] [{0}] {1}", alarm.getTypeVal(), alarm.getSubject());
				}
			}
			JSONObject json = new JSONObject();
			json.put("to", to);
			json.put("message", sms);
			return SMSUtil.sendSMS(json);
		}
		return null;
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
	
	public static List<AlarmSeverityContext> getAlarmSeverityList() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AlarmSeverityContext> selectBuilder = new SelectRecordsBuilder<AlarmSeverityContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_SEVERITY))
																		.moduleName(FacilioConstants.ContextNames.ALARM_SEVERITY)
																		.beanClass(AlarmSeverityContext.class);
		
		return selectBuilder.get();
	}
}
