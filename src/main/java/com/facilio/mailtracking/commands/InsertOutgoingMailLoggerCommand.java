package com.facilio.mailtracking.commands;

import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.services.email.EmailClient;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j
public class InsertOutgoingMailLoggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        preprocessMailJson(mailJson);
        V3OutgoingMailLogContext mailLogContext = OutgoingMailAPI.convertToMailLogContext(mailJson);
        long loggerId = NewTransactionService.newTransactionWithReturn(() -> OutgoingMailAPI.insertV3(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER, mailLogContext));
        context.put(MailConstants.Params.LOGGER_ID, loggerId);
        LOGGER.info("OG_MAIL_LOG :: loggerId inserted :: "+loggerId);
        return false;
    }

    private void preprocessMailJson(JSONObject mailJson) {
        mailJson.put("from", mailJson.get(EmailClient.SENDER));
        if(mailJson.get("message")!=null) {
            if (mailJson.get(EmailClient.MAIL_TYPE) != null && mailJson.get(EmailClient.MAIL_TYPE).equals(EmailClient.HTML)) {
                mailJson.put("htmlContent", mailJson.get("message"));
                mailJson.put("contentType", EmailClient.CONTENT_TYPE_TEXT_HTML);
            } else {
                mailJson.put("textContent", mailJson.get("message"));
                mailJson.put("contentType", EmailClient.CONTENT_TYPE_TEXT_PLAIN);
            }
        }
        revertEmailAddress(mailJson, EmailClient.TO);
        revertEmailAddress(mailJson, EmailClient.CC);
        revertEmailAddress(mailJson, EmailClient.BCC);
    }

    private void revertEmailAddress(JSONObject mailJson, String key) {
        String originalKey = "original"+ StringUtils.capitalize(key);
        if(mailJson.containsKey(originalKey)) {
            JSONObject emailMetaJson = (JSONObject) mailJson.get(originalKey);
            Set<Map.Entry> keys = emailMetaJson.entrySet();
            List<String> emailList = new ArrayList<>();
            for (Map.Entry en : keys) {
                emailList.add(MailMessageUtil.getOriginalEmailAddress.apply(en));
            }
            mailJson.put(key, emailList.stream().collect(Collectors.joining(",")));
        }
    }
}
