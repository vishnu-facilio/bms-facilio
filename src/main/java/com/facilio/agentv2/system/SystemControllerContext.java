package com.facilio.agentv2.system;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SystemControllerContext extends Controller {
    @Override
    public JSONObject getChildJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.NAME,getName());
        return jsonObject;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(CriteriaAPI.getNameCondition(getName(), ModuleFactory.getResourceModule()));
        return conditions;
    }

    @Override
    public String getIdentifier() throws IOException {
        return FacilioControllerType.SYSTEM.asInt()+IDENTIFIER_SEPERATER+getName();
    }
    public int getControllerType() {
        return FacilioControllerType.SYSTEM.asInt();
    }
    public String getModuleName() {
        return FacilioConstants.ContextNames.SYSTEM_CONTROLLER_MODULE_NAME;
    }
}
