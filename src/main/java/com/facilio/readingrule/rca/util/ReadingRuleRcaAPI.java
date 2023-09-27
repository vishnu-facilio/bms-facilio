package com.facilio.readingrule.rca.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
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
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.faultimpact.FaultImpactAPI;
import com.facilio.readingrule.rca.context.*;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.google.common.collect.Lists;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
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
                setModuleNameForCriteria(rcaCond.getCriteria());
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
                .groupBy(fieldsMap.get("rcaFaultId").getCompleteColumnName())
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

    public static boolean evaluateCriteria(Criteria criteria, Long alarmId, Long ruleId, Long resourceId, Long startTime, Long endTime) throws Exception {
        Map<String, Object> props = new HashMap<>();
        for (Condition condition : criteria.getConditions().values()) {
            RCAFieldType fieldType = RCAFieldType.fromString(condition.getFieldName());
            // to handle a case when criteria contains multiple conditions with the same field name
            condition.setFieldName(condition.getFieldName() + "_" + condition.getSequence());
            switch (fieldType) {
                case FAULT_TYPE:
                    props.put(condition.getFieldName(), getFaultType(resourceId, ruleId));
                    break;
                case READING_ALARM_ASSET_CATEGORY:
                    props.put(condition.getFieldName(), getAssetCategory(resourceId, ruleId));
                    break;
                case SEVERITY:
                    props.put(condition.getFieldName(), getSeverity(resourceId, ruleId));
                    break;
                case ENERGY_IMPACT:
                    props.put(condition.getFieldName(), FaultImpactAPI.getEnergyImpact(resourceId, ruleId, startTime, endTime));
                    break;
                case COST_IMPACT:
                    props.put(condition.getFieldName(), FaultImpactAPI.getCostImpact(resourceId, ruleId, startTime, endTime));
                    break;
                case NO_OF_OCCURRENCES:
                    props.put(condition.getFieldName(), getCountOrDurationValue(alarmId, startTime, endTime, false));
                    break;
                case NO_OF_EVENTS:
                    props.put(condition.getFieldName(), getEventCount(resourceId, ruleId, startTime, endTime));
                    break;
                case DURATION:
                    props.put(condition.getFieldName(), getCountOrDurationValue(alarmId, startTime, endTime, true));
                    break;
            }
        }
        return criteria.computePredicate(null).evaluate(props);
    }

    private static Long getCountOrDurationValue(Long alarmId, Long startTime, Long endTime, boolean fetchDuration) throws Exception {
        List<Map<String, Object>> list = getAlarmDurationAndCount(Collections.singletonList(alarmId), startTime, endTime);
        Long value = null;
        if (CollectionUtils.isNotEmpty(list)) {
            Optional<Long> optDuration = list.stream().map(prop -> fetchDuration ? ((BigDecimal) prop.get("duration")).longValue() : (Long) prop.get("count")).findFirst();
            value = optDuration.orElse(null);
        }
        return value;
    }

    /**
     * @param alarmIds
     * @param startTime
     * @param endTime
     * @return list that contains the number of alarms, alarmIds of the alarms and duration of alarms
     * <p> - occurring inside the interval between startTime and endTime </p>
     * <p> - starts occurrence outside the interval but gets cleared inside the interval </p>
     * <p> - starts occurrence outside the interval and occurs throughout the interval or gets cleared outside the interval </p>
     * @throws Exception
     * @Query: SUM(LEAST ( COALESCE ( CLEARED_TIME, LEAST ( CLEARED_TIME, endTime)),endTime)-GREATEST(CREATED_TIME,startTime)) AS `duration`,
     * COUNT(AlarmOccurrence.ID) AS `count`,
     * ALARM_ID AS `alarm`
     * FROM AlarmOccurrence WHERE ALARM_ID IN (alarmIds)
     * AND(
     * CREATED_TIME BETWEEN startTime AND endTime
     * OR CLEARED_TIME BETWEEN startTime AND endTime
     * OR (CREATED_TIME<=startTime AND (CLEARED_TIME IS NULL OR CLEARED_TIME>=endTime))
     * ) GROUP BY ALARM_ID
     */
    public static List<Map<String, Object>> getAlarmDurationAndCount(List<Long> alarmIds, Long startTime, Long endTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        String clearedTimeFieldColumn = fieldMap.get("clearedTime").getColumnName();
        String createdTimeFieldColumn = fieldMap.get("createdTime").getColumnName();

        Long currentTimeMillis = DateTimeUtil.utcTimeToOrgTime(System.currentTimeMillis());
        Long endLimit = endTime > currentTimeMillis ? currentTimeMillis : endTime;
        String durationAggrColumn = "SUM(LEAST(COALESCE(" +
                clearedTimeFieldColumn + "," +
                endLimit + ")," +
                endLimit +
                ") - " +
                "GREATEST(" +
                createdTimeFieldColumn + "," +
                startTime + ")" +
                ")";

        List<FacilioField> selectFields = new ArrayList<>();
        FacilioField durationField = FieldFactory.getField("duration", durationAggrColumn, FieldType.NUMBER);
        selectFields.add(durationField);
        selectFields.addAll(FieldFactory.getCountField(module));
        selectFields.add(fieldMap.get("alarm"));
        selectFields.add(fieldMap.get("resource"));


        Criteria clearTimeCriteria = new Criteria();
        clearTimeCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(startTime), NumberOperators.LESS_THAN_EQUAL));

        Criteria tempCriteria = new Criteria();
        tempCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("clearedTime"), "", CommonOperators.IS_EMPTY));
        tempCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("clearedTime"), String.valueOf(endTime), NumberOperators.GREATER_THAN_EQUAL));
        clearTimeCriteria.andCriteria(tempCriteria);


        Criteria timeCriteria = new Criteria();
        timeCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), startTime + "," + endTime, DateOperators.BETWEEN));
        timeCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("clearedTime"), startTime + "," + endTime, DateOperators.BETWEEN));
        timeCriteria.orCriteria(clearTimeCriteria);

        SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
                .select(selectFields)
                .beanClass(AlarmOccurrenceContext.class)
                .module(module)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"), alarmIds, NumberOperators.EQUALS))
                .andCriteria(timeCriteria)
                .groupBy(fieldMap.get("alarm").getCompleteColumnName() + ", " + fieldMap.get("resource").getCompleteColumnName());

        return builder.getAsProps();
    }

    public static Long getEventCount(Long resourceId, Long ruleId, Long startTime, Long endTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.READING_EVENT);
        FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
        FacilioField timeField = FieldFactory.getField("createdTime", "Created Time", "CREATED_TIME", eventModule, FieldType.NUMBER);
        SelectRecordsBuilder<ReadingEventContext> builder = new SelectRecordsBuilder<ReadingEventContext>()
                .select(FieldFactory.getCountField())
                .beanClass(ReadingEventContext.class)
                .module(module)
                .andCondition(CriteriaAPI.getCondition(eventModule.getTableName() + ".RESOURCE_ID", "resource", String.valueOf(resourceId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(module.getTableName() + ".RULE_ID", "rule", String.valueOf(ruleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(timeField, startTime + "," + endTime, DateOperators.BETWEEN));
        ReadingEventContext props = builder.fetchFirst();
        return (props == null) ? null : (Long) props.getData().get("count");
    }

    public static int getFaultType(Long resourceId, Long ruleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
        FacilioField field = FieldFactory.getField("faultType", "Fault Type", "FAULT_TYPE", module, FieldType.SYSTEM_ENUM);
        ReadingAlarm props = NewAlarmAPI.getReadingAlarm(Collections.singletonList(field), resourceId, ruleId);
        return (props == null) ? -1 : props.getFaultType();
    }

    public static Long getAssetCategory(Long resourceId, Long ruleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
        FacilioField field = FieldFactory.getField("readingAlarmAssetCategory", "AssetCategory", "ASSET_CATEGORY_ID", module, FieldType.LOOKUP);
        ReadingAlarm props = NewAlarmAPI.getReadingAlarm(Collections.singletonList(field), resourceId, ruleId);
        return (props == null) ? null : props.getReadingAlarmAssetCategory().getId();
    }

    public static Long getSeverity(Long resourceId, Long ruleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
        FacilioField field = FieldFactory.getField("severity", "Severity", "SEVERITY_ID", module, FieldType.LOOKUP);
        ReadingAlarm props = NewAlarmAPI.getReadingAlarm(Collections.singletonList(field), resourceId, ruleId);
        return (props == null) ? null : props.getSeverity().getId();
    }

    public static void setModuleNameForCriteria(Criteria criteria) {
        Map<String, Condition> conditions = criteria.getConditions();
        for (Map.Entry<String, Condition> entry : conditions.entrySet()) {
            RCAFieldType fieldType = RCAFieldType.fromString(entry.getValue().getFieldName());
            switch (fieldType) {
                case DURATION:
                case NO_OF_EVENTS:
                    entry.getValue().setModuleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
                    break;
                default:
                    entry.getValue().setModuleName(FacilioConstants.ContextNames.NEW_READING_ALARM);
                    break;
            }
        }
    }
}

