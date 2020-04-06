package com.facilio.cb.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;

public class HandleContextWorkflowFillables extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Boolean skipActionExecution = (Boolean) context.get(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION);
		
		if(skipActionExecution != null && skipActionExecution) {
			return false;
		}
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		ChatBotSession session = chatBotSessionConversation.getChatBotSession();
		
		if(session.getIntent().getContextWorkflowId() < 0) {
			return false;
		}
		
		ChatBotMLResponse mlResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);
		
		if(mlResponse != null && mlResponse.getIntent() != null && mlResponse.getIntent().equals(ChatBotConstants.CHAT_BOT_AFFERMATIVE_INTENT)) {

			session.addParamsObject("isConfirmed", true);
		}
		
		return false;
	}

}
