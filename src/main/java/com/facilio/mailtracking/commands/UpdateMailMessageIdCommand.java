package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class UpdateMailMessageIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String messageId = (String) context.get(MailConstants.Params.MESSAGE_ID);
        if(StringUtils.isNotEmpty(messageId)) {
            V3OutgoingMailLogContext mailLogContext = (V3OutgoingMailLogContext) context.get(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER);
            if(mailLogContext!=null) {
                mailLogContext.setMessageId(messageId);
                OutgoingMailAPI.updateV3(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER, mailLogContext);
            }
        }
        return false;
    }
}
