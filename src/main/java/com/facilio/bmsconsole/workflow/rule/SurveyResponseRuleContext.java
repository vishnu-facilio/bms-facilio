package com.facilio.bmsconsole.workflow.rule;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.chain.FacilioContext;
import com.facilio.delegate.context.DelegationType;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.qa.context.ResponseContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SurveyResponseRuleContext extends WorkflowRuleContext{

	private SurveyActionType actionType;
	private long surveyParentRuleId;

	@Override
	public boolean evaluateMisc (String moduleName, Object record, Map< String, Object > placeHolders, FacilioContext context) throws Exception {

		SurveyResponseContext responseContext = (SurveyResponseContext) record;

		switch(actionType){
			case ON_CREATE:
				return (responseContext.getRuleId() == getSurveyParentRuleId());
			case ON_SUBMIT:
				if(responseContext.getResStatus() == ResponseContext.ResponseStatus.COMPLETED){
					return (responseContext.getRuleId() == getSurveyParentRuleId());
				}
				break;
		}

		return false;
	}

	public enum SurveyActionType implements FacilioStringEnum{
		ON_CREATE,ON_SUBMIT;
	}
}
