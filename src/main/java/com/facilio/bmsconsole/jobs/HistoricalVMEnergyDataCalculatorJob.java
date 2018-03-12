package com.facilio.bmsconsole.jobs;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;

public class HistoricalVMEnergyDataCalculatorJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long vmId = jc.getJobId();
			EnergyMeterContext meter = DeviceAPI.getEnergyMeter(vmId);
			long endTime = System.currentTimeMillis();
			long startTime = DateTimeUtil.getMonthStartTime(DeviceAPI.VM_HISTORICAL_DATA_CALCULATION_INTERVAL);
			
			long processStartTime = System.currentTimeMillis();
			DeviceAPI.insertVirtualMeterReadings(Collections.singletonList(meter), startTime, endTime, 15);
			logger.info("Time Taken : "+(System.currentTimeMillis()-processStartTime));
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
