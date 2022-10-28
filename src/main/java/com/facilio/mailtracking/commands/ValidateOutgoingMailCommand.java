package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.context.MailEnums;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Log4j
public class ValidateOutgoingMailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        mailJson.put(MailConstants.Params.MAIL_STATUS, MailEnums.MailStatus.TRIGGERED.name());
        this.validateAddresses(mailJson);
        this.validateContent(mailJson);
        return false;
    }

    private void validateContent(JSONObject mailJson) {
        Object message = mailJson.get(MailConstants.Email.MESSAGE);
        if(message!=null && !(message instanceof String)) {
            LOGGER.error("OG_MAIL_ERROR :: Given message content is not a string :: "+message);
            mailJson.put(MailConstants.Params.MAIL_STATUS, MailEnums.MailStatus.INVALID.name());
        }
    }

    private void validateAddresses(JSONObject mailJson) {
        String nodes[] = new String[] {
                MailConstants.Email.ORIGINAL_TO,
                MailConstants.Email.ORIGINAL_CC,
                MailConstants.Email.ORIGINAL_BCC
        };
        for(String node : nodes) {
            Object addresses = mailJson.get(node);
            if (addresses == null) {
                continue;
            }
            try {
                InternetAddress.parse((String) addresses);
            } catch (AddressException e) {
                LOGGER.error("OG_MAIL_ERROR :: Email not sent. Invalid addresses found in :: "+
                        node.replaceAll("original", "").toLowerCase() + " :: "+addresses);
                mailJson.put(MailConstants.Params.MAIL_STATUS, MailEnums.MailStatus.INVALID.name());
                break;
            }
        }
    }
}
