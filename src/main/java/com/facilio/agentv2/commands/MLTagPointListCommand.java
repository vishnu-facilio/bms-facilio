package com.facilio.agentv2.commands;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsole.commands.util.BmsPointsTaggingUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
@Log4j
public class MLTagPointListCommand extends AgentV2Command{

    @Override
    public boolean executeCommand(Context context) throws Exception {
        try{
        List<String>pointNames = (List<String>) context.get(AgentConstants.POINT_NAMES);
        Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
        if(pointNames!=null && !pointNames.isEmpty()){
            HashMap<String,Object>pointsMap = new HashMap<>();
            pointsMap.put("pointName",pointNames);
            pointsMap.put("controller",controller.getName());
            FacilioAgent agent = controller.getAgent();
            pointsMap.put("agentName", agent.getName());
            pointsMap.put("agentType", AgentType.valueOf(agent.getAgentType()).toString() );

//            pointsMap.put("agentId",agent.getId());
                BmsPointsTaggingUtil.tagPointListV1(Collections.singletonList(pointsMap));
            return false;
        }
        return true;
        }catch (Exception e ){
        LOGGER.error("Exception while tagging new points in ml",e);
        }
        return false;
    }
}
