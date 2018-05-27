package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
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
			long enpiId = jc.getJobId();
			FormulaFieldContext enpi = FormulaFieldAPI.getENPI(enpiId);
			
			logger.log(Level.INFO, "Calculating EnPI for "+enpi.getName());
			
			long currentTime = DateTimeUtil.getCurrenTime();
			long startTime = FormulaFieldAPI.getStartTimeForHistoricalCalculation(enpi);
			ScheduleInfo schedule = FormulaFieldAPI.getSchedule(enpi.getFrequencyEnum());
			Map<Long, Long> intervals = DateTimeUtil.getTimeIntervals(startTime, currentTime, schedule);
			
			if (intervals != null && !intervals.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<ReadingContext> readings = new ArrayList<>();
				List<ReadingContext> lastReadings = new ArrayList<>();
				for (Long resourceId : enpi.getMatchedResources()) {
					List<ReadingContext> currentReadings = FormulaFieldAPI.calculateFormulaReadings(resourceId, enpi.getReadingField().getName(), intervals, enpi.getWorkflow());
					if (currentReadings != null) {
						readings.addAll(currentReadings);
						lastReadings.add(readings.get(readings.size() - 1));
					}
				}
				
				if (!readings.isEmpty()) {
					deleteOlderData(startTime, currentTime, enpi.getMatchedResources(), enpi.getReadingField().getModule().getName());
					
					FacilioContext context = new FacilioContext();
					context.put(FacilioConstants.ContextNames.MODULE_NAME, enpi.getReadingField().getModule().getName());
					context.put(FacilioConstants.ContextNames.READINGS, readings);
					context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS, false);
					
					Chain addReadingChain = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
					addReadingChain.execute(context);
				
					List<FacilioField> fieldsList = modBean.getAllFields(enpi.getReadingField().getModule().getName());
					ReadingsAPI.updateReadingDataMeta(fieldsList, lastReadings, null);
					
					List<FormulaFieldContext> allEnPIs = FormulaFieldAPI.getAllENPIs();
					for (FormulaFieldContext currentEnPI : allEnPIs) {
						if (currentEnPI.getId() != enpi.getId()) {
							List<Long> dependentFieldIds = currentEnPI.getWorkflow().getDependentFieldIds();
							if (dependentFieldIds.contains(enpi.getReadingField().getFieldId())) {
								FormulaFieldAPI.recalculateHistoricalData(currentEnPI, currentEnPI.getReadingField());
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
}
