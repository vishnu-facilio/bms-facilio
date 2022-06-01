package com.facilio.workflows.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkflowTemplate;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.taskengine.job.JobContext;
import com.facilio.taskengine.job.JobStore;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class GetAllScheduledWorkflowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getScheduledWorkflowFields())
				.table(ModuleFactory.getScheduledWorkflowModule().getTableName())
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getScheduledWorkflowModule()))
				.andCondition(CriteriaAPI.getCondition("IS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<ScheduledWorkflowContext> scheduledWorkflowContexts = new ArrayList<>();
		
		List<Long> workflowIds = new ArrayList<>();
		
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				ScheduledWorkflowContext scheduledWorkflowContext = FieldUtil.getAsBeanFromMap(prop, ScheduledWorkflowContext.class);
				
				JobContext job = JobStore.getJob(AccountUtil.getCurrentOrg().getId(), scheduledWorkflowContext.getId(), "ScheduledWorkflow");
				
				if(job != null) {
					long nextExecutionTime = job.getExecutionTime()*1000;
					if(nextExecutionTime > DateTimeUtil.getCurrenTime()) {
						scheduledWorkflowContext.setNextExecutionTime(nextExecutionTime);
					}
				}
				
				workflowIds.add(scheduledWorkflowContext.getWorkflowId());
				scheduledWorkflowContexts.add(scheduledWorkflowContext);
			}
		}
		
		SchedulerAPI.getSchedulerActions(scheduledWorkflowContexts);
		
		for(ScheduledWorkflowContext scheduledWorkflowContext :scheduledWorkflowContexts) {
			ActionContext scriptAction = scheduledWorkflowContext.getScriptAction();
			if (scriptAction != null) {
				Template template = scriptAction.getTemplate();
				if (template instanceof WorkflowTemplate) {
					WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(((WorkflowTemplate) template).getResultWorkflowId());
					scheduledWorkflowContext.setWorkflowContext(workflowContext);
				}
			}
		}
		
		context.put(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT_LIST, scheduledWorkflowContexts);
		
		return false;
	}
}
