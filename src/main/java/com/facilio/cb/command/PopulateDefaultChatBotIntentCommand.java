package com.facilio.cb.command;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;

public class PopulateDefaultChatBotIntentCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		List<ChatBotIntent> chatBotIntents = (List<ChatBotIntent>)context.get(ChatBotConstants.CHAT_BOT_INTENT_LIST);
		
		for (ChatBotIntent chatBotIntent :chatBotIntents) {
			FacilioChain chain = TransactionChainFactory.getAddChatBotIntentChain();
			
			FacilioContext newcontext = chain.getContext();
			
			newcontext.put(ChatBotConstants.CHAT_BOT_INTENT, chatBotIntent);
			
			chain.execute();
		}
		return false;
	}

}
