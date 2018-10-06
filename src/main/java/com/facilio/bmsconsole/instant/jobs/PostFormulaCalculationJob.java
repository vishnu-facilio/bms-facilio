package com.facilio.bmsconsole.instant.jobs;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.InstantJob;
import com.facilio.time.SecondsChronoUnit;

public class PostFormulaCalculationJob extends InstantJob {
	private static final Logger LOGGER = LogManager.getLogger(PostFormulaCalculationJob.class.getName());
	@Override
	public void execute(FacilioContext context) {
		// TODO Auto-generated method stub
		ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
		FormulaFieldContext formula = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		try {
			LOGGER.info("Gonna calculate post formula for : "+reading.getParentId()+"_"+formula.getName());
			List<ReadingContext> formulaReadings = null;
			if (reading.isNewReading()) {
				formulaReadings = calculateNewFormula(formula, reading);
			}
			else {
				formulaReadings = updateFormula(formula, reading);
			}
			
			if (formulaReadings != null && !formulaReadings.isEmpty()) {
				FacilioContext formulContext = new FacilioContext();
				formulContext.put(FacilioConstants.ContextNames.MODULE_NAME, formula.getReadingField().getModule().getName());
				formulContext.put(FacilioConstants.ContextNames.READINGS, formulaReadings);
				Chain addReading = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
				addReading.execute(formulContext);
			}
		}
		catch (Exception e) {
			LOGGER.error("Error occurred while calculating of "+formula+" for reading "+reading, e);
			CommonCommandUtil.emailException("PostFormulaCalculationJob", "Error occurred while calculating of "+formula.getName(), e, "Error occurred while calculating of "+formula+" for reading "+reading);
		}
	}
	
	private List<ReadingContext> calculateNewFormula(FormulaFieldContext formula, ReadingContext reading) throws Exception {
		if (AccountUtil.getCurrentOrg().getId() == 135) {
			LOGGER.info("Calculating new formula for : "+formula.getName()+" for resource : "+reading.getParentId());
			LOGGER.info("Reading : "+reading);
		}
		ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(reading.getParentId(), formula.getReadingField());
		if (AccountUtil.getCurrentOrg().getId() == 135) {
			LOGGER.info("RDM : "+meta);
		}
		List<DateRange> intervals = DateTimeUtil.getTimeIntervals(meta.getTtime()+1, reading.getTtime(), formula.getInterval());
		LOGGER.info("Intervals for calculation of : "+formula.getName()+" for "+reading.getParentId()+" is "+intervals);
		if (intervals != null) { //No need to calculate if RDM time is greater
			long startTime = System.currentTimeMillis();
			if (intervals.size() > 1) { //If more than one interval has to be calculated, only the last interval will be calculated here. Previous intervals will be done via scheduler
				long minTime = intervals.get(0).getStartTime();
				long maxTime = intervals.get(intervals.size() - 2).getEndTime();
				FormulaFieldAPI.calculateHistoricalDataForSingleResource(formula.getId(), reading.getParentId(), new DateRange(minTime, maxTime), true, true);
				intervals = Collections.singletonList(intervals.get(intervals.size() - 1));
			}
			List<ReadingContext> formulaReadings = FormulaFieldAPI.calculateFormulaReadings(reading.getParentId(), formula.getReadingField().getModule().getName(), formula.getReadingField().getName(), intervals, formula.getWorkflow(), false, false);
			LOGGER.info("Time taken for formula calculation of : "+formula.getName()+" for "+reading.getParentId()+" is "+(System.currentTimeMillis() - startTime));
			return formulaReadings;
		}
		return null;
	}
	
	private List<ReadingContext>  updateFormula(FormulaFieldContext formula, ReadingContext reading) throws Exception {
		long ttime = reading.getTtime();
		ZonedDateTime zdt = DateTimeUtil.getDateTime(ttime);
		zdt = zdt.truncatedTo(new SecondsChronoUnit(formula.getInterval() * 60));
		long startTime = DateTimeUtil.getMillis(zdt, true);
		long endTime = (startTime + (formula.getInterval() * 60 * 1000)) - 1;
		ReadingContext oldReading = getOldReading(formula, startTime, endTime);
		List<DateRange> intervals = Collections.singletonList(new DateRange(startTime, endTime));
		List<ReadingContext> formulaReadings = FormulaFieldAPI.calculateFormulaReadings(reading.getParentId(), formula.getReadingField().getModule().getName(), formula.getReadingField().getName(), intervals, formula.getWorkflow(), false, false);
		if (formulaReadings != null && !formulaReadings.isEmpty()) {
			ReadingContext newReading = formulaReadings.get(0);
			if (oldReading != null) {
				newReading.setTtime(oldReading.getTtime());
				newReading.setId(oldReading.getId());
			}
			return Collections.singletonList(newReading);
		}
		return null;
	}
	
	private ReadingContext getOldReading (FormulaFieldContext formula, long startTime, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = formula.getReadingField().getModule();
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField ttime = fieldMap.get("ttime");
		
		SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
															.select(fields)
															.module(module)
															.beanClass(ReadingContext.class)
															.andCondition(CriteriaAPI.getCondition(ttime, startTime+","+endTime, DateOperators.BETWEEN))
															;
		List<ReadingContext> readings = builder.get();
		if (readings != null && !readings.isEmpty()) {
			return readings.get(0);
		}
		return null;
	}
}
