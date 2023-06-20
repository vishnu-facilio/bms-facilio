package com.facilio.workflows.functions;


import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.context.MailSourceType;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.services.email.EmailFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.facilio.scriptengine.context.ScriptContext;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.NOTIFICATION_FUNCTION)
public class FacilioNotificationFunctions {
	public Object sendMail(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}

		Map<String,Object> sendMailMap =  (Map<String, Object>) objects[0];
		String sender = null;
		if(sendMailMap.containsKey("from") && sendMailMap.get("from") != null) {
			sender = EmailFactory.getEmailClient().getNotificationFromAddressEmailFromName((String)sendMailMap.get("from"));
		}
		if(sender == null) {
			sender = EmailFactory.getEmailClient().getSystemFromAddress(EmailFromAddress.SourceType.NOTIFICATION);
		}
		sendMailMap.put("sender", sender);
		sendMailMap.put(MailConstants.Params.SOURCE_TYPE, MailSourceType.SCRIPT.name());

		Map<String,String> attachements = (Map<String,String>)sendMailMap.get("attachments");

		if (MapUtils.isNotEmpty(attachements)){
			FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(WorkflowV2Util.getAsJSONObject(sendMailMap),attachements);
		}else {
			FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(WorkflowV2Util.getAsJSONObject(sendMailMap));
		}
		scriptContext.incrementTotalEmailCount();
		return null;
	}

	public Object sendSms(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		Map<String,Object> sendMailMap =  (Map<String, Object>) objects[0];

		FacilioContext context = new FacilioContext();

		context.put(FacilioConstants.ContextNames.NOTIFICATION_TYPE, ActionType.SMS_NOTIFICATION);
		context.put(FacilioConstants.ContextNames.NOTIFICATION_OBJECT, WorkflowV2Util.getAsJSONObject(sendMailMap));

		FacilioTimer.scheduleInstantJob("default","SendNotificationJob", context);
		return null;
	}


		public Object sendPushNotification(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			checkParam(objects);

			if(objects[0] == null) {
				return null;
			}

			List<Long> userId = (objects[0] instanceof ArrayList)? (List<Long>) objects[0] : Collections.singletonList((Long)objects[0]);
			JSONObject sendPushNotificationMap =  FieldUtil.getAsJSON(objects[1]);
			Boolean isPushNotification = (sendPushNotificationMap!=null)?(Boolean) sendPushNotificationMap.getOrDefault("isSendNotification",false):false;
			JSONParser parser = new JSONParser();
			String notiString = new ObjectMapper().writeValueAsString(sendPushNotificationMap.get("notification"));
			JSONObject json = (JSONObject) parser.parse(notiString);
			sendPushNotificationMap.replace("notification", sendPushNotificationMap.get("notification"), json);
			JSONObject notification = (JSONObject) sendPushNotificationMap.get("notification");

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule((String) notification.get(FacilioConstants.ContextNames.MODULE_NAME));

			FacilioContext context=V3Util.getSummary((String) notification.get(FacilioConstants.ContextNames.MODULE_NAME),Collections.singletonList((Long) notification.get("summaryId")));
			List<ModuleBaseWithCustomFields> Record= Constants.getRecordListFromContext(context,(String) notification.get(FacilioConstants.ContextNames.MODULE_NAME));
			NotificationAPI.pushNotification(FieldUtil.getAsJSON(sendPushNotificationMap),userId,isPushNotification,CollectionUtils.isNotEmpty(Record)?Record.get(0):null,module,null);
			return null;
		}


	public Object sendNotification(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		Long userId =  (Long) objects[0];
		Map<String,Object> sendMailMap =  (Map<String, Object>) objects[1];

		FacilioContext context = new FacilioContext();

		context.put(FacilioConstants.ContextNames.USER_ID, userId);
		context.put(FacilioConstants.ContextNames.NOTIFICATION_TYPE, ActionType.PUSH_NOTIFICATION);
		context.put(FacilioConstants.ContextNames.NOTIFICATION_OBJECT, WorkflowV2Util.getAsJSONObject(sendMailMap));

		FacilioTimer.scheduleInstantJob("default","SendNotificationJob", context);
		scriptContext.incrementTotalNotificationCount();
		return null;
	}

	public Object makeCall(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		Map<String,Object> callMap =  (Map<String, Object>) objects[0];

		FacilioContext context = new FacilioContext();

		context.put(FacilioConstants.ContextNames.NOTIFICATION_TYPE, ActionType.MAKE_CALL);
		context.put(FacilioConstants.ContextNames.NOTIFICATION_OBJECT, WorkflowV2Util.getAsJSONObject(callMap));

		FacilioTimer.scheduleInstantJob("default","SendNotificationJob", context);

		return null;
	}

	public Object sendDirectMailTemp(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		Map<String,Object> mailMap =  (Map<String, Object>) objects[0];

		Map<String,String> attachements = (Map<String,String>)mailMap.get("attachments");

		AwsUtil.sendMail(WorkflowV2Util.getAsJSONObject(mailMap), attachements);

		return null;
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}