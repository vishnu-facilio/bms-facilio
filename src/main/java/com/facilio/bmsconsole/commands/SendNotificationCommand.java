package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.NotificationContext;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class SendNotificationCommand extends FacilioCommand
{
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Object record = context.get(FacilioConstants.ContextNames.RECORD);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(record != null) {
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			long userId = AccountUtil.getCurrentUser().getId();
			
			EventType activityType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			if (activityType != null) {
				if (EventType.ASSIGN_TICKET == activityType) {
					Map<String, Object> fields = FieldUtil.getAsProperties(record);
					Long id = (Long) fields.get("id");
					String name = (String) fields.get("subject");
					if (name == null) {
						name = (String) fields.get("name");
					}
					
					Map<String, Object> assignedTo = (Map<String, Object>) fields.get("assignedTo");
					if (assignedTo != null) {
						long assignedToId = (Long) assignedTo.get("id");
						
						JSONObject info = new JSONObject();
						info.put("module", moduleName);
						info.put("recordId", id);
						info.put("record", name);
						
						NotificationContext notification = new NotificationContext();
						notification.setOrgId(orgId);
						notification.setUserId(assignedToId);
						notification.setNotificationType(activityType);
						notification.setActorId(userId);
						notification.setInfo(info.toJSONString());
						notification.setCreatedTime(System.currentTimeMillis());
						
						NotificationAPI.sendNotification(assignedToId, notification);
					}
				}
			}
		}
		return false;
	}
}
