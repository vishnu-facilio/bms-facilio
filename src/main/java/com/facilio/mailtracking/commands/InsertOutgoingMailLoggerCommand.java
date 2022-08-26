package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

@Log4j
public class InsertOutgoingMailLoggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        V3OutgoingMailLogContext mailLogContext = OutgoingMailAPI.convertToMailLogContext(mailJson);
        long loggerId = NewTransactionService.newTransactionWithReturn(() -> OutgoingMailAPI.insertV3(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER, mailLogContext));
        context.put(MailConstants.Params.LOGGER_ID, loggerId);
        LOGGER.info("OG_MAIL_LOG :: loggerId inserted :: "+loggerId);
        return false;
    }
}
