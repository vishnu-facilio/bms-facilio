package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

@Deprecated
public class AggregatedEnergyConsumptionUtil {
	
	public static final String AGGREGATED_READINGS_TABLE_NAME = "Aggregated_Readings";	
	public static final String AGGREGATED_ENERGY_CONSUMPTION_READING_NAME = "aggregatedEnergyConsumption";
	public static final String AGGREGATED_ENERGY_CONSUMPTION_MODULE_NAME = "aggregatedenergyconsumption";

	private static final Logger LOGGER = Logger.getLogger(AggregatedEnergyConsumptionUtil.class.getName());

	public static void checkAddorUpdateOfAggregatedEnergyConsumptionData(LinkedHashMap<String, ReadingContext> finalDeltaReadingsMap, LinkedHashMap<String,ReadingContext> alreadyPresentAggregatedReadingsMap) throws Exception {	
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_MODULE_NAME);
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		
		FacilioField aggregatedEnergyConsumptionField = fieldMap.get(AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_READING_NAME);
		aggregatedEnergyConsumptionField.setModule(module);
		
		FacilioModule energyDataModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
		Map<String,FacilioField> energyDataFieldMap = FieldFactory.getAsMap(modBean.getAllFields(energyDataModule.getName()));
		FacilioField readingDeltaField = energyDataFieldMap.get("totalEnergyConsumptionDelta");
		readingDeltaField.setModule(energyDataModule);
		
		List<ReadingContext> finalReadings = new ArrayList<ReadingContext>();
		for(String parentVsTtimeKey:finalDeltaReadingsMap.keySet()) 
		{
			if(alreadyPresentAggregatedReadingsMap != null && MapUtils.isNotEmpty(alreadyPresentAggregatedReadingsMap) && 
					alreadyPresentAggregatedReadingsMap.containsKey(parentVsTtimeKey) && alreadyPresentAggregatedReadingsMap.get(parentVsTtimeKey).getId() != -1) 
			{
				ReadingContext alreadyPresentReadingContext = alreadyPresentAggregatedReadingsMap.get(parentVsTtimeKey);
				ReadingContext energyDeltaReadingContext = finalDeltaReadingsMap.get(parentVsTtimeKey);
				
				Object aggregatedDeltaConsumption = energyDeltaReadingContext.getReading(readingDeltaField.getName());
				alreadyPresentReadingContext.addReading(AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_READING_NAME, aggregatedDeltaConsumption);
				finalReadings.add(alreadyPresentReadingContext);
			}
			else 
			{
				ReadingContext newReadingContext = new ReadingContext();
				ReadingContext energyDeltaReadingContext = finalDeltaReadingsMap.get(parentVsTtimeKey);
				newReadingContext.setParentId(energyDeltaReadingContext.getParentId());
				newReadingContext.setTtime(energyDeltaReadingContext.getTtime());
				newReadingContext.setModuleId(module.getModuleId());
				
				Object aggregatedDeltaConsumption = energyDeltaReadingContext.getReading(readingDeltaField.getName());
				newReadingContext.addReading(AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_READING_NAME, aggregatedDeltaConsumption);
				finalReadings.add(newReadingContext);
			}
		}
		
		if(finalReadings != null && !finalReadings.isEmpty()) {	
			FacilioChain addOrUpdateReadingsChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			FacilioContext context = addOrUpdateReadingsChain.getContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_MODULE_NAME);
			context.put(FacilioConstants.ContextNames.READINGS, finalReadings);
			context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
			addOrUpdateReadingsChain.execute(context);
			LOGGER.info("AggregatedEnergyConsumptionUtil addOrUpdateReadings -- "+finalReadings+ " for finalDeltaReadingsMap -- "+finalDeltaReadingsMap+ " alreadyPresentAggregatedReadingsMap --" + alreadyPresentAggregatedReadingsMap);
		}					
	}
	
	public static LinkedHashMap<String,ReadingContext> getAlreadyPresentAggregatedReadings(List<ReadingContext> finalDeltaAggregatedReadings) throws Exception {
		
		if(finalDeltaAggregatedReadings != null && !finalDeltaAggregatedReadings.isEmpty()) 
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_MODULE_NAME);
			List<FacilioField> allFields = modBean.getAllFields(module.getName());
			Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
			
			FacilioField aggregatedEnergyConsumptionField = fieldMap.get(AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_READING_NAME);
			aggregatedEnergyConsumptionField.setModule(module);
			
			List<Long> resourceIds = new ArrayList<Long>();
			LinkedHashSet<Long> dayStartTimeList = new LinkedHashSet<Long>();

			for(ReadingContext reading:finalDeltaAggregatedReadings) {
				resourceIds.add(reading.getParentId());
			}
			for(ReadingContext reading:finalDeltaAggregatedReadings) {
				dayStartTimeList.add(reading.getTtime());
			}
			List<FacilioField> selectFields = new ArrayList<>();
			selectFields.add(fieldMap.get("parentId"));
			selectFields.add(fieldMap.get("ttime"));
			selectFields.add(aggregatedEnergyConsumptionField);

			SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
					.table(module.getTableName())
					.module(module)
					.beanClass(ReadingContext.class)
					.select(selectFields)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), resourceIds, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), dayStartTimeList, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(aggregatedEnergyConsumptionField, "", CommonOperators.IS_NOT_EMPTY))
					.skipUnitConversion();

			List<ReadingContext> alreadyPresentAggregatedReadings = selectBuilder.get();

			if(alreadyPresentAggregatedReadings != null && !alreadyPresentAggregatedReadings.isEmpty()) {
				return getReadingsMapWithParentTimeKey(alreadyPresentAggregatedReadings);
			}
			
		}	
		return null;
	}
	
	private static String constructParentWithTtimeKey(long parentId, long ttime) {
		return (parentId + "_" + ttime);			
	}
	
	public static LinkedHashMap<String, ReadingContext> getReadingsMapWithParentTimeKey(List<ReadingContext> readings) {
		LinkedHashMap<String,ReadingContext> readingsWithParentTimeKey = new LinkedHashMap<String,ReadingContext>();
		for(ReadingContext reading:readings) 
		{
			String parentWithTtimeKey = constructParentWithTtimeKey(reading.getParentId(), reading.getTtime());
			readingsWithParentTimeKey.put(parentWithTtimeKey, reading);
		}
		return readingsWithParentTimeKey;			
	}
	
	public static List<ReadingContext> getFinalDeltaAggregatedReadings(LinkedHashMap<Long, DateRange> meterIdVsMaxDateRange) throws Exception {
		List<ReadingContext> aggregatedDeltaReadings = AggregatedEnergyConsumptionUtil.aggregateEnergyConsumptionReadings(meterIdVsMaxDateRange);
		HashMap<Long, Double> energyMeterMFMap = AggregatedEnergyConsumptionUtil.fetchEnergyMeterMultiplicationFactor(new ArrayList(meterIdVsMaxDateRange.keySet()));
		AggregatedEnergyConsumptionUtil.calculateMFOnAggregatedConsumptionDelta(aggregatedDeltaReadings,energyMeterMFMap);
		return aggregatedDeltaReadings;
	}
	
	public static HashMap<Long, Double> fetchEnergyMeterMultiplicationFactor(List<Long> resourceIds) throws Exception {
		
		HashMap<Long,Double> energyMeterMFMap = new HashMap<Long,Double>();
		if(resourceIds != null && !resourceIds.isEmpty()) 
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule energyMeterModule = modBean.getModule("energymeter");
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(energyMeterModule.getName()));			
			
			List<FacilioField> selectFields = new ArrayList<>();
			selectFields.add(fieldMap.get("multiplicationFactor"));

			SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
					.table(energyMeterModule.getTableName())
					.module(energyMeterModule)
					.beanClass(EnergyMeterContext.class)
					.select(selectFields)
					.andCondition(CriteriaAPI.getIdCondition(resourceIds, energyMeterModule));

			List<EnergyMeterContext> energyMeterMFList = selectBuilder.get();	
			for(EnergyMeterContext energyMeterMF:energyMeterMFList) {
				if(energyMeterMF.getMultiplicationFactor() == -1l) {
					energyMeterMF.setMultiplicationFactor(1l);
				}
				energyMeterMFMap.put(energyMeterMF.getId(), energyMeterMF.getMultiplicationFactor());	
			}	
		}
		return energyMeterMFMap;
	}
	
	public static LinkedHashMap<Long, DateRange> getCompleteDateRangeFromReadings(List<ReadingContext> readings, List<Long> filteredResourceIds) throws Exception {
		
		LinkedHashMap<Long, DateRange> meterIdVsMaxDateRange = new LinkedHashMap<Long, DateRange>();
		if(readings != null && !readings.isEmpty()) 
		{
			for(ReadingContext reading:readings) 
			{
				if(filteredResourceIds.contains(reading.getParentId()))
				{
					DateRange dateRange = meterIdVsMaxDateRange.get(reading.getParentId());
					long readingDayStartTime = DateTimeUtil.getDayStartTimeOf(reading.getTtime());
					long readingDayEndTime = DateTimeUtil.getDayEndTimeOf(reading.getTtime());
					if(dateRange == null) {
						dateRange = new DateRange(readingDayStartTime, readingDayEndTime);
						meterIdVsMaxDateRange.put(reading.getParentId(), dateRange);
					}
					else 
					{
						if(readingDayStartTime < dateRange.getStartTime()) {
							dateRange.setStartTime(readingDayStartTime);
						}
						if(readingDayEndTime > dateRange.getEndTime()) {
							dateRange.setEndTime(readingDayEndTime);
						}
					}
				}
				
			}
		}
		return meterIdVsMaxDateRange;
	}
	
	public static List<ReadingContext> aggregateEnergyConsumptionReadings(LinkedHashMap<Long, DateRange> meterIdVsMaxDateRange) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule energyDataModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
		Map<String,FacilioField> energyDataFieldMap = FieldFactory.getAsMap(modBean.getAllFields(energyDataModule.getName()));
		FacilioField readingDeltaField = energyDataFieldMap.get("totalEnergyConsumptionDelta");
		readingDeltaField.setModule(energyDataModule);
		
		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.add(energyDataFieldMap.get("parentId"));
		
		FacilioField timeFieldCloned = energyDataFieldMap.get("ttime").clone();
		timeFieldCloned.setName(timeFieldCloned.getName()+"Grouped");
		FacilioField groupingTimeField = BmsAggregateOperators.DateAggregateOperator.FULLDATE.getSelectField(timeFieldCloned);
	
		
		Criteria criteria = new Criteria();
		for(Long resourceId :meterIdVsMaxDateRange.keySet()) {
			Criteria subCriteria = new Criteria();
			DateRange dateRange = meterIdVsMaxDateRange.get(resourceId);
			subCriteria.addAndCondition(CriteriaAPI.getCondition(energyDataFieldMap.get("parentId"), ""+resourceId, NumberOperators.EQUALS));
			subCriteria.addAndCondition(CriteriaAPI.getCondition(energyDataFieldMap.get("ttime"), dateRange.getStartTime()+","+dateRange.getEndTime(), DateOperators.BETWEEN));
			criteria.orCriteria(subCriteria);
		}
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.table(readingDeltaField.getModule().getTableName())
				.module(readingDeltaField.getModule())
				.select(selectFields)
				.setAggregation()
				.beanClass(ReadingContext.class)
				.aggregate(AggregateOperator.getAggregateOperator(BmsAggregateOperators.NumberAggregateOperator.MIN.getValue()), energyDataFieldMap.get("ttime"))
				.aggregate(AggregateOperator.getAggregateOperator(BmsAggregateOperators.NumberAggregateOperator.SUM.getValue()), readingDeltaField)
				.andCondition(CriteriaAPI.getCondition(readingDeltaField, "", CommonOperators.IS_NOT_EMPTY))
				.groupBy(energyDataFieldMap.get("parentId").getCompleteColumnName()+","+groupingTimeField.getCompleteColumnName())
				.andCriteria(criteria)
				.skipUnitConversion();
		
		List<ReadingContext> aggregatedReadings = selectBuilder.get();
		LOGGER.info("AggregatedEnergyConsumptionUtil delta aggregation -- "+selectBuilder.toString()+ " for meterIdVsMaxDateRange -- "+meterIdVsMaxDateRange+ " aggregatedReadings --" + aggregatedReadings);
		return aggregatedReadings;
	}

	public static void calculateMFOnAggregatedConsumptionDelta(List<ReadingContext> aggregatedDeltaReadings, HashMap<Long, Double> energyMeterMFMap) throws Exception {	
		if(aggregatedDeltaReadings != null && !aggregatedDeltaReadings.isEmpty() && energyMeterMFMap != null && MapUtils.isNotEmpty(energyMeterMFMap)) 
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_DATA_READING));
			FacilioField readingDeltaField =fieldMap.get("totalEnergyConsumptionDelta");
			
			for(ReadingContext readingContext: aggregatedDeltaReadings) {	
				Double multiplicationFactor = energyMeterMFMap.get(readingContext.getParentId());
				Object sumOfDelta = readingContext.getReading(readingDeltaField.getName());
				if(multiplicationFactor != null && sumOfDelta != null) {
					readingContext.addReading(readingDeltaField.getName(), Double.valueOf(sumOfDelta.toString()) * multiplicationFactor);
				}
				readingContext.setTtime(DateTimeUtil.getDayStartTimeOf(readingContext.getTtime()));
			}
		}
	}
	
	public static void addAggregatedEnergyConsumptionMigFields() throws Exception {
		
		if(AssetsAPI.getCategory("Energy Meter") != null && AssetsAPI.getCategory("Energy Meter").getId() != -1) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, AssetsAPI.getCategory("Energy Meter").getId());
			context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.ENERGY_METER);

			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, AggregatedEnergyConsumptionUtil.AGGREGATED_READINGS_TABLE_NAME);
			context.put(FacilioConstants.ContextNames.READING_NAME, AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_READING_NAME);
			
			FacilioField aggregatedEnergyConsumptionField = new FacilioField();
			aggregatedEnergyConsumptionField.setName(AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_READING_NAME);
			aggregatedEnergyConsumptionField.setDisplayName("Daily Energy Consumption");
			aggregatedEnergyConsumptionField.setDisplayType(FacilioField.FieldDisplayType.DECIMAL.getIntValForDB());
			aggregatedEnergyConsumptionField.setDataType(FieldType.DECIMAL.getTypeAsInt());
			aggregatedEnergyConsumptionField.setDataType(FieldType.DECIMAL);
			aggregatedEnergyConsumptionField.setDefault(true);
			aggregatedEnergyConsumptionField.setDisabled(false);
			aggregatedEnergyConsumptionField.setRequired(false);
			context.put(FacilioConstants.ContextNames.MODULE_FIELD, aggregatedEnergyConsumptionField);

			FacilioChain c = TransactionChainFactory.getAddCategoryReadingChain();
			c.execute(context);
			
		}
	}
	
	public static void calculateHistoryForAggregatedEnergyConsumption(long startTime, long endTime, List<Long> resourceIds) throws Exception{
		calculateHistoryForAggregatedEnergyConsumption(startTime, endTime, resourceIds, null);
	}
	
	public static void calculateHistoryForAggregatedEnergyConsumption(long startTime, long endTime, List<Long> resourceIds, List<EnergyMeterContext> energyMeters) throws Exception
	{
		if(energyMeters == null || energyMeters.isEmpty()) {
			energyMeters = AggregatedEnergyConsumptionUtil.getAllEnergyMetersWithMultiplicationFactor(resourceIds);	
		}
		
		LinkedHashMap<Long,DateRange> meterIdVsMaxDateRange = new LinkedHashMap<Long,DateRange>();
		HashMap<Long, Double> energyMeterMFMap = new HashMap<Long,Double>();
		
		if(energyMeters != null && !energyMeters.isEmpty()) 
		{
			if(startTime != -1 && endTime != -1) 
			{
				for(EnergyMeterContext energyMeter :energyMeters)
				{
					if(energyMeter.getMultiplicationFactor() == -1l) {
						energyMeter.setMultiplicationFactor(1l);
					}
					DateRange dateRange = new DateRange(startTime, endTime);
					meterIdVsMaxDateRange.put(energyMeter.getId(), dateRange);
					energyMeterMFMap.put(energyMeter.getId(), energyMeter.getMultiplicationFactor());	
				}
			}
			else 
			{
				for(EnergyMeterContext energyMeter :energyMeters)
				{
					ReadingContext firstReading = fetchSingleResourceReading(energyMeter.getId(), "TTIME ASC");
					ReadingContext lastReading = fetchSingleResourceReading(energyMeter.getId(), "TTIME DESC");
					
					if(firstReading != null) {
						startTime = firstReading.getTtime();
					}	
					if(lastReading != null) {
						endTime = lastReading.getTtime();
					}
				
					if(startTime != -1 && endTime != -1) 
					{
						if(energyMeter.getMultiplicationFactor() == -1l) {
							energyMeter.setMultiplicationFactor(1l);
						}
						DateRange dateRange = new DateRange(startTime, endTime);
						meterIdVsMaxDateRange.put(energyMeter.getId(), dateRange);
						energyMeterMFMap.put(energyMeter.getId(), energyMeter.getMultiplicationFactor());
					}
				}			
			}		
		}
		
		if(meterIdVsMaxDateRange != null && MapUtils.isNotEmpty(meterIdVsMaxDateRange) && energyMeterMFMap != null && MapUtils.isNotEmpty(energyMeterMFMap))
		{									
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule energyDataModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
			Map<String,FacilioField> energyDataFieldMap = FieldFactory.getAsMap(modBean.getAllFields(energyDataModule.getName()));
			FacilioField readingDeltaField = energyDataFieldMap.get("totalEnergyConsumptionDelta");
			readingDeltaField.setModule(energyDataModule);
			
			List<FacilioField> selectFields = new ArrayList<FacilioField>();
			selectFields.add(energyDataFieldMap.get("parentId"));
			
			FacilioField timeFieldCloned = energyDataFieldMap.get("ttime").clone();
			timeFieldCloned.setName(timeFieldCloned.getName()+"Grouped");
			FacilioField groupingTimeField = BmsAggregateOperators.DateAggregateOperator.FULLDATE.getSelectField(timeFieldCloned);
			
			Criteria criteria = new Criteria();
			for(Long resourceId :meterIdVsMaxDateRange.keySet()) {
				Criteria subCriteria = new Criteria();
				DateRange dateRange = meterIdVsMaxDateRange.get(resourceId);
				subCriteria.addAndCondition(CriteriaAPI.getCondition(energyDataFieldMap.get("parentId"), ""+resourceId, NumberOperators.EQUALS));
				subCriteria.addAndCondition(CriteriaAPI.getCondition(energyDataFieldMap.get("ttime"), dateRange.getStartTime()+","+dateRange.getEndTime(), DateOperators.BETWEEN));
				criteria.orCriteria(subCriteria);
			}
			
			SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
					.table(readingDeltaField.getModule().getTableName())
					.module(readingDeltaField.getModule())
					.select(selectFields)
					.setAggregation()
					.beanClass(ReadingContext.class)
					.aggregate(AggregateOperator.getAggregateOperator(BmsAggregateOperators.NumberAggregateOperator.MIN.getValue()), energyDataFieldMap.get("ttime"))
					.aggregate(AggregateOperator.getAggregateOperator(BmsAggregateOperators.NumberAggregateOperator.SUM.getValue()), readingDeltaField)
					.andCondition(CriteriaAPI.getCondition(readingDeltaField, "", CommonOperators.IS_NOT_EMPTY))
					.groupBy(energyDataFieldMap.get("parentId").getCompleteColumnName()+","+groupingTimeField.getCompleteColumnName())
					.andCriteria(criteria)
					.skipUnitConversion();
			
			SelectRecordsBuilder.BatchResult<ReadingContext> bs = selectBuilder.getInBatches(energyDataFieldMap.get("ttime").getColumnName(), 5000);
			while (bs.hasNext()) 
			{
				List<ReadingContext> finalDeltaAggregatedReadings = bs.get();
				AggregatedEnergyConsumptionUtil.calculateMFOnAggregatedConsumptionDelta(finalDeltaAggregatedReadings,energyMeterMFMap);
				
				LinkedHashMap<String,ReadingContext> alreadyPresentAggregatedReadingsMap = AggregatedEnergyConsumptionUtil.getAlreadyPresentAggregatedReadings(finalDeltaAggregatedReadings);
			
				if(finalDeltaAggregatedReadings != null && !finalDeltaAggregatedReadings.isEmpty())
				{
					LinkedHashMap<String, ReadingContext> finalDeltaReadingsMap = AggregatedEnergyConsumptionUtil.getReadingsMapWithParentTimeKey(finalDeltaAggregatedReadings);
					NewTransactionService.newTransaction(() -> AggregatedEnergyConsumptionUtil.checkAddorUpdateOfAggregatedEnergyConsumptionData(finalDeltaReadingsMap, alreadyPresentAggregatedReadingsMap));
				}
			}			
		}	
	}
	
	public static ReadingContext fetchSingleResourceReading(long resourceId, String orderBy) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule energyDataModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
		List<FacilioField> fields = modBean.getAllFields(energyDataModule.getName());
		Map<String,FacilioField> energyDataFieldMap = FieldFactory.getAsMap(fields);
		FacilioField readingDeltaField = energyDataFieldMap.get("totalEnergyConsumptionDelta");
		readingDeltaField.setModule(energyDataModule);
		FacilioField parentField = energyDataFieldMap.get("parentId");
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																.select(fields)
																.module(energyDataModule)
																.beanClass(ReadingContext.class)
																.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(resourceId), PickListOperators.IS))
																.andCondition(CriteriaAPI.getCondition(readingDeltaField, CommonOperators.IS_NOT_EMPTY))
																.orderBy(orderBy).limit(1)
																.skipUnitConversion()
																;
		
		return selectBuilder.fetchFirst();
	}

	public static void recalculateAggregatedEnergyConsumption(List<ReadingContext> readings) throws Exception
	{
		try {
			if(readings != null && !readings.isEmpty() && readings.get(0) != null)
			{
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule energyDataModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
				Map<String,FacilioField> energyDataFieldMap = FieldFactory.getAsMap(modBean.getAllFields(energyDataModule.getName()));
				
				FacilioField readingDeltaField = energyDataFieldMap.get("totalEnergyConsumptionDelta");
				readingDeltaField.setModule(energyDataModule);
				FacilioField energyReadingField = energyDataFieldMap.get("totalEnergyConsumption");
				energyReadingField.setModule(energyDataModule);

				Object readingDeltaVal = readings.get(0).getReading(readingDeltaField.getName());
				if(readingDeltaVal == null) {
					return;
				}
				
				ReadingDataMeta deltaRdm = ReadingsAPI.getReadingDataMeta(readings.get(0).getParentId(), readingDeltaField);
				ReadingDataMeta energyReadingFieldRdm = ReadingsAPI.getReadingDataMeta(readings.get(0).getParentId(), energyReadingField);
				
				if(deltaRdm == null || energyReadingFieldRdm == null) {
					return;
				}
				
				if(energyReadingFieldRdm != null && energyReadingFieldRdm.getInputType() != ReadingInputType.TASK.getValue() && energyReadingFieldRdm.getInputType() != ReadingInputType.WEB.getValue()) {
					return;
				}
				
				FacilioModule module = modBean.getModule(AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_MODULE_NAME);
				if(module == null) {
					return;
				}
				
				LinkedHashMap<Long, DateRange> meterIdVsMaxDateRange = getCompleteDateRangeFromReadings(readings, Collections.singletonList(readings.get(0).getParentId()));	
				
				if(meterIdVsMaxDateRange != null && MapUtils.isNotEmpty(meterIdVsMaxDateRange)) 
				{					
					List<ReadingContext> finalDeltaAggregatedReadings = AggregatedEnergyConsumptionUtil.getFinalDeltaAggregatedReadings(meterIdVsMaxDateRange);
					LinkedHashMap<String,ReadingContext> alreadyPresentAggregatedReadingsMap = AggregatedEnergyConsumptionUtil.getAlreadyPresentAggregatedReadings(finalDeltaAggregatedReadings);
				
					if(finalDeltaAggregatedReadings != null && !finalDeltaAggregatedReadings.isEmpty())
					{
						LinkedHashMap<String, ReadingContext> finalDeltaReadingsMap = AggregatedEnergyConsumptionUtil.getReadingsMapWithParentTimeKey(finalDeltaAggregatedReadings);
						NewTransactionService.newTransaction(() -> AggregatedEnergyConsumptionUtil.checkAddorUpdateOfAggregatedEnergyConsumptionData(finalDeltaReadingsMap, alreadyPresentAggregatedReadingsMap));
					}			
				}	
			}
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error while updating ReCalculateAggregatedEnergyConsumptionCommand in -- readings: "+ readings +
					" Exception: " + e.getMessage() , e);
		}	
	}
	
	public static List<EnergyMeterContext> getAllEnergyMetersWithMultiplicationFactor(List<Long> resourceIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class);
//				.andCondition(CriteriaAPI.getCondition(fieldMap.get("multiplicationFactor"), "", CommonOperators.IS_NOT_EMPTY))
		
		if(resourceIds != null && !resourceIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(resourceIds, module));
		}
		else {
			Criteria subCriteria = new Criteria();
			subCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("connected"), "", CommonOperators.IS_EMPTY));
			subCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("connected"), ""+false, NumberOperators.EQUALS));
			selectBuilder.andCriteria(subCriteria);		
		}
		return selectBuilder.get();
	}
}
