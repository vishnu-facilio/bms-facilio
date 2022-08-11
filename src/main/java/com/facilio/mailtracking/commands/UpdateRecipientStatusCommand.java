package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.MailStatus;
import com.facilio.mailtracking.context.V3OutgoingRecipientContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.List;

@Log4j
public class UpdateRecipientStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long loggerId = (Long) context.get(MailConstants.Params.LOGGER_ID);
        List<V3OutgoingRecipientContext> oldRecords = OutgoingMailAPI.getRecipients(loggerId);
        OutgoingMailAPI.updateRecipientStatus(oldRecords, MailStatus.SENT.getValue());
        return false;
    }
}
