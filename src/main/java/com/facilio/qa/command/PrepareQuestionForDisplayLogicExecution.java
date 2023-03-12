package com.facilio.qa.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.qa.context.ClientAnswerContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.SneakyThrows;

public class PrepareQuestionForDisplayLogicExecution extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Boolean isDisplyLogicExecutionOnPageLoad = (Boolean) context.getOrDefault(DisplayLogicUtil.IS_DISPLAY_LOGIC_EXECUTION_ON_PAGE_LOAD, false);
		Boolean isDisplyLogic = (Boolean) context.getOrDefault("isDisplayLogic", false);

		if(isDisplyLogicExecutionOnPageLoad) {
			List<PageContext> pages = Constants.getRecordList((FacilioContext) context);

			if(CollectionUtils.isNotEmpty(pages)) {
				List<Long> actionQuestionIds = pages.stream()
						.filter(page -> CollectionUtils.isNotEmpty(page.getQuestions()))
						.map(PageContext::getQuestions).flatMap(List::stream)
						.map(QuestionContext::getId).collect(Collectors.toList());
				
				context.put(DisplayLogicUtil.ACTION_QUESTION_IDS, actionQuestionIds);
			}
		}
		else {
			JSONObject answerData = (JSONObject) context.get(FacilioConstants.QAndA.Command.ANSWER_DATA);
			List<Long> triggerQuestionIds;
			if(!isDisplyLogic){
				List<ClientAnswerContext> answers = answerData == null ? null : (List<ClientAnswerContext>) answerData.get("answers");
				V3Util.throwRestException(CollectionUtils.isEmpty(answers), ErrorCode.VALIDATION_ERROR, "Answers cannot be empty for display logic execution");
				triggerQuestionIds = answers.stream().map(ClientAnswerContext::getQuestion).collect(Collectors.toList());
			} else {
				List<Map<String, Object>> answers = answerData == null ? null : (List<Map<String, Object>>) answerData.get("answers");
				V3Util.throwRestException(CollectionUtils.isEmpty(answers), ErrorCode.VALIDATION_ERROR, "Answers cannot be empty while add or updating answers");
				triggerQuestionIds = answers.stream().map(this::fetchQuestionId).collect(Collectors.toList());
			}
	        context.put(DisplayLogicUtil.TRIGGER_QUESTION_IDS, triggerQuestionIds);
		}
		return false;
	}
	
	@SneakyThrows
    private Long fetchQuestionId (Map<String, Object> answer)  {
        Long questionId = (Long) answer.get("question");
        V3Util.throwRestException(questionId == null, ErrorCode.VALIDATION_ERROR, "Question cannot be null while add/ update of answer");
        return questionId;
    }

}
