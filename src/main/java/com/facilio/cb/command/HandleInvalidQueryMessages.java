package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;

public class HandleInvalidQueryMessages extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotModel model = (ChatBotModel) context.get(ChatBotConstants.CHAT_BOT_MODEL);
		
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		ChatBotMLResponse mlResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		
		if(model.getChatBotModelVersion().getAccuracyRate() > 0) {
			if(model.getChatBotModelVersion().getAccuracyRate() > mlResponse.getAccuracy()) {
				setInvalidQuery(mlResponse,session,chatBotSessionConversation);
			}
		}
		else if(ChatBotConstants.DEFAULT_ACCURACY_RATE > mlResponse.getAccuracy()) {
			setInvalidQuery(mlResponse,session,chatBotSessionConversation);
		}
		
		if(chatBotSessionConversation != null && chatBotSessionConversation.getResponseJson() != null) {
			
			ChatBotSessionConversation lastInvalidCBConversation = ChatBotUtil.getLastInvalidQueryConversation(chatBotSessionConversation.getSessionId());
			
			if(lastInvalidCBConversation != null) {
				if(lastInvalidCBConversation.getParentConversationId() > 0) {
					
					chatBotSessionConversation.setParentConversationId(lastInvalidCBConversation.getParentConversationId());
				}
				else {
					chatBotSessionConversation.setParentConversationId(lastInvalidCBConversation.getId());
				}
			}
			
			return false;
		}
		else if(session != null && session.getQueryJson() != null) {
			
			ChatBotSession lastInvalidSession = ChatBotUtil.getLastInvalidQuerySession();
			
			if(lastInvalidSession != null) {
				if(lastInvalidSession.getParentSessionId() > 0) {
					session.setParentSessionId(lastInvalidSession.getParentSessionId());
				}
				else {
					session.setParentSessionId(lastInvalidSession.getId());
				}
			}
			
			return false;
			
		}
		
		return true;
	}

	private void setInvalidQuery(ChatBotMLResponse mlResponse,ChatBotSession session, ChatBotSessionConversation chatBotSessionConversation) {
		
		if(chatBotSessionConversation != null )  {
			
			chatBotSessionConversation.setState(ChatBotSessionConversation.State.REPLIED_INCORRECTLY.getIntVal());
		}
		else {
			mlResponse.setNotAccurate(true);
			mlResponse.setIntent(ChatBotConstants.CHAT_BOT_INTENT_NOT_FOUND_INTENT);
			session.setState(ChatBotSession.State.INVALID_QUERY.getIntVal());
		}
	}
}
