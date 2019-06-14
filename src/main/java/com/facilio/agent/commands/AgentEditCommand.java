package com.facilio.agent.commands;

import com.facilio.agent.AgentKeys;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class AgentEditCommand implements Command
{

    @Override
    public boolean execute(Context context) throws Exception {
        if(!context.containsKey(FacilioConstants.ContextNames.PAY_LOAD)){
            return false;
        }
        JSONObject payload = (JSONObject) context.get(FacilioConstants.ContextNames.PAY_LOAD);
        GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(AgentKeys.AGENT_TABLE)
                .fields(FieldFactory.getAgentDataFields())
//                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentDataModule()))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(ModuleFactory.getAgentDataModule()),payload.get(AgentKeys.ID).toString(), NumberOperators.EQUALS));
        int updatedRows= genericUpdateRecordBuilder.update(payload);
        return (updatedRows > 0);
    }
}
