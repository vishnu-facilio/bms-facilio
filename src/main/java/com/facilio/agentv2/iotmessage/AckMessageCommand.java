package com.facilio.agentv2.iotmessage;

import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class AckMessageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        return IotMessageApiV2.acknowdledgeMessage((long)context.get(AgentConstants.ID), (Status) context.get(AgentConstants.STATUS),(JSONObject) context.get(AgentConstants.DATA));
    }
}
