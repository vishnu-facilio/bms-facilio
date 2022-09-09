package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class UpdateGlobalMapperIdCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        V3OutgoingMailLogContext mailLogContext = OutgoingMailAPI.convertToMailLogContext(mailJson);
        OutgoingMailAPI.updateV3(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER, mailLogContext);
        context.put(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER, mailLogContext);
        context.put(MailConstants.Params.RECORD_MODULE_ID, mailLogContext.getRecordsModuleId());

        return false;
    }
}
