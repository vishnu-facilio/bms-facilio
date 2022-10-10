package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.MailEnums.MailStatus;
import com.facilio.mailtracking.context.MailEnums.RecipientStatus;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.mailtracking.context.V3OutgoingRecipientContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdateRecipientStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3OutgoingMailLogContext mailLogContext = (V3OutgoingMailLogContext) context.get(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER);
        if(mailLogContext == null) {
            return false;
        }
        MailStatus status = (MailStatus) context.get(MailConstants.Params.MAIL_STATUS);
        if(status!=null) {
            mailLogContext.setMailStatus(status);
            if(status == MailStatus.FAILED) {
                return false;
            }
        }
        if(mailLogContext.getTo() == null) { // skipping update status since TO address is empty
            return false;
        }
        Long loggerId = FacilioUtil.parseLong(context.get(MailConstants.Params.LOGGER_ID));
        List<V3OutgoingRecipientContext> oldRecords = OutgoingMailAPI.getRecipients(loggerId);
        if(oldRecords==null || oldRecords.isEmpty()) {
            return false;
        }
        OutgoingMailAPI.updateRecipientStatus(oldRecords, RecipientStatus.SENT.getValue());
        return false;
    }
}
