package com.facilio.agentv2.lonWorks;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LonWorksControllerContext extends Controller {

	private static final Logger LOGGER = LogManager.getLogger(LonWorksControllerContext.class.getName());

    private long id;
    private long siteId;
    private long moduleId;
    private long agentId;
    @JsonInclude
    private String subnetNode;
    @JsonInclude
    private String neuronId;

    public LonWorksControllerContext() {
        setControllerType(FacilioControllerType.LON_WORKS.asInt());
    }

    public LonWorksControllerContext(long agentId, long orgId) throws Exception {
        super(agentId, orgId);
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
    public long getSiteId() {
        return siteId;
    }

    @Override
    public void setSiteId(long siteId) {
        this.siteId = siteId;
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
    
    @JsonIgnore
    public String getModuleName() {
        return FacilioConstants.ContextNames.LON_WORKS_CONTROLLER_MODULE_NAME;
    }
    
    @Override
    public JSONObject getChildJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.SUBNET_NODE,subnetNode);
        jsonObject.put(AgentConstants.NEURON_ID,neuronId);
        return jsonObject;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(FacilioConstants.ContextNames.LON_WORKS_CONTROLLER_MODULE_NAME);
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.NEURON_ID), neuronId, StringOperators.IS));
        //conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.SUBNET_NODE),subnetNode, StringOperators.IS));
        return conditions;
    }

    @Override
    public String getIdentifier() {
        return subnetNode+IDENTIFIER_SEPERATER+neuronId;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof LonWorksControllerContext){
            LonWorksControllerContext obj = (LonWorksControllerContext) o;
            return this.getNeuronId().equals(obj.getNeuronId()) && this.getSubnetNode().equals(obj.getSubnetNode()) && super.equals(obj);
        }else{
            return false;
        }
    }
}
