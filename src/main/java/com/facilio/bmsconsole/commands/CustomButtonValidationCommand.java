package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

public class CustomButtonValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List customButtonIds = (List) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID_LIST);
        Long customButtonId = CollectionUtils.isNotEmpty(customButtonIds) ? (Long) customButtonIds.get(0) : null;

        if (customButtonId == null || customButtonId <= 0) {
            return false;
        }

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        WorkflowRuleContext customButton = WorkflowRuleAPI.getWorkflowRule(customButtonId);
        Map<String, Object> placeholders = WorkflowRuleAPI.getOrgPlaceHolders();

        if (CollectionUtils.isNotEmpty(records)) {

            for (ModuleBaseWithCustomFields record : records) {

                if (record != null) {

                    Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, placeholders);

                    boolean evaluateCustomButton = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(customButton, moduleName, record, null, recordPlaceHolders, (FacilioContext) context, false);
                    if (!evaluateCustomButton) {
                        throw new IllegalArgumentException("Custom button is not valid for current Record");
                    }
                }

            }
        }

        return false;
    }
}
