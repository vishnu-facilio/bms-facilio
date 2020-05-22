package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class TypeControllerAction extends ControllerActions
{
    private static final Logger LOGGER = LogManager.getLogger(TypeControllerAction.class.getName());

    public String getControllerUsingIdType() {
        try {
                Controller controller = null;
            if ((getControllerId() > 0) && (getControllerType() != null)) {
                GetControllerRequest getControllerRequest = new GetControllerRequest()
                        .withControllerId(getControllerId())
                        .ofType(FacilioControllerType.valueOf(getControllerType()));
                controller = getControllerRequest.getController();
                if (controller != null) {
                    try {
                        setResult(AgentConstants.RESULT, SUCCESS);
                        setResult(AgentConstants.DATA, FieldUtil.getAsJSON(controller));
                        setResponseCode(HttpURLConnection.HTTP_OK);
                    } catch (Exception e) {
                        LOGGER.info(" Exception occurred ", e);
                        setResult(AgentConstants.EXCEPTION, e.getMessage());
                        setResult(AgentConstants.RESULT, ERROR);
                        setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    }
                }
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting controller using id and type ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    /**
     * Lists all points for a controller
     * @return
     */
    public String getControllerPoints(){ //TODO use pointId
        try {

            List<Point> points = PointsAPI.getControllerPoints(FacilioControllerType.valueOf(getControllerType()), getControllerId());
            JSONArray array = new JSONArray();
            points.forEach(point -> array.add(point.toJSON()));
            setResult(AgentConstants.DATA, array);
                setResult(AgentConstants.RESULT, SUCCESS);
                return SUCCESS;
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting point",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }
    
}
