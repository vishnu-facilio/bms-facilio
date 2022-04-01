package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLModelParamsContext;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MLServiceUtil {


	public static final String ML_SERVICE_MODULE = "mlservice";
	public static final long DAYS_IN_MILLISECONDS = 24*60*60*1000L; //1 day
	public static final long GRACE_DAYS_IN_MILLISECONDS = 5 * DAYS_IN_MILLISECONDS; //5 days
	public static final long MIN_TRAINING_DAYS = 90;
	public static final Long TRAINING_SAMPLING_PERIOD = MIN_TRAINING_DAYS * DAYS_IN_MILLISECONDS; //90 days
	public static final long PREDICTION_SAMPLING_PERIOD = 60*60*1000L; //1 hour
	public static final long FURUTRE_SAMPLING_PERIOD = 0;

	public static final String MLSERVICE_OLD_CONTEXT = "MLSERVICE_OLD_CONTEXT";
	public static final String MLSERVICE_CONTEXT = "MLSERVICE_CONTEXT";
	public static final String ML_CONTEXT = "ML_CONTEXT";
	public static final Object IS_UPDATE = "IS_UPDATE";

	private static final Logger LOGGER = Logger.getLogger(MLServiceUtil.class.getName());
//	public static long addMLService(Map<String, Object> row) throws Exception {
//		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
//				.table(ModuleFactory.getMLServiceModule().getTableName())
//				.fields(FieldFactory.getMLServiceFields());
//		return builder.insert(row);
//	}

//	public static void updateMLService(long usecaseId, Map<String, Object> row) throws Exception {
//		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
//				.table(ModuleFactory.getMLServiceModule().getTableName())
//				.fields(FieldFactory.getMLServiceFields())
//				.andCondition(CriteriaAPI.getCondition("USECASE_ID", "useCaseId", String.valueOf(usecaseId), NumberOperators.EQUALS));
//		builder.update(row);
//	}

	public static long convertDatetoTTimeZone(String time) { // sample time : 2021-01-01T00:00 / 2021-04-01 15:00
		String fromdateTtime = time.replace('T', ' ');
		ZoneId timeZone = ZoneId.of(AccountUtil.getCurrentOrg().getTimezone());
		LocalDateTime localDateTime = LocalDateTime.parse(fromdateTtime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		long millis = localDateTime.atZone(timeZone).toInstant().toEpochMilli();
		return millis;
	}

	public static Long convertToStartTime(long ttime) throws Exception {
		Date inputDate = new Date();
		inputDate.setTime(ttime);
		DateFormat formatterLocal = new SimpleDateFormat("yyyy-MM-dd");
		formatterLocal.setTimeZone(TimeZone.getTimeZone(AccountUtil.getCurrentOrg().getTimezone()));
		String dateStr = formatterLocal.format(inputDate);
		Date outputDate = formatterLocal.parse(dateStr);
		return outputDate.getTime();
	}

	private static void updateML(Map<String, Object> row, Long id) throws Exception {
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getMLModule().getTableName())
				.fields(FieldFactory.getMLFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", id.toString(), NumberOperators.EQUALS));
		builder.update(row);
	}

	public static void updateMLSequence(List<Long> mlIDList) throws Exception {
		Map<String, Object> row = new HashMap<>();
		row.put("sequence", mlIDList.toString());
		MLServiceUtil.updateML(row , mlIDList.get(0));
	}

	public static List<String> getExistingReadingList(V3MLServiceContext mlServiceContext) throws Exception {
		List<Map<String, Object>> scenarioResult = getSuccessfullProjects(mlServiceContext);
		if(scenarioResult.size() == 0) {
			return null;
		}
		Object metaData = scenarioResult.get(0).get("mlModelMeta");
		JSONParser parser = new JSONParser();
		JSONObject metaDataMap = (JSONObject) parser.parse((String) metaData);
		JSONArray readingVariables = (JSONArray) metaDataMap.get("readingVariables");
		List<String> readingList = new ArrayList<String>();
		readingList.addAll(readingVariables);
		return readingList;
	}

	public static List<Map<String, Object>> getSuccessfullProjects(V3MLServiceContext mlServiceContext) throws Exception {
		if(mlServiceContext.getProjectName() == null) {
			String errorMsg = "projectName param is missing";
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, errorMsg);
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(MLServiceUtil.ML_SERVICE_MODULE);
		List<FacilioField> fields = modBean.getAllFields(MLServiceUtil.ML_SERVICE_MODULE);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField failedField = fieldsMap.get("failed");
		FacilioField projectField = fieldsMap.get("projectName");
		FacilioField creationField = fieldsMap.get("sysCreatedTime");

		GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(projectField, mlServiceContext.getProjectName(), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(creationField, mlServiceContext.getSysCreatedTime()+"", NumberOperators.NOT_EQUALS))
				.andCondition(CriteriaAPI.getCondition(failedField, String.valueOf(false), BooleanOperators.IS));
		return genericSelectBuilder.get();
	}


	private static String convertMapIntoStrings(Object elements) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String json = objectMapper.writeValueAsString(elements);
			return json;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getOrgInfo() throws JSONException {
		JSONObject orgInfo = new JSONObject();
		Organization org = AccountUtil.getCurrentOrg();
		orgInfo.put(FacilioConstants.ContextNames.ORGID, org.getOrgId());
		orgInfo.put(FacilioConstants.ContextNames.TIME_ZONE, org.getTimezone());
		return orgInfo;
	}

	public static Object getCurrentDate(boolean dummyHit) {
		if(dummyHit) {
			return "2020-06-12"; //for development purpose
		}
		else {
			return LocalDate.now(TimeZone.getTimeZone(AccountUtil.getCurrentAccount().getTimeZone()).toZoneId());
		}
	}

	public static void validateDateRange(V3MLServiceContext mlServiceContext) throws Exception {
		Long startTime = mlServiceContext.getStartTime();
		Long endTime = mlServiceContext.getEndTime();
		Long currentTime = MLServiceUtil.convertToStartTime(DateTimeUtil.getCurrenTime());
		LOGGER.info("Initial MLService startTime = " + startTime+", endTime = "+endTime);

		// default start and endtime setting
		if(endTime == null) { //for development purpose
			endTime = currentTime;
			startTime = endTime - MLServiceUtil.TRAINING_SAMPLING_PERIOD;
		} else {
			endTime = MLServiceUtil.convertToStartTime(endTime);
			startTime = MLServiceUtil.convertToStartTime(startTime);
		}
		LOGGER.info("Current MLService startTime = " + startTime+", endTime = "+endTime);

		if(endTime > currentTime) {
			String errorMsg = "MLService data date range falls in future period";
			throw new RESTException(ErrorCode.VALIDATION_ERROR, errorMsg);
		}


		long trainingSamplingPeriod = endTime - startTime;
		if(trainingSamplingPeriod < MLServiceUtil.TRAINING_SAMPLING_PERIOD) {
			String errorMsg = "Training Data date range can't be less than "+ MLServiceUtil.MIN_TRAINING_DAYS+" days";
			throw new RESTException(ErrorCode.VALIDATION_ERROR, errorMsg);
		}

		long gracePeriod = MLServiceUtil.GRACE_DAYS_IN_MILLISECONDS; //5 days
		boolean is_historic = endTime < (currentTime-gracePeriod);
		LOGGER.info("MLService data range falls in past/historic : "+is_historic);
		mlServiceContext.setHistoric(is_historic);

		mlServiceContext.setEndTime(endTime);
		mlServiceContext.setStartTime(startTime);

//		long trainingSamplingPeriod = endTime - startTime;
		mlServiceContext.setTrainingSamplingPeriod(trainingSamplingPeriod);
	}

	public static int getDataDuration(V3MLServiceContext mlServiceContext) {
		return (int) (mlServiceContext.getTrainingSamplingPeriod()/DAYS_IN_MILLISECONDS);
	}

	public static void updateMLModelMeta(V3MLServiceContext mlServiceContext) throws Exception {
		Map<String, Object> mlModelMetaMap = new LinkedHashMap<>();
		addParams(mlModelMetaMap, "modelName", mlServiceContext.getModelName());
		addParams(mlModelMetaMap, "projectName", mlServiceContext.getProjectName());
		addParams(mlModelMetaMap, "parentAssetId", mlServiceContext.getParentAssetId());
		addParams(mlModelMetaMap, "childAssetIds", mlServiceContext.getChildAssetIds());
		addParams(mlModelMetaMap, "serviceType", mlServiceContext.getServiceType());
		addParams(mlModelMetaMap, "readingVariables", mlServiceContext.getReadingVariables()); //constructed variable
		addParams(mlModelMetaMap, "modelReadings", mlServiceContext.getModelReadings());
		addParams(mlModelMetaMap, "mlModelVariables", mlServiceContext.getMlModelVariables());
		addParams(mlModelMetaMap, "filteringMethod", mlServiceContext.getFilteringMethod());
		addParams(mlModelMetaMap, "groupingMethod", mlServiceContext.getGroupingMethod());
		addParams(mlModelMetaMap, "workflowId", mlServiceContext.getWorkflowId());
		addParams(mlModelMetaMap, "workflowInfo", mlServiceContext.getWorkflowInfo());
		addParams(mlModelMetaMap, "startTime", mlServiceContext.getStartTime());
		addParams(mlModelMetaMap, "endTime", mlServiceContext.getEndTime());
		addParams(mlModelMetaMap, "isHistoric", mlServiceContext.isHistoric());
		addParams(mlModelMetaMap, "trainingSamplingPeriod", mlServiceContext.getTrainingSamplingPeriod());
		addParams(mlModelMetaMap, "parentMlIdList", mlServiceContext.getParentMlIdList());
		addParams(mlModelMetaMap, "childMlIdList", mlServiceContext.getChildMlIdList());

		JSONObject requestJson = new JSONObject();
		requestJson.putAll(mlModelMetaMap);
		LOGGER.info("ML_LOGGER ::"+requestJson.toJSONString());
		LOGGER.info("ML_LOGGER ::"+requestJson.toString());
		mlServiceContext.setMlModelMeta(requestJson.toString());
	}

	public static List<Long> getAllAssetIds(V3MLServiceContext mlServiceContext) {
		List<Long> assetIds = new ArrayList<>();
		assetIds.add(mlServiceContext.getParentAssetId());
		if(CollectionUtils.isNotEmpty(mlServiceContext.getChildAssetIds())) {
			assetIds.addAll(mlServiceContext.getChildAssetIds());
		}
		return assetIds;
	}

	private static void addParams(Map<String, Object> map, String key, Object value) {
		if(value!=null) {
			map.put(key, value);
		}
	}

	private static void updateMLService(V3MLServiceContext mlServiceContext) throws Exception {
		updateMLModelMeta(mlServiceContext);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(MLServiceUtil.ML_SERVICE_MODULE);
		List<FacilioField> fields = modBean.getAllFields(MLServiceUtil.ML_SERVICE_MODULE);

		UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(mlServiceContext.getId(), module));
		updateBuilder.update(mlServiceContext);
	}

	public static V3MLServiceContext getMLServiceRecord(long id) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(MLServiceUtil.ML_SERVICE_MODULE);
		List<FacilioField> fields = modBean.getAllFields(MLServiceUtil.ML_SERVICE_MODULE);

		SelectRecordsBuilder<V3MLServiceContext> selectRecordsBuilder = new SelectRecordsBuilder<V3MLServiceContext>()
				.select(fields)
				.module(module)
				.andCondition(CriteriaAPI.getIdCondition(Collections.singletonList(id), module))
				.beanClass(V3MLServiceContext.class);

		List<V3MLServiceContext> records = selectRecordsBuilder.get();
		if(CollectionUtils.isEmpty(records)) {
			throw new IllegalArgumentException("MLService record id not found");
		}
		return records.get(0);
	}


	public static List<MLContext> getMLRecordsByMLServiceId(V3MLServiceContext mlServiceContext) throws Exception {
		Long mlServiceId = mlServiceContext.getId();
		FacilioModule mlModule = ModuleFactory.getMLModule();
		List<FacilioField> mlModuleFields = FieldFactory.getMLFields();

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(mlModuleFields)
				.table(mlModule.getTableName())
				.andCondition(CriteriaAPI.getCondition("MLSERVICE_ID", "mlServiceID", String.valueOf(mlServiceId), NumberOperators.EQUALS));

		List<Map<String, Object>> listMap = selectBuilder.get();


		if(CollectionUtils.isEmpty(listMap)) {
			return null;
		}

		MLContext mlContext = FieldUtil.getAsBeanFromMap(listMap.get(0), MLContext.class);
		List<MLContext> mlContexts = MLUtil.getMLContext(mlContext.getId());
		return mlContexts;
	}


	public static RESTException throwError(V3MLServiceContext mlServiceContext, ErrorCode errorCode, String errorMsg) throws Exception {
		return throwError(mlServiceContext, errorCode, errorMsg, null);
	}

	public static RESTException throwError(V3MLServiceContext mlServiceContext, ErrorCode errorCode, String errorMsg, Exception e) throws Exception {
//		LOGGER.error("Error message : "+errorMsg);
		if(mlServiceContext.isFailed()) {
			errorMsg = mlServiceContext.getStatus();
		} else if(e!=null) {
			LOGGER.error("MLSERVICE_ERROR :: "+errorMsg +" with exception :: "+e);
			e.printStackTrace();
		}
		mlServiceContext.setStatus(errorMsg);
		mlServiceContext.setFailed(true);
		updateMLService(mlServiceContext);
		return new RESTException(errorCode, errorMsg);
	}

	public static void updateMLStatus(V3MLServiceContext mlServiceContext, String message) throws Exception {
		LOGGER.info(message);
		mlServiceContext.setStatus(message);
		updateMLService(mlServiceContext);
	}


	public static void extractAndValidateAssetFields(V3MLServiceContext mlServiceContext) throws Exception{
		List<List<Map<String, Object>>> modelReadings = mlServiceContext.getModelReadings();
		if(CollectionUtils.isEmpty(modelReadings)) {
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, "modelReadings cant be empty");
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<List<String>> modelsReading =  modelReadings.stream().map( model -> {
			List<String> readingNames = model.stream().map( readings -> {
				long fieldId = (long)readings.get("fieldId");
				try {
					FacilioField variableField = modBean.getField(fieldId);
					return variableField.getName();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}).collect(Collectors.toList());
			return readingNames;
		}).collect(Collectors.toList());

		List<String> initModelReading = modelsReading.get(0);
		for(int i = 1; i < modelsReading.size(); i++) {
			List<String> currentModelReading = modelsReading.get(i);
			boolean isHomogeneousModel = org.apache.commons.collections.CollectionUtils.isEqualCollection(initModelReading, currentModelReading);
			if(!isHomogeneousModel) {
				String errorMsg = "Given project name ["+mlServiceContext.getProjectName()+"] has different readings for different modelReadings. Models aren't homogeneous. ";
				errorMsg += "Please give same set of readings for all the modelReadings.";
				throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, errorMsg);
			}
		}
		mlServiceContext.setReadingVariables(initModelReading);
	}

	private static List<Object> addObjects(Object a, Object b) {
		List<Object> data = new ArrayList<>(2);
		if(a!=null) {
			data.add(a);
		}
		if(b!=null) {
			data.add(b);
		}
		return data;
	}

	public static void assertEquals(String variableName, Object a, Object b) throws RESTException {
		if(isDiff(a,b)) {
			throw new RESTException(ErrorCode.VALIDATION_ERROR, variableName+" can't be modified");
		}
	}

	public static boolean isDiff(Object a, Object b) {
		List<Object> data = addObjects(a, b);
		if((data.size() == 1 && a == null) || (data.size() == 2 && !data.get(0).equals(data.get(1)))) {
			return true;
		}
		return false;
	}


	public static void extractAndUpdateWorkflowID(V3MLServiceContext mlServiceContext) {
		JSONObject workflowInfo = mlServiceContext.getWorkflowInfo();
		if(workflowInfo == null || workflowInfo.isEmpty()) {
			return ;
		}
		String namespace = (String) workflowInfo.get("namespace");
		String function = (String) workflowInfo.get("function");
		try {
			WorkflowContext workflowContext = UserFunctionAPI.getWorkflowFunction(namespace, function);
			long workflowId = workflowContext.getId();
			LOGGER.info("ml service workflow namespace = "+namespace+", function = "+function+", workflowId = "+workflowId);
			mlServiceContext.setWorkflowId(workflowId);
			updateWorkflowIdInModelVariables(mlServiceContext);
		} catch (Exception e) {
			LOGGER.error("Error while getting flow id for given namespace <"+namespace+"> and function <"+function+">");
//			e.printStackTrace();
		}
	}

	private static void updateWorkflowIdInModelVariables(V3MLServiceContext mlServiceContext) {
		JSONObject mlModelVariables = mlServiceContext.getMlModelVariables();
		if(mlModelVariables == null) {
			mlModelVariables = new JSONObject();
		}
		mlModelVariables.put("workflowId", mlServiceContext.getWorkflowId());
		mlServiceContext.setMlModelVariables(mlModelVariables);
	}


	public static V3MLServiceContext extractMLMeta(V3MLServiceContext mlServiceContext) throws IOException {
		JSONObject mlModelMetaJson = mlServiceContext.getMlModelMetaJson();
		V3MLServiceContext extractedRecord = FieldUtil.getAsBeanFromJson(mlModelMetaJson, V3MLServiceContext.class);

		if(mlServiceContext.getServiceType() == null) {
			mlServiceContext.setServiceType(extractedRecord.getServiceType());
		}
		if(mlServiceContext.getParentAssetId() == null) {
			mlServiceContext.setParentAssetId(extractedRecord.getParentAssetId());
		}
		if(mlServiceContext.getChildAssetIds() == null) {
			mlServiceContext.setChildAssetIds(extractedRecord.getChildAssetIds());
		}
		if(mlServiceContext.getServiceType() == null) {
			mlServiceContext.setServiceType(extractedRecord.getServiceType());
		}
		if(mlServiceContext.getWorkflowInfo() == null) {
			mlServiceContext.setWorkflowInfo(extractedRecord.getWorkflowInfo());
		}
		if(mlServiceContext.getStartTime() == null) {
			mlServiceContext.setStartTime(extractedRecord.getStartTime());
		}
		if(mlServiceContext.getEndTime() == null) {
			mlServiceContext.setEndTime(extractedRecord.getEndTime());
		}
		if(mlServiceContext.getMlModelVariables() == null) {
			mlServiceContext.setMlModelVariables(extractedRecord.getMlModelVariables());
		}
		if(mlServiceContext.getFilteringMethod() == null) {
			mlServiceContext.setFilteringMethod(extractedRecord.getFilteringMethod());
		}
		if(mlServiceContext.getGroupingMethod() == null) {
			mlServiceContext.setGroupingMethod(extractedRecord.getGroupingMethod());
		}
		if(mlServiceContext.getTrainingSamplingJson() == null) {
			mlServiceContext.setTrainingSamplingPeriod(extractedRecord.getTrainingSamplingPeriod());
		}
		if(mlServiceContext.getReadingVariables() == null & mlModelMetaJson.containsKey("readingVariables")) {
			List<String> extractedReadingVariables = (List<String>) mlModelMetaJson.get("readingVariables");
			mlServiceContext.setReadingVariables(extractedReadingVariables);
		}
		if(mlServiceContext.getModelReadings() == null & mlModelMetaJson.containsKey("modelReadings")) {
			List<List<Map<String, Object>>> extractedModelReadings = (List<List<Map<String, Object>>>) mlModelMetaJson.get("modelReadings");
			mlServiceContext.setModelReadings(extractedModelReadings);
		}
		return mlServiceContext;
	}

    public static List<Map<String, Object>> getModelVariables(String modelName) throws Exception {
		FacilioModule module = ModuleFactory.getMLModelParamsModule();
		GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getMLModelParamsFields())
				.andCondition(CriteriaAPI.getCondition("MODELNAME", "modelName", modelName, StringOperators.IS));


		List<MLModelParamsContext> result = FieldUtil.getAsBeanListFromMapList(genericSelectBuilder.get(), MLModelParamsContext.class);
		for(MLModelParamsContext data : result) {
			if(data.getDataType() == FieldType.ENUM.getTypeAsInt()) {
				data.setKeyValue(new JSONParser().parse(String.valueOf(data.getKeyValue())));
			}
		}

		return FieldUtil.getAsMapList(result, MLModelParamsContext.class);
//		MLContext jobMlContext = FieldUtil.getAsBeanFromMap(listMap.get(0), MLContext.class);
    }
}


