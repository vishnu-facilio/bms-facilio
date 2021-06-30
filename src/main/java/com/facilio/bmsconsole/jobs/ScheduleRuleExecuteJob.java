package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.ScheduledRuleJobsMetaContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class ScheduleRuleExecuteJob extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(ScheduleRuleExecuteJob.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		
		try {			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getScheduledRuleJobsMetaFields())
					.table(ModuleFactory.getScheduledRuleJobsMetaModule().getTableName())
					.andCondition(CriteriaAPI.getIdCondition(jc.getJobId(), ModuleFactory.getScheduledRuleJobsMetaModule()));
						
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {			
				ScheduledRuleJobsMetaContext scheduledRuleJobsMetaContext = FieldUtil.getAsBeanFromMap(props.get(0), ScheduledRuleJobsMetaContext.class);
				if(scheduledRuleJobsMetaContext != null && scheduledRuleJobsMetaContext.isActive()) {
					FacilioContext context = new FacilioContext();
					context.put(FacilioConstants.ContextNames.SCHEDULE_RULE_META, scheduledRuleJobsMetaContext);
					context.put(FacilioConstants.Job.JOB_CONTEXT, jc);
					FacilioChain executeRule = TransactionChainFactory.executeScheduledRuleJobChain();
					executeRule.execute(context);
				}
			}
			
		}
		catch (Exception e) {
			LOGGER.fatal("Error occurred during ScheduleRuleExecuteJob for job : "+jc.getJobId(), e);
			CommonCommandUtil.emailException("ScheduleRuleExecuteJob", "Error occurred during ScheduleRuleExecuteJob for job : "+jc.getJobId(), e);
		}
	}
}