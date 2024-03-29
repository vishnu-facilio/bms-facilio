package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class AddValidationRulesCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(AddValidationRulesCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		LOGGER.info("Inside AddValidationRulesCommand");
		List<List<ReadingRuleContext>> readingRules = (List<List<ReadingRuleContext>>) context.get(FacilioConstants.ContextNames.READING_RULES_LIST);
		List<List<List<ActionContext>>> actionsList = (List<List<List<ActionContext>>>) context.get(FacilioConstants.ContextNames.ACTIONS_LIST);
		Long resourceID = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		List<Long> delReadingRulesIds = (List<Long>) context.get(FacilioConstants.ContextNames.DEL_READING_RULE_IDS);
		Long readingFieldId = (Long) context.get(FacilioConstants.ContextNames.READING_FIELD_ID);
		
		WorkflowRuleAPI.deleteWorkFlowRules(delReadingRulesIds);
		
		if (readingRules == null || readingRules.isEmpty()) {
			return false; 
		}
		

		for (int i = 0; i < readingRules.size(); ++i) {
			if (readingRules.get(i) == null) {
				continue;
			}
			FacilioChain addChain = TransactionChainFactory.addWorkflowRuleChain();
			FacilioChain updateChain = TransactionChainFactory.updateWorkflowRuleChain();
			int len = readingRules.get(i).size();
			for (int j = 0; j < len; ++j) {
				if(readingRules.get(i).get(j).getReadingFieldId() == -1) {
					readingRules.get(i).get(j).setReadingFieldId(readingFieldId);
				}
				ReadingRuleContext rule = readingRules.get(i).get(j);
				rule.setRuleType(WorkflowRuleContext.RuleType.VALIDATION_RULE);
				if (resourceID != null) {
					rule.setResourceId(resourceID);
				}
				context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
				if (actionsList.get(i) != null && !actionsList.get(i).isEmpty()) {
					context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, actionsList.get(i).get(j));
				} else {
					context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, null);
				}
				if (rule.getId() != -1) {
					updateChain.execute(context);
				} else {
					addChain.execute(context);
				}
			}
		}
		return false;
	}

}
