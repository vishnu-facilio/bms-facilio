package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSession.State;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class CheckAccuracyAndFetchIntentCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		ChatBotModel model = (ChatBotModel) context.get(ChatBotConstants.CHAT_BOT_MODEL);
		
		ChatBotMLResponse mlResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);
		
		if(model.getChatBotModelVersion().getAccuracyRate() > 0) {
			if(model.getChatBotModelVersion().getAccuracyRate() > mlResponse.getAccuracy()) {
				setInvalidQuery(mlResponse,session);
			}
		}
		else if(ChatBotConstants.DEFAULT_ACCURACY_RATE > mlResponse.getAccuracy()) {
			setInvalidQuery(mlResponse,session);
		}

		ChatBotIntent intent = ChatBotUtil.getIntent(model.getChatBotModelVersion().getId(), mlResponse.getIntent());
		
		context.put(ChatBotConstants.CHAT_BOT_INTENT, intent);
		
		session.setIntentId(intent.getId());
		session.setIntent(intent);
		
		if(session.getState() != State.INVALID_QUERY.getIntVal()) {
			session.setState(State.WAITING_FOR_PARAMS.getIntVal());
		}
		
		ChatBotUtil.addChatBotSession(session);
		return false;
	}
	
private void setInvalidQuery(ChatBotMLResponse mlResponse,ChatBotSession session) {
		
		mlResponse.setNotAccurate(true);
		mlResponse.setIntent(ChatBotConstants.CHAT_BOT_INTENT_NOT_FOUND_INTENT);
		session.setState(ChatBotSession.State.INVALID_QUERY.getIntVal());
	}

}
