package com.facilio.mailtracking.commands;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class MaskConfidentialUrlCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3OutgoingMailLogContext> mailContexts = Constants.getRecordList((FacilioContext) context);
        if(!CollectionUtils.isEmpty(mailContexts)) {
            for (V3OutgoingMailLogContext mail : mailContexts) {
                String url = mail.getMaskUrl();
                if(url!=null) {
                    if (mail.getHtmlContent() != null) {
                        mail.setHtmlContent(this.getMaskedMessage(mail.getHtmlContent(), url));
                    } else if (mail.getTextContent() != null) {
                        mail.setTextContent(this.getMaskedMessage(mail.getTextContent(), url));
                    }
                }
            }
        }
        return false;
    }

    private String getMaskedMessage(String message, String url) {
        String maskUrl = "https://masked-mail.com/hiddenfor/security/reason/XkjdsnfosieIUbasldaweiwN";
        maskUrl += url.substring(maskUrl.length());
        return message.replaceAll(url, maskUrl);
    }
}
