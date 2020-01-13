package com.facilio.cb.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.facilio.bmsconsole.commands.PopulateDefaultConnectionsCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;

public class ChatBotConstants {
	
	private static final Logger LOGGER = LogManager.getLogger(ChatBotConstants.class.getName());
	
	private static final String DEFAULT_REPLIES = "conf/chatbot/chatBotDefaultReplies.yml";
	
	public static final String CHAT_BOT_SESSION = "chatBotSession";
	public static final String CHAT_BOT_MESSAGE_STRING = "chatBotMessageString";
	public static final String CHAT_BOT_SESSION_CONVERSATION = "chatBotSessionConversation";
	public static final String CHAT_BOT_IS_ACTION_EXECUTED = "chatBotIsActionExecuted";
	public static final String CHAT_BOT_SESSION_PARAM = "chatBotSessionParam";
	public static final String CHAT_BOT_SESSION_PARAMS = "chatBotSessionParams";
	
	public static final String CHAT_BOT_MODEL = "chatBotModel";
	
	public static final String CHAT_BOT_RESPONSE_STRING = "chatBotResponseString";
	
	public static final String CHAT_BOT_INTENT = "chatBotIntent";
	
	public static final String CHAT_BOT_ML_RESPONSE = "chatBotMLResponse";
	
	public static final String CHAT_BOT_INTENT_NOT_FOUND_INTENT = "system_intent_not_fount_intent";
	public static final String CHAT_BOT_SESSION_TERMINATE_INTENT = "system_terminate_session";
	
	public static final String CHAT_BOT_INTENT_PARAM_INVALID_MESSAGE = "The given value seems to be invalid.\n";
	
	
	public static final Double DEFAULT_ACCURACY_RATE = 35d;
	
	public static final Long LAST_INVALID_SESSION_BUFFER_TIME = 300000l;												// 5 mins to millisec
	
	public static final Long LAST_SESSION_WAITING_FOR_PARAM_BUFFER_TIME = 1800000l;											// 30 mins to millisec
	
	
	public static Map<String,List<String>> defaultIntentResponse;
	
	static {

		Yaml yaml = new Yaml();
        InputStream inputStream = null;
        try {
        	
        	inputStream = FacilioEnum.class.getClassLoader().getResourceAsStream(DEFAULT_REPLIES);
        	defaultIntentResponse = yaml.load(inputStream);
        	
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while reading defaultIntentResponse conf file. "+e.getMessage(), e);
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
	}
	
	public static String getDefaultIntentResponse(String intent) {
		
		List<String> responseList = defaultIntentResponse.get(intent);
		Random random = new Random();
		return responseList.get(random.nextInt(responseList.size()));
	}
}
