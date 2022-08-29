package com.facilio.qa.rules.pojo;

import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.client.answers.MatrixAnswerContext;
import com.facilio.qa.context.questions.MatrixQuestionColumn;
import com.facilio.qa.context.questions.MultiQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionRow;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum MultiQuestionRuleHandler implements RuleHandler {

    MULTI_QUESTION;

    @Override
    public List<Map<String, Object>> emptyRuleConditions(QAndARuleType type, QuestionContext question) throws Exception{

        MultiQuestionContext mq = (MultiQuestionContext) question;
        List<Map<String, Object>> props = new ArrayList<>();
        emptyCondition(mq,props);
        return props;
    }

    private void emptyCondition(MultiQuestionContext mq, List<Map<String, Object>> props){

        for(MatrixQuestionColumn column : mq.getColumns()){
            addRowColumns(column, props, mq.getRow());
        }
    }

    private void addRowColumns(MatrixQuestionColumn matrixColumns, List<Map<String, Object>> props, MatrixQuestionRow matrixQuestionRow){
        Map<String,Object> prop = new HashMap<>();
        prop.put("row", matrixQuestionRow);
        prop.put("column", matrixColumns);
        props.add(prop);
    }

    @Override
    public List<Map<String, Object>> serializeConditions(QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception{

        MultiQuestionContext mq = (MultiQuestionContext) question;
        MatrixQuestionRow row = mq.getRow();
        Map<Long, List<RuleCondition>> conditionMap = conditions.stream().collect(Collectors.groupingBy(RuleCondition::getColumnId));
        List<Map<String, Object>> props = new ArrayList<>();

        for (MatrixQuestionColumn column :mq.getColumns()) {
            List<RuleCondition> ruleConditions = conditionMap.get(column.getId()); //ID cannot be null
            if (CollectionUtils.isNotEmpty(ruleConditions)) {
                for (RuleCondition condition : ruleConditions) {
                    Map<String, Object> prop = FieldUtil.getAsProperties(condition);
                    props.add(prop);
                }
            } else {
                addRowColumns(column, props, row);
            }
        }

        return props;
    }

    @Override
    public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception{

        MultiQuestionContext mq = (MultiQuestionContext) question;
        MatrixQuestionRow row = mq.getRow();
        List<RuleCondition> conditions = new ArrayList<>();
        for (Map<String, Object> prop : conditionProps) {
            String value = prop.remove("value").toString();
            String operatorId = (String) prop.remove("operatorId");
            RuleCondition condition = FieldUtil.getAsBeanFromMap(prop, type.getRuleConditionClass());
            condition.setOperator(Integer.parseInt(operatorId));
            condition.setValue(value);
            conditions.add(condition);
        }
        return conditions;
    }

    @Override
    public List<Map<String, Object>> constructAnswersForEval(QAndARuleType type, QuestionContext question, AnswerContext answer) throws Exception{

        FacilioUtil.throwIllegalArgumentException(answer.getMatrixAnswer() == null, MessageFormat.format("At least one option needs to be present for rule evaluation of question : {0}. This is not supposed to happen", question.getId()));
        List<Map<String, Object>> answerProps = new ArrayList<>();
        MatrixAnswerContext.MatrixAnswer matrixAnswer = answer.getMatrixAnswer();
        for(MatrixAnswerContext.RowAnswer option :matrixAnswer.getRowAnswer()){
            List<MatrixAnswerContext.ColumnAnswer> columnAnswers = option.getColumnAnswer();
            List<MatrixAnswerContext.ColumnAnswer> columnAns = columnAnswers.stream().filter(columnAnswer -> columnAnswer.getAnswer()!=null).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(columnAns)){
                columnAns.forEach(columnAnswer -> {
                    Map<String,Object> prop = new HashMap<>();
                    prop.put("rowId",option.getRow());
                    prop.put("columnId", columnAnswer.getColumn());
                    prop.put(RuleCondition.ANSWER_FIELD_NAME,columnAnswer.getAnswer());
                    answerProps.add(prop);
                });
            }
        }

        return answerProps;

    }

    @Override
    public boolean evalMisc(RuleCondition ruleCondition, Map<String, Object> answerProp){

        if(MapUtils.isNotEmpty(answerProp)){

            long columnId = (long) answerProp.getOrDefault("columnId",-1);

            return ((columnId > 0 ) && (ruleCondition.getColumnId() == columnId));
        }
        return false;
    }
}
