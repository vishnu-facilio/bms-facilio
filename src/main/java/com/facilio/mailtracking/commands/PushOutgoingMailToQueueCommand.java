package com.facilio.mailtracking.commands;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class PushOutgoingMailToQueueCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject mailJson = (JSONObject) context.get(MailConstants.Params.MAIL_JSON);
        mailJson.put(MailConstants.Params.FILES, context.get(MailConstants.Params.FILES));
        mailJson.put(MailConstants.Params.MAPPER_ID, context.get(MailConstants.Params.MAPPER_ID));
        mailJson.put(MailConstants.Params.LOGGER_ID, context.get(MailConstants.Params.LOGGER_ID));

        // updating header with extra tracking info
        JSONObject header = (JSONObject) mailJson.getOrDefault(MailConstants.Params.HEADER, new JSONObject());
        header.put(MailConstants.Params.MAPPER_ID, context.get(MailConstants.Params.MAPPER_ID)+"");
        header.put(MailConstants.Params.REGION, FacilioProperties.getRegion());
        mailJson.put(MailConstants.Params.HEADER, header);

        long orgId = (long) context.get(MailConstants.Params.ORGID);
        Message message = new Message();
        message.setTopic("__sendmail__/org/"+orgId);
        message.setOrgId(orgId);
        message.setContent(mailJson);
        SessionManager.getInstance().sendMessage(message);

        return false;
    }
}
