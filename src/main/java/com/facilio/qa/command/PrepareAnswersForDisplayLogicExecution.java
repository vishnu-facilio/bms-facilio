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
import com.facilio.qa.context.ResponseContext;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

import lombok.SneakyThrows;

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
			
			Object answer = null;
			
			if(otherAnswer != null) {
				AnswerHandler handler = questionMap.get(otherAnswer.getQuestion().getId()).getQuestionType().getAnswerHandler();
				
				ClientAnswerContext clientAnswer = handler.serialize(otherAnswer);
				
				answer = clientAnswer.getActualAnswerObject();
			}
			
			mainRecordMap.put(String.valueOf(questionId),answer);
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

}
