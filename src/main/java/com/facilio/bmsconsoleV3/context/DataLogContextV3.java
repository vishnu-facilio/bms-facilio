package com.facilio.bmsconsoleV3.context;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
@Getter@Setter
public class DataLogContextV3 extends V3Context {
//    private long id;
//    private long orgId;
    private long recordId;
    private long partitionId;
    private int messageStatus;
    private long startTime;
    private long endTime;
    private int publishType;
    private String messageSource;
    private String errorStackTrace;
    private String payload;
    private int payloadIndex;

    private long agentId = -1L;
    public long getAgentId(){
        return agentId;
    }

    public void setAgentId(long agentId) {
        FacilioAgent agent = new FacilioAgent();
        this.agent = agent;
        agent.setId(agentId);
//		this.agentId = agentId;
    }
    private String agentName;
    public String getAgentName() {
        return agentName;
    }
    private FacilioAgent agent;
    @JSON(serialize = false)
    public FacilioAgent getAgent() {
        return agent;
    }
    public void setAgent(FacilioAgent agent) {
        this.agent = agent;
        if (agent != null) {
            this.agentName = agent.getDisplayName();
            this.agentId = agent.getId();
        }
    }

    private long controllerId = -1L;
    public long getControllerId(){return controllerId;}

    public void setControllerId(long controllerId){
        Controller controller = new Controller();
        this.controller = controller;
        controller.setId(controllerId);
    }
    private Controller controller;
    private String controllerName;
    public String getControllerName() {
        return controllerName;
    }
    @JSON(serialize = false)
    public Controller getController(){return controller;}
    public void setController(Controller controller){
        this.controller = controller;
        if(controller!=null){
            this.controllerName = controller.getName();
            this.controllerId = controller.getId();
        }
    }

    public static enum Agent_Message_Status implements FacilioIntEnum {
        FAILURE(1,"Failure"),
        PARTIAL_SUCCESS(2,"Partial Success"),
        SUCCESS(3,"Success");
        private int key;
        private String label;
        Agent_Message_Status(int key,String label) {
            this.key = key;
            this.label = label;
        }

        public int getKey() {
            return key;
        }

        public  String getLabel() {
            return label;
        }

        @Override
        public String getValue(){return label;}

    }

}
