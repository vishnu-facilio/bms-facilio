package com.facilio.qa.rules.pojo;

import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.client.answers.MatrixAnswerContext;
import com.facilio.qa.context.questions.MatrixQuestionColumn;
import com.facilio.qa.context.questions.MatrixQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionRow;
import com.facilio.qa.context.questions.MultiQuestionContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum MatrixRuleHandler implements RuleHandler{
	MATRIX;

	@Override
	public List<Map<String, Object>> emptyRuleConditions(QAndARuleType type, QuestionContext question) throws Exception{
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<Map<String, Object>> serializeConditions(QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception{

		MatrixQuestionContext mq = (MatrixQuestionContext) question;
		Map<Long, List<RuleCondition>> conditionMap = conditions.stream().collect(Collectors.groupingBy(RuleCondition::getRowId));
		List<Map<String, Object>> props = new ArrayList<>();
		for (MatrixQuestionRow matrixQuestionRow :mq.getRows()) {
			List<RuleCondition> ruleConditions = conditionMap.get(matrixQuestionRow.getId()); //ID cannot be null
			for(RuleCondition condition:ruleConditions){
				if (condition != null) {
					Map<String, Object> prop = FieldUtil.getAsProperties(condition);
					props.add(prop);
				}
			}
		}

		return props;
	}

	@Override
	public List<RuleCondition> deserializeConditions(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception{

		MatrixQuestionContext mq = (MatrixQuestionContext) question;
		Map<Long, MatrixQuestionRow> options = mq.getRows().stream().collect(Collectors.toMap(MatrixQuestionRow::getId, Function.identity()));
		List<RuleCondition> conditions = new ArrayList<>();
		for (Map<String, Object> prop : conditionProps) {
			MatrixQuestionRow row = options.get(prop.get("rowId"));
			String value = prop.remove("value").toString();
			String operatorId = (String) prop.remove("operatorId");
			V3Util.throwRestException(row == null, ErrorCode.VALIDATION_ERROR, "Invalid option specified while adding MQ Rule");
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

			Long rowId = (Long) answerProp.getOrDefault("rowId",null);
			Long columnId = (Long) answerProp.getOrDefault("columnId",null);

			return (rowId !=null && columnId != null ) && (ruleCondition.getRowId() == rowId && ruleCondition.getColumnId() == columnId);
		}
		return false;
	}

	@Override
	public void beforeQuestionClone(QuestionContext question) throws Exception {
	}

	public void constructConditionsForClone(QAndARuleType type, QuestionContext question, List<Map<String, Object>> conditionProps) throws Exception {
	}
}
