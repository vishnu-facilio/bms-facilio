package com.facilio.bmsconsole.jobs;

import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class VirtualMeterEnergyDataCalculator extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_METER);
			SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
																			.select(fields)
																			.moduleName(FacilioConstants.ContextNames.ENERGY_METER)
																			.beanClass(EnergyMeterContext.class)
																			.andCustomWhere("IS_VIRTUAL = ?", true);
			
			List<EnergyMeterContext> virtualMeters = selectBuilder.get();
			if(virtualMeters != null && !virtualMeters.isEmpty()) {
				
				long endTime = System.currentTimeMillis();
				long startTime = endTime - (jc.getPeriod()*1000);
				
				ReportsUtil.insertVirtualMeterReadings(virtualMeters, startTime,endTime);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
}
