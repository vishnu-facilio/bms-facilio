package com.facilio.modules;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.systemButtons.ButtonEvaluationHandler;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SystemButtonValidationHandler implements ButtonEvaluationHandler {
    @Override
    public boolean evaluateButtons(String moduleName, long recordId,String identifier) throws Exception {

        if(identifier == null){
            return  false;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        ModuleBaseWithCustomFields record = RecordAPI.getRecord(moduleName,recordId);
        FacilioContext context = new FacilioContext();
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();


        boolean evaluateSystemButton = false;

        if (record != null){

            recordMap.put(moduleName,Collections.singletonList(record));
            Constants.setRecordMap(context,recordMap);
            context.put("moduleName",moduleName);

            WorkflowRuleContext systembutton = SystemButtonApi.getSystemButton(module,identifier);

            if (systembutton != null) {
                evaluateSystemButton = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(systembutton, moduleName, record, null, new HashMap<>(), context, false);
            }
        }

        return evaluateSystemButton;
    }
}
