package com.facilio.workflows.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class GetAllScheduledWorkflowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getScheduledWorkflowFields())
				.table(ModuleFactory.getScheduledWorkflowModule().getTableName())
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getScheduledWorkflowModule()));

		List<Map<String, Object>> props = selectBuilder.get();
		
		List<ScheduledWorkflowContext> scheduledWorkflowContexts = new ArrayList<>();
		
		List<Long> workflowIds = new ArrayList<>();
		
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				ScheduledWorkflowContext scheduledWorkflowContext = FieldUtil.getAsBeanFromMap(prop, ScheduledWorkflowContext.class);
				workflowIds.add(scheduledWorkflowContext.getWorkflowId());
				scheduledWorkflowContexts.add(scheduledWorkflowContext);
			}
		}
		
		Map<Long,WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workflowIds);
		
		for(ScheduledWorkflowContext scheduledWorkflowContext :scheduledWorkflowContexts) {
			
			WorkflowContext workflow = workflowMap.get(scheduledWorkflowContext.getWorkflowId());
			scheduledWorkflowContext.setWorkflowContext(workflow);
		}
		
		context.put(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT_LIST, scheduledWorkflowContexts);
		
		return false;
	}
}
