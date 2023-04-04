package com.facilio.storm.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class StormInstructionPublishCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Integer type = (Integer) context.get("type");
        JSONObject data = (JSONObject) context.get("data");
        MessageQueue mq = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());

        JSONObject input = new JSONObject();
        input.put("orgId", AccountUtil.getCurrentOrg().getId());
        input.put("userId", AccountUtil.getCurrentUser().getId());
        input.put("type", type);
        input.put("data", data);

        mq.put(getTopicName(), new FacilioRecord("storm-instruction", input));

        return false;
    }

    private String getTopicName() {
        return FacilioProperties.getEnvironment() + "-instruction-queue";
    }
}
