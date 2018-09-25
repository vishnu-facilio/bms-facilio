package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class FetchApprovalRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<WorkOrderContext> workOrders = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST);
		if (workOrders == null) {
			WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
			if (wo != null) {
				workOrders = Collections.singletonList(wo);
			}
		}
		
		if (workOrders != null && !workOrders.isEmpty()) {
			List<Long> ruleIds = new ArrayList<>();
			for (WorkOrderContext wo : workOrders) {
				if (wo.getApprovalStateEnum() == ApprovalState.REQUESTED) {
					ruleIds.add(wo.getApprovalRuleId());
				}
			}
			
			if (!ruleIds.isEmpty()) {
				List<WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRules(ruleIds);
				Map<Long, ApprovalRuleContext> ruleMap = rules.stream()
														.collect(Collectors.toMap(WorkflowRuleContext::getId, r -> (ApprovalRuleContext) r));
				
				for (WorkOrderContext wo : workOrders) {
					if (wo.getApprovalStateEnum() == ApprovalState.REQUESTED) {
						wo.setApprovalRule(ruleMap.get(wo.getApprovalRuleId()));
					}
				}
			}
		}
		
		return false;
	}

}
