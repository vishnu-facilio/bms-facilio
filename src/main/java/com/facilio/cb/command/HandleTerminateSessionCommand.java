package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cards.util.ChatBotUtil;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;

public class HandleTerminateSessionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotModel model = (ChatBotModel) context.get(ChatBotConstants.CHAT_BOT_MODEL);
		
		ChatBotMLResponse mlResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		if(mlResponse != null && mlResponse.getIntent() != null && mlResponse.getIntent().equals(ChatBotConstants.CHAT_BOT_SESSION_TERMINATE_INTENT)) {
			
			chatBotSessionConversation.setState(ChatBotSessionConversation.State.TERMINATE_GRACEFULLY_RISED.getIntVal());
			
			ChatBotUtil.updateChatBotSessionConversation(chatBotSessionConversation);
			
			ChatBotIntent intent = ChatBotUtil.getIntent(model.getChatBotModelVersion().getId(), mlResponse.getIntent());
			
			context.put(ChatBotConstants.CHAT_BOT_INTENT, intent);
			
			String returnString = intent.executeActions(context, null);
			
			ChatBotSession session = chatBotSessionConversation.getChatBotSession();
			
			session.setState(ChatBotSession.State.TERMINATED_GRACEFULLY.getIntVal());
			session.setResponse(returnString);
			
			ChatBotUtil.updateChatBotSession(session);
			
			context.put(ChatBotConstants.CHAT_BOT_SESSION, session);
			context.put(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED, true);
			
			return true;
		}
		
		return false;
	}

}
