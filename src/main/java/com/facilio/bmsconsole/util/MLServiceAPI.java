package com.facilio.bmsconsole.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MLServiceAPI {

	private static final Logger LOGGER = Logger.getLogger(MLServiceAPI.class.getName());

	public static final long DAYS_IN_MILLISECONDS = 24*60*60*1000l;
	public static final Long TRAINING_SAMPLING_PERIOD = 90*(DAYS_IN_MILLISECONDS); //90 days
	public static final long PREDICTION_SAMPLING_PERIOD = 60*60*1000l; //1 hour
	public static final long FURUTRE_SAMPLING_PERIOD = 0;

	public static long addMLService(Map<String, Object> row) throws Exception {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getMLServiceModule().getTableName())
				.fields(FieldFactory.getMLServiceFields());
		return builder.insert(row);
	}

	public static void updateMLService(long usecaseId, Map<String, Object> row) throws Exception {
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getMLServiceModule().getTableName())
				.fields(FieldFactory.getMLServiceFields())
				.andCondition(CriteriaAPI.getCondition("USECASE_ID", "useCaseId", String.valueOf(usecaseId), NumberOperators.EQUALS));
		builder.update(row);
	}
	
	public static long convertDatetoTTimeZone(String time) {
		String fromdateTtime = time.replace('T', ' ');
		ZoneId timeZone = ZoneId.of(AccountUtil.getCurrentOrg().getTimezone());
		LocalDateTime localDateTime = LocalDateTime.parse(fromdateTtime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		long millis = localDateTime.atZone(timeZone).toInstant().toEpochMilli();
		return millis;
	}
	
	public static void updateMLID(long usecaseId, long mlID) throws Exception {
		FacilioModule module = ModuleFactory.getMLServiceModule();
		List<FacilioField> fields = FieldFactory.getMLServiceFields(); 

		GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("USECASE_ID", "useCaseId", usecaseId+"", StringOperators.IS))
				.limit(1);

		Object ml_id = genericSelectBuilder.get().get(0).get("mlId");
		if(ml_id == null) {
			Map<String, Object> row = new HashMap<>();
			row.put("mlId", mlID);
			MLServiceAPI.updateMLService(usecaseId, row);
		}
	}


	public static void updateMLServiceStatus(long usecaseId, String status) throws Exception {
		Map<String, Object> row = new HashMap<>();
		row.put("status", status);
		MLServiceAPI.updateMLService(usecaseId, row);
	}

	public static void updateML(Map<String, Object> row,Long id) throws Exception {
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getMLModule().getTableName())
				.fields(FieldFactory.getMLFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", id.toString(), NumberOperators.EQUALS));
		builder.update(row);

	}

	public static void updateMLSequence(List<Long> mlIDList) throws Exception {
		Map<String, Object> row = new HashMap<>();
		row.put("sequence", mlIDList.toString());
		MLServiceAPI.updateML(row , mlIDList.get(0));
	}

	public static List<String> getReadingListForScenario(String scenario) throws Exception {
		List<Map<String, Object>> scenarioResult = getScenarioInfo(scenario); 
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

	public static List<Map<String, Object>> getScenarioInfo(String scenario) throws Exception {
		FacilioModule module = ModuleFactory.getMLServiceModule();
		List<FacilioField> fields = FieldFactory.getMLServiceFields(); 

		GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("SCENARIO", "scenario", scenario, StringOperators.IS));
		return genericSelectBuilder.get();
	}

	public static List<Map<String, Object>> getSuccessfullScenarios(String scenario) throws Exception {
		FacilioModule module = ModuleFactory.getMLServiceModule();
		List<FacilioField> fields = FieldFactory.getMLServiceFields(); 
		GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("SCENARIO", "scenario", scenario, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("ML_ID", "mlID", null, CommonOperators.IS_NOT_EMPTY));
		return genericSelectBuilder.get();
	}

	public static List<Map<String, Object>> getReadingFields(long assetId, List<String> readingFields) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule readingDataMeta = modBean.getModule(FacilioConstants.ContextNames.READING_DATA_META);
		FacilioModule fieldsModule = ModuleFactory.getFieldsModule();
		FacilioModule moduleModule = ModuleFactory.getModuleModule();

		List<FacilioField> fields = FieldFactory.getMinimalFieldsFields();
		fields.addAll(FieldFactory.getReadingDataMetaFields());
		fields.addAll(FieldFactory.getModuleFields());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		List<FacilioField> selectFields = new ArrayList<>(5);
		selectFields.add(fieldMap.get("fieldId"));
		selectFields.add(fieldMap.get("name"));
		selectFields.add(fieldMap.get("moduleId"));
		selectFields.add(fieldMap.get("resourceId"));
		selectFields.add(fieldMap.get("value"));
		selectFields.add(fieldMap.get("tableName"));

		List<String> mlReadingTables = new ArrayList<>(2);
		mlReadingTables.add("ML_Readings");
		mlReadingTables.add("ML_Log_Readings");
		GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
				.table(readingDataMeta.getTableName())
				.select(selectFields)
				.innerJoin(fieldsModule.getTableName())
				.on(fieldsModule.getTableName()+".FIELDID = "+ readingDataMeta.getTableName()+".FIELD_ID")
				.innerJoin(moduleModule.getTableName())
				.on(fieldsModule.getTableName()+".MODULEID = "+moduleModule.getTableName()+".MODULEID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), assetId +"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("value"), "-1", NumberOperators.NOT_EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("tableName"), StringUtils.join(mlReadingTables, ","), StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), StringUtils.join(readingFields, ","), StringOperators.IS));
		return genericSelectBuilder.get();
	}

	public static String convertMapIntoStrings(Object elements) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String json = objectMapper.writeValueAsString(elements);
			return json;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static long getWorkFlowId(JSONObject workflowInfo) throws JSONException {
		String namespace = (String) workflowInfo.get("namespace");
		String function = (String) workflowInfo.get("function");
		try {
			WorkflowContext workflowContext = UserFunctionAPI.getWorkflowFunction(namespace, function);
			long workflowId = workflowContext.getId();
			LOGGER.info("ml service workflow namespace = "+namespace+", function = "+function+", workflowId = "+workflowId);
			return workflowContext.getId();
		} catch (Exception e) {
			LOGGER.error("Error while getting flow id for given namespace <"+namespace+"> and function <"+function+">");
			e.printStackTrace();
		}
		return 0;
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

	public static List<List<String>> getModelReadings(List<List<Map<String, Object>>> models) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<List<String>> modelsReading =  models.stream().map( model -> {
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
		return modelsReading;
	}
	
}
