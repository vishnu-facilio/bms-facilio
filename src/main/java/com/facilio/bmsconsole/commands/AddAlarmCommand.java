package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
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
			
			List<FacilioField> fields = AlarmAPI.getAlarmFields(alarm.getSourceTypeEnum());
			String moduleName = AlarmAPI.getAlarmModuleName(alarm.getSourceTypeEnum());

			if (alarm.getResource() != null && alarm.getResource().getId() != -1) {
				long resourceId = alarm.getResource().getId();
				ResourceContext resource = ResourceAPI.getResource(resourceId);
				long siteId = -1;
				if (resource.getResourceTypeEnum() == ResourceType.SPACE) {
					BaseSpaceContext space = SpaceAPI.getBaseSpace(resourceId);
					siteId = space.getSiteId();
				} else {
					AssetContext asset = AssetsAPI.getAssetInfo(resourceId);
					if (asset.getSpaceId() != -1) {
						BaseSpaceContext space = SpaceAPI.getBaseSpace(asset.getSpaceId());
						siteId = space.getSiteId();
					}
				}
				alarm.setSiteId(siteId);
			}

			
			InsertRecordBuilder<AlarmContext> builder = new InsertRecordBuilder<AlarmContext>()
																.moduleName(moduleName)
																.fields(fields);
			
			if (AccountUtil.getCurrentOrg().getId() != 75) {
				builder.withLocalId();
			}
			
			AlarmAPI.updateAlarmDetailsInTicket(alarm, alarm);
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
			
			List<User> users = AccountUtil.getOrgBean().getActiveOrgUsers(AccountUtil.getCurrentOrg().getId());
			List<Long> recipients = new ArrayList<>();
			for (User user : users) {
				if (user.getOuid() == 914477) {	// Temp done Al Seef CEO
					if (alarm.getSeverity() == null || alarm.getSeverity().getId() != AlarmAPI.getAlarmSeverity(FacilioConstants.Alarm.CRITICAL_SEVERITY).getId()) {
						continue;						
					}
				}
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
