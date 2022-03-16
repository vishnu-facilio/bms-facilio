package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.DBUtil;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddPointsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
        List<Map<String,Object>> points = (List<Map<String, Object>>) context.get(AgentConstants.POINTS);
        PointsUtil.addPoints(controller,points);
        return false;
    }
}
