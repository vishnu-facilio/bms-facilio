package com.facilio.mailtracking.commands;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Map;

public class SendMailWithoutTrackingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = this.constructMailJson(context);
        Map<String, String> files = (Map<String, String>) context.get(MailConstants.Params.FILES);
        EmailClient emailClient = EmailFactory.getEmailClient();
        String messageId = emailClient.sendEmailFromWMS(mailJson, files);
        context.put(MailConstants.Params.MESSAGE_ID, messageId);
        return false;
    }

    private JSONObject constructMailJson(Context context) {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);

        // remove mapperId
        mailJson.remove(MailConstants.Params.MAPPER_ID); // tracking id

        // removing extra tracking info from header
        JSONObject header = (JSONObject) mailJson.getOrDefault(MailConstants.Params.HEADER, new JSONObject());
        if(!header.isEmpty()) {
            header.remove(MailConstants.Params.MAPPER_ID);
            header.remove(MailConstants.Params.REGION);
            mailJson.put(MailConstants.Params.HEADER, header);
        }

        //updating content as message if available
        OutgoingMailAPI.restoreMailMessage(mailJson);
        return mailJson;
    }

}