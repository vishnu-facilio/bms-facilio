package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentAction;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
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
            Map<String, Controller> controllersForAgent = ControllerApiV2.getControllersForAgent(getAgentId(), constructListContext(new FacilioContext()));
            setResult(AgentConstants.DATA,controllersForAgent);
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
            Controller controllerUsingIdAndType = ControllerApiV2.getControllerUsingIdAndType(getControllerId(), FacilioControllerType.valueOf(getControllerType()));
            setResult(AgentConstants.DATA,controllerUsingIdAndType.toJSON());
        } catch (Exception e) {
            LOGGER.info(" Exception while getting controller using agentId ");
        }
        return SUCCESS;
    }

    public String getTsControllerTest(){
        try {
            Controller controller = ControllerApiV2.getControllerUsingIdAndType(getControllerId(), FacilioControllerType.valueOf(getControllerType()));
            Controller controllerFromDb = ControllerApiV2.getControllerFromDb(controller.getChildJSON(), agentId, FacilioControllerType.valueOf(controller.getControllerType()));
            setResult("data",controllerFromDb.toJSON());
        }catch (Exception e){
            LOGGER.info(" exception while getting ts controller ",e);
        }
        return SUCCESS;
    }

    public String testGetControllerBuilder(){
        try{
            List<Controller> controllers = ControllerApiV2.getControllerOfType(FacilioControllerType.valueOf(getControllerType()));
            setResult("usingType",controllers);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting controller using get controller builder");
        }
        return SUCCESS;
    }

    public String getControllerUsingId(){
        try{
            GetControllerRequest controllerRequest = new GetControllerRequest()
                    .withControllerId(getControllerId());
            List<Controller> controllers = controllerRequest.getControllers();
            setResult("controller",controllers.get(0));
            //ControllerApiV2.getControllerUsingControllerJSON();
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
