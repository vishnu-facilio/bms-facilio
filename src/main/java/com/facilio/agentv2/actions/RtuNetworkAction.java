package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.modbusrtu.RtuNetworkContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.net.HttpURLConnection;

public class RtuNetworkAction extends AgentIdAction {

    private static final Logger LOGGER = LogManager.getLogger(RtuNetworkAction.class.getName());


    @NotNull
    @Size(min = 1)
    private String comPort;

    @NotNull
    @PositiveOrZero
    private Long baudRate;

    @NotNull
    @PositiveOrZero
    private Integer dataBits;

    @NotNull
    @PositiveOrZero
    private Integer stopBits;

    @NotNull
    @PositiveOrZero
    private Integer parity;

    @NotNull
    @Size(min = 3, max = 15)
    private String name;

    public String getComPort() {
        return comPort;
    }

    public void setComPort(String comPort) {
        this.comPort = comPort;
    }

    public Long getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(Long baudRate) {
        this.baudRate = baudRate;
    }

    public Integer getDataBits() {
        return dataBits;
    }

    public void setDataBits(Integer dataBits) {
        this.dataBits = dataBits;
    }

    public Integer getStopBits() {
        return stopBits;
    }

    public void setStopBits(Integer stopBits) {
        this.stopBits = stopBits;
    }

    public Integer getParity() {
        return parity;
    }

    public void setParity(Integer parity) {
        this.parity = parity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   /* public String addRtuNetwork() {
        try {
            LOGGER.info(" adding network  ");
            RtuNetworkContext rtuNetwork = new RtuNetworkContext();
            rtuNetwork.setAgentId(getAgentId());
            rtuNetwork.setBaudRate(getBaudRate());
            rtuNetwork.setComPort(getComPort());
            rtuNetwork.setDataBits(getDataBits());
            rtuNetwork.setName(getName());
            rtuNetwork.setStopBits(getStopBits());
            rtuNetwork.setParity(getParity());
            FacilioChain addRtuNetworkChain = TransactionChainFactory.getAddRtuChain();
            FacilioContext context = addRtuNetworkChain.getContext();
            context.put(AgentConstants.RTU_NETWORK, rtuNetwork);
            addRtuNetworkChain.execute();
            if (context.containsKey(AgentConstants.ID)) {
                setResult(AgentConstants.ID, context.get(AgentConstants.ID));
                setResponseCode(HttpURLConnection.HTTP_OK);
                setResult(AgentConstants.RESULT, SUCCESS);
            }
            else {
                setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
            }
        } catch (Exception e) {
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            LOGGER.info(" Exception while adding rtuNetworkContxt ", e);
        }
        return SUCCESS;
    }*/
}
