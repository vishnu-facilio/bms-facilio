package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.MailStatus;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.mailtracking.context.V3OutgoingRecipientContext;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.List;

@Log4j
public class UpdateRecipientStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3OutgoingMailLogContext mailLogContext = (V3OutgoingMailLogContext) context.get(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER);
        if(mailLogContext.getOriginalTo() == null) { // skipping update status since TO address is empty
            return false;
        }
        Long loggerId = FacilioUtil.parseLong(context.get(MailConstants.Params.LOGGER_ID));
        List<V3OutgoingRecipientContext> oldRecords = OutgoingMailAPI.getRecipients(loggerId);
        if(oldRecords==null || oldRecords.isEmpty()) {
            return false;
        }
        OutgoingMailAPI.updateRecipientStatus(oldRecords, MailStatus.SENT.getValue());
        return false;
    }
}
