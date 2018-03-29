package com.facilio.bmsconsole.jobs;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;

public class HistoricalVMEnergyDataCalculatorJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	@Override
	public void execute(JobContext jc) {
		try {
			long jobId = jc.getJobId();
			FacilioModule module=ModuleFactory.getHistoricalVMModule();

			GenericSelectRecordBuilder jobBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getHistoricalVMCalculationFields())
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getIdCondition(jobId, module));
					
			List<Map<String, Object>> jobPropsList = jobBuilder.get();
			
			if (jobPropsList==null || jobPropsList.isEmpty()) {
				
				return;
			}
			
			Map<String, Object> jobProps= jobPropsList.get(0);
			Long endTime = (Long)jobProps.get("endTime");
			long startTime = (Long)jobProps.get("startTime");
			Long meterId=(Long)jobProps.get("meterId");
			Integer interval=(Integer)jobProps.get("intervalValue");
			Boolean updateReading= (Boolean)jobProps.get("updateReading");
			long processStartTime = System.currentTimeMillis();
			List<EnergyMeterContext> vmList= DeviceAPI.getVirtualMeters(Collections.singletonList(meterId));
			if(vmList.isEmpty()) {
				return;
			}
			DeviceAPI.insertVirtualMeterReadings(vmList.get(0), startTime, endTime, interval,updateReading);
			logger.info("Time Taken for jobid "+jobId+" : "+(System.currentTimeMillis()-processStartTime));
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
