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

public class StormHistoricalProxyCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
//        int jobType = (int) context.get(FacilioConstants.ContextNames.RULE_JOB_TYPE);
        JSONObject loggerInfo = (JSONObject) context.get(FacilioConstants.ContextNames.HISTORICAL_RULE_LOGGER_PROPS);

        MessageQueue mq = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());

        JSONObject input = new JSONObject();
        input.put("orgId", AccountUtil.getCurrentOrg().getId());
        input.put("ruleId", loggerInfo.get("rule"));
        input.put("assetIds", loggerInfo.get("resource"));
        input.put("startTime", range.getStartTime());
        input.put("endTime", range.getEndTime());

        mq.put(getTopicName(), new FacilioRecord("rule-historical", input));

        return false;
    }

    private String getTopicName() {
        return FacilioProperties.getEnvironment() + "-storm-historical-queue";
    }
}
