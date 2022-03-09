package com.facilio.qa.command;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.context.DisplayLogicTriggerQuestions;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;

public class FetchDisplayLogicForExecution extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> triggerQuestionIds = (List<Long>) context.get(DisplayLogicUtil.TRIGGER_QUESTION_IDS);
		
		List<DisplayLogicTriggerQuestions> triggerQuestions = DisplayLogicUtil.fetchDisplayLogicTriggerQuestion(triggerQuestionIds);
		
		List<Long> displayLogicIds = triggerQuestions.stream().map(DisplayLogicTriggerQuestions::getDisplayLogicId).collect(Collectors.toList());

		List<DisplayLogicContext> displayLogics = DisplayLogicUtil.fetchDisplayLogic(CriteriaAPI.getIdCondition(displayLogicIds, ModuleFactory.getQAndADisplayLogicModule()));
		
		context.put(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXTS, displayLogics);
		
		return false;
	}

}
