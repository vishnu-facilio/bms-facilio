package com.facilio.bmsconsole.jobs;

import java.util.List;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class VirtualMeterEnergyDataCalculator extends FacilioJob {

	private static final int CALCULATION_DELAY = 60*1000; //One minute delay
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			List<EnergyMeterContext> virtualMeters = DeviceAPI.getAllVirtualMeters();
			if(virtualMeters != null && !virtualMeters.isEmpty()) {
				
				long endTime = System.currentTimeMillis() - CALCULATION_DELAY;
				long startTime = endTime - (jc.getPeriod()*1000);
				
				ReportsUtil.insertVirtualMeterReadings(virtualMeters, startTime,endTime);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	
	
	
}
