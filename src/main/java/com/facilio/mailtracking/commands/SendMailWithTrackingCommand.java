package com.facilio.mailtracking.commands;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.MailEnums;
import com.facilio.mailtracking.context.MailEnums.RecipientStatus;
import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Map;

@Log4j
public class SendMailWithTrackingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = this.constructMailJson(context);
        Map<String, String> files = (Map<String, String>) context.get(MailConstants.Params.FILES);
        EmailClient emailClient = EmailFactory.getEmailClient();
        String logMeta = OutgoingMailAPI.getLogMeta(mailJson);
        try {
            String messageId = emailClient.sendEmailFromWMS(mailJson, files);
            context.put(MailConstants.Params.MESSAGE_ID, messageId);
            context.put(MailConstants.Params.MAIL_STATUS, MailEnums.MailStatus.SENT);
            context.put(MailConstants.Params.RECIPIENT_STATUS, RecipientStatus.SENT);
            LOGGER.info("OG_MAIL_LOG :: email sent successfully"+logMeta);
        } catch (Exception e) {
            context.put(MailConstants.Params.MAIL_STATUS, MailEnums.MailStatus.FAILED);
            LOGGER.error("OG_MAIL_ERROR :: SendMailFailed "+logMeta+" with exception :: ", e);
        }
        return false;
    }

    private JSONObject constructMailJson(Context context) {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);

        // updating header with extra tracking info
        JSONObject header = (JSONObject) mailJson.getOrDefault(MailConstants.Params.HEADER, new JSONObject());
        header.put(MailConstants.Params.MAPPER_ID, context.get(MailConstants.Params.MAPPER_ID)+"");
        header.put(MailConstants.Params.REGION, FacilioProperties.getRegion());
        mailJson.put(MailConstants.Params.HEADER, header);

        //updating content as message if available
        OutgoingMailAPI.restoreMailMessage(mailJson);
        return mailJson;
    }


}
