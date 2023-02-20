package com.facilio.readingrule.rca.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.RCAConditionScoreContext;
import com.facilio.readingrule.rca.context.RCAGroupContext;
import com.facilio.readingrule.rca.context.RCAScoreReadingContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.time.DateRange;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.google.common.collect.Lists;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ReadingRuleRcaAPI {

    public static void createRCA(NewReadingRuleContext rule) throws Exception {
        if (rule.getRca() != null) {
            ReadingRuleRCAContext rca = rule.getRca();
            FacilioModule rcaModule = Constants.getModBean().getModule(FacilioConstants.ReadingRules.RCA.RCA_MODULE);
            Map<String, Object> rcaProps = new HashMap<>();
            rcaProps.put("dataSetInterval", rca.getDataSetInterval());
            rcaProps.put("ruleInterval", rca.getRuleInterval());
            rcaProps.put("ruleId", rule.getId());

            FacilioContext ctx = V3Util.createRecord(rcaModule, rcaProps);

            List<ModuleBaseWithCustomFields> recordList = Constants.getRecordList(ctx);
            if (CollectionUtils.isNotEmpty(recordList)) {
                rca.setId(recordList.get(0).getId());
                rca.setRuleId(rule.getId());
            }
        }
    }

    public static void updateReadingRuleRca(ReadingRuleRCAContext rcaContext) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule rcaModule = modBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_MODULE);
        UpdateRecordBuilder<ReadingRuleRCAContext> updateRecordBuilder = new UpdateRecordBuilder<ReadingRuleRCAContext>()
                .module(rcaModule)
                .fields(modBean.getModuleFields(FacilioConstants.ReadingRules.RCA.RCA_MODULE))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(rcaModule), Collections.singleton(rcaContext.getId()), NumberOperators.EQUALS));

        updateRecordBuilder.update(rcaContext);
    }

    public static void deleteReadingRuleRca(Long ruleId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule rcaModule = modBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_MODULE);
        DeleteRecordBuilder<ReadingRuleRCAContext> deleteRecordBuilder = new DeleteRecordBuilder<ReadingRuleRCAContext>()
                .module(rcaModule)
                .andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(ruleId), NumberOperators.EQUALS));
        deleteRecordBuilder.delete();
    }

    public static ReadingRuleRCAContext getReadingRuleRca(Long ruleId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_MODULE);
        List<FacilioField> fields = modBean.getModuleFields(FacilioConstants.ReadingRules.RCA.RCA_MODULE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<ReadingRuleRCAContext> builder = new SelectRecordsBuilder<ReadingRuleRCAContext>()
                .select(fields)
                .module(module)
                .beanClass(ReadingRuleRCAContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("ruleId"), Collections.singleton(ruleId), NumberOperators.EQUALS));
        List<ReadingRuleRCAContext> contexts = builder.get();
        if (CollectionUtils.isNotEmpty(contexts)) {
            return contexts.get(0);
        }
        return null;
    }

    public static List<Map<String, Object>> getRcaMappingProps(Long rcaId, List<Long> rcaRuleIds) {
        List<Map<String, Object>> props = new ArrayList<>();
        rcaRuleIds.forEach(rcaRuleId -> {
            Map<String, Object> rcaMappingRecord = new HashMap<>();
            rcaMappingRecord.put("rcaId", rcaId);
            rcaMappingRecord.put("rcaRuleId", rcaRuleId);
            props.add(rcaMappingRecord);
        });
        return props;
    }

    public static ReadingRuleRCAContext getRcaForAlarm(Long alarmId) throws Exception {
        ReadingAlarm parentAlarm = (ReadingAlarm) NewAlarmAPI.getAlarm(alarmId);
        ReadingRuleRCAContext rca = getReadingRuleRca(parentAlarm.getRule().getId());
        return rca;
    }

    public static List<Long> getRcaMappingForAlarm(Long alarmId) throws Exception {

        ReadingAlarm parentAlarm = (ReadingAlarm) NewAlarmAPI.getAlarm(alarmId);
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE)) {
            ReadingRuleRCAContext rca = getReadingRuleRca(parentAlarm.getRule().getId());
            if (rca != null) {
                return getRcaMappingRuleIds(rca.getId(), true);
            }
        } else {
            return getRcaMappingRuleIds(parentAlarm.getRule().getId(), false);
        }

        return new ArrayList<>();
    }

    public static List<Long> getRcaMappingRuleIds(Long rcaOrRuleId, Boolean isNewRule) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
        FacilioModule module;
        List<FacilioField> fields;
        Map<String, FacilioField> fieldsMap;
        List<Long> rcaIds = new ArrayList<>();

        if (isNewRule) {
            module = ModuleFactory.getReadingRuleRCAMapping();
            fields = FieldFactory.getReadingRuleRCAMapping();

            builder.table(module.getTableName())
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition("RCA_ID", "rcaId", String.valueOf(rcaOrRuleId), NumberOperators.EQUALS));
            List<Map<String, Object>> props = builder.get();
            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> prop : props) {
                    rcaIds.add((Long) prop.get("rcaRuleId"));
                }
                return rcaIds;
            }
        } else {
            module = ModuleFactory.getWorkflowRuleRCAMapping();
            fields = FieldFactory.getWorkflowRuleRCAMapping();
            fieldsMap = FieldFactory.getAsMap(fields);
            builder.table("Workflow_RCA_Mapping")
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("rule"), String.valueOf(rcaOrRuleId), NumberOperators.EQUALS))
                    .select(FieldFactory.getField("rcaRule", "RCA_RULE_ID", module, FieldType.NUMBER));

            List<Map<String, Object>> props = builder.get();
            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> prop : props) {
                    rcaIds.add((Long) prop.get("rcaRule"));
                }
                return rcaIds;
            }
        }
        return new ArrayList<>();
    }

    public static void insertRcaMapping(List<Map<String, Object>> props) throws SQLException {
        FacilioModule rcaMappingModule = ModuleFactory.getReadingRuleRCAMapping();
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(rcaMappingModule.getTableName())
                .fields(FieldFactory.getReadingRuleRCAMapping())
                .addRecords(props);
        insertRecordBuilder.save();
    }

    public static void deleteRcaMapping(Long rcaId, List<Long> ids) throws Exception {
        FacilioModule rcaMappingModule = ModuleFactory.getReadingRuleRCAMapping();
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(rcaMappingModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("RCA_ID", "rcaId", String.valueOf(rcaId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("RCA_RULE_ID", "rcaRuleId", getRcaRuleIdList(ids), NumberOperators.EQUALS));
        deleteRecordBuilder.delete();
    }

    private static String getRcaRuleIdList(List<Long> ids) {
        StringJoiner stringBuilder = new StringJoiner(",");
        ids.forEach(id -> stringBuilder.add(String.valueOf(id)));
        return stringBuilder.toString();
    }

    public static void updateRcaGroup(RCAGroupContext rcaGroup) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule rcaGroupModule = modBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_GROUP_MODULE);
        UpdateRecordBuilder<RCAGroupContext> updateRecordBuilder = new UpdateRecordBuilder<RCAGroupContext>()
                .module(rcaGroupModule)
                .fields(modBean.getModuleFields(FacilioConstants.ReadingRules.RCA.RCA_GROUP_MODULE))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(rcaGroupModule), Collections.singleton(rcaGroup.getId()), NumberOperators.EQUALS));

        updateRecordBuilder.update(rcaGroup);
    }

    public static List<RCAGroupContext> getRcaGroupsForAlarm(Long alarmId) throws Exception {
        ReadingRuleRCAContext rca = getRcaForAlarm(alarmId);
        if (rca != null) {
            Long rcaId = rca.getId();
            ModuleBean modBean = Constants.getModBean();
            FacilioModule rcaGroupModule = modBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_GROUP_MODULE);
            SelectRecordsBuilder<RCAGroupContext> builder = new SelectRecordsBuilder<RCAGroupContext>()
                    .module(rcaGroupModule)
                    .beanClass(RCAGroupContext.class)
                    .select(Arrays.asList(FieldFactory.getIdField(rcaGroupModule)))
                    .andCondition(CriteriaAPI.getCondition("RCA_ID", "rcaId", String.valueOf(rcaId), NumberOperators.EQUALS));
            List<RCAGroupContext> rcaGroups = builder.get();
            if (CollectionUtils.isNotEmpty(rcaGroups)) {
                return rcaGroups;
            }
        }
        return new ArrayList<>();
    }

    public static void insertRcaGroups(List<RCAGroupContext> rcaGroups) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule rcaGroupModule = modBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_GROUP_MODULE);
        InsertRecordBuilder<RCAGroupContext> insertRecordBuilder = new InsertRecordBuilder<RCAGroupContext>()
                .module(rcaGroupModule)
                .fields(modBean.getModuleFields(FacilioConstants.ReadingRules.RCA.RCA_GROUP_MODULE))
                .addRecords(rcaGroups);
        insertRecordBuilder.save();
    }

    public static void deleteRcaGroups(List<Long> rcaGroupIds) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule rcaGroupModule = modBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_GROUP_MODULE);
        DeleteRecordBuilder<RCAGroupContext> deleteRecordBuilder = new DeleteRecordBuilder<RCAGroupContext>()
                .module(rcaGroupModule)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(rcaGroupModule), rcaGroupIds, NumberOperators.EQUALS));
        deleteRecordBuilder.delete();
    }

    public static List<Long> removeIntersection(List<Long> toBeAdded, List<Long> toBeDeleted) {
        List<Long> intersection = new ArrayList<>(toBeAdded);
        intersection.retainAll(toBeDeleted);
        toBeAdded.removeAll(intersection); // to be added new
        toBeDeleted.removeAll(intersection); // to be deleted
        return intersection;
    }

    public static void insertRcaScoreConditionsForGroup(RCAGroupContext group) throws Exception {
        List<RCAConditionScoreContext> conditions = group.getConditions();
        if (CollectionUtils.isNotEmpty(conditions)) {
            for (RCAConditionScoreContext rcaCond : conditions) {
                V3Util.throwRestException(rcaCond.getScore() == null, ErrorCode.RESOURCE_NOT_FOUND, group.getName() + "'s Score cannot be null");
                Long criteriaId = createCriteria(rcaCond.getCriteria());
                rcaCond.setCriteriaId(criteriaId);
                rcaCond.setGroupId(group.getId());
            }
            List<Map<String, Object>> conditionsMap = FieldUtil.getAsMapList(conditions, RCAConditionScoreContext.class);

            FacilioContext ctx = V3Util.createRecordList(Constants.getModBean().getModule(FacilioConstants.ReadingRules.RCA.RCA_SCORE_CONDITION_MODULE), conditionsMap, null, null);

            List<ModuleBaseWithCustomFields> recordList = Constants.getRecordList(ctx);
            if (CollectionUtils.isNotEmpty(recordList)) {
                System.out.println(recordList);
            }
        }

    }

    public static void insertRcaScoreConditions(List<RCAConditionScoreContext> rcaScoreConditions) throws
            Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule rcaScoreConditionModule = modBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_SCORE_CONDITION_MODULE);
        InsertRecordBuilder<RCAConditionScoreContext> insertRecordBuilder = new InsertRecordBuilder<RCAConditionScoreContext>()
                .module(rcaScoreConditionModule)
                .fields(modBean.getModuleFields(FacilioConstants.ReadingRules.RCA.RCA_SCORE_CONDITION_MODULE))
                .addRecords(rcaScoreConditions);
        insertRecordBuilder.save();
    }

    public static void updateRcaScoreCondition(RCAConditionScoreContext rcaScoreConditions) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule rcaScoreConditionModule = modBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_SCORE_CONDITION_MODULE);
        UpdateRecordBuilder<RCAConditionScoreContext> updateRecordBuilder = new UpdateRecordBuilder<RCAConditionScoreContext>()
                .module(rcaScoreConditionModule)
                .fields(modBean.getModuleFields(FacilioConstants.ReadingRules.RCA.RCA_SCORE_CONDITION_MODULE))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(rcaScoreConditionModule), Collections.singleton(rcaScoreConditions.getId()), NumberOperators.EQUALS));

        updateRecordBuilder.update(rcaScoreConditions);
    }

    public static void deleteRcaScoreCondition(List<RCAConditionScoreContext> rcaScoreConditions) throws
            Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule rcaScoreConditionModule = modBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_SCORE_CONDITION_MODULE);
        DeleteRecordBuilder<RCAConditionScoreContext> deleteRecordBuilder = new DeleteRecordBuilder<RCAConditionScoreContext>()
                .module(rcaScoreConditionModule)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(rcaScoreConditionModule), rcaScoreConditions.stream().map(x -> x.getId()).collect(Collectors.toList()), NumberOperators.EQUALS));
        deleteRecordBuilder.delete();
    }

    public static Long createCriteria(Criteria criteria) throws Exception {
        V3Util.throwRestException(criteria == null, ErrorCode.RESOURCE_NOT_FOUND, "Criteria cannot be null");
        if (StringUtils.isEmpty(criteria.getPattern())) {
            criteria.setPattern("1");
        }
        return CriteriaAPI.addCriteria(criteria);
    }

    private static void setModuleNameForCriteria(Criteria criteria) {
        Map<String, Condition> conditions = criteria.getConditions();
        for (Map.Entry<String, Condition> entry : conditions.entrySet()) {
            entry.getValue().setModuleName(FacilioConstants.ContextNames.NEW_READING_ALARM);
        }
    }

    public static void prepareGroupForInsert(RCAGroupContext group, Long rcaId) throws Exception {
        setModuleNameForCriteria(group.getCriteria());
        Long criteriaId = ReadingRuleRcaAPI.createCriteria(group.getCriteria());
        group.setCriteriaId(criteriaId);
        group.setRcaId(rcaId);
        group.setScoreRange(-1L);
        group.setScoreType(RCAGroupContext.ScoreType.RANGE);
    }

    public static void updateCriteriaOfGroup(RCAGroupContext group) throws Exception {
        CriteriaAPI.deleteCriteria(group.getCriteriaId());
        setModuleNameForCriteria(group.getCriteria());
        Long criteriaId = createCriteria(group.getCriteria());
        group.setCriteriaId(criteriaId);
    }

    public static List<RCAScoreReadingContext> getRcaReadingsForAlarm(Long alarmId, DateRange dateRange) throws
            Exception {
        return getRcaReadingsForAlarm(alarmId, dateRange, 0, 50, null);
    }

    public static List<RCAScoreReadingContext> getRcaReadingsForAlarm(Long alarmId, DateRange dateRange, Integer
            offset, Integer perPage, Criteria filterCriteria) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule rcaReadingsModule = moduleBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS_MODULE);
        List<FacilioField> fields = moduleBean.getModuleFields(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS_MODULE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        FacilioField score = fieldsMap.get("score");
        List<FacilioField> selectFields = fields.stream().filter((field) -> !Objects.equals(field.getName(), "score")).collect(Collectors.toList());
        SelectRecordsBuilder<RCAScoreReadingContext> builder = new SelectRecordsBuilder<RCAScoreReadingContext>()
                .module(rcaReadingsModule)
                .beanClass(RCAScoreReadingContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(alarmId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("ttime"), dateRange.getStartTime() + "," + dateRange.getEndTime(), DateOperators.BETWEEN))
                .aggregate(BmsAggregateOperators.NumberAggregateOperator.AVERAGE, score)
                .groupBy(fieldsMap.get("rcaFault").getCompleteColumnName())
                .orderBy("score desc")
                .offset(offset)
                .limit(perPage);

        builder.select(selectFields);

        if (filterCriteria != null)
            builder.andCriteria(filterCriteria);

        List<RCAScoreReadingContext> rcaScoreReadingContexts = builder.get();
        if (CollectionUtils.isNotEmpty(rcaScoreReadingContexts)) {
            return rcaScoreReadingContexts;
        }
        return new ArrayList<>();
    }

    public static Long getRcaReadingsCount(Long alarmId, Criteria filterCriteria, DateRange dateRange) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule rcaReadingsModule = moduleBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS_MODULE);
        List<FacilioField> fields = moduleBean.getModuleFields(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS_MODULE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        FacilioField distinctRcaFaultField = FieldFactory.getField("rcaFault", "DISTINCT RCA_FAULT_ID", FieldType.NUMBER);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(rcaReadingsModule.getTableName())
                .select(new ArrayList<>())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentId"), String.valueOf(alarmId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("ttime"), dateRange.getStartTime() + "," + dateRange.getEndTime(), DateOperators.BETWEEN))
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, distinctRcaFaultField);

        if (filterCriteria != null)
            builder.andCriteria(filterCriteria);

        Map<String, Object> props = builder.fetchFirst();
        return (Long) props.get("rcaFault");
    }

    public static DateRange getDateRange(Context context) {
        Integer dateOperatorInt = (Integer) context.get(FacilioConstants.ContextNames.DATE_OPERATOR);
        String dateOperatorValue = (String) context.get(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE);
        DateOperators operator;
        DateRange dateRange = null;
        if (dateOperatorInt != null && dateOperatorInt > -1) {
            operator = (DateOperators) Operator.getOperator(dateOperatorInt);
            dateRange = operator.getRange(dateOperatorValue);
        }
        return dateRange;
    }

    public static List<ReadingAlarm> getRCAReadingAlarms(List<Long> rcaRuleIds, Long resourceId, Long
            dataSetInterval) throws Exception {

        List<NewReadingRuleContext> rcaRules = NewReadingRuleAPI.getReadingRules(rcaRuleIds);
        AssetContext asset = AssetsAPI.getAssetInfo(resourceId);
        asset.setCategory(AssetsAPI.getCategoryForAsset(asset.getCategory().getId()));

        List<NewReadingRuleContext> sameCategoryRules = new ArrayList<>();
        List<NewReadingRuleContext> otherCategoryRules = new ArrayList<>();
        for (NewReadingRuleContext rule : rcaRules) {
            if (rule.getAssetCategory().getId() == asset.getCategory().getId()) {
                sameCategoryRules.add(rule);
            } else {
                otherCategoryRules.add(rule);
            }
        }

        List<ReadingAlarm> alarms = new ArrayList<>();
        alarms.addAll(fetchFaultsWithResourceIds(sameCategoryRules, Lists.newArrayList(resourceId), dataSetInterval));
        alarms.addAll(getOtherCategoryFaults(otherCategoryRules, resourceId, asset, dataSetInterval));
        return alarms;
    }

    private static List<ReadingAlarm> getOtherCategoryFaults(List<NewReadingRuleContext> rcaRules, Long
            resourceId, AssetContext asset, Long dataSetInterval) throws Exception {
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule fromModule = bean.getModule(asset.getCategory().getAssetModuleID());
        FacilioUtil.throwRunTimeException(fromModule == null, "RCA: from module cannot be empty!!");
        List<ReadingAlarm> alarms = new ArrayList<>();
        for (NewReadingRuleContext rule : rcaRules) {
            FacilioModule toModule = bean.getModule(rule.getAssetCategory().getAssetModuleID());
            FacilioUtil.throwRunTimeException(toModule == null, "RCA: to module cannot be empty!!");
            List<Long> resourceIds = fetchReverseParentIds(fromModule, toModule, resourceId);
            List<ReadingAlarm> readingAlarms = fetchFaultsWithResourceIds(Lists.newArrayList(rule), resourceIds, dataSetInterval);
            alarms.addAll(readingAlarms);

        }
        return alarms;
    }

    private static List<Long> fetchReverseParentIds(FacilioModule fromModule, FacilioModule toModule, Long parentId) throws
            Exception {
        List<Long> otherEndResourceIds = new ArrayList<>();
        List<RelationRequestContext> allRelations = RelationUtil.getAllRelations(fromModule, toModule);
        for (RelationRequestContext rel : allRelations) {
            JSONObject recordsWithRelationship = RelationUtil.getRecordsWithRelationship(rel.getReverseRelationLinkName(), rel.getToModuleName(), parentId, -1, -1);
            JSONObject data = (JSONObject) recordsWithRelationship.get("data");
            if (data != null) {
                List<Map> resources = (ArrayList<Map>) data.get(rel.getToModuleName());
                for (Map res : resources) {
                    otherEndResourceIds.add((Long) res.get("id"));
                }
            }
        }
        return otherEndResourceIds;
    }

    public static List<ReadingAlarm> fetchFaultsWithResourceIds
            (List<NewReadingRuleContext> rcaRules, List<Long> resourceIds, Long dataSetInterval) throws Exception {
        List<Long> rcaRuleIds = rcaRules.stream().map(rule -> rule.getId()).collect(Collectors.toList());
        if (org.apache.commons.collections.CollectionUtils.isEmpty(rcaRuleIds) || org.apache.commons.collections.CollectionUtils.isEmpty(resourceIds)) {
            return new ArrayList<>();
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule readingAlarmModule = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.NEW_READING_ALARM);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<ReadingAlarm> builder = new SelectRecordsBuilder<ReadingAlarm>()
                .select(fields)
                .beanClass(ReadingAlarm.class)
                .moduleName(readingAlarmModule.getName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("rule"), rcaRuleIds, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), resourceIds, NumberOperators.EQUALS));
        if (dataSetInterval != null)
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("lastOccurredTime"), String.valueOf(dataSetInterval), DateOperators.IS_AFTER));

        List<ReadingAlarm> readingAlarms = builder.get();
        return org.apache.commons.collections.CollectionUtils.isNotEmpty(readingAlarms) ? readingAlarms : new ArrayList<>();
    }

    public static boolean isMatchedWithCriteriaId(Long criteriaId, Long alarmId) throws Exception {
        Criteria criteria = CriteriaAPI.getCriteria(criteriaId);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule readingAlarmModule = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
        SelectRecordsBuilder<ReadingAlarm> builder = new SelectRecordsBuilder<ReadingAlarm>()
                .select(Collections.singleton(FieldFactory.getIdField(readingAlarmModule)))
                .beanClass(ReadingAlarm.class)
                .moduleName(readingAlarmModule.getName())
                .andCriteria(criteria)
                .andCondition(CriteriaAPI.getIdCondition(alarmId, readingAlarmModule));
        List<ReadingAlarm> readingAlarms = builder.get();
        return org.apache.commons.collections.CollectionUtils.isNotEmpty(readingAlarms);
    }
}

