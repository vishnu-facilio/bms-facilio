package com.facilio.cb.command;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentAction;
import com.facilio.cb.context.ChatBotIntentInvokeSample;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotModelVersion;
import com.facilio.cb.util.ChatBotConstants;

public class AddChatBotIntentCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		ChatBotIntent chatBotIntent = (ChatBotIntent)context.get(ChatBotConstants.CHAT_BOT_INTENT);
		
		ChatBotModelVersion modelVersion = (ChatBotModelVersion)context.get(ChatBotConstants.CHAT_BOT_MODEL_VERSION);
		
		chatBotIntent.setModelVersionId(modelVersion.getId());
		chatBotIntent.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		if(chatBotIntent.getContextWorkflow() != null) {
			if(chatBotIntent.getContextWorkflow().validateWorkflow()) {
				Long wfid = WorkflowUtil.addWorkflow(chatBotIntent.getContextWorkflow());
				chatBotIntent.setContextWorkflowId(wfid);
			}
			else {
				throw new Exception(chatBotIntent.getContextWorkflow().getErrorListener().getErrorsAsString());
			}
		}
		
		ChatBotUtil.addChatbotIntent(chatBotIntent);
		
		if(chatBotIntent.getParams() != null && !chatBotIntent.getParams().isEmpty()) {
			for(ChatBotIntentParam param :chatBotIntent.getParams()) {
				param.setIntentId(chatBotIntent.getId());
				param.setOrgId(AccountUtil.getCurrentOrg().getId());
				ChatBotUtil.addChatbotIntentParam(param);
			}
		}
		
		if(chatBotIntent.getInvokeSamples() != null && !chatBotIntent.getInvokeSamples().isEmpty()) {
			
			for(ChatBotIntentInvokeSample invokeSamples :chatBotIntent.getInvokeSamples()) {
				invokeSamples.setOrgId(AccountUtil.getCurrentOrg().getId());
				invokeSamples.setIntentId(chatBotIntent.getId());
				ChatBotUtil.addChatbotIntentInvokeSample(invokeSamples);
			}
		}
		
		if(chatBotIntent.getActions() != null && !chatBotIntent.getActions().isEmpty()) {
			
			for(ChatBotIntentAction cbaction :chatBotIntent.getActions()) {
				
				if(cbaction.getAction() != null) {
					
					List<ActionContext> actions = ActionAPI.addActions(Collections.singletonList(cbaction.getAction()), null);
					
					cbaction.setActionId(actions.get(0).getId());
				}
				
				cbaction.setIntentId(chatBotIntent.getId());
				cbaction.setOrgId(AccountUtil.getCurrentOrg().getId());
				ChatBotUtil.addChatbotIntentAction(cbaction);
			}
		}
		
		return false;
	}

}
