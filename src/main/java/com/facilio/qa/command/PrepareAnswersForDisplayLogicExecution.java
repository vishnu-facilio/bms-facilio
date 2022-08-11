package com.facilio.qa.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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

/**
 * @author krishna
 * This class is to fetch already answered questions in the same response.
 */
public class PrepareAnswersForDisplayLogicExecution extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		
		ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
		
		Long templateId = Objects.requireNonNull(response, "Response cannot be null for on submit process").getParent().getId();
		
		Map<Long, QuestionContext> questionMap = QAndAUtil.getQuestionsFromTemplate(templateId).stream().collect(Collectors.toMap(QuestionContext::getId, Function.identity()));
		Map<Long, AnswerContext> otherAnswers = QAndAUtil.getAnswersFromTemplateAndResponse(templateId, response.getId()).stream().collect(Collectors.toMap(AnswerContext::getQuestionId, Function.identity()));
		
		Map<String,Object> mainRecordMap = new HashMap<String, Object>();
		
		for(Long questionId : questionMap.keySet()) {
			
			AnswerContext otherAnswer = otherAnswers.get(questionId);
			
			QuestionContext questionContext = questionMap.get(questionId);
			
			if(questionContext.getQuestionType() == QuestionType.MULTI_QUESTION || questionContext.getQuestionType() == QuestionType.MATRIX) {
				
				if(otherAnswer != null) {
					AnswerHandler handler = questionMap.get(otherAnswer.getQuestion().getId()).getQuestionType().getAnswerHandler();
					
					ClientAnswerContext clientAnswer = handler.serialize(otherAnswer);
					
					MatrixAnswer answer = (MatrixAnswer)clientAnswer.getActualAnswerObject();
					
					for(RowAnswer rowAns : answer.getRowAnswer()) {
						for(ColumnAnswer columnAns :  rowAns.getColumnAnswer()) {
							
							mainRecordMap.put(questionId+"_"+rowAns.getRow()+"_"+columnAns.getColumn(),columnAns.getAnswer());
						}
					}
				}
			}
			else {
				Object answer = null;
				
				if(otherAnswer != null) {
					AnswerHandler handler = questionMap.get(otherAnswer.getQuestion().getId()).getQuestionType().getAnswerHandler();
					
					ClientAnswerContext clientAnswer = handler.serialize(otherAnswer);
					
					answer = clientAnswer.getActualAnswerObject();
				}
				
				mainRecordMap.put(String.valueOf(questionId),answer);
			}
		}
		
        context.put(DisplayLogicUtil.ANSWER_MAP, mainRecordMap);
        
		return false;
	}
	
}
