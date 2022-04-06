package com.facilio.agentv2.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.CloudAgentUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class FetchMessageSourcesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List sources = CloudAgentUtil.fetchMessageSources();
        if (sources!=null && !sources.isEmpty()){
            context.put(AgentConstants.MESSAGE_SOURCES,sources);
        }
        return false;
    }
}
