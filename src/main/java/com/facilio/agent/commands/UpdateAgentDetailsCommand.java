package com.facilio.agent.commands;

import com.facilio.agent.AgentKeys;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.modules.FieldFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class UpdateAgentDetailsCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(UpdateAgentDetailsCommand.class.getName());

    @Override
    public boolean execute(Context context) throws Exception {
    if(context.containsKey(FacilioConstants.ContextNames.CRITERIA) && context.containsKey(FacilioConstants.ContextNames.TO_UPDATE_MAP)) {
        GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(AgentKeys.AGENT_TABLE)
                .fields(FieldFactory.getAgentDataFields()).andCondition((Condition) context.get(FacilioConstants.ContextNames.CRITERIA));

        int updatedRows = genericUpdateRecordBuilder.update((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_MAP));
        if (updatedRows > 0) {
            return true;
        }
    }
        LOGGER.info("AgentDetails updation failed from"+UpdateAgentDetailsCommand.class.getName());
        return false;
    }
}
