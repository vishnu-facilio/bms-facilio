package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class UpdatePostMailLoggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3OutgoingMailLogContext mailLogContext = (V3OutgoingMailLogContext) context.get(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER);
        if(mailLogContext!=null) {
            String messageId = (String) context.get(MailConstants.Params.MESSAGE_ID);
            Map<String, Object> row = new HashMap<>();
            row.put(MailConstants.Params.MAIL_STATUS, mailLogContext.getMailStatus());
            if (StringUtils.isNotEmpty(messageId)) {
                row.put(MailConstants.Params.MESSAGE_ID, messageId);
            }
            OutgoingMailAPI.updateRecord(mailLogContext.getId(), MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER, row);
        }
        return false;
    }
}
