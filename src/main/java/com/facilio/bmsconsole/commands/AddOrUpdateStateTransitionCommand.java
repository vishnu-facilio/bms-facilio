package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateStateTransitionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		StateflowTransitionContext stateTransition = (StateflowTransitionContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (stateTransition != null) {
			FacilioChain chain;
			if (stateTransition.getId() < 0) {
				chain = TransactionChainFactory.addWorkflowRuleChain();
			} 
			else {
				chain = TransactionChainFactory.updateWorkflowRuleChain();
			}
			chain.execute(context);
		}
		return false;
	}

}
