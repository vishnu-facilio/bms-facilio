package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;

public class HandleInvalidQueryMessageForConversations extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Boolean skipActionExecution = (Boolean) context.get(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION);
		
		if(skipActionExecution != null && skipActionExecution) {
			return false;
		}
		
		ChatBotModel model = (ChatBotModel) context.get(ChatBotConstants.CHAT_BOT_MODEL);
		
		ChatBotMLResponse mlResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		int intitialState = chatBotSessionConversation.getState();
		
		
		if(model.getChatBotModelVersion().getAccuracyRate() > 0) {
			if(model.getChatBotModelVersion().getAccuracyRate() > mlResponse.getAccuracy()) {
				setInvalidQuery(mlResponse,chatBotSessionConversation);
			}
		}
		else if(ChatBotConstants.DEFAULT_ACCURACY_RATE > mlResponse.getAccuracy()) {
			setInvalidQuery(mlResponse,chatBotSessionConversation);
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
			
			if(chatBotSessionConversation.getState() == ChatBotSessionConversation.State.REPLIED_INCORRECTLY.getIntVal()) {
				
				ChatBotSessionConversation clonnedChatBotSessionConversation = chatBotSessionConversation.clone();
				
				clonnedChatBotSessionConversation.setState(intitialState);
				
				ChatBotUtil.addChatBotSessionConversation(clonnedChatBotSessionConversation);
				
				context.put(ChatBotConstants.NEW_CHAT_BOT_SESSION_CONVERSATION, clonnedChatBotSessionConversation);
				
				context.put(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION, Boolean.TRUE);
			}
			
			return false;
		}
		return true;
	}

	private void setInvalidQuery(ChatBotMLResponse mlResponse, ChatBotSessionConversation chatBotSessionConversation) {
		
		chatBotSessionConversation.setState(ChatBotSessionConversation.State.REPLIED_INCORRECTLY.getIntVal());
	}
}
