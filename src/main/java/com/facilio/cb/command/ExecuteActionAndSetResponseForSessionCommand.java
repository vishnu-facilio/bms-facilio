package com.facilio.cb.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.cb.context.ChatBotConfirmContext;
import com.facilio.cb.context.ChatBotExecuteContext;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotParamContext;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSession.State;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

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
		
		if(intent.getContextWorkflow() != null) {
			
			session.setState(State.WAITING_FOR_PARAMS.getIntVal());
			
			ChatBotUtil.addChatBotSession(session);
			
			ChatBotUtil.executeContextWorkflow(intent, session, context);
			
			return false;
		}
		
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
				
				ChatBotUtil.executeIntentAction(session, intent, context);
				
			}
			else {
				
				ChatBotIntentParam nextParam = intent.getChatBotIntentParamList().get(0);
				
				ChatBotSessionConversation chatBotSessionConversation = ChatBotUtil.constructAndAddCBSessionConversationParams(nextParam, session,null,null);
				
				context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, chatBotSessionConversation);
			}
			ChatBotUtil.updateChatBotSession(session);
		}
		else {
			if(session.getState() < 0) {
				session.setState(State.RESPONDED.getIntVal());
			}
			JSONArray response = intent.executeActions(context, null);
			
			session.setResponse(response.toJSONString());
			
			ChatBotUtil.addChatBotSession(session);
		}
		
		return false;
	}
	
}
