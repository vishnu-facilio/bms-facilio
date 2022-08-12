package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.MailStatus;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.mailtracking.context.V3OutgoingRecipientContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class InsertOutgoingRecipientsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);

        V3OutgoingMailLogContext mailLogContext = OutgoingMailAPI.convertToMailLogContext(null, mailJson);
        context.put(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER, mailLogContext);
        context.put(MailConstants.Params.RECORD_MODULE_ID, mailLogContext.getRecordsModuleId());

        Map<String, String> emailMeta = new HashMap<>();
        if(mailLogContext.getOriginalTo()!=null) {
            emailMeta.putAll(mailLogContext.getOriginalTo());
        }
        if(mailLogContext.getOriginalCc()!=null) {
            emailMeta.putAll(mailLogContext.getOriginalCc());
        }
        if(mailLogContext.getOriginalBcc()!=null) {
            emailMeta.putAll(mailLogContext.getOriginalBcc());
        }

        if(emailMeta.isEmpty()) {
            LOGGER.info("No recipients found in the given outgoing mail record. So stopping here..");
            return true;
        }
        List<V3OutgoingRecipientContext> records = new ArrayList<>();
        for(Map.Entry<String, String> en : emailMeta.entrySet()) {
            V3OutgoingRecipientContext record = new V3OutgoingRecipientContext();
            record.setLogger(mailLogContext);
            record.setName(en.getValue());
            record.setRecipient(en.getKey());
            record.setStatus(MailStatus.IN_PROGRESS.getValue());
            records.add(record);
        }
//        OutgoingMailAPI.insertOutgoingRecipients(records);
        OutgoingMailAPI.insertV3(MailConstants.ModuleNames.OUTGOING_RECIPIENT_LOGGER, records);
        return false;
    }
}