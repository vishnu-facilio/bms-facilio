package com.facilio.bmsconsole.jobs;

import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class VirtualMeterEnergyDataCalculator extends FacilioJob {

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
			long endTime = System.currentTimeMillis() - CALCULATION_DELAY;
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField deltaField= modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
			for(EnergyMeterContext meter:virtualMeters) {
				try {
					ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(meter.getId(), deltaField);
					long startTime = meta.getTtime()+1;
					DeviceAPI.insertVirtualMeterReadings(meter, startTime,endTime,minutesInterval, true);
				}
				catch (Exception e) {
					e.printStackTrace();
					CommonCommandUtil.emailException("VM Calculation failed for meter : "+meter.getId(), e);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CommonCommandUtil.emailException("VM Calculation failed", e);
		}
	}
}
