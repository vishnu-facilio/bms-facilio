package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.facilio.workflowlog.context.WorkflowLogContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class LoadWorkflowLogFieldNamesCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<WorkflowLogContext> workflowLogs = Constants.getRecordList((FacilioContext)context);

        if (CollectionUtils.isEmpty(workflowLogs)) {
            return false;
        }

        Set<Long> workflowRuleIds = new HashSet<>();

        for (WorkflowLogContext workflowLog : workflowLogs) {
            if (workflowLog.getParentId() > 0){
                workflowRuleIds.add(workflowLog.getParentId());
            }
        }

        Map<Long,WorkflowRuleContext> workflowRulesAsMap = WorkflowRuleAPI.getWorkflowRulesAsMap(new ArrayList<>(workflowRuleIds),false,false);


        for (WorkflowLogContext workflowLog : workflowLogs) {
            Long moduleId = workflowLog.getRecordModuleId();
            if (moduleId != null) {
                FacilioModule module = Constants.getModBean().getModule(moduleId);
                if (module == null) {
                    continue;
                }
                workflowLog.setRecordModuleName(module.getDisplayName());
            }

            if (workflowLog.getParentId() > 0 && (workflowRulesAsMap != null && !workflowRulesAsMap.isEmpty())) {
                workflowLog.setWorkflowRuleName(workflowRulesAsMap.get(workflowLog.getParentId()).getName());
            }
        }
        return false;
    }
}
