package com.facilio.qa.command;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.owasp.esapi.util.CollectionsUtil;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.context.DisplayLogicTriggerQuestions;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;

public class FetchDisplayLogicForExecution extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Boolean isDisplyLogicExecutionOnPageLoad = (Boolean) context.getOrDefault(DisplayLogicUtil.IS_DISPLAY_LOGIC_EXECUTION_ON_PAGE_LOAD, false);
		
		List<Long> displayLogicIds = null;
		
		if(isDisplyLogicExecutionOnPageLoad) {
			
			List<Long> actionQuestionIds = (List<Long>) context.get(DisplayLogicUtil.ACTION_QUESTION_IDS);
			
			if(CollectionUtils.isNotEmpty(actionQuestionIds)) {
				List<DisplayLogicContext> displyLogicByQuestion = DisplayLogicUtil.fetchDisplayLogic(CriteriaAPI.getCondition(FieldFactory.filterField(FieldFactory.getQAndADisplayLogicFields(), "questionId"), actionQuestionIds, NumberOperators.EQUALS));
				if(CollectionUtils.isNotEmpty(displyLogicByQuestion)) {
					displayLogicIds = displyLogicByQuestion.stream().map(DisplayLogicContext::getId).collect(Collectors.toList());
				}
			}
		}
		else {
			List<Long> triggerQuestionIds = (List<Long>) context.get(DisplayLogicUtil.TRIGGER_QUESTION_IDS);
			
			if(CollectionUtils.isNotEmpty(triggerQuestionIds)) {
				List<DisplayLogicTriggerQuestions> triggerQuestions = DisplayLogicUtil.fetchDisplayLogicTriggerQuestion(triggerQuestionIds);
				
				displayLogicIds = triggerQuestions.stream().map(DisplayLogicTriggerQuestions::getDisplayLogicId).collect(Collectors.toList());
			}
		}
		
		if(CollectionUtils.isNotEmpty(displayLogicIds)) {
			List<DisplayLogicContext> displayLogics = DisplayLogicUtil.fetchDisplayLogic(CriteriaAPI.getIdCondition(displayLogicIds, ModuleFactory.getQAndADisplayLogicModule()));
			
			context.put(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXTS, displayLogics);
		}
		else {
			return true;
		}
		
		return false;
	}

}
