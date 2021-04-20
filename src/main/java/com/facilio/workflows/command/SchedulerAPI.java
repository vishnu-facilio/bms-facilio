package com.facilio.workflows.command;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SchedulerAPI {

    public static void deleteAllActions(long schedulerId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSchedulerActionRelModule().getTableName())
                .select(FieldFactory.getScheduledActionRelFields())
                .andCondition(CriteriaAPI.getCondition("SCHEDULER_ID", "schedulerId", String.valueOf(schedulerId), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<Long> actionIds = new ArrayList<>();
            for (Map<String, Object> prop : props) {
                actionIds.add((Long) prop.get("actionId"));
            }
            ActionAPI.deleteActions(actionIds);
        }
    }

    public static void getSchedulerActions(List<ScheduledWorkflowContext> scheduledWorkflowContexts) throws Exception {
        if (CollectionUtils.isEmpty(scheduledWorkflowContexts)) {
            return;
        }

        List<Long> ids = scheduledWorkflowContexts.stream().map(ScheduledWorkflowContext::getId).collect(Collectors.toList());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSchedulerActionRelModule().getTableName())
                .select(FieldFactory.getScheduledActionRelFields())
                .andCondition(CriteriaAPI.getCondition("SCHEDULER_ID", "schedulerId", StringUtils.join(ids, ","), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<Long> actionIds = new ArrayList<>();
            for (Map<String, Object> prop : props) {
                actionIds.add((Long) prop.get("actionId"));
            }
            Map<Long, ActionContext> actionsAsMap = ActionAPI.getActionsAsMap(actionIds);
            if (MapUtils.isNotEmpty(actionsAsMap)) {
                Map<Long, ScheduledWorkflowContext> scheduledWorkflowContextMap = scheduledWorkflowContexts.stream().collect(Collectors.toMap(ScheduledWorkflowContext::getId, Function.identity()));
                for (Map<String, Object> prop : props) {
                    scheduledWorkflowContextMap.get(prop.get("schedulerId")).addAction(actionsAsMap.get(prop.get("actionId")));
                }
            }
        }

    }

    public static void addActions(ScheduledWorkflowContext scheduledWorkflowContext) throws Exception {
        if (CollectionUtils.isNotEmpty(scheduledWorkflowContext.getActions())) {
            WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
            workflowRuleContext.setName(scheduledWorkflowContext.getName()); // we are doing this, since action is tightly integrated with rule
            List<ActionContext> actions = ActionAPI.addActions(scheduledWorkflowContext.getActions(), workflowRuleContext);
            addSchedulerActions(scheduledWorkflowContext, actions);
        }
    }

    private static void addSchedulerActions(ScheduledWorkflowContext scheduledWorkflowContext, List<ActionContext> actions) throws Exception {
        if (CollectionUtils.isEmpty(actions)) {
            return;
        }

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getSchedulerActionRelModule().getTableName())
                .fields(FieldFactory.getScheduledActionRelFields());
        for (ActionContext action : actions) {
            Map<String, Object> map = new HashMap<>();
            map.put("schedulerId", scheduledWorkflowContext.getId());
            map.put("actionId", action.getId());
            builder.addRecord(map);
        }
        builder.save();
    }
}
