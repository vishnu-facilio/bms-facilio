package com.facilio.cb.command;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSession.State;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class ExecuteActionAndSetResponseForConversationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Boolean skipActionExecution = (Boolean) context.get(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION);
		
		if(skipActionExecution != null && skipActionExecution) {
			return false;
		}
		
		ChatBotMLResponse chatBotMLResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
			
		if(chatBotSessionConversation.getState() != ChatBotSessionConversation.State.REPLIED_INCORRECTLY.getIntVal()) { 		//correct case
			
			chatBotSessionConversation.setState(ChatBotSessionConversation.State.REPLIED_CORRECTLY.getIntVal());
			
			ChatBotUtil.updateChatBotSessionConversation(chatBotSessionConversation);
			
			if(chatBotSessionConversation.getIntentParamId() > 0) {
				ChatBotUtil.deleteAndAddSessionParam(chatBotSessionConversation.getIntentParamId(),chatBotSessionConversation.getChatBotSession().getId(),chatBotMLResponse.getAnswer());	// even user may enter wrong answer on that case ML will correct it, so answer is taken form mlResponse
			}
			
			chatBotSessionConversation.getChatBotSession().setRecievedParamCount(chatBotSessionConversation.getChatBotSession().getRecievedParamCount()+1);
			
			ChatBotUtil.updateChatBotSession(chatBotSessionConversation.getChatBotSession());
			
			ChatBotIntent intent = chatBotSessionConversation.getChatBotSession().getIntent();
			
			if(intent.getContextWorkflow() != null) {
				
				ChatBotUtil.executeContextWorkflow(intent, chatBotSessionConversation.getChatBotSession(), context);
				
				return false;
			}
			
			if(chatBotSessionConversation.getChatBotSession().getRequiredParamCount() <= chatBotSessionConversation.getChatBotSession().getRecievedParamCount()) {
				
				ChatBotSession session = chatBotSessionConversation.getChatBotSession();
				
				if(intent.isConfirmationNeeded() && (session.isConfirmed() == null || !session.isConfirmed())) {
					
					session.setState(State.WAITING_FOR_CONFIRMATION.getIntVal());
					
					ChatBotSessionConversation newChatBotSessionConversation = ChatBotUtil.constructAndAddConfirmationCBSessionConversationParams(session);
					
					context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, newChatBotSessionConversation);
					
					ChatBotUtil.updateChatBotSession(session);
				}
				else {
					ChatBotUtil.executeIntentAction(session, intent, context);
				}
				
			}
			else {
				
				List<ChatBotIntentParam> remainingIntentParamList = ChatBotUtil.fetchRemainingMainChatBotIntentParams(chatBotSessionConversation.getChatBotSession().getIntent().getId(),chatBotSessionConversation.getChatBotSession().getId());
				
				ChatBotSessionConversation newChatBotSessionConversation = ChatBotUtil.constructAndAddCBSessionConversationParams(remainingIntentParamList.get(0), chatBotSessionConversation.getChatBotSession(),null,null);
				
				context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, newChatBotSessionConversation);
			}
			
		}
			
		return false;
	}
	
}