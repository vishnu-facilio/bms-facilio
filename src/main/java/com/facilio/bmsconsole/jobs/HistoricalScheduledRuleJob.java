package com.facilio.bmsconsole.jobs;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HistoricalScheduledRuleJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(HistoricalScheduledRuleJob.class.getName());
	
	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		long jobStartTime = System.currentTimeMillis();
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(jc.getJobId(), true);
		
		JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
		long startTime = (long) props.get("startTime") / 1000;
		long endTime = (long) props.get("endTime") / 1000;
		
		LOGGER.info("Historical execution of rule : "+rule.getId());
		
		long currentStartTime = rule.getSchedule().nextExecutionTime(startTime);
		
		Map<Long, ReadingRuleAlarmMeta> alarmMetaMap = new HashMap<>();
		while (currentStartTime <= endTime) {
			LOGGER.info("Gonna run for time : "+currentStartTime + " :: " + DateTimeUtil.getDateTime(currentStartTime, true));
			FacilioContext context = new FacilioContext();
			if (rule instanceof ReadingRuleContext) {
				context.put(FacilioConstants.ContextNames.READING_RULE_ALARM_META, alarmMetaMap);
			}
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID, rule.getId());
			context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, DateTimeUtil.getHourStartTimeOf(currentStartTime * 1000)); //TODO hourStartTime should be changed to direct execution time later
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
