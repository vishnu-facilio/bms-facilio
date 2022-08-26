package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointEnum.ConfigureStatus;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.*;

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
