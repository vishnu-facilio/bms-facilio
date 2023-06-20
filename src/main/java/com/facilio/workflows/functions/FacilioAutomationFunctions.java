package com.facilio.workflows.functions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.AUTOMATION_FUNCTION)
public class FacilioAutomationFunctions {
    public Object reExecuteSLA(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);

        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.RERUN_WORKFLOW)) {
            throw new FunctionParamException("Access denied to perform this action");
        }
        WorkflowRuleAPI.reExecuteSLA((String) objects[0], (Long) objects[1]);

        return null;
    }

    public void checkParam(Object... objects) throws Exception {
        if (objects.length <= 1) {
            throw new FunctionParamException("Invalid data");
        }
    }

    /*public Object rerunWorkFlow(Map<String, Object> globalParam, Object... objects) throws Exception {

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
}
