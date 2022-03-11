package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
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
		List<AnswerContext> otherAnswers = QAndAUtil.getAnswersFromTemplateAndResponse(templateId, response.getId());
		
		Map<String,Object> mainRecordMap = new HashMap<String, Object>();
		
		for(AnswerContext otherAnswer : otherAnswers) {
			
			AnswerHandler handler = questionMap.get(otherAnswer.getQuestion().getId()).getQuestionType().getAnswerHandler();
			
			ClientAnswerContext clientAnswer = handler.serialize(otherAnswer);
			
			mainRecordMap.put(otherAnswer.getQuestion().getId()+"", clientAnswer.getActualAnswerObject());
		}
		
		
		JSONObject answerData = (JSONObject) context.get(FacilioConstants.QAndA.Command.ANSWER_DATA);
		
        List<Map<String, Object>> answers = answerData == null ? null : (List<Map<String, Object>>) answerData.get("answers");
        
        V3Util.throwRestException(CollectionUtils.isEmpty(answers), ErrorCode.VALIDATION_ERROR, "Answers cannot be empty for display logic execution");

        List<Long> triggerQuestionIds = new ArrayList<Long>();
        for (Map<String, Object> prop : answers) {
            
        	Long questionId = (Long) prop.get("question");
        	
        	triggerQuestionIds.add(questionId);
            QuestionContext question = questionMap.get(questionId);
            AnswerHandler handler = question.getQuestionType().getAnswerHandler();
            ClientAnswerContext answer = FieldUtil.<ClientAnswerContext>getAsBeanFromMap(prop, handler.getAnswerClass());
            
            mainRecordMap.put(questionId+"", answer.getActualAnswerObject());
        }
        
        context.put(DisplayLogicUtil.ANSWER_MAP, mainRecordMap);
        context.put(DisplayLogicUtil.TRIGGER_QUESTION_IDS, triggerQuestionIds);
        
		return false;
	}
	
	@SneakyThrows
    private Long fetchQuestionId (Map<String, Object> answer)  {
        Long questionId = (Long) answer.get("question");
        V3Util.throwRestException(questionId == null, ErrorCode.VALIDATION_ERROR, "Question cannot be null while add/ update of answer");
        return questionId;
    }

}
