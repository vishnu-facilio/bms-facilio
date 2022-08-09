package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import org.apache.commons.chain.Context;

public class UpdateMailMessageIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String messageId = (String) context.get(MailConstants.Params.MESSAGE_ID);

        V3OutgoingMailLogContext mailLogContext = (V3OutgoingMailLogContext) context.get(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER);
        mailLogContext.setMessageId(messageId);
        OutgoingMailAPI.updateV3(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER, mailLogContext);

        return false;
    }
}
