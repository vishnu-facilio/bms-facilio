package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class InsertOutgoingMailLoggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        long mapperId = (long) context.get(MailConstants.Params.MAPPER_ID);

        V3OutgoingMailLogContext mailLogContext = OutgoingMailAPI.convertToMailLogContext(mapperId, mailJson);

        long loggerId = OutgoingMailAPI.insertV3(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER, mailLogContext);
        context.put(MailConstants.Params.LOGGER_ID, loggerId);

        return false;
    }
}
