package com.facilio.bmsconsole.scoringrule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.ScoringRuleTrigger;
import com.facilio.trigger.context.TriggerType;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ScoringRuleAPI extends WorkflowRuleAPI {

    public static void validateRule(ScoringRuleContext rule) {
        if (rule.getScoreTypeEnum() == null) {
            throw new IllegalArgumentException("Score type cannot be empty");
        }
        switch (rule.getScoreTypeEnum()) {
            case PERCENTAGE:
                rule.setScoreRange(100);
                break;

            case RANGE:
                if (rule.getScoreRange() < 0) {
                    rule.setScoreRange(5);
                }
                break;
        }

        for (ScoringCommitmentContext scoringCommitment : rule.getScoringCommitmentContexts()) {

            List<BaseScoringContext> baseScoringContexts = scoringCommitment.getBaseScoringContexts();
            if (CollectionUtils.isEmpty(baseScoringContexts)) {
                throw new IllegalArgumentException("Scoring contexts cannot be empty");
            }
            float totalWeightage = 0f;
            for (BaseScoringContext scoringContext : baseScoringContexts) {
                scoringContext.validate();
                totalWeightage += scoringContext.getWeightage();
            }

            if (totalWeightage != 100f) {
                throw new IllegalArgumentException("Weightage should be always 100%");
            }
        }
    }

    public static void addScoringRuleChildren(ScoringRuleContext rule, boolean add) throws Exception {
        validateRule(rule);
        addBaseScoringContexts(rule, rule.getScoringCommitmentContexts());
    }

    private static Map<BaseScoringContext.Type, List<BaseScoringContext>> getScoringMap(List<BaseScoringContext> baseScoringContexts) {
        Map<BaseScoringContext.Type, List<BaseScoringContext>> map = new HashMap<>();
        for (BaseScoringContext baseScoringContext : baseScoringContexts) {
            List<BaseScoringContext> list = map.get(baseScoringContext.getTypeEnum());
            if (list == null) {
                list = new ArrayList<>();
                map.put(baseScoringContext.getTypeEnum(), list);
            }
            list.add(baseScoringContext);
        }
        return map;
    }

    private static void addBaseScoringContexts(ScoringRuleContext rule, List<ScoringCommitmentContext> scoringCommitmentContexts) throws Exception {
        for (int i = 0; i < scoringCommitmentContexts.size(); i++) {
            ScoringCommitmentContext scoringCommitmentContext = scoringCommitmentContexts.get(i);
            scoringCommitmentContext.setOrder(i + 1);
            scoringCommitmentContext.setScoringRuleId(rule.getId());

            GenericInsertRecordBuilder commitmentInsertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getScoringCommitmentModule().getTableName())
                    .fields(FieldFactory.getScoringCommitmentFields());
            Map<String, Object> commitmentMap = FieldUtil.getAsProperties(scoringCommitmentContext);
            commitmentInsertBuilder.addRecord(commitmentMap);
            commitmentInsertBuilder.save();
            scoringCommitmentContext.setId((Long) commitmentMap.get("id"));

            Map<BaseScoringContext.Type, List<BaseScoringContext>> map = getScoringMap(scoringCommitmentContext.getBaseScoringContexts());
            for (BaseScoringContext.Type type : map.keySet()) {
                Class clazz = getClass(type);
                FacilioModule module = getModule(type);
                List<FacilioField> fields = getFields(type);
                List<FacilioModule> modules = new ArrayList<>();
                while (module != null) {
                    modules.add(0, module);
                    module = module.getExtendModule();
                }

                Map<String, List<FacilioField>> fieldMap = new HashMap<>();
                for (FacilioField f : fields) {
                    FacilioModule m = f.getModule();
                    List<FacilioField> list = fieldMap.get(m.getName());
                    if (list == null) {
                        list = new ArrayList<>();
                        fieldMap.put(m.getName(), list);
                    }
                    list.add(f);
                }

                List<BaseScoringContext> contextList = map.get(type);
                for (BaseScoringContext scoringContext : contextList) {
                    scoringContext.setScoringCommitmentId(scoringCommitmentContext.getId());
                    scoringContext.saveChildren();
                }

                List<Map<String, Object>> mapList = FieldUtil.getAsMapList(contextList, clazz);
                for (FacilioModule m : modules) {
                    GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                            .table(m.getTableName())
                            .fields(fieldMap.get(m.getName()));
                    builder.addRecords(mapList);
                    builder.save();
                }

                for (BaseScoringContext scoringContext : contextList) {
                    scoringContext.afterSave(rule);
                }
            }
        }
    }

    private static void deleteScoringContext(long ruleId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getScoringCommitmentModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("SCORING_RULE_ID", "scoringRuleId", String.valueOf(ruleId), NumberOperators.EQUALS));
        builder.delete();
    }

    public static void updateScoringRule(ScoringRuleContext rule) throws Exception {
        ScoringRuleContext oldRule = (ScoringRuleContext) getWorkflowRule(rule.getId());
        List<BaseTriggerContext> triggers = TriggerUtil.getTriggers(oldRule.getModule(), Collections.singletonList(EventType.INVOKE_TRIGGER),
                null, false, TriggerType.SCORING_RULE_TRIGGER);
        if (CollectionUtils.isNotEmpty(triggers)) {
            TriggerUtil.deleteTriggers(triggers.stream().map(BaseTriggerContext::getId).collect(Collectors.toList()));
        }
        validateRule(rule);

        deleteScoringContext(rule.getId());
        addScoringRuleChildren(rule, false);

        updateWorkflowRuleWithChildren(rule);
        updateExtendedRule(rule, ModuleFactory.getScoringRuleModule(), FieldFactory.getScoringRuleFields());
    }

    public static FacilioModule getModule(BaseScoringContext.Type type) {
        switch (type) {
            case NODE:
                return ModuleFactory.getNodeScoringModule();

            case CONDITIONED:
                return ModuleFactory.getConditionScoringModule();

            default:
                throw new IllegalArgumentException("Invalid scoring context type");
        }
    }

    public static List<FacilioField> getFields(BaseScoringContext.Type type) {
        switch (type) {
            case NODE:
                return FieldFactory.getNodeScoringFields();

            case CONDITIONED:
                return FieldFactory.getConditionScoringFields();

            default:
                throw new IllegalArgumentException("Invalid scoring context type");
        }
    }

    public static Class getClass(BaseScoringContext.Type type) {
        switch (type) {
            case NODE:
                return NodeScoringContext.class;
            case CONDITIONED:
                return ConditionScoringContext.class;
            default:
                throw new IllegalArgumentException("Invalid scoring context type");
        }
    }

    public static List<BaseScoringContext> convertToObject(List<Map<String, Object>> scoringContextMapList) {
        List<BaseScoringContext> scoringContexts = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(scoringContextMapList)) {
            for (Map<String, Object> map : scoringContextMapList) {
                BaseScoringContext.Type type = BaseScoringContext.Type.valueOf(((Number) map.get("type")).intValue());
                BaseScoringContext baseContext = (BaseScoringContext) FieldUtil.getAsBeanFromMap(map, getClass(type));
                scoringContexts.add(baseContext);
            }
        }
        return scoringContexts;
    }

    public static List<ScoringCommitmentContext> getScoringCommitmentContexts(List<Long> ruleIds) throws Exception {
        List<ScoringCommitmentContext> list = null;
        if (CollectionUtils.isNotEmpty(ruleIds)) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getScoringCommitmentModule().getTableName())
                    .select(FieldFactory.getScoringCommitmentFields())
                    .andCondition(CriteriaAPI.getCondition("SCORING_RULE_ID", "scoringRuleId", StringUtils.join(ruleIds, ","), NumberOperators.EQUALS))
                    .orderBy("COMMITMENT_ORDER ASC");
            list = FieldUtil.getAsBeanListFromMapList(builder.get(), ScoringCommitmentContext.class);

            if (CollectionUtils.isNotEmpty(list)) {
                List<Long> ids = new ArrayList<>();
                for (ScoringCommitmentContext commitmentContext : list) {
                    if (commitmentContext.getNamedCriteriaId() > 0) {
                        ids.add(commitmentContext.getNamedCriteriaId());
                    }
                }
                Map<Long, NamedCriteria> criteriaMap = NamedCriteriaAPI.getCriteriaAsMap(ids);
                if (MapUtils.isNotEmpty(criteriaMap)) {
                    for (ScoringCommitmentContext commitmentContext : list) {
                        if (commitmentContext.getNamedCriteriaId() > 0) {
                            commitmentContext.setNamedCriteria(criteriaMap.get(commitmentContext.getNamedCriteriaId()));
                        }
                    }
                }
            }
        }
        return list;
    }

    public static List<BaseScoringContext> getBaseScoringContexts(List<Long> commitmentIds) throws Exception {
        List<BaseScoringContext> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(commitmentIds)) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getBaseScoringModule().getTableName())
                    .select(FieldFactory.getBaseScoringFields())
                    .andCondition(CriteriaAPI.getCondition("SCORING_COMMITMENT_ID", "scoringCommitmentId", StringUtils.join(commitmentIds, ","), NumberOperators.EQUALS));
            List<BaseScoringContext> baseScoringContextList = FieldUtil.getAsBeanListFromMapList(builder.get(), BaseScoringContext.class);

            list.addAll(getExtendedScoringContexts(baseScoringContextList));
        }
        return list;
    }

    private static List<BaseScoringContext> getExtendedScoringContexts(List<BaseScoringContext> baseScoringContextList) throws Exception {
        List<BaseScoringContext> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(baseScoringContextList)) {
            Map<BaseScoringContext.Type, List<BaseScoringContext>> map = getScoringMap(baseScoringContextList);
            for (BaseScoringContext.Type type : map.keySet()) {
                FacilioModule module = getModule(type);
                List<BaseScoringContext> baseScoringContexts = map.get(type);
                List<Long> ids = baseScoringContexts.stream().map(BaseScoringContext::getId).collect(Collectors.toList());
                GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                        .table(ModuleFactory.getBaseScoringModule().getTableName())
                        .innerJoin(module.getTableName())
                            .on(ModuleFactory.getBaseScoringModule().getTableName() + ".ID = " + module.getTableName() + ".ID")
                        .select(getFields(type))
                        .andCondition(CriteriaAPI.getIdCondition(ids, module));
                List<? extends BaseScoringContext> scoringList = FieldUtil.getAsBeanListFromMapList(builder.get(), getClass(type));

                if (CollectionUtils.isNotEmpty(scoringList)) {
                    switch (type) {
                        case CONDITIONED:
                            List<ConditionScoringContext> conditionedList = (List<ConditionScoringContext>) scoringList;
                            List<Long> criteriaIds = conditionedList.stream()
                                    .filter(conditioned -> conditioned.getNamedCriteriaId() > 0)
                                    .map(ConditionScoringContext::getNamedCriteriaId).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(criteriaIds)) {
                                Map<Long, NamedCriteria> criteriaMap = NamedCriteriaAPI.getCriteriaAsMap(criteriaIds);
                                for (ConditionScoringContext conditioned : conditionedList) {
                                    if (conditioned.getNamedCriteriaId() > 0) {
                                        conditioned.setNamedCriteria(criteriaMap.get(conditioned.getNamedCriteriaId()));
                                    }
                                }
                            }
                            break;
                    }
                    list.addAll(scoringList);
                }
            }
        }
        return list;
    }

    public static void constructScoringRule(List<WorkflowRuleContext> workflows) throws Exception {
        if (CollectionUtils.isNotEmpty(workflows)) {
            List<Long> ruleIds = new ArrayList<>();
            for (WorkflowRuleContext workflowRuleContext : workflows) {
                if (workflowRuleContext instanceof ScoringRuleContext) {
                    ScoringRuleContext scoringRuleContext = (ScoringRuleContext) workflowRuleContext;
                    ruleIds.add(scoringRuleContext.getId());
                }
            }

            List<ScoringCommitmentContext> scoringCommitmentContexts = getScoringCommitmentContexts(ruleIds);
            if (CollectionUtils.isNotEmpty(scoringCommitmentContexts)) {
                List<Long> commitmentIds = new ArrayList<>();
                Map<Long, List<ScoringCommitmentContext>> commitmentMap = new HashMap<>();
                for (ScoringCommitmentContext scoringCommitmentContext : scoringCommitmentContexts) {
                    commitmentIds.add(scoringCommitmentContext.getId());
                    List<ScoringCommitmentContext> list = commitmentMap.get(scoringCommitmentContext.getScoringRuleId());
                    if (list == null) {
                        list = new ArrayList<>();
                        commitmentMap.put(scoringCommitmentContext.getScoringRuleId(), list);
                    }
                    list.add(scoringCommitmentContext);
                }

                for (WorkflowRuleContext workflowRuleContext : workflows) {
                    if (workflowRuleContext instanceof ScoringRuleContext) {
                        ScoringRuleContext scoringRuleContext = (ScoringRuleContext) workflowRuleContext;
                        scoringRuleContext.setScoringCommitmentContexts(commitmentMap.get(scoringRuleContext.getId()));
                    }
                }

                List<BaseScoringContext> baseScoringContexts = getBaseScoringContexts(commitmentIds);
                if (CollectionUtils.isNotEmpty(baseScoringContexts)) {
                    Map<Long, List<BaseScoringContext>> map = new HashMap<>();
                    for (BaseScoringContext baseScoringContext : baseScoringContexts) {
                        List<BaseScoringContext> list = map.get(baseScoringContext.getScoringCommitmentId());
                        if (list == null) {
                            list = new ArrayList<>();
                            map.put(baseScoringContext.getScoringCommitmentId(), list);
                        }
                        list.add(baseScoringContext);
                    }

                    for (ScoringCommitmentContext scoringCommitmentContext : scoringCommitmentContexts) {
                        scoringCommitmentContext.setBaseScoringContexts(map.get(scoringCommitmentContext.getId()));
                    }
                }
            }
        }
    }

    public static ScoreContext getScoreRecord(FacilioModule scoreModule, long scoreRuleId, ModuleBaseWithCustomFields parent) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(scoreModule.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        SelectRecordsBuilder<ScoreContext> builder = new SelectRecordsBuilder<ScoreContext>()
                .module(scoreModule)
                .beanClass(ScoreContext.class)
                .select(allFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), String.valueOf(parent.getId()), PickListOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("scoreRuleId"), String.valueOf(scoreRuleId), NumberOperators.EQUALS));
        return builder.fetchFirst();
    }

    public static FacilioModule getScoreModule(long moduleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> subModules = modBean.getSubModules(moduleId, FacilioModule.ModuleType.SCORE);
        if (subModules.size() == 1) {
            return subModules.get(0);
        }
        throw new IllegalArgumentException("Score module not configured");
    }

    public static void addOrUpdateScoreRecord(FacilioModule scoreModule, ScoreContext scoreContext) throws Exception {
        if (scoreContext == null || scoreContext == null) {
            return;
        }
        scoreContext.setCreatedTime(System.currentTimeMillis());
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (scoreContext.getId() > 0) {
            UpdateRecordBuilder<ScoreContext> builder = new UpdateRecordBuilder<ScoreContext>()
                    .module(scoreModule)
                    .fields(modBean.getAllFields(scoreModule.getName()))
                    .andCondition(CriteriaAPI.getIdCondition(scoreContext.getId(), scoreModule));
            builder.update(scoreContext);
        }
        else {
            InsertRecordBuilder<ScoreContext> builder = new InsertRecordBuilder<ScoreContext>()
                    .module(scoreModule)
                    .fields(modBean.getAllFields(scoreModule.getName()));
            builder.insert(scoreContext);
        }
    }

    public static void addTriggersToBeExecuted(long scoreRuleId, ScoringRuleTrigger trigger) throws Exception {
        ScoringRuleContext scoreRule = (ScoringRuleContext) getWorkflowRule(scoreRuleId);
        if (scoreRule == null) {
            return;
        }

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getScoringRuleTriggerCallRelModule().getTableName())
                .fields(FieldFactory.getScoringRuleTriggerCallRelFields());
        Map<String, Object> map = new HashMap<>();
        map.put("ruleId", scoreRuleId);
        map.put("triggerId", trigger.getId());
        builder.insert(map);
    }
}
