package com.facilio.cb.command;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cards.util.ChatBotUtil;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotModel.App_Type;
import com.facilio.cb.util.ChatBotConstants;

public class GetCurrentActiveModel extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		int userType = AccountUtil.getCurrentAccount().getUser().getUserType();
		
		App_Type appType = App_Type.getAllAppTypesUsers().get(userType);
		
		ChatBotModel model = ChatBotUtil.getActiveModel(appType);
		
		context.put(ChatBotConstants.CHAT_BOT_MODEL, model);
		
		return false;
	}

}
