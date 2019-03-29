package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class DummyVMCalculator extends FacilioJob {

	private Logger log = LogManager.getLogger(DummyVMCalculator.class.getName());

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
			log.info("Exception occurred ", e);
		}
	}

}
