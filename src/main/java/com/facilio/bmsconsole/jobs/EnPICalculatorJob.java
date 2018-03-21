package com.facilio.bmsconsole.jobs;

import java.time.Month;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.EnergyPerformanceIndicatorContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.EnergyPerformanceIndicatiorAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class EnPICalculatorJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			List<Integer> types = getFrequencyTypesToBeFetched();
			List<EnergyPerformanceIndicatorContext> enPIs = EnergyPerformanceIndicatiorAPI.getEnPIsOfType(types);
			List<Long> calculatedFieldIds = new ArrayList<>();

			long endTime = DateTimeUtil.getDayStartTime() - 1;
			while (!enPIs.isEmpty()) {
				Iterator<EnergyPerformanceIndicatorContext> it = enPIs.iterator();
				while (it.hasNext()) {
					EnergyPerformanceIndicatorContext enpi = it.next();
					if(isCalculatable(enpi, calculatedFieldIds)) {
						List<Map<String, Object>> lastReadings = FieldUtil.getLastReading(Collections.singletonList(enpi.getSpaceId()), Collections.singletonList(enpi.getReadingFieldId()));
						long startTime = (long) lastReadings.get(0).get("ttime") + 1;
						
						ReadingContext reading = EnergyPerformanceIndicatiorAPI.calculateENPI(enpi, startTime, endTime);
						
						FacilioContext context = new FacilioContext();
						context.put(FacilioConstants.ContextNames.MODULE_NAME, enpi.getReadingField().getModule().getName());
						context.put(FacilioConstants.ContextNames.READING, reading);
						
						Chain addReadingChain = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
						addReadingChain.execute(context);
						
						calculatedFieldIds.add(enpi.getReadingFieldId());
						it.remove();
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CommonCommandUtil.emailException("EnPI Calculation job failed", e);
		}
	}
	
	private boolean isCalculatable(EnergyPerformanceIndicatorContext enpi, List<Long> calculatedFieldIds) {
		if (enpi.getDependentFields() != null && !enpi.getDependentFields().isEmpty()) {
			for(FacilioField field : enpi.getDependentFields()) {
				if (field.getModule().getTypeEnum() == ModuleType.ENPI && !calculatedFieldIds.contains(field.getFieldId())) {
					return false;
				}
			}
		}
		return true;
	}
	
	private List<Integer> getFrequencyTypesToBeFetched() {
		List<Integer> types = new ArrayList<Integer>();
		types.add(FacilioFrequency.DAILY.getValue());
		
		ZonedDateTime zdt = DateTimeUtil.getDateTime();
		
		if (zdt.getDayOfWeek() == DateTimeUtil.getWeekFields().getFirstDayOfWeek()) {
			types.add(FacilioFrequency.WEEKLY.getValue());
		}
		
		if (zdt.getDayOfMonth() == 1) {
			types.add(FacilioFrequency.MONTHLY.getValue());
			
			if (zdt.getMonth() == Month.JANUARY) {
				types.add(FacilioFrequency.QUARTERTLY.getValue());
				types.add(FacilioFrequency.HALF_YEARLY.getValue());
				types.add(FacilioFrequency.ANNUALLY.getValue());
			}
			else if (zdt.getMonth() == Month.JULY) {
				types.add(FacilioFrequency.QUARTERTLY.getValue());
				types.add(FacilioFrequency.HALF_YEARLY.getValue());
			}
			else if (zdt.getMonth() == Month.APRIL || zdt.getMonth() == Month.OCTOBER) {
				types.add(FacilioFrequency.QUARTERTLY.getValue());
			}
		}
		return types;
	}

}
