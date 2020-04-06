package com.facilio.cb.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSession.State;
import com.facilio.cb.context.ChatBotSessionConversation;
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
		
		ChatBotIntent intent = (ChatBotIntent) context.get(ChatBotConstants.CHAT_BOT_INTENT);
		
		if(intent.isWithParams()) {
			
			if(session.getRecievedParamCount() >= session.getRequiredParamCount()) {
				
				ChatBotUtil.executeIntentAction(session, intent, context);
				
				context.put(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED,true);
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
		}
		else {
			session.setState(State.RESPONDED.getIntVal());
			
			JSONArray response = intent.executeActions(context, null);
			
			session.setResponse(response.toJSONString());
			
			context.put(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED,true);
		}
		
		return false;
	}
	
}
