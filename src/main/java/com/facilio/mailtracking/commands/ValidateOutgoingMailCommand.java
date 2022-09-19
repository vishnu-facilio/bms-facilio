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
        this.validateAddresses(mailJson);
        return false;
    }

    private void validateAddresses(JSONObject mailJson) throws Exception {
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
                LOGGER.error("OG_MAIL_ERROR :: Invalid addresses found in :: "+node.replaceAll("oringal", "").toLowerCase());
                LOGGER.error("OG_MAIL_ERROR :: email not sent");
                mailJson.put(MailConstants.Params.MAIL_STATUS, MailEnums.MailStatus.INVALID.name());
                break;
            }
        }
    }
}
