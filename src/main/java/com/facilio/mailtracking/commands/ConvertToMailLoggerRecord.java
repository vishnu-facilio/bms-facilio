package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

public class ConvertToMailLoggerRecord extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        V3OutgoingMailLogContext mailLogContext = FieldUtil.getAsBeanFromJson(mailJson, V3OutgoingMailLogContext.class);
        String messageId = (String) context.get(MailConstants.Params.MESSAGE_ID);
        if (StringUtils.isNotEmpty(messageId)) {
            mailLogContext.setMessageId(messageId);
        }
        context.put(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER, mailLogContext);
        return false;
    }

}
