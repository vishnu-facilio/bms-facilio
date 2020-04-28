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
	
	public static final String CHAT_BOT_DEFAULT_CONFIRMATION_TEXT = "Thanks for providing me with the information";
	
	public static final String CHAT_BOT_DEFAULT_SUBMIT_CONFIRMATION_TEXT = "Can I go ahead and submit this for you?";
	
	
	public static final String CHAT_BOT_SESSION = "chatBotSession";
	public static final String CHAT_BOT_SESSIONS = "chatBotSessions";
	public static final String CHAT_BOT_SESSION_COUNT = "chatBotSessionCount";
	public static final String CHAT_BOT_MESSAGE_JSON = "chatBotMessageJson";
	public static final String CHAT_BOT_OPTION_STRING = "chatBotOptionString";
	public static final String CHAT_BOT_SESSION_CONVERSATION = "chatBotSessionConversation";
	public static final String NEW_CHAT_BOT_SESSION_CONVERSATION = "newChatBotSessionConversation";
	public static final String CHAT_BOT_IS_ACTION_EXECUTED = "chatBotIsActionExecuted";
	public static final String CHAT_BOT_SESSION_PARAM = "chatBotSessionParam";
	public static final String CHAT_BOT_SESSION_PARAMS = "chatBotSessionParams";
	
	public static final String CHAT_BOT_ADD_ACTION_INTENT_PARAM = "chatBotaddActionIntentParam";
	public static final String CHAT_BOT_EDIT_ACTION_INTENT_PARAM = "chatBotEditActionIntentParam";
	
	public static final String CHAT_BOT_ATTACHMENT = "chatbotAttachment";
	
	public static final String CHAT_BOT_WORKFLOW_RETURN_TEXT = "returnText";
	public static final String CHAT_BOT_WORKFLOW_RETURN_EXTRA_PARAM = "extraParams";
	public static final String CHAT_BOT_WORKFLOW_RETURN_TEXT_LIST = "textList";
	
	public static final String CHAT_BOT_SKIP_ACTION_EXECUTION = "skipActionExecution";
	
	public static final String CHAT_BOT_SUGGESTIONS = "chatBotSuggestions";
	public static final String CHAT_BOT_SUGGESTION = "chatBotSuggestion";
	
	public static final String CHAT_BOT_MODEL = "chatBotModel";
	
	public static final String CHAT_BOT_ML_MODEL_NAME = "chatBotMLModelName";
	
	public static final String CHAT_BOT_MODEL_VERSION = "chatBotModelVersion";
	
	public static final String CHAT_BOT_RESPONSE = "chatBotResponse";
	public static final String CHAT_BOT_CONFIRMATION_RESPONSE = "chatBotConfirmationResponse";
	public static final String CHAT_BOT_RESPONSE_TYPE = "chatBotResponseType";
	public static final String CHAT_BOT_RESPONSE_OPTIONS = "chatBotResponseOptions";
	
	public static final String CHAT_BOT_RESPONSE_SLOTS = "slots";
	public static final String CHAT_BOT_PREVIOUS_VALUE = "previousValue";
	public static final String CHAT_BOT_PARAM_TYPE = "chatBotParamType";
	public static final String CHAT_BOT_PARAM_IS_MULTIPLE_ALLOWED = "chatBotParamIsMultipleAlowed";
	
	public static final String CHAT_BOT_INTENT = "chatBotIntent";
	
	public static final String CHAT_BOT_LABEL = "label";
	public static final String CHAT_BOT_ID = "id";
	
	public static final String CHAT_BOT_INTENT_LIST = "chatBotIntentList";
	
	public static final String CHAT_BOT_ML_RESPONSE = "chatBotMLResponse";
	
	public static final String CHAT_BOT_INTENT_NOT_FOUND_INTENT = "system_intent_not_fount_intent";
	public static final String CHAT_BOT_SESSION_TERMINATE_INTENT = "system_terminate_session_intent";
	
	public static final String CHAT_BOT_AFFERMATIVE_INTENT = "system_affirmative_intent";
	
	public static final String CHAT_BOT_INTENT_PARAM_INVALID_MESSAGE = "The given value seems to be invalid.\n";
	
	
	public static final Double DEFAULT_ACCURACY_RATE = 0.4d;
	
	public static final Long LAST_INVALID_SESSION_BUFFER_TIME = 300000l;													// 5 mins to millisec
	
	public static final Long LAST_SESSION_WAITING_FOR_PARAM_BUFFER_TIME = 1800000l;											// 30 mins to millisec
	
	
	public static Map<String,List<String>> defaultIntentResponse;
	
	static {

		Yaml yaml = new Yaml();
        try(InputStream inputStream = FacilioEnum.class.getClassLoader().getResourceAsStream(DEFAULT_REPLIES);) {
        	defaultIntentResponse = yaml.load(inputStream);
        	
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while reading defaultIntentResponse conf file. "+e.getMessage(), e);
        }
	}
	
	public static String getDefaultIntentResponse(String intent) {
		
		List<String> responseList = defaultIntentResponse.get(intent);
		Random random = new Random();
		return responseList.get(random.nextInt(responseList.size()));
	}
}
