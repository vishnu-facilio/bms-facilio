package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.sqlitebuilder.SqliteBridge;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;

public class AgentMigrationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception{
        long agentId = (long)context.get(AgentConstants.AGENT_ID);
        com.facilio.agentv2.FacilioAgent newAgent = SqliteBridge.migrateAgentToV2(agentId);
        List<Pair<Long, ControllerContext>> controllerContextPairs = SqliteBridge.migrateControllerToV2(agentId, newAgent);

        for (Pair<Long, ControllerContext> pair : controllerContextPairs) {
            SqliteBridge.migratePoints(pair.getRight(), pair.getLeft());
        }
        return true;
    }

}
