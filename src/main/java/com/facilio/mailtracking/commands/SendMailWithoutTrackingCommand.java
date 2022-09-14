package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.MailStatus;
import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Map;

@Log4j
public class SendMailWithoutTrackingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        MailStatus status = (MailStatus) context.get(MailConstants.Params.STATUS);
        if(status!=null && status == MailStatus.SENT) {
            LOGGER.info("Skipping SendMailWithoutTrackingCommand since its already sent");
            return false;
        }
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
