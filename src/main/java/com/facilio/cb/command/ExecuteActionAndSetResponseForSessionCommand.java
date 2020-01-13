package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cards.util.ChatBotUtil;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSession.State;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSessionParam;
import com.facilio.cb.util.ChatBotConstants;

public class ExecuteActionAndSetResponseForSessionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		ChatBotModel model = (ChatBotModel) context.get(ChatBotConstants.CHAT_BOT_MODEL);
		
		ChatBotMLResponse chatBotMLResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);

		ChatBotIntent intent = ChatBotUtil.getIntent(model.getChatBotModelVersion().getId(), chatBotMLResponse.getIntent());
		
		context.put(ChatBotConstants.CHAT_BOT_INTENT, intent);
		
		session.setIntentId(intent.getId());
		session.setIntent(intent);
		
		if(intent.isWithParams()) {
			session.setState(State.WAITING_FOR_PARAMS.getIntVal());
			session.setRequiredParamCount(intent.getChatBotIntentParamMap().size());
			
			ChatBotUtil.addChatBotSession(session);
			
			ChatBotIntentParam firstParam = intent.getChatBotIntentParamMap().get(1);
			
			ChatBotSessionConversation chatBotSessionConversation = ChatBotUtil.constructCBSessionConversationParams(firstParam, session,null);
			
			context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, chatBotSessionConversation);
		}
		else {
			if(session.getState() < 0) {
				session.setState(State.RESPONDED.getIntVal());
			}
			String response = intent.executeActions(context, null);
			
			session.setResponse(response);
			
			ChatBotUtil.addChatBotSession(session);
		}
			
		
		return false;
	}
	
}
