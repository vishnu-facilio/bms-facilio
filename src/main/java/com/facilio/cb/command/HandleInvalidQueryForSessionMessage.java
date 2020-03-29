package com.facilio.cb.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSession.State;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class HandleInvalidQueryForSessionMessage extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotModel model = (ChatBotModel) context.get(ChatBotConstants.CHAT_BOT_MODEL);
		
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		ChatBotMLResponse mlResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);
		
		if(model.getChatBotModelVersion().getAccuracyRate() > 0) {
			if(model.getChatBotModelVersion().getAccuracyRate() > mlResponse.getAccuracy()) {
				setInvalidQuery(mlResponse,session);
			}
		}
		else if(ChatBotConstants.DEFAULT_ACCURACY_RATE > mlResponse.getAccuracy()) {
			setInvalidQuery(mlResponse,session);
		}
		
		if(session != null) {
			
			ChatBotSession lastInvalidSession = ChatBotUtil.getLastInvalidQuerySession();
			
			if(lastInvalidSession != null) {
				if(lastInvalidSession.getParentSessionId() > 0) {
					session.setParentSessionId(lastInvalidSession.getParentSessionId());
				}
				else {
					session.setParentSessionId(lastInvalidSession.getId());
				}
			}
			
			if(session.getState() == ChatBotSession.State.INVALID_QUERY.getIntVal()) {
				
				ChatBotIntent intent = ChatBotUtil.getIntent(model.getChatBotModelVersion().getId(), mlResponse.getIntent());
				
				context.put(ChatBotConstants.CHAT_BOT_INTENT,intent);
				
				JSONArray response = intent.executeActions(context, null);
				
				session.setResponse(response.toJSONString());
				session.setIntent(intent);
				
				ChatBotUtil.addChatBotSession(session);
				
				context.put(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION, Boolean.TRUE);
			}
			
		}
		
		return false;
	}
	
	private void setInvalidQuery(ChatBotMLResponse mlResponse,ChatBotSession session) {
		
		mlResponse.setNotAccurate(true);
		mlResponse.setIntent(ChatBotConstants.CHAT_BOT_INTENT_NOT_FOUND_INTENT);
		session.setState(ChatBotSession.State.INVALID_QUERY.getIntVal());
	}
}
