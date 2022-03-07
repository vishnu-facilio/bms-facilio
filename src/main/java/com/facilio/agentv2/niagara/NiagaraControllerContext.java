package com.facilio.agentv2.niagara;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NiagaraControllerContext extends Controller {

    private static final Logger LOGGER = LogManager.getLogger(NiagaraControllerContext.class.getName());

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.NIAGARA_CONTROLLER_MODULE_NAME;

    @JsonInclude
    private String ipAddress;

    @JsonInclude
    private long portNumber = -1;

    @JsonInclude
    private String identifier;

    public NiagaraControllerContext() {
        setControllerType(FacilioControllerType.NIAGARA.asInt());
    }

    public NiagaraControllerContext(long agentId, long orgId) throws Exception {
        super(agentId, orgId);
        setControllerType(FacilioControllerType.NIAGARA.asInt());
    }

    public String getModuleName() { return ASSETCATEGORY; }

    public long getPortNumber() { return portNumber; }
    public void setPortNumber(long portNumber) { this.portNumber = portNumber; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }



    @Override
    public JSONObject getChildJSON() {
        JSONObject niagaraControllerJSON = new JSONObject();
        niagaraControllerJSON.put(AgentConstants.ID, getId());
        niagaraControllerJSON.put(AgentConstants.PORT_NUMBER, getPortNumber());
        niagaraControllerJSON.put(AgentConstants.IP_ADDRESS, getIpAddress());
        return niagaraControllerJSON;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.PORT_NUMBER), String.valueOf(getPortNumber()), NumberOperators.EQUALS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.IP_ADDRESS),getIpAddress(), StringOperators.IS));
        return conditions;
    }

    @Override
    public String getIdentifier() {
        return portNumber+IDENTIFIER_SEPERATER+ipAddress;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof NiagaraControllerContext){
            NiagaraControllerContext obj = (NiagaraControllerContext) o;
            return this.getIpAddress().equals(obj.getIpAddress()) && this.getPortNumber()==obj.getPortNumber() && super.equals(obj);
        }else{
            return false;
        }
    }
}
