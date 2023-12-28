package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.opcua.OpcUaPointContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.util.Objects;

@Getter
@Setter
public class OpcUaPointAction extends DeviceIdActions {
    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(OpcUaPointAction.class.getName());

    @Min(0)
    private Integer controllerType;
    private Long controllerId;
    private Long agentId = -1L;
    private int interval = 0;
    private int namespace = 0;
    private String uaPointIdentifier;

    public String addOpcPoint() {
        try {
            Controller controller = AgentConstants.getControllerBean().getController(controllerId, getAgentId());
            Objects.requireNonNull(controller, "controller can't be null");
            if (getControllerType() == FacilioControllerType.OPC_UA.asInt()) {
                OpcUaPointContext opc = new OpcUaPointContext(getAgentId(), controller.getId());
                opc.setNamespace(namespace);
                opc.setUaPointIdentifier(uaPointIdentifier);
                ControllerMessenger.sendAddPointCommand(controller, opc, interval);
                setResponseCode(HttpURLConnection.HTTP_OK);
                return SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.info("Exception while sending configure modbus point command", e);
            internalError();
        }
        return SUCCESS;
    }
}
