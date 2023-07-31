package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateGlobalMapperIdCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        V3OutgoingMailLogContext mailLogContext = FieldUtil.getAsBeanFromJson(mailJson, V3OutgoingMailLogContext.class);
        context.put(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER, mailLogContext);

        Map<String, Object> row = new HashMap<>();
        row.put(MailConstants.Params.MAPPER_ID, mailLogContext.getMapperId());
        row.put(MailConstants.Params.MAIL_STATUS, mailLogContext.getMailStatus());

        OutgoingMailAPI.updateRecord(mailLogContext.getId(), MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER, row);
        return false;
    }
}