package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.MailConstants.Email;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.FieldUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

@Log4j
public class InsertOutgoingMailLoggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        this.preprocessMailJson(mailJson);
        JSONObject clonedMailJSON =  (JSONObject) mailJson.clone();
        clonedMailJSON.remove(Email.MAIL_TYPE);
        V3OutgoingMailLogContext mailLogContext = FieldUtil.getAsBeanFromJson(clonedMailJSON, V3OutgoingMailLogContext.class);
        long loggerId = NewTransactionService.newTransactionWithReturn(() ->
                OutgoingMailAPI.insertV3(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER, mailLogContext));
        context.put(MailConstants.Params.LOGGER_ID, loggerId);
        context.put(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER, mailLogContext);
        LOGGER.info("OG_MAIL_LOG :: LOGGER_ID inserted :: "+loggerId);
        return false;
    }

    private void preprocessMailJson(JSONObject mailJson) {
        mailJson.put(Email.FROM, mailJson.get(Email.SENDER));
        Object message = mailJson.get(Email.MESSAGE);
        if(message!=null && message instanceof String) {
            if (mailJson.get(Email.MAIL_TYPE) != null && mailJson.get(Email.MAIL_TYPE).equals(Email.HTML)) {
                mailJson.put(Email.HTML_CONTENT, message);
                mailJson.put(Email.CONTENT_TYPE, Email.CONTENT_TYPE_TEXT_HTML);
            } else {
                mailJson.put(Email.TEXT_CONTENT, message);
                mailJson.put(Email.CONTENT_TYPE, Email.CONTENT_TYPE_TEXT_PLAIN);
            }
        }
        this.restoreEmailAddress(mailJson);
    }

    private void restoreEmailAddress(JSONObject mailJson) {
        OutgoingMailAPI.restoreEmailAddress(mailJson, Email.TO);
        OutgoingMailAPI.restoreEmailAddress(mailJson, Email.CC);
        OutgoingMailAPI.restoreEmailAddress(mailJson, Email.BCC);
    }

}
