package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.MultiRuleContext;
import com.facilio.constants.FacilioConstants;

public class AddMultiWorkflowsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<MultiRuleContext> rules = (List<MultiRuleContext>)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
		if(CollectionUtils.isNotEmpty(rules)) {
			for(MultiRuleContext rule : rules) {
				if(rule.getRule().getId() > 0) {
					context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule.getRule());
					context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, rule.getActions());
					TransactionChainFactory.updateWorkflowRuleChain().execute(context);
				}
				else {
					context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule.getRule());
					context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, rule.getActions());
					TransactionChainFactory.configureStoreNotificationsChain().execute(context);
				}
			}
		}
		return false;
	}

}
