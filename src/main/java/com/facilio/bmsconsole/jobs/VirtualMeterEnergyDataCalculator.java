package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.StatUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.tasker.tasks.EventTransformJob;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.util.ExpressionEvaluator;

public class VirtualMeterEnergyDataCalculator extends FacilioJob {

	private static Logger logger = Logger.getLogger(EventTransformJob.class.getName());
	
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
				insertVirtualMeterReadings(virtualMeters, jc.getPeriod());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void insertVirtualMeterReadings(List<EnergyMeterContext> virtualMeters, int period) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		InsertRecordBuilder<ReadingContext> insertBuilder = new InsertRecordBuilder<ReadingContext>()
																.fields(modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_DATA_READING))
																.moduleName(FacilioConstants.ContextNames.ENERGY_DATA_READING);
		for(EnergyMeterContext meter : virtualMeters) {
			try {
				GenericSelectRecordBuilder childMeterBuilder = new GenericSelectRecordBuilder()
																	.select(FieldFactory.getVirtualMeterRelFields())
																	.table(ModuleFactory.getVirtualMeterRelModule().getTableName())
																	.andCustomWhere("VIRTUAL_METER_ID = ?", meter.getId());
				List<Map<String, Object>> childProps = childMeterBuilder.get();
				if(childProps != null && !childProps.isEmpty()) {
					List<Long> childMeterIds = new ArrayList<>();
					for(Map<String, Object> childProp : childProps) {
						childMeterIds.add((Long) childProp.get("childMeterId"));
					}
					ReadingContext virtualMeterReading = evaluateChildExpression(meter, childMeterIds, period);
					if(virtualMeterReading != null) {
						insertBuilder.addRecord(virtualMeterReading);
					}
				}
			}
			catch(Exception e) {
				logger.log(Level.WARNING, "Exception occurred during calculation of energy data for meter : "+meter.getId(), e);
			}
		}
		insertBuilder.save();
	}
	
	private ReadingContext evaluateChildExpression(EnergyMeterContext meter, List<Long> childIds, int period) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long endTime = System.currentTimeMillis();
		long startTime = endTime - (period*1000);
		SelectRecordsBuilder<ReadingContext> getReadings = new SelectRecordsBuilder<ReadingContext>()
																.beanClass(ReadingContext.class)
																.select(modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_DATA_READING))
																.moduleName(FacilioConstants.ContextNames.ENERGY_DATA_READING)
																.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentId", StringUtils.join(childIds, ","), NumberOperators.EQUALS))
																.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+", "+endTime, DateOperators.BETWEEN))
																;
		
		List<ReadingContext> readings = getReadings.get();
		if(readings != null && !readings.isEmpty()) {
			Map<Long, List<ReadingContext>> readingMap = new HashMap<>();
			
			for(ReadingContext reading : readings) {
				List<ReadingContext> readingList = readingMap.get(reading.getParentId());
				if(readingList == null) {
					readingList = new ArrayList<>();
					readingMap.put(reading.getParentId(), readingList);
				}
				readingList.add(reading);
			}
			
			for(Long childId : childIds) {
				if(!readingMap.containsKey(childId)) {
					return null;
				}
			}
			
			EnergyDataEvaluator evluator = new EnergyDataEvaluator(readingMap);
			String expression = meter.getChildMeterExpression();
			return evluator.evaluateExpression(expression);
		}
		return null;
	}
	
	private static class EnergyDataEvaluator extends ExpressionEvaluator<ReadingContext> {

		private Map<Long, List<ReadingContext>> readingMap;
		public EnergyDataEvaluator(Map<Long, List<ReadingContext>> readingMap) {
			// TODO Auto-generated constructor stub
			super.setRegEx(EnergyMeterContext.EXP_FORMAT);
			this.readingMap = readingMap;
		}
		
		@Override
		public ReadingContext getOperand(String operand) {
			// TODO Auto-generated method stub
			List<ReadingContext> readings = readingMap.get(Long.parseLong(operand));
			if(readings.size() == 1) {
				return readings.get(0);
			}
			ReadingContext aggregatedReading = new ReadingContext();
			
			List<Double> totalConsumptions = new ArrayList<Double>();
			for(ReadingContext reading : readings) {
				totalConsumptions.add((Double) reading.getReading("totalEnergyConsumption"));
			}
			aggregatedReading.addReading("totalEnergyConsumption", StatUtils.max(totalConsumptions.stream().mapToDouble(Double::doubleValue).toArray()));
			return aggregatedReading;
		}

		@Override
		public ReadingContext applyOp(String operator, ReadingContext rightOperand, ReadingContext leftOperand) {
			// TODO Auto-generated method stub
			if(operator.equals("+")) {
				ReadingContext reading = new ReadingContext();
				reading.addReading("totalEnergyConsumption", ((Double)leftOperand.getReading("totalEnergyConsumption") + (Double)leftOperand.getReading("totalEnergyConsumption")));
			}
			else if(operator.equals("-")) {
				ReadingContext reading = new ReadingContext();
				reading.addReading("totalEnergyConsumption", ((Double)leftOperand.getReading("totalEnergyConsumption") - (Double)leftOperand.getReading("totalEnergyConsumption")));
			}
			return null;
		}
		
	}
}
