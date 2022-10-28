package com.facilio.mailtracking.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.context.MailEnums;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.Message;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

@Log4j
public class PushOutgoingMailToQueueCommand extends FacilioCommand {

    private String message;
    
    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3OutgoingMailLogContext mailLogContext = (V3OutgoingMailLogContext) context.get(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER);
        V3Util.throwRestException(
                mailLogContext.getMailStatus() == MailEnums.MailStatus.INVALID,
                ErrorCode.VALIDATION_ERROR,
                "OG_MAIL_ERROR :: MailStatus is flagged as INVALID for LOGGER_ID :: "+mailLogContext.getId() + ".. So, email not sent"); // its invalid, so throwing e

        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        mailJson.put(MailConstants.Params.FILES, context.get(MailConstants.Params.FILES));
        mailJson.put(MailConstants.Params.LOGGER_ID, context.get(MailConstants.Params.LOGGER_ID));
        mailJson.put(MailConstants.Params.ID, context.get(MailConstants.Params.LOGGER_ID));
        this.removeContent(mailJson);

        long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();

        String topicIdentifier = this.getTopicIdentifier(mailJson, orgId);
        SessionManager.getInstance().sendMessage(new Message()
                .setTopic(Topics.Mail.outgoingMail+"/"+topicIdentifier)
                .setOrgId(orgId)
                .setContent(mailJson));
        LOGGER.info("OG_MAIL_LOG :: Pushing outgoing mail to queue/wms for LOGGER_ID ::"+mailJson.get(MailConstants.Params.ID));
        mailJson.put(MailConstants.Email.MESSAGE, this.message);
        return false;
    }

    private void removeContent(JSONObject mailJson) {
        mailJson.remove(MailConstants.Email.HTML_CONTENT);
        mailJson.remove(MailConstants.Email.TEXT_CONTENT);
        this.message = (String) mailJson.remove(MailConstants.Email.MESSAGE);
    }

    private String getTopicIdentifier(JSONObject mailJson, long orgId) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgId);
        if(mailJson.containsKey(MailConstants.Params.SOURCE_TYPE)) {
            sb.append("/");
            sb.append(mailJson.get(MailConstants.Params.SOURCE_TYPE));
        }
        if(mailJson.containsKey(MailConstants.Params.RECORDS_MODULE_ID)) {
            sb.append("/");
            sb.append(mailJson.get(MailConstants.Params.RECORDS_MODULE_ID));
        }
        if(mailJson.containsKey(MailConstants.Params.RECORD_ID)) {
            sb.append("/");
            sb.append(mailJson.get(MailConstants.Params.RECORD_ID));
        }
        return sb.toString();
    }


}
