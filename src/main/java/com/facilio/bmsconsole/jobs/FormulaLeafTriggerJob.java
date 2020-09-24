package com.facilio.bmsconsole.jobs;

import java.time.Month;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldResourceStatusContext;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.FormulaFieldResourceStatusAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.modules.FieldUtil;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class FormulaLeafTriggerJob extends FacilioJob{
	
	private static final Logger LOGGER = Logger.getLogger(FormulaLeafTriggerJob.class.getName());
	
	public void execute(JobContext jc) throws Exception {
		
		List<Integer> types = getFrequencyTypesToBeFetched();
		LOGGER.log(Level.INFO, "Frequencies to be fetched for Scheduled Formula Calculation in FormulaLeafTriggerJob: "+types);

		if(types != null && !types.isEmpty()) {
			List<FormulaFieldResourceStatusContext> formulaResourcesAtLeaf = FormulaFieldResourceStatusAPI.getLeafFormulaFieldResourceStatusByFrequency(types);		
			if (formulaResourcesAtLeaf != null && !formulaResourcesAtLeaf.isEmpty()) {	
				List<FormulaFieldResourceStatusContext> formulaeForActiveResources = FormulaFieldResourceStatusAPI.checkForActiveResourcesInFormulae(formulaResourcesAtLeaf);
				if (formulaeForActiveResources != null && !formulaeForActiveResources.isEmpty()) {	
					for(FormulaFieldResourceStatusContext activeFormulaResourceAtLeaf:formulaeForActiveResources)
					{		
						activeFormulaResourceAtLeaf.setStatus(FormulaFieldResourceStatusContext.Status.IN_QUEUE.getIntVal());
						NewTransactionService.newTransactionWithReturn(() -> FormulaFieldResourceStatusAPI.updateCompletedFormulaFieldResourceStatus(activeFormulaResourceAtLeaf));
						
						FacilioContext context = new FacilioContext();
						context.put(FacilioConstants.ContextNames.FORMULA_RESOURCE_JOB_ID, activeFormulaResourceAtLeaf.getId());
						context.put(FacilioConstants.ContextNames.FORMULA_FREQUENCY_TYPES, types);
						FacilioTimer.scheduleInstantJob("formula","FormulaFieldCalculatorJob", context);
					}	
				}		
			}	
		}
		
	}
	
	private List<Integer> getFrequencyTypesToBeFetched() {
		List<Integer> types = new ArrayList<Integer>();
		
		ZonedDateTime zdt = DateTimeUtil.getDateTime();
		int currentMinute = zdt.getMinute();
		int roundedMinute = FormulaFieldAPI.getRoundedMinute(currentMinute, 5);
		
		if(roundedMinute%10 == 0) {
			types.add(FacilioFrequency.TEN_MINUTES.getValue());
		}
		if(roundedMinute%15 == 0) {
			types.add(FacilioFrequency.FIFTEEN_MINUTES.getValue());
		}
		if(roundedMinute%60 == 0) {
			types.add(FacilioFrequency.HOURLY.getValue());
		}

		if (zdt.getHour() == 0) {
			types.add(FacilioFrequency.DAILY.getValue());
			if (zdt.getDayOfWeek() == DateTimeUtil.getWeekFields().getFirstDayOfWeek()) {
				types.add(FacilioFrequency.WEEKLY.getValue());
			}
			
			if (zdt.getDayOfMonth() == 1) {
				types.add(FacilioFrequency.MONTHLY.getValue());
				
				if (zdt.getMonth() == Month.JANUARY) {
					types.add(FacilioFrequency.QUARTERTLY.getValue());
					types.add(FacilioFrequency.HALF_YEARLY.getValue());
					types.add(FacilioFrequency.ANNUALLY.getValue());
				}
				else if (zdt.getMonth() == Month.JULY) {
					types.add(FacilioFrequency.QUARTERTLY.getValue());
					types.add(FacilioFrequency.HALF_YEARLY.getValue());
				}
				else if (zdt.getMonth() == Month.APRIL || zdt.getMonth() == Month.OCTOBER) {
					types.add(FacilioFrequency.QUARTERTLY.getValue());
				}
			}
		}
		return types;
	}
	
	

}


