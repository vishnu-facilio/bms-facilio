package com.facilio.agentv2;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestControllerContext extends Controller {


    public RestControllerContext() {
        setControllerType(FacilioControllerType.REST.asInt());
    }

    @Override
    public JSONObject getChildJSON() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.NAME,getName());
        return jsonObject;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.NAME),getName(), StringOperators.IS));
        return conditions;
    }

    @Override
    public String getIdentifier() {
        return getName();
    }
}
