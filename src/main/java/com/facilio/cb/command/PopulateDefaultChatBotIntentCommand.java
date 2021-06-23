package com.facilio.cb.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentChildContent;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class PopulateDefaultChatBotIntentCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		List<ChatBotIntent> chatBotIntents = (List<ChatBotIntent>)context.get(ChatBotConstants.CHAT_BOT_INTENT_LIST);
		
		Map<String,Long> intentNameVsIdMAp = new HashMap<String, Long>();
		
		for (ChatBotIntent chatBotIntent :chatBotIntents) {
			FacilioChain chain = TransactionChainFactory.getAddChatBotIntentChain();
			
			FacilioContext newcontext = chain.getContext();
			
			newcontext.put(ChatBotConstants.CHAT_BOT_INTENT, chatBotIntent);
			
			chain.execute();
			
			intentNameVsIdMAp.put(chatBotIntent.getName(), chatBotIntent.getId());
		}
		
		for (ChatBotIntent chatBotIntent :chatBotIntents) {
			
			if(chatBotIntent.getChildIntentNames() != null && !chatBotIntent.getChildIntentNames().isEmpty()) {
				
				for(String name : chatBotIntent.getChildIntentNames()) {
					
					ChatBotIntentChildContent botIntentChildContent = new ChatBotIntentChildContent();
					
					botIntentChildContent.setOrgId(AccountUtil.getCurrentOrg().getId());
					botIntentChildContent.setParentId(chatBotIntent.getId());
					botIntentChildContent.setChildId(intentNameVsIdMAp.get(name));
					
					GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
							.table(ModuleFactory.getCBIntentChildModule().getTableName())
							.fields(FieldFactory.getCBIntentChildFields());

					Map<String, Object> props = FieldUtil.getAsProperties(botIntentChildContent);
					insertBuilder.addRecord(props);
					insertBuilder.save();

					botIntentChildContent.setId((Long) props.get("id"));
				}
			}
			
		}
		return false;
	}

}
