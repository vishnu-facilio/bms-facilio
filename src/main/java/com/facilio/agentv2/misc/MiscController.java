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

public class MiscController extends Controller {

    public MiscController(long agentId, long orgId) {
        super(agentId, orgId);
        setControllerType(FacilioControllerType.MISC.asInt());
    }

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.MISC_CONTROLLER_MODULE_NAME;

    public MiscController() {
    }

    @JsonIgnore
    public String getModuleName() { return ASSETCATEGORY; }


    public static Controller getMiscControllerFromJSON(long agentId, Map<String,Object> controllerJSON){
        MiscController controller = new MiscController(agentId, AccountUtil.getCurrentOrg().getOrgId());
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
    public String makeIdentifier() throws Exception {
        return getName();
    }

    @Override
    public JSONObject getChildJSON() {
        return new JSONObject();
    }

    @Override
    public List<Condition> getControllerConditions(String identifier) throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.NAME),identifier, StringOperators.IS));
        return conditions;
    }
}

