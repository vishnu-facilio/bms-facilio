package com.facilio.agentv2.E2;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class E2ControllerContext extends Controller {


    public static final String MODULENAME = FacilioConstants.ContextNames.E2_CONTROLLER_MODULE_NAME;

    private String ipAddress;
    private long portNumber = -1;
    private String identifier;

    public E2ControllerContext() {
        setControllerType(FacilioControllerType.E2.asInt());
    }


    public String getModuleName() { return MODULENAME; }

    public long getPortNumber() { return portNumber; }
    public void setPortNumber(long portNumber) { this.portNumber = portNumber; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }



    @Override
    public JSONObject getChildJSON() {
        JSONObject e2ControllerJSON = new JSONObject();
        e2ControllerJSON.put(AgentConstants.ID, getId());
        e2ControllerJSON.put(AgentConstants.PORT_NUMBER, getPortNumber());
        e2ControllerJSON.put(AgentConstants.IP_ADDRESS, getIpAddress());
        return e2ControllerJSON;
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
        return ipAddress+IDENTIFIER_SEPERATER+portNumber;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof E2ControllerContext){
            E2ControllerContext obj = (E2ControllerContext) o;
            return this.getIpAddress().equals(obj.getIpAddress()) && this.getPortNumber()==obj.getPortNumber() && super.equals(obj);
        }else{
            return false;
        }
    }
}
