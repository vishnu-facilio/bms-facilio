package com.facilio.agentv2.lonWorks;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LonWorksControllerContext extends Controller {

    private static final Logger LOGGER = LogManager.getLogger(LonWorksControllerContext.class.getName());

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.BACNET_IP_CONTROLLER_MODULE_NAME;


    private long id;
    private long moduleId;
    private long agentId;
    @JsonInclude
    private String subnetNode;
    @JsonInclude
    private String neuronId;

    public LonWorksControllerContext() {
        setControllerType(FacilioControllerType.LON_WORKS.asInt());
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getModuleId() {
        return moduleId;
    }

    @Override
    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public long getAgentId() {
        return agentId;
    }

    @Override
    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public String getNeuronId() {
        return neuronId;
    }

    public void setNeuronId(String neuronId) {
        this.neuronId = neuronId;
    }

    public String getSubnetNode() {
        return subnetNode;
    }

    public void setSubnetNode(String subnetNode) {
        this.subnetNode = subnetNode;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.SUBNET_NODE,subnetNode);
        jsonObject.put(AgentConstants.NEURON_ID,neuronId);
        return jsonObject;
    }
/*
    public void processIdentifier(String identifier) throws Exception {
        if ((identifier != null) && (!identifier.isEmpty())) {
            String[] uniques = identifier.split(IDENTIFIER_SEPERATOR);
            if ((uniques.length == 3) && ((FacilioControllerType.valueOf(Integer.parseInt(uniques[0])) == FacilioControllerType.LON_WORKS))) {
                LOGGER.info("setting subnet node " + subnetNode + "  neuron id " + neuronId );
                subnetNode = uniques[1];
                neuronId = uniques[2];
            } else {
                throw new Exception(" Exceprion while processing identifier -- length or Type didnt match ");
            }
        } else {
            throw new Exception("Exception Occurred, identifier can't be null or empty ->"+identifier);
        }
    }*/


    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        //TODO implement this
        return null;
    }

    @Override
    public String getIdentifier() {
        return subnetNode+IDENTIFIER_SEPERATER+neuronId;
    }
}
