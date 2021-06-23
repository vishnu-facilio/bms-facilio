package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotModel.App_Type;
import com.facilio.cb.context.ChatBotModelVersion;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class GetOrAddCurrentActiveModel extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		int userType = AccountUtil.getCurrentAccount().getUser().getUserType();
		
		App_Type appType = App_Type.getAllAppTypesUsers().get(userType);
		
		ChatBotModel model = ChatBotUtil.getActiveModel(appType);
		
		if(model == null) {
			model = ChatBotUtil.prepareAndAddDefaultModel((String)context.get(ChatBotConstants.CHAT_BOT_ML_MODEL_NAME));
			
			ChatBotModelVersion modelVersion = ChatBotUtil.prepareAndAddModelVersion(model);
			
			model.setChatBotModelVersion(modelVersion);
		}
		
		context.put(ChatBotConstants.CHAT_BOT_MODEL, model);
		context.put(ChatBotConstants.CHAT_BOT_MODEL_VERSION, model.getChatBotModelVersion());
		
		
		
		return false;
	}

}
