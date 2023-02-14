package com.facilio.agentv2.rdm;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data

public class RdmControllerContext extends Controller {
    private static final Logger LOGGER = LogManager.getLogger(RdmControllerContext.class.getName());

    private String device;
    private String url;
    private String username;
    private String password;

    public boolean getIsTdb() {
        return isTdb;
    }

    public void setIsTdb(boolean tdb) {
        isTdb = tdb;
    }

    private boolean isTdb;

    public RdmControllerContext() {
        setControllerType(FacilioControllerType.RDM.asInt());
    }

    public static String getKey(RdmControllerContext rdmControllerContext) throws URISyntaxException {
        URI uri = new URI(rdmControllerContext.getUrl());
        return rdmControllerContext.getDevice() + ":" + uri.getHost();
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.RDM_CONTROLLER_MODULE_NAME;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject object = new JSONObject();
        object.put(AgentConstants.ID, getId());
        object.put(AgentConstants.URL, getUrl());
        object.put(AgentConstants.DEVICE, getDevice());
        object.put(AgentConstants.USER_NAME, getUsername());
        object.put(AgentConstants.PASSWORD, getPassword());
        object.put(AgentConstants.IS_TDB, getIsTdb());
        return object;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        FacilioField deviceField = fieldsMap.get(AgentConstants.DEVICE);

        conditions.add(CriteriaAPI.getCondition(deviceField, getDevice(), StringOperators.IS));

        return conditions;
    }

    @Override
    public String getIdentifier() throws Exception {
        return device + ":" + getHost(url);
    }

    private String getHost(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return uri.getHost();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof RdmControllerContext){
            RdmControllerContext obj = (RdmControllerContext) o;
            return this.getDevice().equals(obj.getDevice()) && this.getUrl().equals(obj.getUrl()) && super.equals(obj);
        }else{
            return false;
        }
    }

}
