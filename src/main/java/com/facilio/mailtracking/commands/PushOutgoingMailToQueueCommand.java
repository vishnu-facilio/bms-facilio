package com.facilio.mailtracking.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.Message;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

@Log4j
public class PushOutgoingMailToQueueCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        mailJson.put(MailConstants.Params.FILES, context.get(MailConstants.Params.FILES));
        mailJson.put(MailConstants.Params.LOGGER_ID, context.get(MailConstants.Params.LOGGER_ID));
        mailJson.put(MailConstants.Params.ID, context.get(MailConstants.Params.LOGGER_ID));

        long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();

        String topicIdentifier = this.getTopicIdentifier(mailJson, orgId);
        SessionManager.getInstance().sendMessage(new Message()
                .setTopic(Topics.Mail.outgoingMail+"/"+topicIdentifier)
                .setOrgId(orgId)
                .setContent(mailJson));
        LOGGER.info("OG_MAIL_LOG :: Pushing outgoing mail to queue/wms");
        this.resetMailJson(mailJson);
        return false;
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

    private void resetMailJson(JSONObject mailJson) {
        mailJson.remove(MailConstants.Params.ORIGINAL_TO);
        mailJson.remove(MailConstants.Params.ORIGINAL_CC);
        mailJson.remove(MailConstants.Params.ORIGINAL_BCC);
        mailJson.remove(MailConstants.Params.MASK_URL);
    }

}
