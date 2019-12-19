package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.twilio.Twilio;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class TwilioUtil {
	private static final Logger LOGGER = LogManager.getLogger(TwilioUtil.class.getName());
	
	protected static final String ACCOUNTS_ID = "AC49fd18185d9f484739aa73b648ba2090"; // Your Account SID from www.twilio.com/user/account
	protected static final String AUTH_TOKEN = "3683aa0033af81877501961dc886a52b"; // Your Auth Token from www.twilio.com/user/account
	
	protected static final String FROM_NUMBER = "+16106248741";
	static {
		Twilio.init(ACCOUNTS_ID, AUTH_TOKEN);
	}
	protected final static String FROM = "facilio";

	protected static void log(CommonAPI.NotificationType type, String to, String msg, String msgId) {
		if (AccountUtil.getCurrentOrg() != null) {
			JSONObject info = new JSONObject();
			info.put("msg", msg);
			info.put("msgId", msgId);
			try {
				CommonAPI.addNotificationLogger(CommonAPI.NotificationType.SMS, to, null, null, info);
			} catch (Exception e) {
				LOGGER.error("Error occurred while logging " + type + " to " + to + " with props : "+info.toJSONString(), e);
			}
		}
	}
}
