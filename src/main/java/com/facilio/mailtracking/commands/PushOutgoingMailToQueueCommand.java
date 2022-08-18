package com.facilio.mailtracking.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
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

        long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
        Message message = new Message();
        message.setTopic("__sendmail__/org/"+orgId);
        message.setOrgId(orgId);
        message.setContent(mailJson);
        SessionManager.getInstance().sendMessage(message);
        LOGGER.info("Pushing outgoing mail to queue/wms");

        return false;
    }
}
