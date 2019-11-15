package com.facilio.bmsconsole.commands;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

public class GetPermaLinkTokenCommand extends FacilioCommand{
	
	private static final Logger LOGGER = Logger.getLogger(GetTotalConsumptionBySiteCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		LOGGER.log(Level.SEVERE, "startTime -- "+System.currentTimeMillis());
			
		String url = (String) context.get(FacilioConstants.ContextNames.PERMALINK_FOR_URL);
		String email = (String) context.get(FacilioConstants.ContextNames.USER_EMAIL);
		User user = AccountUtil.getUserBean().getUser(AccountUtil.getCurrentOrg().getId(), email);

		JSONObject sessionObject = (JSONObject) context.get(FacilioConstants.ContextNames.SESSION);
		String token;
		if (sessionObject != null) {
			if (StringUtils.isNotEmpty(url)) {
				sessionObject.put("url", url);
			}
			token = AccountUtil.getUserBean().generatePermalink(user, sessionObject);
		}
		else {
			token = AccountUtil.getUserBean().generatePermalinkForURL(url, user);
		}
		   
		context.put(FacilioConstants.ContextNames.PERMALINK_TOKEN_FOR_URL, token);
		
		
		return false;
	}
	

}
