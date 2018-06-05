package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;

public class HistoricalFormulaFieldCalculatorJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long formulaId = jc.getJobId();
			FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(formulaId);
			logger.log(Level.INFO, "Calculating EnPI for "+formula.getName());
			DateRange range = getRange(jc, formula);
			
			if (range == null) {
				logger.log(Level.SEVERE, "Historical Formula calculation not done for formula : "+formulaId+", because no range specified");
			}
			
			Map<Long, Long> intervals = getIntervals(formula, range);
			if (intervals != null && !intervals.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<ReadingContext> readings = new ArrayList<>();
				List<ReadingContext> lastReadings = new ArrayList<>();
				for (Long resourceId : formula.getMatchedResources()) {
					List<ReadingContext> currentReadings = FormulaFieldAPI.calculateFormulaReadings(resourceId, formula.getReadingField().getName(), intervals, formula.getWorkflow());
					if (currentReadings != null && !currentReadings.isEmpty()) {
						readings.addAll(currentReadings);
						lastReadings.add(readings.get(readings.size() - 1));
					}
				}
				
				if (!readings.isEmpty()) {
					deleteOlderData(range.getStartTime(), range.getEndTime(), formula.getMatchedResources(), formula.getReadingField().getModule().getName());
					
					FacilioContext context = new FacilioContext();
					context.put(FacilioConstants.ContextNames.MODULE_NAME, formula.getReadingField().getModule().getName());
					context.put(FacilioConstants.ContextNames.READINGS, readings);
					context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS, false);
					
					Chain addReadingChain = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
					addReadingChain.execute(context);
				
					List<FacilioField> fieldsList = modBean.getAllFields(formula.getReadingField().getModule().getName());
					ReadingsAPI.updateReadingDataMeta(fieldsList, lastReadings, null);
					
					List<FormulaFieldContext> dependentFormulas = FormulaFieldAPI.getFormulasDependingOnFields(TriggerType.SCHEDULE, Collections.singletonList(formula.getReadingField().getId()));
					if (dependentFormulas != null && !dependentFormulas.isEmpty()) {
						for (FormulaFieldContext currentFormula : dependentFormulas) {
							List<Long> dependentFieldIds = currentFormula.getWorkflow().getDependentFieldIds();
							if (dependentFieldIds.contains(formula.getReadingField().getFieldId())) {
								FormulaFieldAPI.recalculateHistoricalData(currentFormula, currentFormula.getReadingField(), range);
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e.getMessage(), e);
			CommonCommandUtil.emailException("Historical EnPI calculation failed for : "+jc.getJobId(), e);
		}
	}
	
	private int deleteOlderData(long startTime, long endTime, List<Long> parentIds, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));
		FacilioField parentId = fieldMap.get("parentId");
		FacilioField ttime = fieldMap.get("ttime");
		DeleteRecordBuilder<ReadingContext> deleteBuilder = new DeleteRecordBuilder<ReadingContext>()
																.module(module)
																.andCondition(CriteriaAPI.getCondition(parentId, parentIds, PickListOperators.IS))
																.andCondition(CriteriaAPI.getCondition(ttime, startTime+","+endTime, DateOperators.BETWEEN))
																;
		return deleteBuilder.delete();
	}
	
	private Map<Long, Long> getIntervals(FormulaFieldContext formula, DateRange range) {
		switch (formula.getTriggerTypeEnum()) {
			case LIVE_READING:
				return DateTimeUtil.getTimeIntervals(range.getStartTime(), range.getEndTime(), formula.getInterval());
			case SCHEDULE:
				ScheduleInfo schedule = FormulaFieldAPI.getSchedule(formula.getFrequencyEnum());
				return DateTimeUtil.getTimeIntervals(range.getStartTime(), range.getEndTime(), schedule);
		}
		return null;
	}
	
	private DateRange getRange(JobContext jc, FormulaFieldContext formula) throws Exception {
		JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
		long currentTime = DateTimeUtil.getCurrenTime();
		DateRange range = null;
		switch (formula.getTriggerTypeEnum()) {
			case LIVE_READING:
				if (props == null || props.isEmpty()) {
					return null;
				}
				range = FieldUtil.getAsBeanFromJson(props, DateRange.class);
				if (range.getStartTime() == -1) {
					return null;
				}
				if (range.getEndTime() == -1) {
					range.setEndTime(currentTime);
				}
				break;
			case SCHEDULE:
				if (props == null || props.isEmpty()) {
					range = new DateRange(FormulaFieldAPI.getStartTimeForHistoricalCalculation(formula), currentTime);
				}
				else {
					range = FieldUtil.getAsBeanFromJson(props, DateRange.class);
					if (range.getStartTime() == -1) {
						range = new DateRange(FormulaFieldAPI.getStartTimeForHistoricalCalculation(formula), currentTime);
					}
					if (range.getEndTime() == -1) {
						range.setEndTime(currentTime);
					}
				}
				break;
		}
		return range;
	}
}
