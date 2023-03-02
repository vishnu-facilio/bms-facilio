package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentAction;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;

import java.util.List;
import java.util.Map;

public class TestAction extends AgentAction {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(TestAction.class);


    @Override
    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    private long agentId;
    public String getControllerUsingAgentId(){
        try {
            FacilioContext context = constructListContext(new FacilioContext());
            GetControllerRequest controllerRequest = new GetControllerRequest()
                    .withAgentId(agentId);
            if(context != null){
                controllerRequest.withPagination(context);
            }
            Map<String, Controller> controllers = controllerRequest.getControllersMap();
            //Map<String, Controller> controllersForAgent = AgentConstants.getControllerBean().getControllersForAgent(getAgentId(), constructListContext(new FacilioContext()));
            setResult(AgentConstants.DATA,controllers);
        } catch (Exception e) {
            LOGGER.info(" Exception while getting controller using agentId ");
        }
        return SUCCESS;
    }

    @Override
    public Long getControllerId() {
        return controllerId;
    }

    @Override
    public void setControllerId(Long controllerId) {
        this.controllerId = controllerId;
    }

    private Long controllerId;

    @Override
    public Integer getControllerType() {
        return controllerType;
    }

    @Override
    public void setControllerType(Integer controllerType) {
        this.controllerType = controllerType;
    }

    private Integer controllerType;

    public String getControllerUsingControllerId(){
        try {
            if ((controllerId > 0) && (getControllerType() != null)) {
                GetControllerRequest getControllerRequest = new GetControllerRequest()
                        .withControllerId(controllerId)
                        .ofType(FacilioControllerType.valueOf(getControllerType()));
                Controller controller = getControllerRequest.getController();
                // Map<String, Controller> controllers = getControllersFromDb(null, -1, type, controllerId, null);
                if(controller != null){
                    setResult(AgentConstants.DATA,controller.toJSON());
                }else {
                    setResult("Exception"," no controllers found ");
                }
            }
        } catch (Exception e) {
            LOGGER.info(" Exception while getting controller using agentId ");
        }
        return SUCCESS;
    }

    public String getControllerUsingId(){
        try{
            GetControllerRequest controllerRequest = new GetControllerRequest()
                    .withControllerId(getControllerId());
            List<Controller> controllers = controllerRequest.getControllers();
            setResult("controller",controllers.get(0));
            //AgentConstants.getControllerBean().getControllerUsingControllerJSON();
        }catch (Exception e){
            LOGGER.info("Exception while getting ts controller ",e);
        }
        return SUCCESS;
    }

    public String getAgentSites(){
        try{

            }catch (Exception e){
            LOGGER.info("Exception occurred while getting agent sites",e);
        }
        return SUCCESS;
    }
}
