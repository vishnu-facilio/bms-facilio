package com.facilio.qa.rules.pojo;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.EnumFieldValue;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.client.answers.MatrixAnswerContext;
import com.facilio.qa.context.questions.MatrixQuestionColumn;
import com.facilio.qa.context.questions.MatrixQuestionRow;
import com.facilio.qa.context.questions.MultiQuestionContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public enum MultiQuestionRuleHandler implements RuleHandler {

    MULTI_QUESTION;

    @Override
    public List<Map<String, Object>> emptyRuleConditions(QAndARuleType type, QuestionContext question) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Map<String, Object>> serializeConditions(QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception {

        MultiQuestionContext mq = (MultiQuestionContext) question;
        Map<Long, List<RuleCondition>> conditionMap = conditions.stream().collect(Collectors.groupingBy(RuleCondition::getColumnId));
        List<Map<String, Object>> props = new ArrayList<>();

        for (MatrixQuestionColumn column : mq.getColumns()) {
            List<RuleCondition> ruleConditions = conditionMap.get(column.getId()); //ID cannot be null
            if (CollectionUtils.isNotEmpty(ruleConditions)) {
                for (RuleCondition condition : ruleConditions) {
                    Map<String, Object> prop = FieldUtil.getAsProperties(condition);
                    props.add(prop);
                }
            }
        }

        return props;
    }

    @Override
    public void beforeQuestionClone(QuestionContext question) throws Exception {
        if (question != null) {
            MultiQuestionContext mq = (MultiQuestionContext) question;
            List<MatrixQuestionColumn> mqColumns = mq.getColumns();
            List<MatrixQuestionColumn> newColumns = new ArrayList<>();
            mqColumns.stream().forEach(column -> {
                MatrixQuestionColumn columnNew = new MatrixQuestionColumn();
                columnNew.setName(column.getName());
                if (column.getField().getDataType() == FieldType.ENUM.getTypeAsInt()){
                    EnumField enumField = (EnumField) column.getField();
                    EnumField enumFieldClone = new EnumField();
                    enumFieldClone.setDataType(FieldType.ENUM);
                    enumFieldClone.setDisplayName(column.getField().getDisplayName());
                    enumFieldClone.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
                    List<EnumFieldValue<Integer>> enumFieldValuesClone = new ArrayList<>();
                    for(EnumFieldValue fieldValue :enumField.getValues()){
                        EnumFieldValue enumFieldValueClone = new EnumFieldValue();
                        enumFieldValueClone.setValue(fieldValue.getValue());
                        enumFieldValueClone.setVisible(true);
                        enumFieldValuesClone.add(enumFieldValueClone);
                    }
                    enumFieldClone.setValues(enumFieldValuesClone);
                    columnNew.setField(enumFieldClone);
                } else {
                    FacilioField fieldNew = column.getField().clone();
                    fieldNew.setId(-1);
                    fieldNew.setSequenceNumber(-1);
                    fieldNew.setModule(null);
                    fieldNew.setColumnName(null);
                    columnNew.setField(fieldNew);
                }
                newColumns.add(columnNew);
            });
            mq.setColumns(newColumns);
        }
    }

    public void constructConditionsForClone(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
        MultiQuestionContext mq = (MultiQuestionContext) question;
        SelectRecordsBuilder<MatrixQuestionColumn> selectBuilder = new SelectRecordsBuilder<MatrixQuestionColumn>()
                .moduleName(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN)
                .beanClass(MatrixQuestionColumn.class)
                .select(Constants.getModBean().getAllFields(FacilioConstants.QAndA.Questions.MATRIX_QUESTION_COLUMN))
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", question.getClonedQuestionId() + "", NumberOperators.EQUALS));
        List<MatrixQuestionColumn> oldQuestionColumns = selectBuilder.get();
        Map<Long, Long> oldQuestionColumnIDVsFieldId = oldQuestionColumns.stream()
                .collect(Collectors.toMap(MatrixQuestionColumn::getId, MatrixQuestionColumn::getFieldId));
        Map<Long, Long> newQuestionFieldIdVsID = mq.getColumns().stream()
                .collect(Collectors.toMap(MatrixQuestionColumn::getClonedFieldId, MatrixQuestionColumn::getId));
        for (Map<String, Object> condition : conditionProps) {
            condition.put("id", null);
            if (oldQuestionColumnIDVsFieldId.containsKey(condition.get("columnId"))) {
                if (newQuestionFieldIdVsID.containsKey(oldQuestionColumnIDVsFieldId.get(condition.get("columnId")))) {
                    condition.put("columnId", newQuestionFieldIdVsID.get(oldQuestionColumnIDVsFieldId.get(condition.get("columnId"))));
                }
            }
        }
    }

    @Override
    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {

        List<RuleCondition> conditions = new ArrayList<>();
        for (Map<String, Object> prop : conditionProps) {
            String operatorId = null;
            String value = prop.remove("value").toString();
            if (prop.containsKey("operatorId")) {
                operatorId = (String) prop.remove("operatorId");
            }
            if (prop.containsKey("operator")) {
                operatorId = prop.remove("operator") + "";
            }
            RuleCondition condition = FieldUtil.getAsBeanFromMap(prop, type.getRuleConditionClass());
            condition.setOperator(Integer.parseInt(operatorId));
            condition.setValue(value);
            conditions.add(condition);
        }
        return conditions;
    }

    @Override
    public List<Map<String, Object>> constructAnswersForEval(QAndARuleType type, QuestionContext question, AnswerContext answer) throws Exception {

        FacilioUtil.throwIllegalArgumentException(answer.getMatrixAnswer() == null, MessageFormat.format("At least one option needs to be present for rule evaluation of question : {0}. This is not supposed to happen", question.getId()));
        List<Map<String, Object>> answerProps = new ArrayList<>();
        MatrixAnswerContext.MatrixAnswer matrixAnswer = answer.getMatrixAnswer();
        for (MatrixAnswerContext.RowAnswer option : matrixAnswer.getRowAnswer()) {
            List<MatrixAnswerContext.ColumnAnswer> columnAnswers = option.getColumnAnswer();
            List<MatrixAnswerContext.ColumnAnswer> columnAns = columnAnswers.stream().filter(columnAnswer -> columnAnswer.getAnswer() != null).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(columnAns)) {
                columnAns.forEach(columnAnswer -> {
                    Map<String, Object> prop = new HashMap<>();
                    prop.put("rowId", option.getRow());
                    prop.put("columnId", columnAnswer.getColumn());
                    prop.put(RuleCondition.ANSWER_FIELD_NAME, columnAnswer.getAnswer());
                    answerProps.add(prop);
                });
            }
        }

        return answerProps;

    }

    @Override
    public boolean evalMisc(RuleCondition ruleCondition, Map<String, Object> answerProp) {

        if (MapUtils.isNotEmpty(answerProp)) {

            long columnId = (long) answerProp.getOrDefault("columnId", -1);

            return ((columnId > 0) && (ruleCondition.getColumnId() == columnId));
        }
        return false;
    }
}
