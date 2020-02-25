package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SLAWorkflowAPI extends WorkflowRuleAPI {

    public static void updateSLAPolicyRule(SLAPolicyContext rule) throws Exception {
        WorkflowRuleAPI.updateWorkflowRuleWithChildren(rule);
    }

    public static void deleteSLAPolicyEscalation(SLAPolicyContext rule) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getSLAWorkflowEscalationModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("SLA_POLICY_ID", "slaPolicyId", String.valueOf(rule.getId()), NumberOperators.EQUALS));
        builder.delete();
    }

    public static void addEscalations(SLAPolicyContext rule) throws Exception {
        List<SLAPolicyContext.SLAPolicyEntityEscalationContext> escalationContexts = rule.getEscalations();
        if (CollectionUtils.isNotEmpty(escalationContexts)) {
            for (SLAPolicyContext.SLAPolicyEntityEscalationContext escalationContext : escalationContexts) {
                List<SLAWorkflowEscalationContext> escalations = escalationContext.getLevels();
                if (CollectionUtils.isNotEmpty(escalations)) {
                    GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                            .table(ModuleFactory.getSLAWorkflowEscalationModule().getTableName())
                            .fields(FieldFactory.getSlaWorkflowEscalationFields());

                    Map<SLAWorkflowEscalationContext, List<ActionContext>> escalationActionMap = new HashMap<>();
                    for (SLAWorkflowEscalationContext escalation : escalations) {
                        escalation.setSlaPolicyId(rule.getId());
                        escalation.setSlaEntityId(escalationContext.getSlaEntityId());
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
        }
    }

    public static List<SLAPolicyContext.SLAPolicyEntityEscalationContext> getSLAPolicyEntityEscalations(long slaRuleId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSLAWorkflowEscalationModule().getTableName())
                .select(FieldFactory.getSlaWorkflowEscalationFields())
                .andCondition(CriteriaAPI.getCondition("SLA_POLICY_ID", "slaPolicyId", String.valueOf(slaRuleId), NumberOperators.EQUALS));
        List<SLAWorkflowEscalationContext> slaEscalations = FieldUtil.getAsBeanListFromMapList(builder.get(), SLAWorkflowEscalationContext.class);
        if (CollectionUtils.isNotEmpty(slaEscalations)) {
            Map<Long, List<SLAWorkflowEscalationContext>> map = new HashMap<>();
            List<SLAPolicyContext.SLAPolicyEntityEscalationContext> list = new ArrayList<>();
            for (SLAWorkflowEscalationContext e : slaEscalations) {
                long slaEntityId = e.getSlaEntityId();
                if (map.containsKey(slaEntityId)) {
                    List<SLAWorkflowEscalationContext> slaWorkflowEscalationContexts = map.get(slaEntityId);
                    slaWorkflowEscalationContexts.add(e);
                }
                else {
                    List<SLAWorkflowEscalationContext> slaWorkflowEscalationContexts = new ArrayList<>();
                    map.put(slaEntityId, slaWorkflowEscalationContexts);
                    slaWorkflowEscalationContexts.add(e);
                    SLAPolicyContext.SLAPolicyEntityEscalationContext slaEntityEscalationContext = new SLAPolicyContext.SLAPolicyEntityEscalationContext();
                    slaEntityEscalationContext.setSlaEntityId(slaEntityId);
                    slaEntityEscalationContext.setLevels(slaWorkflowEscalationContexts);
                    list.add(slaEntityEscalationContext);
                }
            }
            return list;
        }

        return null;
    }

    public static List<SLAWorkflowEscalationContext> getEscalations(long slaRuleId, long slaEntityId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSLAWorkflowEscalationModule().getTableName())
                .select(FieldFactory.getSlaWorkflowEscalationFields())
                .andCondition(CriteriaAPI.getCondition("SLA_POLICY_ID", "slaPolicyId", String.valueOf(slaRuleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("SLA_ENTITY_ID", "slaEntityId", String.valueOf(slaEntityId), NumberOperators.EQUALS));
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

    public static SLAEntityContext getSLAEntity(long slaEntityId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSLAEntityModule().getTableName())
                .select(FieldFactory.getSLAEntityFields())
                .andCondition(CriteriaAPI.getIdCondition(slaEntityId, ModuleFactory.getSLAEntityModule()));
        SLAEntityContext slaEntityContext = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), SLAEntityContext.class);
        return slaEntityContext;
    }

    public static List<SLAWorkflowCommitmentRuleContext.SLAEntityDuration> getSLAEntitiesForCommitment(long commitmentId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSLACommitmentDurationModule().getTableName())
                .select(FieldFactory.getSLACommitmentDurationFields())
                .andCondition(CriteriaAPI.getCondition("SLA_COMMITMENT_ID", "slaCommitmentID", String.valueOf(commitmentId), NumberOperators.EQUALS));
        return FieldUtil.getAsBeanListFromMapList(builder.get(), SLAWorkflowCommitmentRuleContext.SLAEntityDuration.class);
    }

    public static void updateSLACommitmentRule(SLAWorkflowCommitmentRuleContext rule) throws Exception {
        WorkflowRuleAPI.updateWorkflowRuleWithChildren(rule);
        deleteSLACommitmentDuration(rule);
        addSLACommitmentDuration(rule);
    }

    public static void deleteAllSLACommitments(SLAPolicyContext slaPolicy) throws Exception {
        if (slaPolicy == null) {
            return;
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_RULE_ID", "parentRuleId", String.valueOf(slaPolicy.getId()), NumberOperators.EQUALS));
        List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getWorkflowRules(WorkflowRuleContext.RuleType.SLA_WORKFLOW_RULE, false, criteria, null, null);
        if (CollectionUtils.isNotEmpty(workflowRules)) {
            List<Long> workflowIds = workflowRules.stream().map(WorkflowRuleContext::getWorkflowId).collect(Collectors.toList());
            WorkflowRuleAPI.deleteWorkFlowRules(workflowIds);
        }
    }

    public static void deleteSLACommitmentDuration(SLAWorkflowCommitmentRuleContext rule) throws Exception {
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getSLACommitmentDurationModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("SLA_COMMITMENT_ID", "slaCommitmentId", String.valueOf(rule.getId()), NumberOperators.EQUALS));
        deleteRecordBuilder.delete();
    }

    public static void addSLACommitmentDuration(SLAWorkflowCommitmentRuleContext rule) throws Exception {
        if (CollectionUtils.isEmpty(rule.getSlaEntities())) {
            throw new IllegalArgumentException("SLA Entities cannot be empty");
        }

        Set<Long> slaEntityIds = new HashSet<>();
        for (SLAWorkflowCommitmentRuleContext.SLAEntityDuration slaEntityDuration : rule.getSlaEntities()) {
            slaEntityDuration.setSlaCommitmentId(rule.getId());
            if (slaEntityIds.contains(slaEntityDuration.getSlaEntityId())) {
                throw new IllegalArgumentException("Same entity is added twice");
            }
            slaEntityIds.add(slaEntityDuration.getSlaEntityId());
        }

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getSLACommitmentDurationModule().getTableName())
                .fields(FieldFactory.getSLACommitmentDurationFields());
        builder.addRecords(FieldUtil.getAsMapList(rule.getSlaEntities(), SLAWorkflowCommitmentRuleContext.SLAEntityDuration.class));
        builder.save();
    }
}
