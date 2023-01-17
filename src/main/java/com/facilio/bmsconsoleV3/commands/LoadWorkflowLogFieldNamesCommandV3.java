package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import com.facilio.workflowlog.context.WorkflowLogContext;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LoadWorkflowLogFieldNamesCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<WorkflowLogContext> workflowLogs = Constants.getRecordList((FacilioContext)context);

        if (CollectionUtils.isEmpty(workflowLogs)) {
            return false;
        }

        Set<Long> workflowRuleIds = new HashSet<>();
        Set<Long> scheduledWoIds = new HashSet<>();

        for (WorkflowLogContext workflowLog : workflowLogs) {
            if (workflowLog.getParentId() > 0){
                if (workflowLog.getLogType() == WorkflowLogContext.WorkflowLogType.SCHEDULER.getTypeId()) {
                    scheduledWoIds.add(workflowLog.getParentId());
                    continue;
                }
                workflowRuleIds.add(workflowLog.getParentId());
            }

        }

        Map<Long,WorkflowRuleContext> workflowRulesAsMap = WorkflowRuleAPI.getWorkflowRulesAsMap(new ArrayList<>(workflowRuleIds),false,false);
        Map<Long,ScheduledWorkflowContext> scheduledWorkflowContextMap = getScheduledWorkflowContext(scheduledWoIds);


        for (WorkflowLogContext workflowLog : workflowLogs) {
            Long moduleId = workflowLog.getRecordModuleId();
            if (moduleId != null) {
                FacilioModule module = Constants.getModBean().getModule(moduleId);
                if (module == null) {
                    continue;
                }
                workflowLog.setRecordModuleName(module.getDisplayName());
            }

            if (workflowLog.getParentId() > 0) {
                if (workflowLog.getLogType() == WorkflowLogContext.WorkflowLogType.SCHEDULER.getTypeId() && scheduledWorkflowContextMap != null) {
                    ScheduledWorkflowContext scheduledWorkflowContext = scheduledWorkflowContextMap.get(workflowLog.getParentId());

                    if (scheduledWorkflowContext != null) {
                        workflowLog.setWorkflowRuleName(scheduledWorkflowContext.getName());
                    }
                } else if (workflowRulesAsMap != null && !workflowRulesAsMap.isEmpty()) {
                    WorkflowRuleContext ruleContext = workflowRulesAsMap.get(workflowLog.getParentId());
                    if (ruleContext != null) {
                        workflowLog.setWorkflowRuleName(ruleContext.getName());
                    }
                }
            }


            // due to response size issues , so we set null for log and exception value.

            if (workflowLog.getLogValue() != null){
                workflowLog.setLogValue(null);
                workflowLog.setLogAvailable(true);
            }

            if (workflowLog.getException() != null){
                workflowLog.setException(null);
                workflowLog.setExceptionAvailable(true);
            }
        }
        return false;
    }

    private Map<Long,ScheduledWorkflowContext> getScheduledWorkflowContext(Set<Long> scheduledWorkflowIds) throws Exception {

        FacilioModule scheduledWoModule = ModuleFactory.getScheduledWorkflowModule();
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(FieldFactory.getScheduledWorkflowFields())
                .table(scheduledWoModule.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(scheduledWorkflowIds,scheduledWoModule));

        List<Map<String, Object>> props = select.get();

        if(props != null && !props.isEmpty()) {
           List<ScheduledWorkflowContext> rules =  FieldUtil.getAsBeanListFromMapList(props, ScheduledWorkflowContext.class);
            return rules.stream()
                    .collect(Collectors.toMap(ScheduledWorkflowContext::getId, Function.identity()))
                    ;
        }
        return null;
    }
}
