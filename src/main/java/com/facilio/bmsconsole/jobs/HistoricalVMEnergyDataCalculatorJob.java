package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class HistoricalVMEnergyDataCalculatorJob extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(HistoricalVMEnergyDataCalculatorJob.class.getName());
	@Override
	public void execute(JobContext jcontext) {
		long jobId = jcontext.getJobId();
		try {
			String jobName = jcontext.getJobName();
			JSONObject jobProps = BmsJobUtil.getJobProps(jobId, jobName);
	
			
			Long meterId=(Long)jobProps.get("meterId");
			Long startTime = (Long)jobProps.get("startTime");
			Long endTime = (Long)jobProps.get("endTime");
			
			Boolean updateReading= (Boolean)jobProps.get("updateReading");
			long processStartTime = System.currentTimeMillis();
			
			HistoricalLoggerContext historicalLogger = HistoricalLoggerUtil.getActiveParentHistoricalLogger(meterId);
			
			List<HistoricalLoggerContext> historicalLoggers = new ArrayList<HistoricalLoggerContext>();
			historicalLoggers.add(historicalLogger);
			
			while(historicalLoggers != null && !historicalLoggers.isEmpty())
			{
				
				List<Long> historicalLoggerIds = new ArrayList<Long>();
				
				for (HistoricalLoggerContext historicalLoggerContext : historicalLoggers) {
					EnergyMeterContext meter = DeviceAPI.getEnergyMeter(historicalLoggerContext.getParentId());
					List<Long> childMeterIds = DeviceAPI.getChildrenMeters(meter);
					if (childMeterIds == null) {
						continue;
					}
					
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField energyField=modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
					
					int interval = ReadingsAPI.getDataInterval(meter.getId(), energyField);
					DeviceAPI.insertVirtualMeterReadings(meter, childMeterIds, startTime, endTime, interval,updateReading, true);
					
					historicalLoggerContext.setStatus(HistoricalLoggerContext.Status.RESOLVED.getIntVal());
					historicalLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
					HistoricalLoggerUtil.updateHistoricalLogger(historicalLoggerContext);
					historicalLoggerIds.add(historicalLoggerContext.getId());
				}
				
				historicalLoggers = HistoricalLoggerUtil.getActiveHistoricalLogger(historicalLoggerIds);
				
			}		
			
		}
		catch (Exception e) {
			LOGGER.error("Error occurred while doing historical calculation for VM : "+jobId, e);
			e.printStackTrace();
		}
	}

}


