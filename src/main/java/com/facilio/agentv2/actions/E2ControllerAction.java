package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.E2.E2ControllerContext;


public class E2ControllerAction extends AgentIdAction {

    private int port;
    private String ipAddress;
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPortNumber() {
        return port;
    }

    public void setPortNumber(int portNumber) {
        this.port = portNumber;
    }

    public String addController(){
        try {
            E2ControllerContext e2Controller = new E2ControllerContext();
            e2Controller.setIpAddress(ipAddress);
            e2Controller.setPort(port);
            e2Controller.setName(name);
            e2Controller.setAgentId(getAgentId());
            e2Controller.setControllerType(getControllerType());
            AgentConstants.getControllerBean().addController(e2Controller, false);
            setResult(AgentConstants.RESULT,SUCCESS);
        }
        catch (Exception e){
            setResult(AgentConstants.RESULT,ERROR);
        }
       return SUCCESS;
    }


}
