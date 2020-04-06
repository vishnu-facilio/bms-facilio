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
		
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		ChatBotIntent intent = (ChatBotIntent) context.get(ChatBotConstants.CHAT_BOT_INTENT);
		
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
				
				context.put(ChatBotConstants.CHAT_BOT_INTENT,intent);
				
				JSONArray response = intent.executeActions(context, null);
				
				session.setResponse(response.toJSONString());
				session.setIntent(intent);
				
				context.put(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION, Boolean.TRUE);
			}
			
		}
		return false;
	}
	
}
