package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;

import java.util.Map;

public class ApprovalStateTransitionRuleContext extends AbstractStateTransitionRuleContext {

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
        if (moduleRecord instanceof WorkOrderContext) {
            WorkOrderContext workOrderContext = (WorkOrderContext) moduleRecord;
            return evaluateStateFlow(workOrderContext.getApprovalFlowId(), workOrderContext.getApprovalStatus(),
                    moduleName, record, placeHolders, context);
        }
        return false;
    }

    @Override
    protected void executeTrue(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        System.out.println("What");
    }
}
