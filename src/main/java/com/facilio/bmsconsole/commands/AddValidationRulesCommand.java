package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class AddValidationRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<List<ReadingRuleContext>> readingRules = (List<List<ReadingRuleContext>>) context.get(FacilioConstants.ContextNames.READING_RULES_LIST); 
		List<List<List<ActionContext>>> actionsList = (List<List<List<ActionContext>>>) context.get(FacilioConstants.ContextNames.ACTIONS_LIST);
		
		Chain c = FacilioChainFactory.getAddWorkflowRuleChain();
		
		for (int i = 0; i < readingRules.size(); ++i) {
			for (int j = 0; j < readingRules.get(i).size(); ++j) {
				ReadingRuleContext rule = readingRules.get(i).get(j);
				rule.setRuleType(WorkflowRuleContext.RuleType.VALIDATION_RULE);
				context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
				context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION, actionsList.get(i).get(j));
				c.execute(context);
			}
		}
		return false;
	}

}
