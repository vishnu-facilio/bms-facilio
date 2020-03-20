package com.facilio.cb.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSession.State;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSuggestionContext;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class ExecuteActionAndSetResponseForSessionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Boolean skipActionExecution = (Boolean) context.get(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION);
		
		if(skipActionExecution != null && skipActionExecution) {
			return false;
		}
		
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		ChatBotModel model = (ChatBotModel) context.get(ChatBotConstants.CHAT_BOT_MODEL);
		
		ChatBotMLResponse chatBotMLResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);

		ChatBotIntent intent = ChatBotUtil.getIntent(model.getChatBotModelVersion().getId(), chatBotMLResponse.getIntent());
		
		context.put(ChatBotConstants.CHAT_BOT_INTENT, intent);
		
		session.setIntentId(intent.getId());
		session.setIntent(intent);
		
		session.setState(State.WAITING_FOR_PARAMS.getIntVal());
		
		ChatBotUtil.addChatBotSession(session);
		
		if(intent.isWithParams()) {
			
			int requriedCount = ChatBotUtil.getRequiredParamCount(intent.getChatBotIntentParamList());
			
			int recievedCount = 0;
			
//			ChatBotSuggestionContext suggestion = (ChatBotSuggestionContext) context.get(ChatBotConstants.CHAT_BOT_SUGGESTION);
			
			int size = intent.getChatBotIntentParamList().size();
			
			List<ChatBotIntentParam> filledParams = new ArrayList<>();
			
			ChatBotSession prevoiusSession = ChatBotUtil.checkChildIntentAndGetPrevoisSessionContext(intent.getId());
			
			if(prevoiusSession != null) {
				
				Map<String, Object> resProps = ChatBotUtil.fetchAllSessionParams(prevoiusSession.getId());
				
				for(ChatBotIntentParam chatBotParam : intent.getChatBotIntentParamList()) {
					
					if(chatBotParam.isFillableByParent() && resProps.containsKey(chatBotParam.getName())) {
						
						ChatBotUtil.deleteAndAddSessionParam(chatBotParam.getId(), session.getId(), resProps.get(chatBotParam.getName()).toString());
						
						filledParams.add(chatBotParam);
						recievedCount++;
					}
				}
				intent.getChatBotIntentParamList().removeAll(filledParams);
			}
			else if(chatBotMLResponse.getMlParams() != null) {
				
				filledParams = new ArrayList<>();
				
				for(ChatBotIntentParam chatBotParam : intent.getChatBotIntentParamList()) {
					
					if(chatBotMLResponse.getMlParams() != null && chatBotMLResponse.getMlParams().get(chatBotParam.getMlTypeEnum().getMLName()) != null) {
						
						ChatBotUtil.deleteAndAddSessionParam(chatBotParam.getId(), session.getId(), chatBotMLResponse.getMlParams().get(chatBotParam.getMlTypeEnum().getMLName()));
						
						filledParams.add(chatBotParam);
						recievedCount++;
					}
				}
			}
			
			session.setRequiredParamCount(requriedCount);
			session.setRecievedParamCount(recievedCount);
			
			ChatBotUtil.updateChatBotSession(session);
		}
		
		if(intent.getContextWorkflow() != null) {
			
			ChatBotUtil.executeContextWorkflow(intent, session, context);
			
			return false;
		}
		
		if(intent.isWithParams()) {
			
			if(session.getRecievedParamCount() >= session.getRequiredParamCount()) {
				
				if(intent.isConfirmationNeeded() && (session.isConfirmed() == null || !session.isConfirmed())) {
					
					session.setState(State.WAITING_FOR_CONFIRMATION.getIntVal());
					
					ChatBotSessionConversation chatBotSessionConversation = ChatBotUtil.constructAndAddConfirmationCBSessionConversationParams(session);
					
					context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, chatBotSessionConversation);
					
				}
				else {
					ChatBotUtil.executeIntentAction(session, intent, context);
				}
			}
			else {
				ChatBotIntentParam nextParam = null;
				for(ChatBotIntentParam intentParam : intent.getChatBotIntentParamList()) {
					if(intentParam.getTypeConfig() == ChatBotIntentParam.Type_Config.MANDATORY.getIntVal()) {
						nextParam = intentParam;
						break;
					}
				}
				
				ChatBotSessionConversation chatBotSessionConversation = ChatBotUtil.constructAndAddCBSessionConversationParams(nextParam, session,null,null);
				
				context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, chatBotSessionConversation);
			}
			ChatBotUtil.updateChatBotSession(session);
		}
		else {
			session.setState(State.RESPONDED.getIntVal());
			
			JSONArray response = intent.executeActions(context, null);
			
			session.setResponse(response.toJSONString());
			
			ChatBotUtil.updateChatBotSession(session);
		}
		
		return false;
	}
	
}
