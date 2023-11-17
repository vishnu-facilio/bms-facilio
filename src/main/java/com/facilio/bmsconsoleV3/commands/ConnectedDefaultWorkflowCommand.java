package com.facilio.bmsconsoleV3.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;

public class ConnectedDefaultWorkflowCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName= (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if(!moduleName.isEmpty()) {
            int workflowId=getWorkflowId(context, moduleName);
            context.put(WorkflowV2Util.DEFAULT_WORKFLOW_ID, workflowId);
        }
        return false;
    }

    private int getWorkflowId(Context context, String moduleName) {
        switch (moduleName) {
            case FacilioConstants.ContextNames.NEW_READING_ALARM:
                return 108;
            case FacilioConstants.ContextNames.READING_ALARM:
                return 8;
            case FacilioConstants.ContextNames.AGENT_ALARM:
            case FacilioConstants.ContextNames.OPERATION_ALARM:
                return 11;
            case FacilioConstants.ReadingRules.NEW_READING_RULE:
                return getRuleWorkflowId((String) context.get(FacilioConstants.ContextNames.WIDGET_NAME));
        }
        return -1;
    }

    private int getRuleWorkflowId(String widgetName) {
        switch (widgetName) {
            case "assetNdAlarm":
                return 50;
            case "ruleAlarmInsight":
                return 53;
            case "readingRuleWorkorder":
                return 51;
        }
        return -1;
    }
}
