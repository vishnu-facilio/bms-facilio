package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.SurveyResponseRuleContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Log4j
public class DeleteSurveyRulesCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		List<Long> ruleIds = (List<Long>) context.get("ruleIds");

		if(CollectionUtils.isNotEmpty(ruleIds)){
			SurveyResponseRuleContext surveyResponseRuleContext = SurveyUtil.fetchChildRuleId(ruleIds);
			if(surveyResponseRuleContext != null){
				long createRuleId = surveyResponseRuleContext.getExecuteCreateRuleId();
				long submitRuleId = surveyResponseRuleContext.getExecuteSubmitRuleId();

				if(createRuleId > 0){
					ruleIds.add(createRuleId);
				}
				if(submitRuleId > 0){
					ruleIds.add(submitRuleId);
				}
			}
			WorkflowRuleAPI.deleteWorkFlowRules(ruleIds);
		}

		return false;
	}


}
