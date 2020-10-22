package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.MLAlarmOccurenceContext;
import com.facilio.bmsconsole.context.MLAssetVariableContext;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLModelVariableContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.time.DateRange;

public class MLAPI {
	
	private static final Logger LOGGER = Logger.getLogger(MLAPI.class.getName());

	public static MLContext getSubMeterDetails (long mlAnomalyAlarmId) throws Exception {
		
			return null;
		
	}
	
	public static MLAlarmOccurenceContext getRCALastOccurrence (Long alarmId, Long parentId ) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ANOMALY_ALARM_OCCURRENCE);
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<MLAlarmOccurenceContext> builder = new SelectRecordsBuilder<MLAlarmOccurenceContext>()
				.beanClass(MLAlarmOccurenceContext.class).moduleName(FacilioConstants.ContextNames.ANOMALY_ALARM_OCCURRENCE)
				.select(fields).andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"),
						String.valueOf(alarmId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentAlarm"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("mlanomalyType"),
						String.valueOf(MLAlarmOccurenceContext.MLAnomalyType.RCA.getIndex()), NumberOperators.EQUALS))
				.orderBy("CREATED_TIME DESC, ID DESC").limit(1);
		MLAlarmOccurenceContext alarmOccurrenceContext = builder.fetchFirst();
		
		return alarmOccurrenceContext;
		
	}
	
	
	public static List<Map<String, Object>> getAlarmInsight (Context context) throws Exception {
		long alarmId = (long) context.get(ContextNames.ALARM_ID);
		DateOperators operator = DateOperators.CURRENT_WEEK;
		DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		if (dateRange == null) {
			Integer dateOperatorInt = (Integer) context.get(FacilioConstants.ContextNames.DATE_OPERATOR);
			if (dateOperatorInt != null && dateOperatorInt > -1) {
				String dateOperatorValue = (String) context.get(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE);
				operator = (DateOperators) Operator.getOperator(dateOperatorInt);
				dateRange = operator.getRange(dateOperatorValue);
			}
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule baseAlarmModule = modBean.getModule(ContextNames.BASE_ALARM);
		FacilioModule occurrenceModule = modBean.getModule(ContextNames.ALARM_OCCURRENCE);

		List<FacilioField> baseAlarmFields = modBean.getAllFields(baseAlarmModule.getName());
		List<FacilioField> occurrenceFields = modBean.getAllFields(occurrenceModule.getName());
		Map<String, FacilioField> fieldMaps = FieldFactory.getAsMap(baseAlarmFields);
		fieldMaps.putAll(FieldFactory.getAsMap(occurrenceFields));

		List<FacilioField> selectFields = new ArrayList<>();
		String clearedTimeFieldColumn = fieldMaps.get("clearedTime").getColumnName();
		String createdTimeFieldColumn = fieldMaps.get("createdTime").getColumnName();
		FacilioField resourceFieldColumn = fieldMaps.get("resource");

		StringBuilder durationAggrColumn = new StringBuilder("SUM(COALESCE(")
				.append(clearedTimeFieldColumn).append(",").append(dateRange.getEndTime()).append(") - ")
				.append(createdTimeFieldColumn).append(")");
		FacilioField durationField = FieldFactory.getField("duration", durationAggrColumn.toString(), FieldType.NUMBER);

//		selectFields.add(ruleField);
		selectFields.add(durationField);
		selectFields.add(resourceFieldColumn);
		selectFields.addAll(FieldFactory.getCountField(occurrenceModule));
		FacilioField alarmField = fieldMaps.get("alarm");
		SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.select(selectFields)
				.beanClass(AlarmOccurrenceContext.class)
				.module(baseAlarmModule)
				.innerJoin(occurrenceModule.getTableName())
				.on(occurrenceModule.getTableName() + ".ALARM_ID = " + baseAlarmModule.getTableName() + ".ID")
				;
		if (alarmId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMaps.get("alarm"), String.valueOf(alarmId), NumberOperators.EQUALS))
			.groupBy(alarmField.getCompleteColumnName());
		}

		if (dateRange != null) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMaps.get("createdTime"), dateRange.toString(), DateOperators.BETWEEN));
		}
		else {
			builder.andCondition(CriteriaAPI.getCondition(fieldMaps.get("createdTime"), operator));
		}

		List<Map<String, Object>> props = builder.getAsProps();
		// backward-compatibility
		if (CollectionUtils.isNotEmpty(props)) {
			List<Long> resourcesId = props.stream().map(prop -> {
				HashMap<Object,Object> hsp = (HashMap<Object, Object>) prop.get("resource");
				return (long) hsp.get("id");
				}).collect(Collectors.toList());
			Map<Long, ResourceContext> resources = ResourceAPI.getResourceAsMapFromIds(resourcesId);
			for (Map<String, Object> prop : props) {
				HashMap<Object,Object> resourceId =  (HashMap<Object, Object>) prop.get("resource");
				prop.put("subject", resources.get((long) resourceId.get("id")).getName());
			}
		}
		return props;
		
	}
	
	public static long addMLModel(String modelPath,long logReadingModuleID,long readingModuleID) throws Exception
	{
		MLContext mlContext = new MLContext();
		mlContext.setModelPath(modelPath);
		if(logReadingModuleID!=-1)
		{
			mlContext.setPredictionLogModuleID(logReadingModuleID);
		}
		if(readingModuleID!=-1)
		{
			mlContext.setPredictionModuleID(readingModuleID);
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                									.table(ModuleFactory.getMLModule().getTableName())
                									.fields(FieldFactory.getMLFields());
		return insertBuilder.insert(FieldUtil.getAsProperties(mlContext));
	}
	
	public static void addReading(List<Long> assetIds, String readingName,List<FacilioField> fields,String tableName,FacilioModule module) throws Exception 
	{
		addReading(assetIds,readingName,fields,tableName,null,module);
	}
	
	public static Long addReading(List<Long> assetIds,String readingName,List<FacilioField> fields,String tableName,ModuleType moduleType,FacilioModule module) throws Exception 
	{
		if(module != null){
			//to remove if reading fields already added for the resource in ReadingDataMeta
		    List<FacilioField> rdmFields = FieldFactory.getReadingDataMetaFields();
		    FacilioField resourceField = rdmFields.stream().filter(f->f.getName().equalsIgnoreCase("resourceId")).findFirst().get();
		    FacilioField fieldIdField = rdmFields.stream().filter(f->f.getName().equalsIgnoreCase("fieldId")).findFirst().get();
			GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder().table(ModuleFactory.getReadingDataMetaModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(resourceField, assetIds, NumberOperators.EQUALS)) 
					.andCondition(CriteriaAPI.getCondition(fieldIdField, fields.stream().map(FacilioField::getId).collect(Collectors.toList()), NumberOperators.EQUALS));
			deleteRecordBuilder.delete();
			
			//to remove if reading module added for the resource in ResourceReading
			List<FacilioField> rrFields = FieldFactory.getResourceReadingsFields();
		    FacilioField resField = rrFields.stream().filter(f->f.getName().equalsIgnoreCase("resourceId")).findFirst().get();
		    FacilioField readingField = rrFields.stream().filter(f->f.getName().equalsIgnoreCase("readingId")).findFirst().get();
			deleteRecordBuilder = new GenericDeleteRecordBuilder().table(ModuleFactory.getResourceReadingsModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(resField, assetIds, NumberOperators.EQUALS)) 
					.andCondition(CriteriaAPI.getCondition(readingField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
			deleteRecordBuilder.delete();
			
		}
		
		 FacilioChain chain = TransactionChainFactory.addResourceReadingChain(module != null);
		 FacilioContext context = chain.getContext();
         context.put(FacilioConstants.ContextNames.READING_NAME,readingName);
         context.put(FacilioConstants.ContextNames.MODULE,module);
         context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
         context.put(FacilioConstants.ContextNames.PARENT_ID_LIST,assetIds);
         context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, tableName);
         context.put(FacilioConstants.ContextNames.OVER_RIDE_READING_SPLIT, true);
		 if (moduleType != null) {
			context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
		 }
         chain.execute();
         return (Long) chain.getContext().get(FacilioConstants.ContextNames.MODULE_ID);
	}
	
	public static void addMLModelVariables(long mlId,String Key,String value) throws Exception
	{
		MLModelVariableContext context = new MLModelVariableContext();
		context.setMlID(mlId);
		context.setVariableKey(Key);
		context.setVariableValue(value);
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
												.table(ModuleFactory.getMLModelVariablesModule().getTableName())
												.fields(FieldFactory.getMLModelVariablesFields());

		builder.insert(FieldUtil.getAsProperties(context));
	}
	
	public static void addMLVariables(long mlID,long moduleid,long fieldid,long parentFieldid,long parentid,long maxSamplingPeriod,long futureSamplingPeriod,boolean isSource,String aggregation) throws Exception
	{
		MLVariableContext variableContext = new MLVariableContext();
		variableContext.setMlID(mlID);
		variableContext.setModuleID(moduleid);
		variableContext.setFieldID(fieldid);
		variableContext.setParentFieldID(parentFieldid);
		variableContext.setParentID(parentid);
		variableContext.setMaxSamplingPeriod(maxSamplingPeriod);
		variableContext.setFutureSamplingPeriod(futureSamplingPeriod);
		variableContext.setIsSource(isSource);
		variableContext.setAggregation(aggregation);
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
											.table(ModuleFactory.getMLVariablesModule().getTableName())
											.fields(FieldFactory.getMLVariablesFields());

		builder.insert(FieldUtil.getAsProperties(variableContext));
	}
	
	public static void addMLAssetVariables(long mlId,long parentid,String key,String value) throws Exception
	{
		MLAssetVariableContext context = new MLAssetVariableContext();
		context.setMlId(mlId);
		context.setAssetID(parentid);
		context.setVariableKey(key);
		context.setVariableValue(value);
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
												.table(ModuleFactory.getMLAssetVariablesModule().getTableName())
												.fields(FieldFactory.getMLAssetVariablesFields());
		builder.insert(FieldUtil.getAsProperties(context));
	}
	
	public static void addJobs(long mlID,String jobName,ScheduleInfo info,String executorName) throws Exception
	{
		FacilioTimer.scheduleCalendarJob(mlID, jobName, System.currentTimeMillis(), info, executorName);
	}
	
	public static long addMLServiceInfo(Map<String, Object> row) throws Exception {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getMLServiceModule().getTableName())
				.fields(FieldFactory.getMLServiceFields());
		return builder.insert(row);
	}
	
	public static void updateMLServiceInfo(long usecaseId, Map<String, Object> row) throws Exception {
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getMLServiceModule().getTableName())
				.fields(FieldFactory.getMLServiceFields())
				.andCondition(CriteriaAPI.getCondition("USECASE_ID", "useCaseId", String.valueOf(usecaseId), NumberOperators.EQUALS));
		builder.update(row);
				
	}
	
	public static List<Map<String, Object>> getReadingFields(long assetId, List<String> readingFields) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule readingDataMeta = modBean.getModule(FacilioConstants.ContextNames.READING_DATA_META);
		FacilioModule fieldsModule = ModuleFactory.getFieldsModule();

		List<FacilioField> fields = FieldFactory.getMinimalFieldsFields();
		fields.addAll(FieldFactory.getReadingDataMetaFields());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		List<FacilioField> selectFields = new ArrayList<>(5);
		selectFields.add(fieldMap.get("fieldId"));
		selectFields.add(fieldMap.get("name"));
		selectFields.add(fieldMap.get("moduleId"));
		selectFields.add(fieldMap.get("resourceId"));
		selectFields.add(fieldMap.get("value"));

		GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
				.table(readingDataMeta.getTableName())
				.select(selectFields)
				.innerJoin(fieldsModule.getTableName())
				.on(fieldsModule.getTableName() +".FIELDID = "+ readingDataMeta.getTableName()+".FIELD_ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), assetId +"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("value"), "-1", NumberOperators.NOT_EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), StringUtils.join(readingFields, ","), StringOperators.IS));
		return genericSelectBuilder.get();
	}
	
}
