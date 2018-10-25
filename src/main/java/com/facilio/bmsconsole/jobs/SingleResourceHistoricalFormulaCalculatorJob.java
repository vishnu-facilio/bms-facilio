package com.facilio.bmsconsole.jobs;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class SingleResourceHistoricalFormulaCalculatorJob extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(SingleResourceHistoricalFormulaCalculatorJob.class.getName());
	
	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		try {
			long jobStartTime = System.currentTimeMillis();
			Map<String, Object> prop = FormulaFieldAPI.getFormulaFieldResourceJob(jc.getJobId());
			long startTime = (long) prop.get("startTime");
			long endTime = (long) prop.get("endTime");
			long resourceId = (long) prop.get("resourceId");
			long formulaId = (long) prop.get("formulaId");
			boolean isSystem = (boolean) prop.get("isSystem");
			boolean historicalAlarm = false;
			if (prop.get("historicalAlarm") != null) {
				historicalAlarm = (boolean) prop.get("historicalAlarm");
			}
			
			FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(formulaId);
			DateRange range = new DateRange(startTime, endTime);
			
			switch (formula.getTriggerTypeEnum()) {
				case POST_LIVE_READING:
					FormulaFieldAPI.optimisedHistoricalCalculation(formula, range, resourceId, isSystem, historicalAlarm);
					break;
				default:
					FormulaFieldAPI.historicalCalculation(formula, range, resourceId, isSystem, historicalAlarm);
			}
			
			String msg = "Time taken for Historical Formula calculation of formula : "+formulaId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is "+(System.currentTimeMillis() - jobStartTime);
			LOGGER.info(msg);
			
			if (AccountUtil.getCurrentOrg().getId() == 88 && !isSystem) {
				JSONObject json = new JSONObject();
				json.put("to", "error@facilio.com");
				json.put("sender", "noreply@facilio.com");
				json.put("subject", "Historical Calculation completed for Formula : "+formulaId);
				json.put("message", msg);
				
				AwsUtil.sendEmail(json);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occurred during formula calculation of single resource for job id : "+jc.getJobId(), e);
			CommonCommandUtil.emailException(SingleResourceHistoricalFormulaCalculatorJob.class.getName(), "Error occurred during formula calculation of single resource for job id : "+jc.getJobId(), e);
			throw e;
		}
		
	}
	
}
