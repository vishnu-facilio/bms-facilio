package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflows.command.SchedulerAPI;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScheduledWorkflowMigration {

    public static void migrateScheduledWorkflow() throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getScheduledWorkflowFields())
                .table(ModuleFactory.getScheduledWorkflowModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("WORKFLOW_ID", "workflowId", "", CommonOperators.IS_NOT_EMPTY));
        List<ScheduledWorkflowContext> scheduledWorkflowContexts = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), ScheduledWorkflowContext.class);
        if (CollectionUtils.isNotEmpty(scheduledWorkflowContexts)) {

            List<Long> workflowIds = scheduledWorkflowContexts.stream().map(ScheduledWorkflowContext::getWorkflowId).collect(Collectors.toList());
            Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workflowIds);

            for (ScheduledWorkflowContext scheduledWorkflowContext : scheduledWorkflowContexts) {
                scheduledWorkflowContext.setWorkflowContext(workflowMap.get(scheduledWorkflowContext.getWorkflowId()));

                ActionContext executeAction = new ActionContext();
                executeAction.setActionType(ActionType.WORKFLOW_ACTION);
                JSONObject templateJson = new JSONObject();
                templateJson.put("resultWorkflowContext", FieldUtil.getAsJSON(scheduledWorkflowContext.getWorkflowContext()));
                executeAction.setTemplateJson(templateJson);

                List<ActionContext> actions = new ArrayList<>();
                actions.add(executeAction);
                scheduledWorkflowContext.setActions(actions);
                SchedulerAPI.addActions(scheduledWorkflowContext, false);
            }
        }
    }
}
