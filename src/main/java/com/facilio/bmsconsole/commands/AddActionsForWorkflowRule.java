package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActionType;
import com.facilio.bmsconsole.workflow.AlarmTemplate;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.bmsconsole.workflow.SMSTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate.Type;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.google.gson.JsonObject;

public class AddActionsForWorkflowRule implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<ActionContext> actions = (List<ActionContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_ACTION);
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		
		for(ActionContext action:actions) {
			System.out.println(action.getTemplateJson());
			if(action.getActionType().equals(ActionType.EMAIL_NOTIFICATION)) {
				EMailTemplate emailTemplate = new EMailTemplate();
				emailTemplate.setFrom("support@${org.orgDomain}.facilio.com");
				String toAdresses = action.getTemplateJson().get("to").toString();
				toAdresses = toAdresses.substring(1, toAdresses.length()-1);
				emailTemplate.setTo(toAdresses);
				emailTemplate.setSubject((String) action.getTemplateJson().get("subject"));
				emailTemplate.setBody((String) action.getTemplateJson().get("body"));
				emailTemplate.setType(Type.EMAIL);
				action.setTemplate(emailTemplate);
			}
			else if(action.getActionType().equals(ActionType.SMS_NOTIFICATION)) {
				SMSTemplate smsTemplate = new SMSTemplate();
				String toAdresses = action.getTemplateJson().get("to").toString();
				toAdresses = toAdresses.substring(1, toAdresses.length()-1);
				User user = AccountUtil.getUserBean().getUser(toAdresses);
				smsTemplate.setTo(user.getPhone());
				smsTemplate.setMsg((String) action.getTemplateJson().get("body"));
				smsTemplate.setType(Type.SMS);
				action.setTemplate(smsTemplate);	
			}
			else if(action.getActionType().equals(ActionType.ADD_ALARM)) {
				List<Map> alarmFieldMatcher = (ArrayList) action.getTemplateJson().get("fieldMatcher");
				
				JsonObject content = new JsonObject();
				for(Map alarmField:alarmFieldMatcher) {
					content.addProperty(alarmField.get("field").toString(), alarmField.get("regex").toString());
				}
				AlarmTemplate alarmTemplate = new AlarmTemplate();
				alarmTemplate.setContent(content.toString());
				action.setTemplate(alarmTemplate);
			}
			
			FacilioContext templateContext = new FacilioContext();
			templateContext.put(FacilioConstants.Workflow.TEMPLATE, action.getTemplate());
			
			Chain addTemplate = FacilioChainFactory.getAddTemplateChain();
			addTemplate.execute(templateContext);
			action.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			UserTemplate userTemplate = (UserTemplate)action.getTemplate();
			action.setTemplateId(userTemplate.getId());
		}
		ActionAPI.addActions(actions);
		
		if(rule != null) {
			for(ActionContext action:actions) {
				Map<String, Object> workflowRuleActionProps = new HashMap<String, Object>();
				workflowRuleActionProps.put("workflowRuleId", rule.getId());
				workflowRuleActionProps.put("actionId", action.getId());
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table("Workflow_Rule_Action")
															.fields(FieldFactory.getWorkflowRuleActionFields())
															.addRecord(workflowRuleActionProps);
				insertBuilder.save();
			}
		}
		return false;
	}
}
