package com.facilio.qa.command;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionType;
import com.facilio.qa.context.ResponseContext;
import com.facilio.qa.context.client.answers.MatrixAnswerContext;
import com.facilio.qa.context.client.answers.MatrixAnswerContext.ColumnAnswer;
import com.facilio.qa.context.client.answers.MatrixAnswerContext.MatrixAnswer;
import com.facilio.qa.context.client.answers.MatrixAnswerContext.RowAnswer;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

/**
 * @author krishna
 * This class is to fetch already answered questions in the same response.
 */
public class PrepareAnswersForDisplayLogicExecution extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Boolean isDisplyLogic = (Boolean) context.getOrDefault("isDisplayLogic", false);
		//  is for executing display logic for those questions which have display logic without saving it.
		Boolean isDisplyLogicExecutionOnPageLoad = (Boolean) context.getOrDefault(DisplayLogicUtil.IS_DISPLAY_LOGIC_EXECUTION_ON_PAGE_LOAD, false);
		// is for executing display logic during summary page load.

		ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
		
		Long templateId = Objects.requireNonNull(response, "Response cannot be null for on submit process").getParent().getId();
		
		Map<Long, QuestionContext> questionMap = QAndAUtil.getQuestionsFromTemplate(templateId).stream().collect(Collectors.toMap(QuestionContext::getId, Function.identity()));
		Map<Long, AnswerContext> otherAnswers = null;
		if(!isDisplyLogic) {
			// no need to fetch other answers while executing only the logic for the answered questions alone.
			// earlier, other answers is fetched always removing auto save, because the current changes will also be saved.
			otherAnswers = QAndAUtil.getAnswersFromTemplateAndResponse(templateId, response.getId()).stream().collect(Collectors.toMap(AnswerContext::getQuestionId, Function.identity()));
		}
		Map<String,Object> mainRecordMap = new HashMap<String, Object>();
		
		for(Long questionId : questionMap.keySet()) {
			AnswerContext otherAnswer = null;
			if(otherAnswers!=null) {
				otherAnswer = otherAnswers.get(questionId);
			}
			QuestionContext questionContext = questionMap.get(questionId);

			if(otherAnswer != null) {
				if (questionContext.getQuestionType() == QuestionType.MULTI_QUESTION || questionContext.getQuestionType() == QuestionType.MATRIX) {
					AnswerHandler handler = questionMap.get(otherAnswer.getQuestion().getId()).getQuestionType().getAnswerHandler();

					ClientAnswerContext clientAnswer = handler.serialize(otherAnswer);

					MatrixAnswer answer = (MatrixAnswer) clientAnswer.getActualAnswerObject();

					for (RowAnswer rowAns : answer.getRowAnswer()) {
						for (ColumnAnswer columnAns : rowAns.getColumnAnswer()) {

							mainRecordMap.put(questionId + "_" + rowAns.getRow() + "_" + columnAns.getColumn(), columnAns.getAnswer());
						}
					}
				} else {
					Object answer = null;
					AnswerHandler handler = questionMap.get(otherAnswer.getQuestion().getId()).getQuestionType().getAnswerHandler();
					ClientAnswerContext clientAnswer = handler.serialize(otherAnswer);
					answer = clientAnswer.getActualAnswerObject();
					mainRecordMap.put(String.valueOf(questionId),answer);
				}
			}
			else if(!isDisplyLogicExecutionOnPageLoad && otherAnswer == null && isDisplyLogic){
				FacilioChain validateAnswersChain = QAndAReadOnlyChainFactory.constructAnswersForDisplayLogicChain();
				FacilioContext context1 = validateAnswersChain.getContext();
				context1.put(FacilioConstants.QAndA.Command.ANSWER_DATA, context.get(FacilioConstants.QAndA.Command.ANSWER_DATA));
				context1.put(FacilioConstants.QAndA.RESPONSE, context.get(FacilioConstants.QAndA.RESPONSE));
				validateAnswersChain.execute();

				List<Map<String, Object>> errors = (List<Map<String, Object>>) context1.get(FacilioConstants.QAndA.Command.ANSWER_ERRORS);
				JSONObject data = new JSONObject();
				if (CollectionUtils.isNotEmpty(errors)) {
					data.put("errors", errors);
				}

				Map<Long, AnswerContext> questionVsAnswer = (Map<Long, AnswerContext>) context1.get(FacilioConstants.QAndA.Command.QUESTION_VS_ANSWER);
				Object answer = null;
				if(MapUtils.isNotEmpty(questionVsAnswer) && questionVsAnswer.get(questionId)!=null){
					AnswerContext answerContext = questionVsAnswer.get(questionId);
					AnswerHandler handler = questionMap.get(questionId).getQuestionType().getAnswerHandler();
					ClientAnswerContext clientAnswer = handler.serialize(answerContext);
					if (questionContext.getQuestionType() == QuestionType.MULTI_QUESTION || questionContext.getQuestionType() == QuestionType.MATRIX) {
						MatrixAnswer matrixanswer = (MatrixAnswer) clientAnswer.getActualAnswerObject();

						for (RowAnswer rowAns : matrixanswer.getRowAnswer()) {
							for (ColumnAnswer columnAns : rowAns.getColumnAnswer()) {

								mainRecordMap.put(questionId + "_" + rowAns.getRow() + "_" + columnAns.getColumn(), columnAns.getAnswer());
							}
						}
					} else {
						answer = clientAnswer.getActualAnswerObject();
						mainRecordMap.put(String.valueOf(questionId), answer);
					}
				}
			} else {
				mainRecordMap.put(String.valueOf(questionId), null);
			}
		}
        context.put(DisplayLogicUtil.ANSWER_MAP, mainRecordMap);
        
		return false;
	}
	@SneakyThrows
	private Long fetchQuestionId (Map<String, Object> answer)  {
		Long questionId = (Long) answer.get("question");
		V3Util.throwRestException(questionId == null, ErrorCode.VALIDATION_ERROR, "Question cannot be null while add/ update of answer");
		return questionId;
	}

	private List<Map<String, Object>> errors = null;
	private List<Map<String, Object>> getErrors() {
		errors = errors == null ? new ArrayList<>() : errors;
		return errors;
	}
}
