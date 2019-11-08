package com.facilio.bmsconsole.util;

import com.twilio.Twilio;

public class TwilioUtil {
	
	protected static final String ACCOUNTS_ID = "AC49fd18185d9f484739aa73b648ba2090"; // Your Account SID from www.twilio.com/user/account
	protected static final String AUTH_TOKEN = "3683aa0033af81877501961dc886a52b"; // Your Auth Token from www.twilio.com/user/account
	
	protected static final String FROM_NUMBER = "+16106248741";
	static {
		Twilio.init(ACCOUNTS_ID, AUTH_TOKEN);
	}
	protected final static String FROM = "facilio";

}
