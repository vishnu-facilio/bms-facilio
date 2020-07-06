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

			if (stateTransition.getTypeEnum() == AbstractStateTransitionRuleContext.TransitionType.FIELD_SCHEDULED) {
				if (stateTransition.getScheduleTypeEnum() == null) {
					throw new IllegalArgumentException("Schedule type should not be empty");
				}
				if (stateTransition.getInterval() < 0) {
					throw new IllegalArgumentException("Invalid schedule Time");
				}
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField field = modBean.getField(stateTransition.getDateFieldId(), stateTransition.getModuleId());
				if (field == null) {
					throw new IllegalArgumentException("Invalid date field");
				}

				WorkflowRuleContext fieldScheduleRule = WorkflowRuleAPI.getWorkflowRuleByRuletype(stateTransition.getId(), WorkflowRuleContext.RuleType.STATE_TRANSACTION_FIELD_SCHEDULED);
				if (fieldScheduleRule != null) {
					WorkflowRuleAPI.deleteWorkflowRule(fieldScheduleRule.getId());
				}

				FacilioModule module = modBean.getModule(stateTransition.getModuleId());

				StateTransitionFieldScheduleRuleContext ruleContext = new StateTransitionFieldScheduleRuleContext();
				ruleContext.setName("StateTransition_FieldSchedule_" + stateTransition.getId());
				ruleContext.setParentRuleId(stateTransition.getId());
				ruleContext.setModule(module);
				ruleContext.setActivityType(EventType.SCHEDULED);

				FacilioField stateFlowIdField = modBean.getField("stateFlowId", stateTransition.getModuleName());
				FacilioField moduleStateField = modBean.getField("moduleState", stateTransition.getModuleName());

				Criteria criteria = new Criteria();
				criteria.addAndCondition(CriteriaAPI.getCondition(stateFlowIdField, String.valueOf(stateTransition.getStateFlowId()), NumberOperators.EQUALS));
				criteria.addAndCondition(CriteriaAPI.getCondition(moduleStateField, String.valueOf(stateTransition.getFromStateId()), PickListOperators.IS));
				ruleContext.setCriteria(criteria);

				ruleContext.setScheduleType(stateTransition.getScheduleTypeEnum());
				ruleContext.setInterval(stateTransition.getInterval());
				ruleContext.setDateFieldId(stateTransition.getDateFieldId());

				ruleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_TRANSACTION_FIELD_SCHEDULED);

				FacilioChain scheduledFieldChain = TransactionChainFactory.addWorkflowRuleChain();
				FacilioContext scheduledFieldChainContext = scheduledFieldChain.getContext();
				scheduledFieldChainContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, ruleContext);
				scheduledFieldChain.execute();
			}
		}
		return false;
	}

}
