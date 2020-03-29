package com.facilio.cb.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentChildContent;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSuggestionContext;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class FetchSugestionForChatBotIntent extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSession session = null;
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		List<ChatBotSuggestionContext> chatBotSuggestionContexts = new ArrayList<>();
		
		if(chatBotSessionConversation != null) {
			
			session = chatBotSessionConversation.getChatBotSession();
			
			if(chatBotSessionConversation.getState() == ChatBotSessionConversation.State.CONFIRMATION_RAISED.getIntVal()) {		// in conversation suggestions
				
				chatBotSuggestionContexts.addAll(getDefaultSuggestionForConfirmationCard());
				
				List<ChatBotIntentParam> editableIntextParams = getEditableParamList(session.getIntent().getChatBotIntentParamList());
				
				for(ChatBotIntentParam editableIntextParam :editableIntextParams) {
					
					ChatBotSuggestionContext chatBotSuggestionContext = new ChatBotSuggestionContext();
					
					chatBotSuggestionContext.setSuggestion("Edit "+(editableIntextParam.getDisplayName() == null ? editableIntextParam.getName() : editableIntextParam.getDisplayName()));
					chatBotSuggestionContext.setType(ChatBotSuggestionContext.Type.EDITABLE_PARAM.getIntVal());
					
					chatBotSuggestionContexts.add(chatBotSuggestionContext);
				}
				
				List<ChatBotIntentParam> optionalIntextParams = ChatBotUtil.fetchRemainingOptionalChatBotIntentParams(session.getIntentId(), session.getId());
				
				if(optionalIntextParams != null) {
					for(ChatBotIntentParam optionalIntextParam :optionalIntextParams) {
						
						ChatBotSuggestionContext chatBotSuggestionContext = new ChatBotSuggestionContext();
						
						chatBotSuggestionContext.setSuggestion("Add "+(optionalIntextParam.getDisplayName() == null ? optionalIntextParam.getName() : optionalIntextParam.getDisplayName()));
						chatBotSuggestionContext.setType(ChatBotSuggestionContext.Type.OPTIONAL_PARAM.getIntVal());
						
						chatBotSuggestionContexts.add(chatBotSuggestionContext);
					}
				}
				
				context.put(ChatBotConstants.CHAT_BOT_SUGGESTIONS, chatBotSuggestionContexts);
				
				return false;
			}
		}
		else {
			session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		}
		
		if(session.getState() == ChatBotSession.State.RESPONDED.getIntVal() || session.getState() == ChatBotSession.State.INVALID_QUERY.getIntVal()) {
			
			ChatBotIntent intent = session.getIntent();
			
			List<ChatBotIntentChildContent> intentChilds = ChatBotUtil.getChildIntent(intent.getId());
			
			if(intentChilds != null) {
				for(ChatBotIntentChildContent intentChild :intentChilds) {
					
					ChatBotSuggestionContext chatBotSuggestionContext = new ChatBotSuggestionContext();
					
					chatBotSuggestionContext.setSuggestion(intentChild.getChildIntent().getDisplayName());
					chatBotSuggestionContext.setType(ChatBotSuggestionContext.Type.CHAINED_INTENT.getIntVal());
					
					chatBotSuggestionContexts.add(chatBotSuggestionContext);
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
	
	private List<ChatBotSuggestionContext> getDefaultSuggestionForConfirmationCard() {
		
		List<ChatBotSuggestionContext> ChatBotSuggestionContexts = new ArrayList<ChatBotSuggestionContext>();
		
		ChatBotSuggestionContext chatBotSuggestionContext = new ChatBotSuggestionContext();
		chatBotSuggestionContext.setType(ChatBotSuggestionContext.Type.OTHERS.getIntVal());
		chatBotSuggestionContext.setSuggestion("Confirm");
		
		ChatBotSuggestionContexts.add(chatBotSuggestionContext);
		
		chatBotSuggestionContext = new ChatBotSuggestionContext();
		chatBotSuggestionContext.setType(ChatBotSuggestionContext.Type.OTHERS.getIntVal());
		chatBotSuggestionContext.setSuggestion("Cancel");
		
		ChatBotSuggestionContexts.add(chatBotSuggestionContext);
		
		return ChatBotSuggestionContexts;
	}
	
}
