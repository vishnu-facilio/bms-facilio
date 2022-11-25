package com.facilio.workflows.functions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum FacilioAutomationFunctions implements FacilioWorkflowFunctionInterface {

    RE_EXECUTE_SLARULE(1,"reExecuteSLA") {
        @Override
        public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

            checkParam(objects);

            if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.RERUN_WORKFLOW)) {
                throw new FunctionParamException("Access denied to perform this action");
            }
            WorkflowRuleAPI.reExecuteSLA((String) objects[0], (Long) objects[1]);

            return null;
        }

        ;

        public void checkParam(Object... objects) throws Exception {
            if (objects.length <= 1) {
                throw new FunctionParamException("Invalid data");
            }
        }
    };
    /*
    RERUN_WORKFLOW(2,"rerunWorkFlow") {
        @Override
        public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

            checkParam(objects);

            if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.RERUN_WORKFLOW)) {
                throw new FunctionParamException("Access denied to perform this action");
            }
            WorkflowRuleAPI.rerunWorkflow((String)objects[0], (Long)objects[1], WorkflowRuleContext.RuleType.valueOf((Integer)objects[2]));

            return null;
        };

        public void checkParam(Object... objects) throws Exception {
            if(objects.length <= 2) {
                throw new FunctionParamException("Invalid data");
            }
        }*/

    private Integer value;
    private String functionName;
    private String namespace = "automation";
    private String params;
    private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.AUTOMATION;

    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value;
    }
    public String getFunctionName() {
        return functionName;
    }
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    public String getNamespace() {
        return namespace;
    }
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    public String getParams() {
        return params;
    }
    FacilioAutomationFunctions(Integer value, String functionName) {
        this.value = value;
        this.functionName = functionName;
    }

    public static Map<String, FacilioAutomationFunctions> getAllFunctions() {
        return DEFAULT_FUNCTIONS;
    }
    public static FacilioAutomationFunctions getFacilioAutomationFunction(String functionName) {
        return DEFAULT_FUNCTIONS.get(functionName);
    }
    static final Map<String, FacilioAutomationFunctions> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
    static Map<String, FacilioAutomationFunctions> initTypeMap() {
        Map<String, FacilioAutomationFunctions> typeMap = new HashMap<>();
        for(FacilioAutomationFunctions type : FacilioAutomationFunctions.values()) {
            typeMap.put(type.getFunctionName(), type);
        }
        return typeMap;
    }

}
