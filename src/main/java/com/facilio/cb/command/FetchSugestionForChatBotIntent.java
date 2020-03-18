package com.facilio.cb.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSuggestionContext;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class FetchSugestionForChatBotIntent extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		if(chatBotSessionConversation != null) {
			
			ChatBotSession session = chatBotSessionConversation.getChatBotSession();
			
			List<ChatBotSuggestionContext> chatBotSuggestionContexts = new ArrayList<>();
			
			if(chatBotSessionConversation.getState() == ChatBotSessionConversation.State.CONFIRMATION_RAISED.getIntVal()) {
				
				List<ChatBotIntentParam> editableIntextParams = getEditableParamList(session.getIntent().getChatBotIntentParamList());
				
				for(ChatBotIntentParam editableIntextParam :editableIntextParams) {
					
					ChatBotSuggestionContext chatBotSuggestionContext = new ChatBotSuggestionContext();
					
					chatBotSuggestionContext.setSuggestion("Edit "+(editableIntextParam.getDisplayName() == null ? editableIntextParam.getName() : editableIntextParam.getDisplayName()));
					chatBotSuggestionContext.setType(ChatBotSuggestionContext.Type.EDITABLE_PARAM.getIntVal());
					chatBotSuggestionContext.setIntentParamId(editableIntextParam.getId());
					
					chatBotSuggestionContexts.add(chatBotSuggestionContext);
				}
				
				List<ChatBotIntentParam> optionalIntextParams = ChatBotUtil.fetchRemainingOptionalChatBotIntentParams(session.getIntentId(), session.getId());
				
				if(optionalIntextParams != null) {
					for(ChatBotIntentParam optionalIntextParam :optionalIntextParams) {
						
						ChatBotSuggestionContext chatBotSuggestionContext = new ChatBotSuggestionContext();
						
						chatBotSuggestionContext.setSuggestion("Add "+(optionalIntextParam.getDisplayName() == null ? optionalIntextParam.getName() : optionalIntextParam.getDisplayName()));
						chatBotSuggestionContext.setType(ChatBotSuggestionContext.Type.OPTIONAL_PARAM.getIntVal());
						chatBotSuggestionContext.setIntentParamId(optionalIntextParam.getId());
						
						chatBotSuggestionContexts.add(chatBotSuggestionContext);
					}
				}
				
				context.put(ChatBotConstants.CHAT_BOT_SUGGESTIONS, chatBotSuggestionContexts);
			}
		}
		
		return false;
	}

	private List<ChatBotIntentParam> getEditableParamList(List<ChatBotIntentParam> params) {
		
		List<ChatBotIntentParam> editableIntextParams = new ArrayList<ChatBotIntentParam>();
		for(ChatBotIntentParam param : params) {
			if(param.isEditable()) {
				editableIntextParams.add(param);
			}
		}
		return editableIntextParams;
	}
	
}
