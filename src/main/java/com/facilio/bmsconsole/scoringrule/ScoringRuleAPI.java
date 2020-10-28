package com.facilio.bmsconsole.scoringrule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ScoringRuleAPI extends WorkflowRuleAPI {

    public static void validateRule(ScoringRuleContext rule) {
        if (rule.getScoreFieldId() == -1) {
            throw new IllegalArgumentException("Scoring field cannot be empty");
        }

        if (!rule.isDraft()) {
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
    }

    public static void addScoringRuleChildren(ScoringRuleContext rule) throws Exception {
        addBaseScoringContexts(rule.getId(), rule.getScoringCommitmentContexts());
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

    private static void addBaseScoringContexts(long ruleId, List<ScoringCommitmentContext> scoringCommitmentContexts) throws Exception {
        for (int i = 0; i < scoringCommitmentContexts.size(); i++) {
            ScoringCommitmentContext scoringCommitmentContext = scoringCommitmentContexts.get(i);
            scoringCommitmentContext.setOrder(i + 1);
            scoringCommitmentContext.setScoringRuleId(ruleId);

            if (scoringCommitmentContext.getNamedCriteria() != null) {
                scoringCommitmentContext.getNamedCriteria().validate();
                long l = NamedCriteriaAPI.addNamedCriteria(scoringCommitmentContext.getNamedCriteria());
                scoringCommitmentContext.setNamedCriteriaId(l);
            }

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
        deleteScoringContext(rule.getId());
        addScoringRuleChildren(rule);

        ScoringRuleContext oldRule = (ScoringRuleContext) getWorkflowRule(rule.getId());
        rule.setScoreFieldId(oldRule.getScoreFieldId());
        rule.setScoreField(oldRule.getScoreField());

        if (!oldRule.isDraft()) {
            if (rule.isDraft()) {
                throw new IllegalArgumentException("Cannot draft published scoring rule");
            }
        }

        validateRule(rule);
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

//    public static void addActualScore(List<Map<String, Object>> scores, long recordId, long recordModuleId) throws Exception {
//        if (CollectionUtils.isEmpty(scores)) {
//            return;
//        }
//
//        List<Long> baseScoreIds = scores.stream().map(score -> (Long) score.get("baseScoreId"))
//                .collect(Collectors.toList());
//        if (CollectionUtils.isNotEmpty(baseScoreIds)) {
//            GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
//                    .table(ModuleFactory.getActualScoreModule().getTableName())
//                    .andCondition(CriteriaAPI.getCondition("BASE_SCORE_ID", "baseScoreId", StringUtils.join(baseScoreIds, ","), NumberOperators.EQUALS))
//                    .andCondition(CriteriaAPI.getCondition("RECORD_MODULEID", "recordModuleId", String.valueOf(recordModuleId), NumberOperators.EQUALS))
//                    .andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", String.valueOf(recordId), NumberOperators.EQUALS));
//            deleteBuilder.delete();
//
//            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
//                    .table(ModuleFactory.getActualScoreModule().getTableName())
//                    .fields(FieldFactory.getActualScoreFields());
//            builder.addRecords(scores);
//            builder.save();
//        }
//    }

//    public static Map<Long, Map<Long, Float>> getActualScore(List<Long> recordIds, long recordModuleId, List<Long> baseScoreIds) throws Exception {
//        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
//                .table(ModuleFactory.getActualScoreModule().getTableName())
//                .select(FieldFactory.getActualScoreFields())
//                .andCondition(CriteriaAPI.getCondition("BASE_SCORE_ID", "baseScoreId", StringUtils.join(baseScoreIds, ","), NumberOperators.EQUALS))
//                .andCondition(CriteriaAPI.getCondition("RECORD_MODULEID", "recordModuleId", String.valueOf(recordModuleId), NumberOperators.EQUALS))
//                .andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
//        List<Map<String, Object>> props = builder.get();
//        Map<Long, Map<Long, Float>> scoreMap = new HashMap<>();
//        for (Map<String, Object> prop : props) {
//            Long recordId = (Long) prop.get("recordId");
//            Map<Long, Float> score = scoreMap.get(recordId);
//            if (score == null) {
//                score = new HashMap<>();
//                scoreMap.put(recordId, score);
//            }
//            score.put((Long) prop.get("baseScoreId"), ((Number) prop.get("score")).floatValue());
//        }
//        return scoreMap;
//    }

//    public static void updateParentScores(ModuleBaseWithCustomFields moduleRecord, long scoreFieldId, boolean isDirty) throws Exception {
//        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
//                .table(ModuleFactory.getNodeScoringModule().getTableName())
//                .innerJoin(ModuleFactory.getBaseScoringModule().getTableName())
//                .on(ModuleFactory.getNodeScoringModule().getTableName() + ".ID = " + ModuleFactory.getBaseScoringModule().getTableName() + ".ID")
//                .select(FieldFactory.getNodeScoringFields())
//                .andCondition(CriteriaAPI.getCondition("SCORING_FIELD_ID", "scoringFieldID", String.valueOf(scoreFieldId), NumberOperators.EQUALS));
//        List<NodeScoringContext> nodeScoringList = FieldUtil.getAsBeanListFromMapList(builder.get(), NodeScoringContext.class);
//        if (CollectionUtils.isNotEmpty(nodeScoringList)) {
//            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//            Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
//            for (NodeScoringContext nodeScore : nodeScoringList) {
//                ScoringRuleContext workflowRule = (ScoringRuleContext) WorkflowRuleAPI.getWorkflowRule(nodeScore.getScoringRuleId());
//                List<BaseScoringContext> baseScoringContexts = workflowRule.getBaseScoringContexts();
//                List<Long> baseScoreIds = baseScoringContexts.stream().filter(baseScore -> baseScore.getId() != nodeScore.getId()).map(BaseScoringContext::getId).collect(Collectors.toList());
//
//                FacilioModule module = workflowRule.getModule();
//
//                switch (nodeScore.getNodeTypeEnum()) {
//                    case CURRENT_MODULE:
//                        // don't do anything
//                        break;
//
//                    case PARENT_MODULE: {
//                        long fieldId = nodeScore.getFieldId();
//                        LookupField field = (LookupField) modBean.getField(fieldId, nodeScore.getFieldModuleId());
//
//                        Object value = FieldUtil.getValue(moduleRecord, field);
//                        if (value instanceof ModuleBaseWithCustomFields) {
//                            ModuleBaseWithCustomFields emptyParentRecord = (ModuleBaseWithCustomFields) value;
//                            SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>()
//                                    .module(module)
//                                    .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
//                                    .select(modBean.getAllFields(module.getName()))
//                                    .andCondition(CriteriaAPI.getIdCondition(emptyParentRecord.getId(), module));
//                            ModuleBaseWithCustomFields parentRecord = selectBuilder.fetchFirst();
//                            if (parentRecord != null) {
//                                Map<Long, Map<Long, Float>> actualScore = getActualScore(Collections.singletonList(parentRecord.getId()), parentRecord.getModuleId(), baseScoreIds);
//                                setBaseScoreValues(actualScore.get(parentRecord.getId()), workflowRule.getBaseScoringContexts(), nodeScore, isDirty);
//
//                                Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), parentRecord, placeHolders);
//                                WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, module.getName(), parentRecord, null, recordPlaceHolders, null);
//                            }
//                        }
//                        break;
//                    }
//
//                    case SUB_MODULE: {
//                        long fieldId = nodeScore.getFieldId();
//                        LookupField field = (LookupField) modBean.getField(fieldId, nodeScore.getFieldModuleId());
//
//                        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>()
//                                .module(module)
//                                .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
//                                .select(modBean.getAllFields(module.getName()))
//                                .andCondition(CriteriaAPI.getCondition(field, String.valueOf(moduleRecord.getId()), NumberOperators.EQUALS));
//                        List<? extends ModuleBaseWithCustomFields> records = selectBuilder.get();
//                        if (CollectionUtils.isNotEmpty(records)) {
//                            List<Long> recordIds = records.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());
//                            Map<Long, Map<Long, Float>> actualScoreMap = getActualScore(recordIds, module.getModuleId(), baseScoreIds);
//
//                            for (ModuleBaseWithCustomFields record : records) {
//                                Map<Long, Float> scoreMap = actualScoreMap.get(record.getId());
//                                setBaseScoreValues(scoreMap, workflowRule.getBaseScoringContexts(), nodeScore, isDirty);
//
//                                Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), record, placeHolders);
//                                WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, module.getName(), record, null, recordPlaceHolders, null);
//                            }
//                        }
//                        break;
//                    }
//                }
//            }
//        }
//    }

//    private static void setBaseScoreValues(Map<Long, Float> scoreMap, List<BaseScoringContext> baseScoringContexts, NodeScoringContext nodeScore, boolean isDirty) {
//        for (BaseScoringContext baseScoringContext : baseScoringContexts) {
//            if (scoreMap == null || !(scoreMap.containsKey(baseScoringContext.getId()))) {
//                baseScoringContext.setDirty(true);
//                continue;
//            }
//
//            if (baseScoringContext.equals(nodeScore) && isDirty) {
//                baseScoringContext.setDirty(true);
//                continue;
//            }
//
//            baseScoringContext.setDirty(false);
//            baseScoringContext.setScore(scoreMap.get(baseScoringContext.getId()));
//        }
//    }

    public static void deleteField(ScoringRuleContext rule) throws Exception {
        long scoreFieldId = rule.getScoreFieldId();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = modBean.getModule(rule.getModuleId());

        // clear all data in that field
        UpdateRecordBuilder<? extends ModuleBaseWithCustomFields> builder = new UpdateRecordBuilder<>()
                .module(module)
                .fields(Collections.singletonList(rule.getScoreField()));
        Map<String, Object> map = new HashMap<>();
        map.put(rule.getScoreField().getName(), -99);
        builder.updateViaMap(map);

        modBean.deleteField(scoreFieldId);
    }
}
