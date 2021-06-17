package com.facilio.qa.command;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.activity.QAndAActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.AnswerHandler;
import com.facilio.qa.context.ClientAnswerContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.ResponseContext;

public class ConstructActivityForAddOrUpdateAnswerCommand extends FacilioCommand {

	@Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<AnswerContext> addedAnswers = (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_ADDED);
		List<AnswerContext> updatedAnswers = (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_UPDATED);
		Map<Long, QuestionContext> questions = (Map<Long, QuestionContext>) context.get(FacilioConstants.QAndA.Command.QUESTION_MAP);
		
		ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
		
		if(CollectionUtils.isNotEmpty(addedAnswers)) {
			
			for(AnswerContext addedAnswer : addedAnswers) {
				
				JSONObject info = getInfoJSON(addedAnswer, questions);
				CommonCommandUtil.addActivityToContext(response.getId(), -1, QAndAActivityType.ANSWERED, info, (FacilioContext) context);
			}
		}
		
		if(CollectionUtils.isNotEmpty(updatedAnswers)) {
			
			for(AnswerContext updatedAnswer : updatedAnswers) {
				
				JSONObject info = getInfoJSON(updatedAnswer, questions);
				CommonCommandUtil.addActivityToContext(response.getId(), -1, QAndAActivityType.ANSWER_UPDATED, info, (FacilioContext) context);
			}
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME,modBean.getModule(response.getModuleId()).getName());
		
		return false;
	}
	
	private JSONObject getInfoJSON(AnswerContext addedAnswer,Map<Long, QuestionContext> questions) throws Exception {
		
		JSONObject info = new JSONObject();
		
		QuestionContext question = questions.get(addedAnswer.getQuestion().getId());
		info.put("question", question.getQuestion());
		
		AnswerHandler handler = question.getQuestionType().getAnswerHandler();
		String answerString = handler.getAnswerStringValue(addedAnswer, question);
		
		info.put("answer", answerString);
		
		return info;
	}

}
