package com.facilio.bmsconsole.jobs;

import java.util.List;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class DummyVMCalculator extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long endTime = System.currentTimeMillis();
			long startTime = endTime - (jc.getPeriod()*1000);
			
			System.out.println("Before fetching VMs");
			System.out.println(startTime+"::"+endTime);
			
			List<EnergyMeterContext> virtualMeters = DeviceAPI.getAllVirtualMeters();
			if(virtualMeters != null && !virtualMeters.isEmpty()) {
				
				endTime = System.currentTimeMillis();
				startTime = endTime - (jc.getPeriod()*1000);
				
				System.out.println("After fetching VMs");
				System.out.println(startTime+"::"+endTime);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
