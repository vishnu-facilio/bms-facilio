package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.SecondsChronoUnit;

public class VirtualMeterEnergyDataCalculator extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(VirtualMeterEnergyDataCalculator.class.getName());

	private static final int CALCULATION_DELAY = 60*1000; //One minute delay
	
	@Override
	public void execute(JobContext jc) {
		try {
			List<EnergyMeterContext> virtualMeters = DeviceAPI.getAllVirtualMeters();
			if(virtualMeters == null || virtualMeters.isEmpty()) {
				return;
			}
			int period=jc.getPeriod();
			int minutesInterval=period/60;
			long endTime = DateTimeUtil.getDateTime(System.currentTimeMillis()).truncatedTo(getDefaultDataInterval()).toInstant().toEpochMilli();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField deltaField= modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
			for(EnergyMeterContext meter:virtualMeters) {
				try {
					ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(meter.getId(), deltaField);
					long startTime = meta.getTtime()+1;
					DeviceAPI.insertVirtualMeterReadings(meter, startTime,endTime,minutesInterval, true, false);
				}
				catch (Exception e) {
					LOGGER.info("Exception occurred ", e);
					CommonCommandUtil.emailException("VMEnergyDataCalculatorForMeter", "VM Calculation failed for meter : "+meter.getId(), e);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Exception occurred ", e);
			CommonCommandUtil.emailException("VMEnergyDataCalculator", "VM Calculation failed", e);
		}
	}
	
	private SecondsChronoUnit getDefaultDataInterval() throws Exception {
		Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		String defaultIntervalProp = orgInfo.get(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		SecondsChronoUnit defaultAdjustUnit = null;
		if (defaultIntervalProp == null || defaultIntervalProp.isEmpty()) {
			defaultAdjustUnit = ReadingsAPI.DEFAULT_DATA_INTERVAL_UNIT;
		}
		else {
			defaultAdjustUnit = new SecondsChronoUnit(Long.parseLong(defaultIntervalProp) * 60);
		}
		return defaultAdjustUnit;
	}
}
