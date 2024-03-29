package com.facilio.cb.action;

import java.io.File;
import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FileContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSuggestionContext;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.wms.constants.WmsEventType;
import com.facilio.wms.endpoints.LiveSession.LiveSessionType;
import com.facilio.wms.message.WmsChatMessage;
import com.facilio.wms.message.WmsEvent;
import com.facilio.wms.util.WmsApi;

public class ChatBotAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JSONObject chatMessage;
	ChatBotSuggestionContext suggestion;
	
	private File fileContent;
	private String fileContentFileName;
	private String fileContentContentType;

	public File getFileContent() {
		return fileContent;
	}

	public void setFileContent(File fileContent) {
		this.fileContent = fileContent;
	}

	public String getFileContentFileName() {
		return fileContentFileName;
	}

	public void setFileContentFileName(String fileContentFileName) {
		this.fileContentFileName = fileContentFileName;
	}

	public String getFileContentContentType() {
		return fileContentContentType;
	}

	public void setFileContentContentType(String fileContentContentType) {
		this.fileContentContentType = fileContentContentType;
	}
	
	public ChatBotSuggestionContext getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(ChatBotSuggestionContext suggestion) {
		this.suggestion = suggestion;
	}

	long startTime = -1;
	
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public JSONObject getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(JSONObject chatMessage) {
		this.chatMessage = chatMessage;
	}

	ChatBotSessionConversation chatBotSessionConversation;
	ChatBotSession chatBotSession;

	public ChatBotSessionConversation getChatBotSessionConversation() {
		return chatBotSessionConversation;
	}

	public void setChatBotSessionConversation(ChatBotSessionConversation chatBotSessionConversation) {
		this.chatBotSessionConversation = chatBotSessionConversation;
	}

	public ChatBotSession getChatBotSession() {
		return chatBotSession;
	}

	public void setChatBotSession(ChatBotSession chatBotSession) {
		this.chatBotSession = chatBotSession;
	}
	
	String modelName;
	
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String chat() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.HandleChatBotMessageChain();
		
		FacilioContext context = chain.getContext();
		
		if(fileContent != null) {
			
			FacilioChain addFileChain = FacilioChainFactory.getAddFileChain();
				
			FacilioContext fileContext = addFileChain.getContext();
			
			fileContext.put(FacilioConstants.ContextNames.FILE, this.fileContent);
			fileContext.put(FacilioConstants.ContextNames.FILE_NAME, this.fileContentFileName);
			fileContext.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, this.fileContentContentType);
	 		
			addFileChain.execute();
			
			FileContext newFile = (FileContext) fileContext.get(FacilioConstants.ContextNames.FILE_CONTEXT_LIST);
			
			if(chatMessage == null) {
				chatMessage = new JSONObject();
			}
			
			chatMessage.put(ChatBotConstants.CHAT_BOT_ID, newFile.getFileId());
			
			context.put(ChatBotConstants.CHAT_BOT_ATTACHMENT, AttachmentsAPI.getAttachmentContentFromFileContext(newFile.getFileId()));
		}
		
		if(chatMessage != null) {
			
			context.put(ChatBotConstants.CHAT_BOT_MESSAGE_JSON, chatMessage);
			context.put(ChatBotConstants.CHAT_BOT_SUGGESTION, suggestion);
			
			chain.execute();
			
			String chatBotReplyMessage = (String)context.get(ChatBotConstants.CHAT_BOT_MESSAGE_JSON);
			
			setResult(ChatBotConstants.CHAT_BOT_MESSAGE_JSON, chatBotReplyMessage);
			setResult(ChatBotConstants.CHAT_BOT_SUGGESTIONS, FieldUtil.getAsJSONArray((List<ChatBotSuggestionContext>)context.get(ChatBotConstants.CHAT_BOT_SUGGESTIONS), ChatBotSuggestionContext.class));
			setResult(ChatBotConstants.CHAT_BOT_ATTACHMENT,FieldUtil.getAsJSON(context.get(ChatBotConstants.CHAT_BOT_ATTACHMENT)));
			
		}
		
		long uniqueID = -1;
		if(context.get(ChatBotConstants.NEW_CHAT_BOT_SESSION_CONVERSATION) != null) {
			uniqueID = ((ChatBotSessionConversation) context.get(ChatBotConstants.NEW_CHAT_BOT_SESSION_CONVERSATION) ).getId();
		}
		else if (context.get(ChatBotConstants.CHAT_BOT_SESSION) != null) {
			uniqueID = ((ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION) ).getId();
		}
		
		if(chatMessage != null) {
			chatMessage.put("uniqueID", uniqueID);
			sendToWMS(chatMessage,"userMessage");
		}
		setResult("uniqueID", uniqueID);
		
		sendToWMS(getResult(),"botMessage");
		
		return SUCCESS;
	}
	
	private void sendToWMS(JSONObject chatMessage2,String action) throws Exception {
		
		if(chatMessage2 != null) {
			WmsEvent event = new WmsEvent();
			event.setNamespace("chatbot");
			event.setAction(action);
			event.setEventType(WmsEventType.ChatBot.NEW_MESSAGE);
			event.setSessionType(LiveSessionType.TENANT_PORTAL);
			event.addData("result", chatMessage2.toJSONString());
			
			WmsApi.sendEvent(AccountUtil.getCurrentUser().getId(), event);
		}
		
	}

	public String flushIntentAndModel() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.FlushIntentAndModelChain();
		FacilioContext context = chain.getContext();
		
		context.put(ChatBotConstants.CHAT_BOT_ML_MODEL_NAME, getModelName());
		
		chain.execute();
		
		return SUCCESS;
		
	}
	
	public String getChatMessages() throws Exception {
		
		if(getPerPage() < 0) {
			setPerPage(20);
		}
		if(getPage() < 0) {
			setPage(1);
		}
		
		FacilioChain rdmChain = ReadOnlyChainFactory.getChatBotConversationChain();
		FacilioContext constructListContext = rdmChain.getContext();
		constructListContext(constructListContext);
		
		constructListContext.put(FacilioConstants.ContextNames.START_TIME, startTime);
		
		rdmChain.execute();
		
		setResult(ChatBotConstants.CHAT_BOT_SESSIONS, constructListContext.get(ChatBotConstants.CHAT_BOT_SESSIONS));
		
		return SUCCESS;
		
	}
	
}
