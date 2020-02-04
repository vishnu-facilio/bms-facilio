package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowEscalationContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowCommitmentRuleContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class SLAWorkflowAPI extends WorkflowRuleAPI {

    public static void addEscalations(SLAWorkflowCommitmentRuleContext rule, List<SLAWorkflowEscalationContext> escalations) throws Exception {
        if (CollectionUtils.isNotEmpty(escalations)) {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getSLAWorkflowEscalationModule().getTableName())
                    .fields(FieldFactory.getSlaWorkflowEscalationFields());

            Map<SLAWorkflowEscalationContext, List<ActionContext>> escalationActionMap = new HashMap<>();
            for (SLAWorkflowEscalationContext escalation : escalations) {
                escalation.setSlaRuleId(rule.getId());
                builder.addRecord(FieldUtil.getAsProperties(escalation));

                List<ActionContext> actions = escalation.getActions();
                if (CollectionUtils.isNotEmpty(actions)) {
                    List<ActionContext> actionContexts = ActionAPI.addActions(actions, rule);
                    escalationActionMap.put(escalation, actionContexts);
                }
            }
            builder.save();
            List<Map<String, Object>> records = builder.getRecords();
            for (int i = 0; i < records.size(); i++) {
                escalations.get(i).setId((Long) records.get(i).get("id"));
            }

            if (MapUtils.isNotEmpty(escalationActionMap)) {
                GenericInsertRecordBuilder relInsertBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getSLAWorkflowEscalationActionModule().getTableName())
                        .fields(FieldFactory.getSlaWorkflowEscalationActionFields());
                for (SLAWorkflowEscalationContext escalation : escalationActionMap.keySet()) {
                    List<ActionContext> actions = escalationActionMap.get(escalation);
                    if (CollectionUtils.isNotEmpty(actions)) {
                        for (ActionContext action : actions) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("escalationId", escalation.getId());
                            map.put("actionId", action.getId());
                            relInsertBuilder.addRecord(map);
                        }
                    }
                }
                relInsertBuilder.save();
            }
        }
    }

    public static List<SLAWorkflowEscalationContext> getEscalations(long slaRuleId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSLAWorkflowEscalationModule().getTableName())
                .select(FieldFactory.getSlaWorkflowEscalationFields())
                .andCondition(CriteriaAPI.getCondition("SLA_RULE_ID", "slaRuleId", String.valueOf(slaRuleId), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = builder.get();
        return FieldUtil.getAsBeanListFromMapList(maps, SLAWorkflowEscalationContext.class);
    }

    public static void getActions(List<SLAWorkflowEscalationContext> escalations) throws Exception {
        Map<Long, SLAWorkflowEscalationContext> map = new HashMap<>();
        for (SLAWorkflowEscalationContext escalation : escalations) {
            map.put(escalation.getId(), escalation);
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSLAWorkflowEscalationActionModule().getTableName())
                .select(FieldFactory.getSlaWorkflowEscalationActionFields())
                .andCondition(CriteriaAPI.getCondition("ESCALATION_ID", "escalationId", StringUtils.join(map.keySet(), ","), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = builder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
            List<Long> actionIds = new ArrayList<>();
            for (Map<String, Object> escalationActionMap : maps) {
                long actionId = (long) escalationActionMap.get("actionId");
                actionIds.add(actionId);
            }

            Map<Long, ActionContext> actionsAsMap = ActionAPI.getActionsAsMap(actionIds);
            if (MapUtils.isNotEmpty(actionsAsMap)) {
                for (Map<String, Object> escalationActionMap : maps) {
                    long actionId = (long) escalationActionMap.get("actionId");
                    long escalationId = (long) escalationActionMap.get("escalationId");
                    SLAWorkflowEscalationContext escalationContext = map.get(escalationId);
                    escalationContext.addAction(actionsAsMap.get(actionId));
                }
            }
        }
    }
}
