package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.qa.context.QuestionType;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.displaylogic.context.DisplayLogicAction;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;
import com.facilio.v3.context.Constants;

public class ExecuteDisplayLogicRules extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		Map<String,Object> mainRecordMap = (Map<String, Object>) context.get(DisplayLogicUtil.ANSWER_MAP);

		List<DisplayLogicContext> displayLogics = (List<DisplayLogicContext>) context.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXTS);

		context.put(DisplayLogicUtil.DISPLAY_LOGIC_RULE_RESULT_JSON,new JSONArray());

		Boolean isDisplyLogicExecutionOnPageLoad = (Boolean) context.getOrDefault(DisplayLogicUtil.IS_DISPLAY_LOGIC_EXECUTION_ON_PAGE_LOAD, false);

		Map<Long, QuestionContext> questionMap = null;
		List<Long> booleanQuestionIds =  new ArrayList<>();

		if(isDisplyLogicExecutionOnPageLoad) {

			List<PageContext> pages = Constants.getRecordList((FacilioContext) context);

			questionMap = pages.stream().map(PageContext::getQuestions).flatMap(List::stream).collect(Collectors.toMap(QuestionContext::getId, Function.identity()));

			for (Map.Entry<Long, QuestionContext> entry : questionMap.entrySet()) {
				if(entry.getValue().getQuestionType() == QuestionType.BOOLEAN){
					booleanQuestionIds.add(entry.getKey());
				}
			}

		}

		if(displayLogics != null) {
			for(DisplayLogicContext displayLogic : displayLogics) {

				Criteria criteria = null;

				if(displayLogic.getCriteriaId()!=null){
					 criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), displayLogic.getCriteriaId());
				}

				context.put(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT, displayLogic);
				if(questionMap != null) {
					context.put(DisplayLogicUtil.QUESTION_CONTEXT, questionMap.get(displayLogic.getQuestionId()));
				}

				Boolean criteriaFlag = true;

				if(criteria !=null && mainRecordMap != null) {
					 criteriaFlag = criteria.computePredicate(mainRecordMap).evaluate(mainRecordMap);
					 if(criteria.getConditions()!=null && !criteria.getConditions().isEmpty() && criteria.getConditions().size()>0 && booleanQuestionIds!=null && booleanQuestionIds.size()>0 && displayLogic.getDisplayLogicTypeEnum()!= DisplayLogicContext.DisplayLogicType.ANSWER_DISPLAY){
						 for (Map.Entry<String, Condition> entry : criteria.getConditions().entrySet()) {
							 if(booleanQuestionIds.contains(Long.parseLong(entry.getValue().getFieldName())) && entry.getValue().getOperator() == BooleanOperators.IS && mainRecordMap.get(entry.getValue().getFieldName())==null){
								 criteriaFlag = false;
							 }
						 }
					 }
				}
				for(DisplayLogicAction displayLogicAction :displayLogic.getActions()) {

					context.put(DisplayLogicUtil.DISPLAY_LOGIC_ACTION_CONTEXT, displayLogicAction);

					if(criteriaFlag) {
						displayLogicAction.getActionTypeEnum().performAction((FacilioContext)context);
					}
					else {
						if(displayLogicAction.getActionTypeEnum().getInverseType() != null) {
							displayLogicAction.getActionTypeEnum().getInverseType().performAction((FacilioContext)context);
						}
					}
				}
			}
		}
		return false;
	}

}
