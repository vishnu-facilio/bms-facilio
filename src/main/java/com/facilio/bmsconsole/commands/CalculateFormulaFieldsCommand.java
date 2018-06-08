package com.facilio.bmsconsole.commands;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.time.SecondsChronoUnit;

public class CalculateFormulaFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, ReadingDataMeta> readingDataMeta = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.READING_DATA_META);
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		if (readingDataMeta != null && !readingDataMeta.isEmpty() && readingMap != null && !readingMap.isEmpty()) {
			Collection<ReadingDataMeta> metaList = readingDataMeta.values();
			Set<Long> fieldIds = metaList.stream().map(meta -> meta.getField().getId()).collect(Collectors.toSet());
			
			List<FormulaFieldContext> formulaFields = FormulaFieldAPI.getFormulasDependingOnFields(TriggerType.LIVE_READING, fieldIds);
			if (formulaFields != null && !formulaFields.isEmpty()) {
				Map<String, List<ReadingContext>> formulaMap = new HashMap<>();
				Set<String> completedFormulas = new HashSet<>();
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for (Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
					String moduleName = entry.getKey();
					if (moduleName != null && !moduleName.isEmpty()) {
						List<ReadingContext> readings = entry.getValue();
						Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(entry.getKey()));
						if (readings != null && !readings.isEmpty()) {
							for (ReadingContext reading : readings) {
								calculateDependentFormulas(reading, formulaMap, completedFormulas, formulaFields, fieldMap);
							}
						}
					}
				}
				if (!formulaMap.isEmpty()) {
					FacilioContext formulContext = new FacilioContext();
					formulContext.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.ENERGY_DATA_READING );
					formulContext.put(FacilioConstants.ContextNames.READINGS_MAP, formulaMap);
					Chain addReading = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
					addReading.execute(formulContext);
				}
			}
		}
		
		return false;
	}

	private void calculateDependentFormulas(ReadingContext reading, Map<String, List<ReadingContext>> formulaMap, Set<String> completedFormulas, List<FormulaFieldContext> formulas, Map<String, FacilioField> fieldMap) throws Exception {
		// TODO Auto-generated method stub
		for (FormulaFieldContext formula : formulas) {
			if (formula.getMatchedResources().contains(reading.getParentId())) {
				if (reading.isNewReading()) {
					calculateNewFormula(formula, reading, formulaMap, completedFormulas, fieldMap);
				}
				else {
					updateFormula(formula, reading, formulaMap, completedFormulas, fieldMap);
				}
			}
		}
	}
	
	private void calculateNewFormula(FormulaFieldContext formula, ReadingContext reading, Map<String, List<ReadingContext>> formulaMap, Set<String> completedFormulas, Map<String, FacilioField> fieldMap) throws Exception {
		String completedKey = formula.getId()+"|"+reading.getParentId();
		if (!completedFormulas.contains(completedKey)) {
			Map<String, Object> readingData = reading.getData();
			if (readingData != null && !readingData.isEmpty()) {
				for (String fieldName : readingData.keySet()) {
					FacilioField field = fieldMap.get(fieldName);
					if (formula.getWorkflow().getDependentFieldIds().contains(field.getId())) {
						ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(reading.getParentId(), formula.getReadingField());
						Map<Long, Long> intervals = DateTimeUtil.getTimeIntervals(meta.getTtime(), System.currentTimeMillis(), formula.getInterval());
						List<ReadingContext> formulaReadings = FormulaFieldAPI.calculateFormulaReadings(reading.getParentId(), formula.getReadingField().getName(), intervals, formula.getWorkflow());
						if (formulaReadings != null && !formulaReadings.isEmpty()) {
							List<ReadingContext> existingReadings = formulaMap.get(formula.getReadingField().getModule().getName());
							if (existingReadings == null) {
								formulaMap.put(formula.getReadingField().getModule().getName(), formulaReadings);
							}
							else {
								existingReadings.addAll(formulaReadings);
							}
						}
						completedFormulas.add(completedKey);
						break;
					}
				}
			}
		}
	}
	
	private void updateFormula(FormulaFieldContext formula, ReadingContext reading, Map<String, List<ReadingContext>> formulaMap, Set<String> completedFormulas, Map<String, FacilioField> fieldMap) throws Exception {
		long ttime = reading.getTtime();
		ZonedDateTime zdt = DateTimeUtil.getDateTime(ttime);
		zdt = zdt.truncatedTo(new SecondsChronoUnit(formula.getInterval() * 60));
		long startTime = DateTimeUtil.getMillis(zdt, true);
		long endTime = (startTime + (formula.getInterval() * 60 * 1000)) - 1;
		String completedKey = formula.getId()+"|"+reading.getParentId()+"|"+startTime+"|"+endTime;
		if (!completedFormulas.contains(completedKey)) {
			Map<String, Object> readingData = reading.getData();
			if (readingData != null && !readingData.isEmpty()) {
				ReadingContext oldReading = null;
				for (String fieldName : readingData.keySet()) {
					FacilioField field = fieldMap.get(fieldName);
					if (formula.getWorkflow().getDependentFieldIds().contains(field.getId())) {
						oldReading = getOldReading(formula, startTime, endTime);
						Map<Long, Long> intervals = Collections.singletonMap(startTime, endTime);
						List<ReadingContext> formulaReadings = FormulaFieldAPI.calculateFormulaReadings(reading.getParentId(), formula.getReadingField().getName(), intervals, formula.getWorkflow());
						if (formulaReadings != null && !formulaReadings.isEmpty()) {
							ReadingContext newReading = formulaReadings.get(0);
							if (oldReading != null) {
								newReading.setTtime(oldReading.getTtime());
								newReading.setId(oldReading.getId());
							}
							List<ReadingContext> existingReadings = formulaMap.get(formula.getReadingField().getModule().getName());
							if (existingReadings == null) {
								existingReadings = new ArrayList<>();
								formulaMap.put(formula.getReadingField().getModule().getName(), existingReadings);
							}
							existingReadings.add(newReading);
						}
						completedFormulas.add(completedKey);
						break;
					}
				}
			}
		}
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
