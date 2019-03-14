package com.facilio.bmsconsole.jobs;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class HistoricalScheduledRuleJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(HistoricalScheduledRuleJob.class.getName());
	
	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		long jobStartTime = System.currentTimeMillis();
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(jc.getJobId(), true);
		
		JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
		long startTime = (long) props.get("startTime");
		long endTime = (long) props.get("endTime");
		
		LOGGER.info("Historical execution of rule : "+rule.getId());
		
		long currentStartTime = rule.getSchedule().nextExecutionTime(startTime / 1000);
		
		while (currentStartTime <= endTime) {
			LOGGER.info("Gonna run for time : "+currentStartTime);
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID, rule.getId());
			context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, DateTimeUtil.getHourStartTimeOf(currentStartTime) * 1000); //TODO hourStartTime should be changed to direct execution time later
			Chain scheduledChain = TransactionChainFactory.executeScheduledReadingRuleChain();
			scheduledChain.execute(context);
			
			currentStartTime = rule.getSchedule().nextExecutionTime(currentStartTime);
		}
		long timeTaken = (System.currentTimeMillis() - jobStartTime);
		LOGGER.info("Total Time taken for Historical Run for Scheduled Rule : "+jc.getJobId()+" between "+startTime+" and "+endTime+" is "+timeTaken);
		
		JSONObject json = new JSONObject();
		json.put("to", "praveen@facilio.com, manthosh@facilio.com, shivaraj@facilio.com");
		json.put("sender", "noreply@facilio.com");
		json.put("subject", "Historical Run completed for Scheduled Rule : "+jc.getJobId());
		json.put("message", "Total Time taken for Historical Run for Scheduled Rule : "+jc.getJobId()+" between "+startTime+" and "+endTime+" is "+timeTaken);
		
		AwsUtil.sendEmail(json);
	}

}
