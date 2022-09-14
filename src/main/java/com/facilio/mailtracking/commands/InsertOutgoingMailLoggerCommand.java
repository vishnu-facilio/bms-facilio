package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.FieldUtil;
import com.facilio.services.email.EmailClient;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

@Log4j
public class InsertOutgoingMailLoggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        this.preprocessMailJson(mailJson);
        V3OutgoingMailLogContext mailLogContext = FieldUtil.getAsBeanFromJson(mailJson, V3OutgoingMailLogContext.class);
        long loggerId = NewTransactionService.newTransactionWithReturn(() ->
                OutgoingMailAPI.insertV3(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER, mailLogContext));
        context.put(MailConstants.Params.LOGGER_ID, loggerId);
        context.put(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER, mailLogContext);
        LOGGER.info("OG_MAIL_LOG :: LOGGER_ID inserted :: "+loggerId);
        return false;
    }

    private void preprocessMailJson(JSONObject mailJson) {
        mailJson.put("from", mailJson.get(EmailClient.SENDER));
        if(mailJson.get("message")!=null) {
            if (mailJson.get(EmailClient.MAIL_TYPE) != null && mailJson.get(EmailClient.MAIL_TYPE).equals(EmailClient.HTML)) {
                mailJson.put("htmlContent", mailJson.remove("message"));
                mailJson.put("contentType", EmailClient.CONTENT_TYPE_TEXT_HTML);
            } else {
                mailJson.put("textContent", mailJson.remove("message"));
                mailJson.put("contentType", EmailClient.CONTENT_TYPE_TEXT_PLAIN);
            }
        }
        this.restoreEmailAddress(mailJson);
    }

    private void restoreEmailAddress(JSONObject mailJson) {
        OutgoingMailAPI.restoreEmailAddress(mailJson, EmailClient.TO);
        OutgoingMailAPI.restoreEmailAddress(mailJson, EmailClient.CC);
        OutgoingMailAPI.restoreEmailAddress(mailJson, EmailClient.BCC);
    }
}
