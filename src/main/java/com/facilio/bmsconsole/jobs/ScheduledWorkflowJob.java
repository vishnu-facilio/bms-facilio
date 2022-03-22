package com.facilio.bmsconsole.jobs;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.workflowlog.context.WorkflowLogContext;
import com.facilio.workflows.command.SchedulerAPI;
import com.facilio.workflows.context.ScheduledWorkflowContext;

public class ScheduledWorkflowJob extends FacilioJob{

	private static final Logger LOGGER = LogManager.getLogger(ScheduledWorkflowJob.class.getName());

	public void execute(JobContext jc)throws Exception  {
		try {
			long id = jc.getJobId();
			
			LOGGER.error("ScheduledWorkflowJob for ID - "+ id +" Started at "+System.currentTimeMillis());
			
			ScheduledWorkflowContext scheduledWorkflowContext = ScheduledWorkflowJob.getScheduledWorkflowContext(id,true);
			
			if(scheduledWorkflowContext != null) {
				
				List<ActionContext> actions = scheduledWorkflowContext.getActions();
				if (CollectionUtils.isNotEmpty(actions)) {
					for (ActionContext action : actions) {
						FacilioContext context = new FacilioContext();
						context.put(FacilioConstants.Workflow.WORKFLOW_LOG_PARENT_ID, scheduledWorkflowContext.getId());
						context.put(FacilioConstants.Workflow.WORKFLOW_LOG_PARENT_TYPE, WorkflowLogContext.WorkflowLogType.SCHEDULER);
						action.executeAction(null, context, null, null);
					}
				}

//				WorkflowContext workflow = WorkflowUtil.getWorkflowContext(scheduledWorkflowContext.getWorkflowId());
//
//				FacilioContext context = new FacilioContext();
//				context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
//
//				FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
//
//				chain.execute(context);
			}
			
			LOGGER.error("ScheduledWorkflowJob for ID - "+ id +" Completed at "+System.currentTimeMillis());
		}
		catch (Exception e) {
			CommonCommandUtil.emailException("Scheduled Workflow Failed", "Orgid -- "+AccountUtil.getCurrentOrg().getId() + " jobid -- "+jc.getJobId(), e);
			LOGGER.log(Priority.ERROR, e.getMessage(), e);
		}
	}
	
	public static ScheduledWorkflowContext getScheduledWorkflowContext(long scheduledWorkflowId,Boolean isActive) throws Exception {
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(FieldFactory.getScheduledWorkflowFields())
				.table(ModuleFactory.getScheduledWorkflowModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(scheduledWorkflowId, ModuleFactory.getScheduledWorkflowModule()))
				.andCondition(CriteriaAPI.getCondition("IS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getScheduledWorkflowFields());
		if(isActive != null) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("isActive"), isActive.toString(), BooleanOperators.IS));
		}
		
		List<Map<String, Object>> props = select.get();
		
		ScheduledWorkflowContext scheduledWorkflowContext = null;
		if(props != null && !props.isEmpty()) {
			scheduledWorkflowContext = FieldUtil.getAsBeanFromMap(props.get(0), ScheduledWorkflowContext.class);
			
			SchedulerAPI.getSchedulerActions(Collections.singletonList(scheduledWorkflowContext));
		}
		return scheduledWorkflowContext;
	}
	
	public static ScheduledWorkflowContext getScheduledWorkflowContext(String scheduleName) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getScheduledWorkflowFields());
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(FieldFactory.getScheduledWorkflowFields())
				.table(ModuleFactory.getScheduledWorkflowModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("linkName"), scheduleName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("IS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));
		
		List<Map<String, Object>> props = select.get();
		
		ScheduledWorkflowContext scheduledWorkflowContext = null;
		if(props != null && !props.isEmpty()) {
			scheduledWorkflowContext = FieldUtil.getAsBeanFromMap(props.get(0), ScheduledWorkflowContext.class);
			
			SchedulerAPI.getSchedulerActions(Collections.singletonList(scheduledWorkflowContext));
		}
		return scheduledWorkflowContext;
	}
}
