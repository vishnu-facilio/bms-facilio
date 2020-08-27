package com.facilio.bmsconsole.scoringrule;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoringRuleAPI extends WorkflowRuleAPI {

    public static void validateRule(ScoringRuleContext rule) {
        if (rule.getScoreFieldId() == -1) {
            throw new IllegalArgumentException("Scoring field cannot be empty");
        }

        List<BaseScoringContext> baseScoringContexts = rule.getBaseScoringContexts();
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

    public static void addScoringRuleChildren(ScoringRuleContext rule) throws Exception {
        addBaseScoringContexts(rule.getId(), rule.getBaseScoringContexts());
    }

    private static Map<BaseScoringContext.Type, List<BaseScoringContext>> getScoringMap(List<BaseScoringContext> baseScoringContexts) {
        Map<BaseScoringContext.Type, List<BaseScoringContext>> map = new HashMap<>();
        for (BaseScoringContext baseScoringContext : baseScoringContexts) {
            List<BaseScoringContext> list = map.get(baseScoringContext.getType());
            if (list == null) {
                list = new ArrayList<>();
                map.put(baseScoringContext.getTypeEnum(), list);
            }
            list.add(baseScoringContext);
        }
        return map;
    }

    private static void addBaseScoringContexts(long ruleId, List<BaseScoringContext> baseScoringContexts) throws Exception {
        Map<BaseScoringContext.Type, List<BaseScoringContext>> map = getScoringMap(baseScoringContexts);
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
                scoringContext.setScoringRuleId(ruleId);
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
        }
    }

    private static void deleteScoringContext(long ruleId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getScoringRuleModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("SCORING_RULE_ID", "scoringRuleId", String.valueOf(ruleId), NumberOperators.EQUALS));
        builder.delete();
    }

    public static void updateScoringRule(ScoringRuleContext rule) throws Exception {
        deleteScoringContext(rule.getId());
        addScoringRuleChildren(rule);

        updateWorkflowRuleWithChildren(rule);
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

    public static List<BaseScoringContext> getBaseScoringContexts(List<Long> ruleIds) throws Exception {
        List<BaseScoringContext> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ruleIds)) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getBaseScoringModule().getTableName())
                    .select(FieldFactory.getBaseScoringFields())
                    .andCondition(CriteriaAPI.getCondition("SCORING_RULE_ID", "scoringRuleId", StringUtils.join(ruleIds, ","), NumberOperators.EQUALS));
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
                            Map<Long, Criteria> criteriaMap = CriteriaAPI.getCriteriaAsMap(conditionedList.stream()
                                    .filter(conditioned -> conditioned.getCriteriaId() > 0)
                                    .map(ConditionScoringContext::getCriteriaId).collect(Collectors.toList()));
                            for (ConditionScoringContext conditioned : conditionedList) {
                                if (conditioned.getCriteriaId() > 0) {
                                    conditioned.setCriteria(criteriaMap.get(conditioned.getCriteriaId()));
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

            List<BaseScoringContext> baseScoringContexts = getBaseScoringContexts(ruleIds);
            Map<Long, List<BaseScoringContext>> map = new HashMap<>();
            for (BaseScoringContext baseScoringContext : baseScoringContexts) {
                List<BaseScoringContext> list = map.get(baseScoringContext.getScoringRuleId());
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(baseScoringContext.getScoringRuleId(), list);
                }
                list.add(baseScoringContext);
            }

            for (WorkflowRuleContext workflowRuleContext : workflows) {
                if (workflowRuleContext instanceof ScoringRuleContext) {
                    ScoringRuleContext scoringRuleContext = (ScoringRuleContext) workflowRuleContext;
                    scoringRuleContext.setBaseScoringContexts(map.get(scoringRuleContext.getId()));
                }
            }
        }
    }
}
