package com.facilio.agentv2.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointsAPI;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class DeletePointCommand extends AgentV2Command {

    private static final Logger LOGGER = LogManager.getLogger(DeletePointCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(containsCheck(AgentConstants.POINT_IDS,context)){
           List<Long> pointIds = (List<Long>) context.get(AgentConstants.POINT_IDS);
            PointsAPI.executeDeletePoints(pointIds);
        }else {
            throw new Exception(" pointIds  missing from context");
        }
        return false;
    }
}
