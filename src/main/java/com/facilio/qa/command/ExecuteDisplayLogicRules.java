package com.facilio.qa.command;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.qa.displaylogic.context.DisplayLogicAction;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;

public class ExecuteDisplayLogicRules extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Map<String,Object> mainRecordMap = (Map<String, Object>) context.get(DisplayLogicUtil.ANSWER_MAP);
		
		List<DisplayLogicContext> displayLogics = (List<DisplayLogicContext>) context.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXTS);
		
		context.put(DisplayLogicUtil.DISPLAY_LOGIC_RULE_RESULT_JSON,new JSONArray());
		
		for(DisplayLogicContext displayLogic : displayLogics) {
			
			Criteria criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), displayLogic.getCriteriaId());
			
			context.put(DisplayLogicUtil.QUESTION_ID, displayLogic.getQuestionId());
			if(criteria != null && mainRecordMap != null) {
				Boolean criteriaFlag = criteria.computePredicate(mainRecordMap).evaluate(mainRecordMap);
				
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
