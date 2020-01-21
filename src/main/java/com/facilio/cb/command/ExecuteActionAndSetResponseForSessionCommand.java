package com.facilio.cb.command;

import java.util.List;
import java.util.Set;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSession.State;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

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
			
			ChatBotUtil.addChatBotSession(session);
			
			int requriedCount = intent.getChatBotIntentParamList().size();
			
			if(chatBotMLResponse.getMlParams() != null) {
				
				for(int i=0;i<intent.getChatBotIntentParamList().size();i++) {
					
					ChatBotIntentParam chatBotParam = intent.getChatBotIntentParamList().get(i);
					
					if(chatBotMLResponse.getMlParams() != null && chatBotMLResponse.getMlParams().get(chatBotParam.getMlTypeEnum().getMLName()) != null) {
						
						ChatBotUtil.addSessionParam(chatBotParam.getIntentId(), session.getId(), chatBotMLResponse.getMlParams().get(chatBotParam.getMlTypeEnum().getMLName()));
						
						intent.getChatBotIntentParamList().remove(i);
					}
				}
			}
			
			int recievedCount  = requriedCount - intent.getChatBotIntentParamList().size();
			
			session.setRequiredParamCount(requriedCount);
			session.setRecievedParamCount(recievedCount);
			
			if(recievedCount == requriedCount) {
				
				List<Object> props = ChatBotUtil.fetchAllSessionParams(session.getId());
				
				context.put(ChatBotConstants.CHAT_BOT_INTENT, intent);
				
				String response = intent.executeActions(context, props);
				
				session.setResponse(response);
				session.setState(ChatBotSession.State.RESPONDED.getIntVal());
				
				context.put(ChatBotConstants.CHAT_BOT_SESSION, session);
				context.put(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED, true);
				
			}
			else {
				
				ChatBotIntentParam nextParam = intent.getChatBotIntentParamList().get(0);
				
				ChatBotSessionConversation chatBotSessionConversation = ChatBotUtil.constructCBSessionConversationParams(nextParam, session,null,null);
				
				context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, chatBotSessionConversation);
			}
			ChatBotUtil.updateChatBotSession(session);
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
