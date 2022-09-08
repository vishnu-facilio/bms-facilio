package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class MaskConfidentialUrlCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        if(mailJson.containsKey("maskUrl")) {
            String url = (String) mailJson.get("maskUrl");
            V3OutgoingMailLogContext mailLogContext = (V3OutgoingMailLogContext) context.get(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER);
            if(mailLogContext.getHtmlContent()!=null) {
                mailLogContext.setHtmlContent(this.getMaskedMessage(mailLogContext.getHtmlContent(), url));
            } else if(mailLogContext.getTextContent()!=null) {
                mailLogContext.setTextContent(this.getMaskedMessage(mailLogContext.getTextContent(), url));
            }
            OutgoingMailAPI.updateV3(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER, mailLogContext);
        }
        return false;
    }

    private String getMaskedMessage(String message, String url) {
        String maskUrl = "https://masked-mail.com/hiddenfor/security/reason/";
        maskUrl += url.substring(maskUrl.length());
        return message.replaceAll(url, maskUrl);
    }
}
