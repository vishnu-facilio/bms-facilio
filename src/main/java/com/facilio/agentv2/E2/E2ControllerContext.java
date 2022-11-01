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
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class E2ControllerContext extends Controller {


    public static final String MODULENAME = FacilioConstants.ContextNames.E2_CONTROLLER_MODULE_NAME;

    private String ipAddress;
    private long port = -1;
    private int deviceId;
    private int minorVersion;
    private int majorVersion;
    private String identifier;

    public E2ControllerContext() {
        setControllerType(FacilioControllerType.E2.asInt());
    }


    public String getModuleName() { return MODULENAME; }

    public static void validateControllerJSON(Map<String, Object> map) throws Exception {
        if(containsValueCheck(AgentConstants.IP_ADDRESS, map) && containsValueCheck(AgentConstants.PORT, map) && containsValueCheck(AgentConstants.DEVICE_ID, map) ){
            return;
        }
        throw new Exception("ControllerJSON of type "+MODULENAME+" must have keys "+ AgentConstants.IP_ADDRESS+ " ,"+AgentConstants.PORT+" ,"+AgentConstants.DEVICE_ID+" and json is -> "+map);
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject e2ControllerJSON = new JSONObject();
        e2ControllerJSON.put(AgentConstants.ID, getId());
        e2ControllerJSON.put(AgentConstants.PORT, getPort());
        e2ControllerJSON.put(AgentConstants.IP_ADDRESS, getIpAddress());
        e2ControllerJSON.put(AgentConstants.DEVICE_ID, getDeviceId());
        e2ControllerJSON.put(AgentConstants.MINOR_VERSION, getMinorVersion());
        e2ControllerJSON.put(AgentConstants.MAJOR_VERSION, getMajorVersion());
        return e2ControllerJSON;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.PORT), String.valueOf(getPort()), NumberOperators.EQUALS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.IP_ADDRESS),getIpAddress(), StringOperators.IS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.DEVICE_ID), String.valueOf(getDeviceId()), NumberOperators.EQUALS));
        return conditions;
    }

    @Override
    public String getIdentifier() {
        return ipAddress+IDENTIFIER_SEPERATER+port+IDENTIFIER_SEPERATER+deviceId;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof E2ControllerContext){
            E2ControllerContext obj = (E2ControllerContext) o;
            return this.getIpAddress().equals(obj.getIpAddress()) && this.getPort()==obj.getPort() && this.getDeviceId()==obj.getDeviceId() && this.getMinorVersion()==obj.getMinorVersion() && this.getMajorVersion()== obj.getMajorVersion() && super.equals(obj);
        }else{
            return false;
        }
    }
}
