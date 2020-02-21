package com.facilio.custom;

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

public class CustomController extends Controller {

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.CUSTOM_CONTROLLER_MODULE_NAME;

    public CustomController() {
        setControllerType(FacilioControllerType.CUSTOM.asInt());
    }

    @JsonIgnore
    public String getModuleName() { return ASSETCATEGORY; }

    public static Controller getCustomControllerFromJSON(long agentId, Map<String,Object> controllerJSON){
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        CustomController controller = new CustomController();
        controller.setAgentId(agentId);
        controller.setOrgId(orgId);
        return controller;
    }

    @Override
    public JSONObject getChildJSON() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.NAME,getName());
        return jsonObject;
    }


    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.NAME),getName(), StringOperators.IS));
        return conditions;
    }

    @Override
    public String getIdentifier()  {
        return getName();
    }
}
