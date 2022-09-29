package com.facilio.mailtracking.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.MailConstants.Email;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.MailEnums.MailStatus;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
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
        V3Util.throwRestException(
                mailLogContext.getMailStatus() == MailStatus.INVALID,
                ErrorCode.VALIDATION_ERROR,
                "OG_MAIL_ERROR :: MailStatus is flagged as INVALID for LOGGER_ID :: "+loggerId); // its invalid, so throwing e
        return false;
    }

    private void preprocessMailJson(JSONObject mailJson) {
        mailJson.put(Email.FROM, mailJson.get(Email.SENDER));
        if(mailJson.get(Email.MESSAGE)!=null) {
            if (mailJson.get(Email.MAIL_TYPE) != null && mailJson.get(Email.MAIL_TYPE).equals(Email.HTML)) {
                mailJson.put(Email.HTML_CONTENT, mailJson.get(Email.MESSAGE));
                mailJson.put(Email.CONTENT_TYPE, Email.CONTENT_TYPE_TEXT_HTML);
            } else {
                mailJson.put(Email.TEXT_CONTENT, mailJson.get(Email.MESSAGE));
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
