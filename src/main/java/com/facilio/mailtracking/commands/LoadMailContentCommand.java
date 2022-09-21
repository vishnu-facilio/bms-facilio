package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.MailConstants.Email;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class LoadMailContentCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        if(mailJson.get(Email.MESSAGE) !=null || mailJson.get(Email.HTML_CONTENT)!=null || mailJson.get(Email.TEXT_CONTENT)!=null) {
            return false;
        }
        if(context.get(MailConstants.Params.LOGGER_ID) == null) {
            return false;
        }
        Long loggerId = FacilioUtil.parseLong(context.get(MailConstants.Params.LOGGER_ID));
        V3OutgoingMailLogContext loggerRecord = OutgoingMailAPI.getLoggerRecord(loggerId);
        if(loggerRecord != null) {
            if (loggerRecord.getHtmlContent() != null) {
                mailJson.put(Email.HTML_CONTENT, loggerRecord.getHtmlContent());
            }
            if (loggerRecord.getTextContent() != null) {
                mailJson.put(Email.TEXT_CONTENT, loggerRecord.getTextContent());
            }
        }

        return false;
    }
}

