package com.facilio.qa.command;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.context.DisplayLogicContext.DisplayLogicType;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

public class FetchDisplayLogicListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Long questionId = (Long) context.get(DisplayLogicUtil.QUESTION_ID);
		
		if(questionId != null) {
			List<DisplayLogicContext> displayLogics = DisplayLogicUtil.fetchDisplayLogic(CriteriaAPI.getCondition(FieldFactory.filterField(FieldFactory.getQAndADisplayLogicFields(), "questionId"), questionId+"", NumberOperators.EQUALS));
			
			context.put(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXTS, displayLogics);
		}
		else {
			Long pageId = (Long) context.get(DisplayLogicUtil.PAGE_ID);
			V3Util.throwRestException(pageId == null, ErrorCode.VALIDATION_ERROR, "errors.qa.fetchDisplayLogicListCommand.pageQuestionIdCheck",true,null);
			//V3Util.throwRestException(pageId == null, ErrorCode.VALIDATION_ERROR, "both pageId and questionid cannot be empty during rules fetch",true,null);
			
			Criteria criteria = new Criteria();
			
			criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.filterField(FieldFactory.getQAndADisplayLogicFields(), "pageId"), pageId+"", NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.filterField(FieldFactory.getQAndADisplayLogicFields(), "type"), DisplayLogicType.PAGE_SKIP.getIntValue()+"", NumberOperators.EQUALS));
			
			List<DisplayLogicContext> displayLogics = DisplayLogicUtil.fetchDisplayLogic(criteria);
			
			context.put(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXTS, displayLogics);
		}
		
		return false;
	}

}
