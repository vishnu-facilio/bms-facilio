package com.facilio.qa.rules.bean;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringSystemEnumOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.RuleCondition;
import com.facilio.qa.rules.pojo.ScoringRule;
import com.facilio.util.FacilioUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QAndARuleBeanImpl implements QAndARuleBean {

    @Override
    public List<QAndARule> getRulesOfTemplate(long templateId) throws Exception {
        return getRulesOfTemplateOfType(templateId, QAndARuleType.WORKFLOW);
    }

    @Override
    public List<ScoringRule> getScoringRulesOfTemplate(long templateId) throws Exception {
        return getRulesOfTemplateOfType(templateId, QAndARuleType.SCORING);
    }

    @Override
    public <T extends QAndARule> List<T> getRulesOfTemplateOfType(long templateId, QAndARuleType type) throws Exception {
        return getRules(templateId, null, type);
    }

    @Override
    public <T extends QAndARule> List<T> getRulesOfQuestionsOfType(long templateId, Collection<Long> questionIds, QAndARuleType type) throws Exception {
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(questionIds), MessageFormat.format("Question Ids cannot be empty while fetching rules of type : {0} and template : {1}", type, templateId));
        return getRules(templateId, questionIds, type);
    }

    private <T extends QAndARule> List<T> getRules(long templateId, Collection<Long> questionIds, QAndARuleType type) throws Exception {
        FacilioUtil.throwIllegalArgumentException(templateId <= 0, "Invalid templateId while fetching rules");
        FacilioUtil.throwIllegalArgumentException(type == null, MessageFormat.format("Type cannot be empty while fetching rules of template : {1}", templateId));
        FacilioModule module = type.getRuleModule();
        List<FacilioField> fields = type.getRuleFields();
        Map<String, FacilioField> fieldMap = Constants.FieldFactory.getAsMap(fields);
        FacilioField templateIdField = fieldMap.get("templateId");
        FacilioField typeField = fieldMap.get("type");

        GenericSelectRecordBuilder selectBuilder = DBUtil.getSelectBuilderWithJoin(module, fields)
                .andCondition(CriteriaAPI.getCondition(typeField, type.name(), StringSystemEnumOperators.IS))
                .andCondition(CriteriaAPI.getCondition(templateIdField, String.valueOf(templateId), PickListOperators.IS))
                ;

        if (CollectionUtils.isNotEmpty(questionIds)) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("questionId"), questionIds, PickListOperators.IS));
        }

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<T> rules = FieldUtil.getAsBeanListFromMapList(props, type.getRuleClass());
            populateRuleConditions(rules, type);
            return rules;
        }
        return null;
    }

    private <T extends QAndARule> void populateRuleConditions (List<T> rules, QAndARuleType type) throws Exception {
        Map<Long, T> ruleMap = rules.stream().collect(Collectors.toMap(QAndARule::getId, Function.identity()));
        FacilioModule module = type.getRuleConditionsModule();
        List<FacilioField> fields = type.getRuleConditionFields();
        Map<String, FacilioField> fieldMap = Constants.FieldFactory.getAsMap(fields);
        FacilioField ruleIdField = fieldMap.get("ruleId");
        FacilioField sequenceField = fieldMap.get("sequence");

        GenericSelectRecordBuilder selectBuilder = DBUtil.getSelectBuilderWithJoin(module, fields)
                .andCondition(CriteriaAPI.getCondition(ruleIdField, ruleMap.keySet(), PickListOperators.IS))
                .orderBy(Stream.of(ruleIdField.getCompleteColumnName(), sequenceField.getCompleteColumnName()).collect(Collectors.joining(",")))
                ;

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<RuleCondition> ruleConditions = FieldUtil.getAsBeanListFromMapList(props, type.getRuleConditionClass());
            populateCriteria(ruleConditions);
            for (RuleCondition condition : ruleConditions) {
                QAndARule rule = ruleMap.get(condition.getRuleId());
                List<RuleCondition> conditions = Objects.requireNonNull(rule, "Rule cannot be null. This is not supposed to happen").getRuleConditions();
                if (conditions == null) {
                    conditions = new ArrayList<>();
                    rule.setRuleConditions(conditions);
                }
                conditions.add(condition);
            }
        }
    }

    private void populateCriteria (List<RuleCondition> conditions) throws Exception {
        List<Long> criteriaIds = conditions.stream().filter(RuleCondition::hasCriteria).map(RuleCondition::getCriteriaId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(criteriaIds)) {
            Map<Long, Criteria> criteriaMap = CriteriaAPI.getCriteriaAsMap(criteriaIds);
            conditions.stream().filter(RuleCondition::hasCriteria).forEach(c -> c.setCriteria(criteriaMap.get(c.getCriteriaId())));
        }
    }

    @Override
    public <T extends QAndARule> void addRules(List<T> rules, QAndARuleType type) throws Exception {
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(rules), "Rules cannot be empty while adding");
        rules.stream().forEach(r -> setDefaultProps(r, type, true));
        List<Map<String, Object>> props = FieldUtil.getAsMapList(rules, type.getRuleClass());
        DBUtil.insertValuesWithJoin(type.getRuleModule(), type.getRuleFields(), props);
        DBUtil.populateIdsInPojoAfterInsert(rules, props, QAndARule::setId);
    }

    private void setDefaultProps (QAndARule rule, QAndARuleType type, boolean isAdd) {
        rule.setSysModifiedTime(System.currentTimeMillis());
        rule.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
        if (isAdd) {
            rule.setType(type);
            rule.setSysCreatedTime(rule.getSysModifiedTime());
            rule.setSysCreatedBy(rule.getSysModifiedBy());
        }
    }

    @SneakyThrows
    private <T> GenericUpdateRecordBuilder.BatchUpdateByIdContext constructBatchUpdateObject (T record) {
        Map<String, Object> props = FieldUtil.getAsProperties(record);
        Long id = (Long) props.get("id");
        GenericUpdateRecordBuilder.BatchUpdateByIdContext batchUpdateObj = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
        batchUpdateObj.setWhereId(id);
        batchUpdateObj.setUpdateValue(props);
        return batchUpdateObj;
    }

    private <T> int updateRecords (FacilioModule module, List<FacilioField> fields, List<T> records) throws Exception {
        List<FacilioField> updateFields = new ArrayList<>(fields);
        updateFields.removeIf(f -> f.getDataTypeEnum() == FieldType.ID); // Removing other id fields;
        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdate = records.stream().map(this::constructBatchUpdateObject).collect(Collectors.toList());
        return DBUtil.getUpdateBuilderWithJoin(module, updateFields).batchUpdateById(batchUpdate);
    }

    @Override
    public <T extends QAndARule> int updateRules(List<T> rules, QAndARuleType type) throws Exception {
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(rules), "Rules cannot be empty while updating");
        rules.stream().forEach(r -> setDefaultProps(r, type, false));
        return updateRecords(type.getRuleModule(), type.getRuleFields(), rules);
    }

    @Override
    public <T extends QAndARule> int deleteRules(List<T> rules, QAndARuleType type) throws Exception {
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(rules), "Rules cannot be empty while deleting");
        return DBUtil.batchDeleteRecords(type.getRuleModule(), rules.stream().map(QAndARule::getId).collect(Collectors.toList()));
    }

    @Override
    public <C extends RuleCondition> void addConditions(List<C> conditions, QAndARuleType type) throws Exception {
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(conditions), "Rule Conditions cannot be empty while adding");
        conditions.stream().forEach(c -> setDefaultProps(c, true));
        List<Map<String, Object>> props = FieldUtil.getAsMapList(conditions, type.getRuleClass());
        DBUtil.insertValuesWithJoin(type.getRuleConditionsModule(), type.getRuleConditionFields(), props);
        DBUtil.populateIdsInPojoAfterInsert(conditions, props, RuleCondition::setId);
    }

    @Override
    public <C extends RuleCondition> int updateCondition(List<C> conditions, QAndARuleType type) throws Exception {
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(conditions), "Conditions cannot be empty while updating");
        conditions.stream().forEach(c -> setDefaultProps(c, false));
        return updateRecords(type.getRuleConditionsModule(), type.getRuleConditionFields(), conditions);
    }

    private void setDefaultProps (RuleCondition condition, boolean isAdd) {
        condition.setSysModifiedTime(System.currentTimeMillis());
        condition.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
        if (isAdd) {
            condition.setSysCreatedTime(condition.getSysModifiedTime());
            condition.setSysCreatedBy(condition.getSysModifiedBy());
        }
    }

    @Override
    public <C extends RuleCondition> int deleteConditions(List<C> conditions, QAndARuleType type) throws Exception {
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(conditions), "Conditions cannot be empty while deleting");
        int deleted = DBUtil.batchDeleteRecords(type.getRuleConditionsModule(), conditions.stream().map(RuleCondition::getId).collect(Collectors.toList()));
        CriteriaAPI.batchDeleteCriteria(conditions.stream().filter(RuleCondition::hasCriteria).map(RuleCondition::getCriteriaId).collect(Collectors.toList()));
        return deleted;
    }

}
