package com.facilio.bmsconsole.jobs;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class SingleResourceHistoricalFormulaCalculatorJob extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(SingleResourceHistoricalFormulaCalculatorJob.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long jobStartTime = System.currentTimeMillis();
			Map<String, Object> prop = FormulaFieldAPI.getFormulaFieldResourceJob(jc.getJobId());
			long startTime = (long) prop.get("startTime");
			long endTime = (long) prop.get("endTime");
			long resourceId = (long) prop.get("resourceId");
			long formulaId = (long) prop.get("formulaId");
			
			FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(formulaId);
			DateRange range = new DateRange(startTime, endTime);
			FormulaFieldAPI.historicalCalculation(formula, range, resourceId);
			LOGGER.info("Time taken for Historical Formula calculation of formula : "+formulaId+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is "+(System.currentTimeMillis() - jobStartTime));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occurred during formula calculation of single resource for job id : "+jc.getJobId(), e);
		}
		
	}
	
}
