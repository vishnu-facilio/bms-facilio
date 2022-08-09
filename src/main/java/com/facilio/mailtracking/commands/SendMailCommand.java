package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Map;

public class SendMailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        Map<String, String> files = (Map<String, String>) context.get(MailConstants.Params.FILES);
        EmailClient emailClient = EmailFactory.getEmailClient();
        String messageId = emailClient.sendEmailFromWMS(mailJson, files);
        context.put(MailConstants.Params.MESSAGE_ID, messageId);
        return false;
    }
}
