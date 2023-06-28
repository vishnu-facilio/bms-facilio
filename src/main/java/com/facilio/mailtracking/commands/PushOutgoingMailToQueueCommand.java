package com.facilio.mailtracking.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.MailEnums;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.facilio.ims.handler.OutgoingMailHandler;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

@Log4j
public class PushOutgoingMailToQueueCommand extends FacilioCommand implements PostTransactionCommand {

    private JSONObject mailJson;
    
    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3OutgoingMailLogContext mailLogContext = (V3OutgoingMailLogContext) context.get(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER);
        V3Util.throwRestException(
                mailLogContext.getMailStatus() == MailEnums.MailStatus.INVALID,
                ErrorCode.VALIDATION_ERROR,
                "OG_MAIL_ERROR :: MailStatus is flagged as INVALID for LOGGER_ID :: "+mailLogContext.getId() + ".. So, email not sent"); // its invalid, so throwing e

        mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        mailJson.put(MailConstants.Params.FILES, context.get(MailConstants.Params.FILES));
        mailJson.put(MailConstants.Params.LOGGER_ID, context.get(MailConstants.Params.LOGGER_ID));
        mailJson.put(MailConstants.Params.ID, context.get(MailConstants.Params.LOGGER_ID));
        this.removeContent(mailJson);

        return false;
    }

    private void removeContent(JSONObject mailJson) {
        mailJson.remove(MailConstants.Email.HTML_CONTENT);
        mailJson.remove(MailConstants.Email.TEXT_CONTENT);
        mailJson.remove(MailConstants.Email.MESSAGE);
    }


    @Override
    public boolean postExecute() throws Exception {
        if(mailJson == null) {
            return false;
        }
        long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
        String topicIdentifier = OutgoingMailAPI.getTopicIdentifier(mailJson, orgId);
        Messenger.getMessenger().sendMessage(new Message()
                .setKey(OutgoingMailHandler.KEY+"/"+topicIdentifier)
                .setOrgId(orgId)
                .setContent(mailJson));
        LOGGER.info("OG_MAIL_LOG :: Pushing outgoing mail to queue/wms for LOGGER_ID ::"+mailJson.get(MailConstants.Params.ID));

        return false;
    }
}
