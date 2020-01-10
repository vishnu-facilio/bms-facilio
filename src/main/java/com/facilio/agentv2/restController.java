package com.facilio.agentv2;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.db.criteria.Condition;
import org.json.simple.JSONObject;

import java.util.List;

public class restController extends Controller {


    public restController() {
        setControllerType(FacilioControllerType.REST.asInt());
    }

    @Override
    public String makeIdentifier() throws Exception {
        return null;
    }

    @Override
    public JSONObject getChildJSON() {
        return null;
    }

    @Override
    public List<Condition> getControllerConditions(String identifier) throws Exception {
        return null;
    }
}
