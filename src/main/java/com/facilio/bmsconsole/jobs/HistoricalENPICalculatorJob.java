package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyPerformanceIndicatorContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.EnergyPerformanceIndicatiorAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;

public class HistoricalENPICalculatorJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long enpiId = jc.getJobId();
			EnergyPerformanceIndicatorContext enpi = EnergyPerformanceIndicatiorAPI.getENPI(enpiId);
			
			long currentTime = DateTimeUtil.getCurrenTime(true);
			long startTime = EnergyPerformanceIndicatiorAPI.getStartTimeForHistoricalCalculation(enpi);
			long endTime = enpi.getSchedule().nextExecutionTime(startTime);
			
			List<ReadingContext> readings = new ArrayList<>();
			while (endTime <= currentTime) {
				ReadingContext reading = EnergyPerformanceIndicatiorAPI.calculateENPI(enpi, startTime*1000, (endTime*1000)-1);
				readings.add(reading);
				startTime = endTime;
				endTime = enpi.getSchedule().nextExecutionTime(startTime);
			}
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, enpi.getReadingField().getModule().getName());
			context.put(FacilioConstants.ContextNames.READINGS, readings);
			context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS, false);
			
			Chain addReadingChain = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
			addReadingChain.execute(context);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fieldsList = modBean.getAllFields(enpi.getReadingField().getModule().getName());
			ReadingsAPI.updateLastReading(fieldsList, Collections.singletonList(readings.get(readings.size() - 1)), null);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e.getMessage(), e);
			CommonCommandUtil.emailException("Historical EnPI calculation failed for : "+jc.getJobId(), e);
		}
	}

}
