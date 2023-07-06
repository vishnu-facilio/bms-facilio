package com.facilio.workflows.functions;

import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.Map;
@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.IAM_FUNCTION)
public class FacilioIAMFunction {
    public Object allowAppAccess(ScriptContext scriptContext, Map<String, Object> map, Object... objects) throws Exception {
        if (objects.length < 4) {
            throw new FunctionParamException("Required arguments is missing. Expected arguments => (peopleId, appId, roleId, sendInvitation)");
        }
        PeopleAPI.addApplicationUsersByPeopleId((Long) objects[0], (Long) objects[1], (Long) objects[2], (boolean) objects[3]);
        return true;
    }
    public Object changeAppRole(ScriptContext scriptContext , Map<String, Object> map, Object... objects) throws Exception {
        if (objects.length < 3) {
            throw new FunctionParamException("Required arguments is missing. Expected arguments => (peopleId, appId, roleId)");
        }
        PeopleAPI.updateUserByPeopleId((Long) objects[0], (Long) objects[1], (Long) objects[2]);
        return true;
    }

    public Object revokeAppAccess(ScriptContext scriptContext, Map<String, Object> map, Object... objects) throws Exception {
        if (objects.length < 2) {
            throw new FunctionParamException("Required arguments is missing. Expected arguments => (peopleId, appId)");
        }
        PeopleAPI.deleteApplicationUsersByPeopleId((Long) objects[0], (Long) objects[1]);
        return true;
    }
};

