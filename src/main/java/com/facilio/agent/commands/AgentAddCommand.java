package com.facilio.agent.commands;

import com.facilio.agent.AgentKeys;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class AgentAddCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(AgentAddCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long agentId;
        if (!context.containsKey(FacilioConstants.ContextNames.PAY_LOAD)) {
            return false;
        }
        JSONObject payload = (JSONObject) context.get(FacilioConstants.ContextNames.PAY_LOAD);
        GenericInsertRecordBuilder genericInsertRecordBuilder = new GenericInsertRecordBuilder()
                .table(AgentKeys.AGENT_TABLE)
                .fields(FieldFactory.getAgentDataFields());
        agentId = genericInsertRecordBuilder.insert(payload);

        context.put(AgentKeys.AGENT_ID,agentId); // get agentId using this key

        if( agentId > -1L ){
            return true;
        }
        LOGGER.info("Exception while creating agent");
        return false;
    }

}
