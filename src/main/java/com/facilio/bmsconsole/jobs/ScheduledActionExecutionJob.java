package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ScheduledActionContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ScheduledActionExecutionJob extends FacilioJob {
private static final Logger LOGGER = LogManager.getLogger(ScheduledActionExecutionJob.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			
			FacilioModule module = ModuleFactory.getScheduledActionModule();
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(FieldFactory.getScheduledActionFields())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(jc.getJobId(), module));
			
			List<Map<String, Object>> props = builder.get();
			if (props != null && !props.isEmpty()) {
				ScheduledActionContext scheduledAction = FieldUtil.getAsBeanFromMap(props.get(0), ScheduledActionContext.class);
				ActionContext action = ActionAPI.getAction(scheduledAction.getActionId());
				Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
				action.executeAction(placeHolders, null, null, null);
			}
		}
		catch (Exception e) {
			LOGGER.fatal("Error occurred during scheduled action execution for job : "+jc.getJobId(), e);
			CommonCommandUtil.emailException("ScheduledActionExecutionJob", "Error occurred during scheduled action execution for job : "+jc.getJobId(), e);
		}
	}
}
