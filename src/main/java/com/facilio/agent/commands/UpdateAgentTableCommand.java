package com.facilio.agent.commands;

import com.facilio.agent.AgentKeys;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class UpdateAgentTableCommand extends FacilioCommand
{
    private static final Logger LOGGER = LogManager.getLogger(UpdateAgentTableCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder().table(AgentKeys.AGENT_TABLE).fields(FieldFactory.getAgentDataFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(ModuleFactory.getAgentDataModule()),context.get(AgentKeys.ID).toString() , StringOperators.IS))
//                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentDataModule()));
                ;
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME, System.currentTimeMillis());
        toUpdate.put(AgentKeys.CONNECTION_STATUS, Boolean.TRUE);
        toUpdate.put(AgentKeys.STATE, 1);
        int rowsUpdated = genericUpdateRecordBuilder.update(toUpdate);
        if( rowsUpdated < 1 ){
            LOGGER.info(" Exception Occured while updating AgentTable LastModifiedTime when Publish_Tupe not agent");
            return false;
        }
        return true;
    }
}
