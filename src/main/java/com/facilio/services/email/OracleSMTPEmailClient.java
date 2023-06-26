package com.facilio.services.email;

import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
class OracleSMTPEmailClient extends SMTPEmailClient {

    private static final OracleSMTPEmailClient INSTANCE = new OracleSMTPEmailClient();

    public static EmailClient getClient(){
        return INSTANCE;
    }
    @Override
    public String sendEmailImpl(JSONObject mailJson) {
        String sender = (String) mailJson.get("sender");
        return sendMail(sender, mailJson);
    }
}
