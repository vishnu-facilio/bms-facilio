package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class CommissionedPointsMLMigration extends AgentV2Command {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long agentId = (Long) context.get(AgentConstants.AGENT_ID);

        GetPointRequest getPointRequest = new GetPointRequest()
                .withAgentId(agentId)
                .filterConfigurePoints()
                .limit(-1)
                ;
        List<Point>points = getPointRequest.getPoints();
        context.put(AgentConstants.POINT,points);
        List<Map<String, Object>> pointsMapList = FieldUtil.getAsMapList(points,Point.class);
        context.put(AgentConstants.POINTS,pointsMapList);
        return false;
    }
}
