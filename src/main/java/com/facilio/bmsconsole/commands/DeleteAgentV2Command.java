package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteAgentV2Command extends FacilioCommand {


    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioModule agentModule = ModuleFactory.getNewAgentModule();
        List<Long> ids = (List<Long>) context.get(AgentConstants.RECORD_IDS);
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(agentModule.getTableName())
                .fields(FieldFactory.getNewAgentFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, agentModule));
        Map<String, Object> toUpdateMap = new HashMap<>();
        toUpdateMap.put(AgentConstants.DELETED_TIME, System.currentTimeMillis());
        int rowsAffected = builder.update(toUpdateMap);
        return rowsAffected <= 0;
    }
}
