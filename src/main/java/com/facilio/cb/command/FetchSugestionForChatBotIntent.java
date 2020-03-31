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
import com.facilio.modules.FieldUtil;

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
				
				List<ChatBotIntentParam> optionalIntextParams = ChatBotUtil.fetchRemainingOptionalChatBotIntentParams(session.getIntentId(), session.getId());
				
				List<Long> optionalIntentParamIds = new ArrayList<>();
				if(optionalIntextParams != null) {
					for(ChatBotIntentParam optionalIntextParam :optionalIntextParams) {
						
						optionalIntentParamIds.add(optionalIntextParam.getId());
						
						ChatBotSuggestionContext chatBotSuggestionContext = new ChatBotSuggestionContext();
						
						chatBotSuggestionContext.setSuggestion(optionalIntextParam.getAddParamTriggerText());
						chatBotSuggestionContext.setType(ChatBotSuggestionContext.Type.OPTIONAL_PARAM.getIntVal());
						
						chatBotSuggestionContexts.add(chatBotSuggestionContext);
					}
				}
				
				List<ChatBotIntentParam> editableIntextParams = getEditableParamList(session.getIntent().getChatBotIntentParamList());
				
				for(ChatBotIntentParam editableIntextParam :editableIntextParams) {
					
					if(optionalIntentParamIds.contains(editableIntextParam.getId())) {
						continue;
					}
					
					ChatBotSuggestionContext chatBotSuggestionContext = new ChatBotSuggestionContext();
					
					chatBotSuggestionContext.setSuggestion(editableIntextParam.getUpdateParamTriggerText());
					chatBotSuggestionContext.setType(ChatBotSuggestionContext.Type.EDITABLE_PARAM.getIntVal());
					
					chatBotSuggestionContexts.add(chatBotSuggestionContext);
				}
				
				chatBotSessionConversation.setSuggestion(FieldUtil.getAsJSONArray(chatBotSuggestionContexts, ChatBotSuggestionContext.class).toJSONString());
				
				ChatBotUtil.updateChatBotSessionConversation(chatBotSessionConversation);
				
				context.put(ChatBotConstants.CHAT_BOT_SUGGESTIONS, chatBotSuggestionContexts);
				
				return false;
			}
			else if(session.getState() == ChatBotSession.State.WAITING_FOR_PARAMS.getIntVal()) {
				chatBotSuggestionContexts.addAll(getDefaultSuggestionForConversation());
				context.put(ChatBotConstants.CHAT_BOT_SUGGESTIONS, chatBotSuggestionContexts);
				
				chatBotSessionConversation.setSuggestion(FieldUtil.getAsJSONArray(chatBotSuggestionContexts, ChatBotSuggestionContext.class).toJSONString());
				
				ChatBotUtil.updateChatBotSessionConversation(chatBotSessionConversation);
				
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
				
				session.setSuggestion(FieldUtil.getAsJSONArray(chatBotSuggestionContexts, ChatBotSuggestionContext.class).toJSONString());
				
				ChatBotUtil.updateChatBotSession(session);
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
	
	private List<ChatBotSuggestionContext> getDefaultSuggestionForConversation() {
		
		List<ChatBotSuggestionContext> ChatBotSuggestionContexts = new ArrayList<ChatBotSuggestionContext>();
		
		ChatBotSuggestionContext chatBotSuggestionContext = new ChatBotSuggestionContext();
		chatBotSuggestionContext.setType(ChatBotSuggestionContext.Type.OTHERS.getIntVal());
		chatBotSuggestionContext.setSuggestion("Cancel");
		
		ChatBotSuggestionContexts.add(chatBotSuggestionContext);
		
		return ChatBotSuggestionContexts;
	}
	
}
