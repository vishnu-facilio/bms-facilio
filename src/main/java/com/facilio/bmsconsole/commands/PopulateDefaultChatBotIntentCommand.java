package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;

public class PopulateDefaultChatBotIntentCommand extends FacilioCommand{
	
	private static final Logger LOGGER = LogManager.getLogger(PopulateDefaultConnectionsCommand.class.getName());

	private static final String DEFAULT_INTENT = "conf/chatbot/chatBotDefaultIntent.yml";
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Yaml yaml = new Yaml();
        InputStream inputStream = null;
        List<Object> cbIntent = null;
        try {
        	
        	inputStream = PopulateDefaultChatBotIntentCommand.class.getClassLoader().getResourceAsStream(DEFAULT_INTENT);
        	cbIntent = yaml.load(inputStream);
        	addCBIntent(cbIntent);
        }
        catch (Exception e) {
        	e.printStackTrace();
            LOGGER.error("Error occurred while reading default chat bot conf file. "+e.getMessage(), e);
        }
		finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					LOGGER.error("Error occurred while clossing stream. "+e.getMessage(), e);
				}
			}
		}
		
		return false;
	}
	
	
	private void addCBIntent(List<Object> cbIntents) throws Exception {
		
		if(cbIntents != null) {
			
			for (Object cbIntent :cbIntents) {
				Map cbIntentMap = (Map) cbIntent;
				ChatBotIntent chatBotIntent = FieldUtil.getAsBeanFromMap(cbIntentMap, ChatBotIntent.class);
				
				FacilioChain chain = TransactionChainFactory.getAddChatBotIntentChain();
				
				FacilioContext context = chain.getContext();
				
				context.put(ChatBotConstants.CHAT_BOT_INTENT, chatBotIntent);
				
				chain.execute();
			}
		}
	}

}
