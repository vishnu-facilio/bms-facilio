package com.facilio.agentv2.misc;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MiscControllerContext extends Controller {

    public MiscControllerContext(long agentId, long orgId) {
        super(agentId, orgId);
        setControllerType(FacilioControllerType.MISC.asInt());
    }

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.MISC_CONTROLLER_MODULE_NAME;

    public MiscControllerContext() {
        setControllerType(FacilioControllerType.MISC.asInt());
    }

    @JsonIgnore
    public String getModuleName() { return ASSETCATEGORY; }


    public static Controller getMiscControllerFromJSON(long agentId, Map<String,Object> controllerJSON){
        MiscControllerContext controller = new MiscControllerContext(agentId, AccountUtil.getCurrentOrg().getOrgId());
        if(controllerJSON.containsKey(AgentConstants.IDENTIFIER)){
            controller.setName(String.valueOf((controllerJSON.get(AgentConstants.IDENTIFIER))));
        }else if(controllerJSON.containsKey(AgentConstants.NAME)){
            controller.setName(String.valueOf(controllerJSON.get(AgentConstants.NAME)));
        }else {
            return null;
        }
        return controller;
    }

    @Override
    public JSONObject getChildJSON() {

        JSONObject childJson = new JSONObject();
        childJson.put(AgentConstants.NAME,getName());
        return childJson;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        if (getName() != null) {
            conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.NAME),getName(), StringOperators.IS));
        }else {
            throw new Exception(" name cant be null for misc controller");
        }
        return conditions;
    }

    @Override
    public String getIdentifier() {
        return getName();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof MiscControllerContext){
            MiscControllerContext obj = (MiscControllerContext) o;
            return this.getName().equals(obj.getName()) && super.equals(obj);
        }else{
            return false;
        }
    }
}

