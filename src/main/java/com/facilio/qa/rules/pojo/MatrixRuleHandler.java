package com.facilio.qa.rules.pojo;

import com.facilio.modules.FieldUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.RuleHandler;
import com.facilio.qa.context.client.answers.MatrixAnswerContext;
import com.facilio.qa.context.questions.MatrixQuestionColumn;
import com.facilio.qa.context.questions.MatrixQuestionContext;
import com.facilio.qa.context.questions.MatrixQuestionRow;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.MapUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum MatrixRuleHandler implements RuleHandler{
	MATRIX;

	@Override
	public List<Map<String, Object>> emptyRuleConditions(QAndARuleType type, QuestionContext question) throws Exception{

		MatrixQuestionContext mq = (MatrixQuestionContext) question;
		List<Map<String, Object>> props = new ArrayList<>();
		emptyCondition(mq,props);
		return props;
	}

	private void emptyCondition(MatrixQuestionContext mq, List<Map<String, Object>> props){

		for(MatrixQuestionRow matrixQuestionRow : mq.getRows()){
			addRowColumns(mq.getColumns(), props,matrixQuestionRow);
		}
	}

	private void addRowColumns(List<MatrixQuestionColumn> matrixColumns, List<Map<String, Object>> props, MatrixQuestionRow matrixQuestionRow){
		Map<String,Object> prop = new HashMap<>();
		prop.put("rows", matrixQuestionRow);
		prop.put("columns", matrixColumns);
		props.add(prop);
	}

	@Override
	public List<Map<String, Object>> serializeConditions(QAndARuleType type, QuestionContext question, List<RuleCondition> conditions) throws Exception{

		MatrixQuestionContext mq = (MatrixQuestionContext) question;
		Map<Long, RuleCondition> conditionMap = conditions.stream().collect(Collectors.toMap(RuleCondition::getRowId, Function.identity()));
		List<Map<String, Object>> props = new ArrayList<>();
		for (MatrixQuestionRow matrixQuestionRow :mq.getRows()) {
			RuleCondition condition = conditionMap.get(matrixQuestionRow.getId()); //ID cannot be null
			if (condition == null) {
				addRowColumns(mq.getColumns(),props,matrixQuestionRow );
			}
			else {
				Map<String, Object> prop = FieldUtil.getAsProperties(condition);
				props.add(prop);
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
			MatrixAnswerContext.ColumnAnswer columnAns = columnAnswers.stream().filter(columnAnswer -> columnAnswer.getAnswer()!=null).collect(Collectors.toList()).get(0);
			Map<String,Object> prop = new HashMap<>();
			prop.put("rowId",option.getRow());
			prop.put("columnId", columnAns.getColumn());
			prop.put(RuleCondition.ANSWER_FIELD_NAME,columnAns.getAnswer());
			answerProps.add(prop);
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
}
