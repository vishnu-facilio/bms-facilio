package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.modules.FieldUtil;

public class ParseDefaultChatBotIntentCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(PopulateDefaultConnectionsCommand.class.getName());

	private static final String DEFAULT_INTENT = "conf/chatbot/chatBotDefaultIntent.yml";
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Yaml yaml = new Yaml();
        List<Object> cbIntent = null;
        try(InputStream inputStream = ParseDefaultChatBotIntentCommand.class.getClassLoader().getResourceAsStream(DEFAULT_INTENT);) {
        	
        	
        	cbIntent = yaml.load(inputStream);
        	List<ChatBotIntent> intents = getCBIntent(cbIntent);
        	context.put(ChatBotConstants.CHAT_BOT_INTENT_LIST, intents);
        }
        catch (Exception e) {
        	e.printStackTrace();
            LOGGER.error("Error occurred while reading default chat bot conf file. "+e.getMessage(), e);
        }
		
		return false;
	}
	
	
	private List<ChatBotIntent> getCBIntent(List<Object> cbIntents) throws Exception {
		
		if(cbIntents != null) {
			
			List<ChatBotIntent> chatBotIntents = new ArrayList<ChatBotIntent>();
			for (Object cbIntent :cbIntents) {
				Map cbIntentMap = (Map) cbIntent;
				ChatBotIntent chatBotIntent = FieldUtil.getAsBeanFromMap(cbIntentMap, ChatBotIntent.class);
				chatBotIntents.add(chatBotIntent);
			}
			return chatBotIntents;
		}
		return null;
	}

}
