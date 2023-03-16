package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.services.email.EmailFactory;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Map;

public class HandleUserDelegationAndIsActiveCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        Map<String, String> files = (Map<String, String>) mailJson.remove(MailConstants.Params.FILES);
        boolean handleUserDelegation = (boolean) mailJson.remove(MailConstants.Params.HANDLE_DELEGATION);
        boolean isActive = (boolean) mailJson.remove(MailConstants.Params.IS_ACTIVE);
        EmailFactory.getEmailClient().prepareAndPushOutgoingMail(mailJson, files, handleUserDelegation, isActive);
        return false;
    }
}
