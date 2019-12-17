package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.bmsconsole.actions.FacilioAction;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class PointIdAction extends FacilioAction
{
    private static final Logger LOGGER = LogManager.getLogger(PointIdAction.class.getName());

    private Long pointId;

    public Long getPointId() { return pointId; }
    public void setPointId(Long pointId) { this.pointId = pointId; }

    public String unConfigurePoint(){
        try {
                setResult(AgentConstants.RESULT, PointsAPI.unConfigurePoint(getPointId()));
        }catch (Exception e){
            LOGGER.info("Exception occurred while unconfiguring point -> "+pointId+" -,",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }
}
