package com.facilio.bmsconsole.instant.jobs;

import java.util.Collections;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.CallUtil;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsole.util.SMSUtil;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.tasker.job.InstantJob;

public class SendNotificationJob extends InstantJob {

	@Override
	public void execute(FacilioContext context) throws Exception {
		
		ActionType type = (ActionType)context.get(FacilioConstants.ContextNames.NOTIFICATION_TYPE);
		
		switch(type) {
		case EMAIL_NOTIFICATION :
			
			JSONObject mailJson = (JSONObject) context.get(FacilioConstants.ContextNames.NOTIFICATION_OBJECT);
			Map<String,String> attachements = (Map<String, String>) context.get(FacilioConstants.ContextNames.ATTACHMENT_MAP_FILE_LIST);
			FacilioFactory.getEmailClient().sendEmail(mailJson,attachements);
			break;
		case SMS_NOTIFICATION:
			
			JSONObject smsJson = (JSONObject) context.get(FacilioConstants.ContextNames.NOTIFICATION_OBJECT);
			
			SMSUtil.sendSMS(smsJson);
			break;
		case MAKE_CALL:
			
			JSONObject callJson = (JSONObject) context.get(FacilioConstants.ContextNames.NOTIFICATION_OBJECT);
			
			CallUtil.makeCall(callJson);
			break;
		case PUSH_NOTIFICATION:
			
			Long userId = (Long) context.get(FacilioConstants.ContextNames.USER_ID);
			
			JSONObject pushnotiJson = (JSONObject) context.get(FacilioConstants.ContextNames.NOTIFICATION_OBJECT);
			
			NotificationAPI.sendPushNotification(Collections.singletonList(userId), pushnotiJson);
			break;
		}
		
	}

}
