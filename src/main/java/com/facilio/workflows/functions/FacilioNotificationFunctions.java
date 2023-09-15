package com.facilio.workflows.functions;


import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.TemplateAttachment;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.emailtemplate.context.EMailStructure;
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
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

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
	public Object sendEmail(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception{

		checkParam(objects);
		if(MapUtils.isEmpty((Map<String, Object>) objects[0])|| objects.length<=1 || MapUtils.isEmpty((Map<String, Object>) objects[1])){
			StringBuilder logStr=new StringBuilder();
			logStr.append(scriptContext.getLogString()+" paramsMap or recordMap cannot be null while sending Email from script ");
			scriptContext.setLogString(logStr);
			return null;
		}

		Map<String,Object> paramsMap= (Map<String, Object>) objects[0];
		Map<String,Object> record= (Map<String, Object>) objects[1];

		Long emailTemplateId= (Long) paramsMap.get("templateId");

        String fromAddress=null;
		if(paramsMap.containsKey("from") && paramsMap.get("from") != null) {
			fromAddress = EmailFactory.getEmailClient().getNotificationFromAddressEmailFromName((String)paramsMap.get("from"));
		}
		if(fromAddress == null) {
			fromAddress = EmailFactory.getEmailClient().getSystemFromAddress(EmailFromAddress.SourceType.NOTIFICATION);
		}


		String recordModuleName=Constants.getModBean().getModule((Long)record.get("moduleId")).getName();
		Template template= TemplateAPI.getTemplate(emailTemplateId);

		if(template!=null && template instanceof EMailStructure) {

			EMailStructure emailStructure = (EMailStructure) template;
			Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
			Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(recordModuleName, record, placeHolders);
			JSONObject actionObj = template.getTemplate(recordPlaceHolders);
			paramsMap.put("sender", fromAddress);
			paramsMap.put("subject", actionObj.get("subject"));
			paramsMap.put("message", actionObj.get("message"));
			paramsMap.put("html", emailStructure.isHtml());
			paramsMap.put(MailConstants.Params.SOURCE_TYPE, MailSourceType.SCRIPT.name());

			paramsMap.put("isAttachmentAdded", emailStructure.getIsAttachmentAdded());
			if (emailStructure.isHtml()) {
				paramsMap.put("mailType", "html");
			}

			Map<String, String> attachmentMap = new HashMap<String, String>();
			if (emailStructure.getIsAttachmentAdded()) {
				if (CollectionUtils.isNotEmpty(template.getAttachments())) {
					for (TemplateAttachment attachment : template.getAttachments()) {
						String url = attachment.fetchFileUrl(record);
						if (url != null) {
							attachmentMap.put(attachment.getFileName(), url);
						}
					}
				}
			}
			if(MapUtils.isNotEmpty(attachmentMap)) {
				FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(WorkflowV2Util.getAsJSONObject(paramsMap), attachmentMap);
			}else {
				FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(WorkflowV2Util.getAsJSONObject(paramsMap));
			}
		}else{
			StringBuilder logStr=new StringBuilder();
			logStr.append(scriptContext.getLogString()+"  template not available while sending mail from script for TemplateId -- "+emailTemplateId);
			scriptContext.setLogString(logStr);
		}
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