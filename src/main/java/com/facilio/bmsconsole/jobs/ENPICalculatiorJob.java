package com.facilio.bmsconsole.jobs;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.EnergyPerformanceIndicatorContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.FacilioExpressionParser;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.EnergyPerformanceIndicatiorAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ENPICalculatiorJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long enpiId = jc.getJobId();
			
			EnergyPerformanceIndicatorContext enpi = EnergyPerformanceIndicatiorAPI.getENPI(enpiId);
			long endTime = System.currentTimeMillis();
			List<Map<String, Object>> lastReadings = FieldUtil.getLastReading(Collections.singletonList(enpi.getSpaceId()), Collections.singletonList(enpi.getReadingFieldId()));
			long startTime = (long) lastReadings.get(0).get("ttime");
			
			ReadingContext reading = EnergyPerformanceIndicatiorAPI.calculateENPI(enpi, startTime, endTime);
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, enpi.getReadingField().getModule().getName());
			context.put(FacilioConstants.ContextNames.READING, reading);
			
			Chain addReadingChain = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
			addReadingChain.execute(context);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
