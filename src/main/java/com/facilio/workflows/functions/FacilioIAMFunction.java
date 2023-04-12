package com.facilio.workflows.functions;

import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.*;

public enum FacilioIAMFunction implements FacilioWorkflowFunctionInterface {

    ALLOW_APP_ACCESS(1,"allowAppAccess")  {

        @Override
        public Object execute(Map<String, Object> map, Object... objects) throws Exception {
            checkParam(objects);
            PeopleAPI.addApplicationUsersByPeopleId((Long)objects[0],(Long) objects[1],(Long) objects[2],(boolean) objects[3]);
            return true;
        }
        public void checkParam(Object... objects) throws Exception {
            if (objects.length < 4) {
                throw new FunctionParamException("Required arguments is missing. Expected arguments => (peopleId, appId, roleId, sendInvitation)");
            }
        }
    },

    CHANGE_APP_ROLE(2,"changeAppRole"){
        @Override
        public Object execute(Map<String, Object> map, Object... objects) throws Exception {
            checkParam(objects);
            PeopleAPI.updateUserByPeopleId((Long)objects[0],(Long) objects[1],(Long) objects[2]);
            return true;
        }
        public void checkParam(Object... objects) throws Exception {
            if (objects.length < 3) {
                throw new FunctionParamException("Required arguments is missing. Expected arguments => (peopleId, appId, roleId)");
            }
        }
    },

    REVOKE_APP_ACCESS(3,"revokeAppAccess"){
        @Override
        public Object execute(Map<String, Object> map, Object... objects) throws Exception {
            checkParam(objects);
            PeopleAPI.deleteApplicationUsersByPeopleId((Long)objects[0],(Long) objects[1]);
            return true;
        }
        public void checkParam(Object... objects) throws Exception {
            if (objects.length < 2) {
                throw new FunctionParamException("Required arguments is missing. Expected arguments => (peopleId, appId)");
            }
        }
    },
    ;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    private Integer value;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    private String functionName;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    private String namespace = "iam";

    public List<FacilioFunctionsParamType> getParams() {
        return params;
    }

    public void setParams(List<FacilioFunctionsParamType> params) {
        this.params = params;
    }

    private List<FacilioFunctionsParamType> params;
    private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.IAM;
    public void addParams(FacilioFunctionsParamType param) {
        this.params = (this.params == null) ? new ArrayList<>() :this.params;
        this.params.add(param);
    }

    FacilioIAMFunction(Integer value,String functionName,FacilioFunctionsParamType... params) {
        this.value = value;
        this.functionName = functionName;

        if(params != null ) {
            for(int i=0;i<params.length;i++) {
                addParams(params[i]);
            }
        }
    }
    public static Map<String, FacilioIAMFunction> getAllFunctions() {
        return FACILIO_IAM_FUNCTION;
    }
    public static FacilioIAMFunction getFacilioIAMFunction(String functionName) {
        return FACILIO_IAM_FUNCTION.get(functionName);
    }
    static final Map<String, FacilioIAMFunction> FACILIO_IAM_FUNCTION = Collections.unmodifiableMap(initTypeMap());
    static Map<String, FacilioIAMFunction> initTypeMap() {
        Map<String, FacilioIAMFunction> typeMap = new HashMap<>();
        for(FacilioIAMFunction type : FacilioIAMFunction.values()) {
            typeMap.put(type.getFunctionName(), type);
        }
        return typeMap;
    }
};

