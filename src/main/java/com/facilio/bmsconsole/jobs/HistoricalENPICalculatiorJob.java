package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.EnergyPerformanceIndicatorContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.EnergyPerformanceIndicatiorAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class HistoricalENPICalculatiorJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long enpiId = jc.getJobId();
			EnergyPerformanceIndicatorContext enpi = EnergyPerformanceIndicatiorAPI.getENPI(enpiId);
			
			long currentTime = DateTimeUtil.getCurrenTime(true);
			long startTime = DateTimeUtil.getDayStartTime(EnergyPerformanceIndicatiorAPI.HISTORICAL_CALCULATION_INTERVAL, true);
			long endTime = enpi.getSchedule().nextExecutionTime(startTime);
			
			List<ReadingContext> readings = new ArrayList<>();
			while (endTime <= currentTime) {
				ReadingContext reading = EnergyPerformanceIndicatiorAPI.calculateENPI(enpi, startTime, endTime);
				readings.add(reading);
				startTime = endTime;
				endTime = enpi.getSchedule().nextExecutionTime(startTime);
			}
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, enpi.getReadingField().getModule().getName());
			context.put(FacilioConstants.ContextNames.READINGS, readings);
			context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS, false);
			
			Chain addReadingChain = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
			addReadingChain.execute(context);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
