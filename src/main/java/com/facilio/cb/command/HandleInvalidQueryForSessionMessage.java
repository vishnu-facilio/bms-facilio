package com.facilio.cb.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class HandleInvalidQueryForSessionMessage extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(HandleInvalidQueryForSessionMessage.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		ChatBotIntent intent = (ChatBotIntent) context.get(ChatBotConstants.CHAT_BOT_INTENT);
		
		if(session != null) {
			
			ChatBotSession lastInvalidSession = ChatBotUtil.getLastInvalidQuerySession(session);
			
			if(lastInvalidSession != null) {
				if(lastInvalidSession.getParentSessionId() > 0) {
					session.setParentSessionId(lastInvalidSession.getParentSessionId());
				}
				else {
					session.setParentSessionId(lastInvalidSession.getId());
				}
			}
			
			specialHandlingForCreateWorkRequestIntent(session,lastInvalidSession);
			
			if(session.getState() == ChatBotSession.State.INVALID_QUERY.getIntVal()) {
				
				context.put(ChatBotConstants.CHAT_BOT_INTENT,intent);
				
				JSONArray response = intent.executeActions(context, null);
				
				session.setResponse(response.toJSONString());
				session.setIntent(intent);
				
				context.put(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED,Boolean.TRUE);
				context.put(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION, Boolean.TRUE);
			}
			
		}
		return false;
	}

	private void specialHandlingForCreateWorkRequestIntent(ChatBotSession session, ChatBotSession lastInvalidSession) {
		
		try {
			if(session.getIntent().getName().equals("system_create_wo_intent") && lastInvalidSession != null) {
				String label = (String) lastInvalidSession.getQueryJson().get("label");
				session.addParamsObject("subject", label);
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
