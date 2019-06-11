package com.facilio.agent.commands;

import com.facilio.agent.AgentKeys;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class AgentCreate implements Command
{
    private static final Logger LOGGER = LogManager.getLogger(AgentCreate.class.getName());

    private long agentId;
    @Override
    public boolean execute(Context context) throws Exception {
        if( !context.containsKey(FacilioConstants.ContextNames.PAY_LOAD )){
            return false;
        }
        JSONObject payload = (JSONObject) context.get(FacilioConstants.ContextNames.PAY_LOAD);
        GenericInsertRecordBuilder genericInsertRecordBuilder = new GenericInsertRecordBuilder()
                .table(AgentKeys.AGENT_TABLE)
                .fields(FieldFactory.getAgentDataFields());
        agentId = genericInsertRecordBuilder.insert(payload);

        if( agentId > -1L ){
            return true;
        }
        LOGGER.info("Exception while creating agent");
        return false;
    }

}
