package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.MLCustomModuleContext;
import com.facilio.bmsconsole.context.MLResponseContext;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ActivateMLServiceCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(ActivateMLServiceCommand.class.getName());
//	private static long maxSamplingPeriod = 7776000000l;
	private static final long TRAINING_SAMPLING_PERIOD = (long)(90*24*60*60*1000); //90 days
	private static final long PREDICTION_SAMPLING_PERIOD = (long)(60*60*1000); //1 hour
	private static long futureSamplingPeriod = 0;

	@Override
	public boolean executeCommand(Context context) throws Exception {

		MLServiceContext mlServiceContext = (MLServiceContext) context.get(FacilioConstants.ContextNames.ML_MODEL_INFO);
		try {

			LOGGER.info("Start of ActivateMLServiceCommand");
			LOGGER.info("whole mlservicecontext : \n"+mlServiceContext);
			LOGGER.info(mlServiceContext.getMlResponse().getModuleInfo());
			MLResponseContext mlResponseContext = mlServiceContext.getMlResponse();
			long assetId = mlResponseContext.getAssetid();
			String scenario = mlServiceContext.getScenario();

			List<String> readingVariables = mlServiceContext.getReadingVariables();

			Map<String, MLCustomModuleContext> mlCustomModuleMap = mlResponseContext.getModuleInfo()
					.stream().collect(Collectors.toMap(MLCustomModuleContext::getModuleName, mlCustomModule -> mlCustomModule));


			List<Map<String, Object>> readingFieldsDetails = MLAPI.getReadingFields(assetId, readingVariables);

			for(MLCustomModuleContext moduleContext : mlResponseContext.getModuleInfo()) {
				LOGGER.info("MAPPPSSS:::"+mlCustomModuleMap);
				LOGGER.info("MODULEEEEEEEEEEE name :: "+moduleContext.getModelPath());
				LOGGER.info("Starting ::"+moduleContext);
				long mlID = addMLModule(moduleContext, scenario, assetId);
				LOGGER.info("addMLModule ::"+moduleContext);
				addMLVariables(mlID, assetId, moduleContext, readingFieldsDetails, mlCustomModuleMap);
				LOGGER.info("addMLVariables ::"+moduleContext);
				addMLModelVariables(mlID, mlServiceContext, moduleContext, mlResponseContext.getAssetid());
				LOGGER.info("addMLModelVariables ::"+moduleContext);
				try {
					ScheduleInfo info = new ScheduleInfo();
					String moduleType = moduleContext.getType();
					if(moduleType.equals("training")) {
						info = FormulaFieldAPI.getSchedule(FacilioFrequency.DAILY);					
					} else if(moduleType.equals("prediction")) {
						info = FormulaFieldAPI.getSchedule(FacilioFrequency.HOURLY);
					} else {
						continue;
					}
					MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");

				} catch (InterruptedException e) {
					Thread.sleep(1000);
				}
			}

		}catch(Exception e){
			LOGGER.error("Error while activating ml service ml response in table");
			throw e;
		}
		LOGGER.info("End of ActivateMLServiceCommand");
		return false;
	}


	private String convertMapIntoString(Object elements) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String json = objectMapper.writeValueAsString(elements);
			return json;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void addMLModelVariables(long mlID, MLServiceContext mlServiceContext, MLCustomModuleContext moduleContext, Long assetId) throws Exception {
		//Already available variables
		MLAPI.addMLModelVariables(mlID,"assetID",String.valueOf(assetId));
		MLAPI.addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
		
//		MLAPI.addMLModelVariables(mlID,"workflowInfo",FieldUtil.getAsBeanFromMap(mlServiceContext.getWorkflowInfo(), String.class));
//		MLAPI.addMLModelVariables(mlID,"filteringMethod",JSONArray.toJSONString(mlServiceContext.getFilteringMethod()));
//
		//ML service variables
		MLAPI.addMLModelVariables(mlID,"workflowInfo",convertMapIntoString(mlServiceContext.getWorkflowInfo()));
		MLAPI.addMLModelVariables(mlID,"filteringMethod",convertMapIntoString(mlServiceContext.getFilteringMethod()));
		MLAPI.addMLModelVariables(mlID,"groupingMethod",convertMapIntoString(mlServiceContext.getGroupingMethod()));
		MLAPI.addMLModelVariables(mlID,"mlVariables",convertMapIntoString(mlServiceContext.getMlVariables()));
		MLAPI.addMLModelVariables(mlID,"readingVariables",convertMapIntoString(mlServiceContext.getReadingVariables()));
		
		//extra variables (duplicates)
		MLAPI.addMLModelVariables(mlID,"modelName",mlServiceContext.getModelName());
		MLAPI.addMLModelVariables(mlID,"scenario",mlServiceContext.getScenario());
		MLAPI.addMLModelVariables(mlID,"assetDetails",convertMapIntoString(mlServiceContext.getAssetDetails()));
		MLAPI.addMLModelVariables(mlID,"orgDetails",convertMapIntoString(mlServiceContext.getOrgDetails()));
		
		MLAPI.addMLModelVariables(mlID,"usecaseId",String.valueOf(mlServiceContext.getUseCaseId()));
		if(moduleContext.getType().equals("prediction")) {
			MLAPI.addMLModelVariables(mlID,"workflowId",String.valueOf(mlServiceContext.getWorkflowId()));
		}
		
		int sleepTime = 10;
		LOGGER.info("I am sleeping for next "+sleepTime+" sec");
		Thread.sleep(1000 * sleepTime);
	}


	private void addMLVariables(long mlID, long assetId, MLCustomModuleContext moduleContext, List<Map<String, Object>> readingFieldsDetails, Map<String, MLCustomModuleContext> mlCustomModuleMap) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		long maxSamplingPeriod = PREDICTION_SAMPLING_PERIOD;
		if(moduleContext.getType().equals("training")) {
			maxSamplingPeriod = TRAINING_SAMPLING_PERIOD;
		}
		
		String prevModuleName = moduleContext.getParentModule();
		if(prevModuleName == null) {
			boolean first = true;
			for(Map<String, Object> map : readingFieldsDetails) {

				long moduleId = (long)map.get("moduleId");
				long fieldId = (long)map.get("fieldId");

				FacilioModule module = modBean.getModule(moduleId);
				FacilioField variableField = modBean.getField(fieldId);
				FacilioField parentField = modBean.getField("parentId", module.getName());

				MLAPI.addMLVariables(mlID,variableField.getModuleId(),variableField.getFieldId(),parentField.getFieldId(),assetId, maxSamplingPeriod, futureSamplingPeriod, first, "SUM");
				first = false;
			}
		} else {
			MLCustomModuleContext prevModuleContext = mlCustomModuleMap.get(prevModuleName);
			FacilioModule prevModule = modBean.getModule(prevModuleContext.getMlReadingModuleId());
			Map<String, List<String>> subFields = prevModuleContext.getFields();
			LOGGER.info("parentModuleSection");
			LOGGER.info("prevModule - facilio:: "+prevModule);
			LOGGER.info("prevModule Fields - facilio:: "+prevModule.getFields());
			LOGGER.info("prevModule - local:: "+prevModuleContext.getMlReadingModuleId());
			LOGGER.info("prevModule Fields - local:: "+prevModuleContext.getRequestFields());
			LOGGER.info("prevModule subFields - local:: "+subFields);

			List<String> defaultFields = getDefaultFields(false).stream().map(field -> field.getName()).collect(Collectors.toList());

			List<FacilioField> finalFields = prevModuleContext.getRequestFields().stream()
					.filter( field -> !defaultFields.contains(field.getName()) )
					.collect(Collectors.toList());

			boolean first = true;
			FacilioField parentField = modBean.getField("parentId", prevModule.getName());
			for(FacilioField variableField : finalFields) {
				MLAPI.addMLVariables(mlID,variableField.getModuleId(),variableField.getFieldId(),parentField.getFieldId(),assetId, maxSamplingPeriod, futureSamplingPeriod, first, "SUM");
				first = false;
			}
			MLAPI.addMLModelVariables(prevModuleContext.getMlId(),"jobid",String.valueOf(mlID));
		}

	}

	private long addMLModule(MLCustomModuleContext moduleContext, String scenario, Long assetId) throws Exception {
		try {
			if(!moduleContext.getModuleNeeded()) {
				return MLAPI.addMLModel(moduleContext.getModelPath(), -1, -1);
			}

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String readingModuleName = (scenario + moduleContext.getModuleName());
			FacilioModule readingModule = modBean.getModule(readingModuleName.toLowerCase());

			LOGGER.info(" ---- readingModuleName ---"+readingModuleName);
			LOGGER.info(" ---- readingModuleName ---"+readingModuleName.toLowerCase());
			LOGGER.info(" ---- readingModuleName ---"+readingModule);

			List<FacilioField> mlFields = readingModule == null ? getCustomFields(moduleContext.getFields(), false) : modBean.getAllFields(readingModule.getName());
			moduleContext.setRequestFields(mlFields);
			LOGGER.info(" -- mlFields -- "+mlFields);
			MLAPI.addReading(Collections.singletonList(assetId),readingModuleName,mlFields,ModuleFactory.getMLReadingModule().getTableName(),readingModule);

			String logReadingModuleName = readingModuleName + "Log";  
			FacilioModule logReadingModule = modBean.getModule(logReadingModuleName.toLowerCase());

			LOGGER.info(" ---- logReadingModuleName ---"+logReadingModuleName);
			LOGGER.info(" ---- logReadingModuleName ---"+logReadingModuleName.toLowerCase());
			LOGGER.info(" ---- logReadingModuleName ---"+logReadingModule);

			List<FacilioField> mlLogFields = logReadingModule == null ? getCustomFields(moduleContext.getFields(), true) : modBean.getAllFields(logReadingModule.getName());
			LOGGER.info(" --ml logField -- "+mlLogFields);
			MLAPI.addReading(Collections.singletonList(assetId), logReadingModuleName, mlLogFields,ModuleFactory.getMLLogReadingModule().getTableName(),ModuleType.PREDICTED_READING, logReadingModule);

			readingModule = modBean.getModule(readingModuleName.toLowerCase());
			logReadingModule = modBean.getModule(logReadingModuleName.toLowerCase());

			moduleContext.setMlReadingModuleId(readingModule.getModuleId());
			long mlId = MLAPI.addMLModel(moduleContext.getModelPath(), logReadingModule.getModuleId(), readingModule.getModuleId());
			moduleContext.setMlId(mlId);
			return mlId;
		}
		catch(Exception e) {
			LOGGER.error("Error while adding ml service model Job", e);
			throw e;
		}
	}

	private List<FacilioField> getCustomFields(Map<String, List<String>> map, boolean isLogModule) {

		FacilioModule module = isLogModule ? ModuleFactory.getMLLogReadingModule() : ModuleFactory.getMLReadingModule();

		List<FacilioField> fields = new ArrayList<>();
		List<String> booleanFields = map.get("boolean");
		List<String> decimanFields = map.get("decimal");
		String dataType = "NUMBER_";
		int count = 1;

		if(isLogModule) {
			LOGGER.info("I am mllogreading..");
		} else {
			LOGGER.info("I am mllreading..");
		}
		if(booleanFields!=null) {
			for(String field : booleanFields) {
				String displayName = field.toUpperCase();
				displayName = isLogModule ? displayName+"_LOG" : displayName;
				LOGGER.info(isLogModule+" :: "+field +" :: "+displayName);
				fields.add(FieldFactory.getField(field, displayName, dataType+"CF"+count, module, FieldType.BOOLEAN));
				count += 1;
			}
		}

		if(decimanFields!=null) {
			dataType = "DECIMAL_";
			count = 1;
			for(String field : decimanFields) {
				String displayName = field.toUpperCase();
				displayName = isLogModule ? displayName+"_LOG" : displayName;
				LOGGER.info(isLogModule+" :: "+field +" :: "+displayName);
				fields.add(FieldFactory.getField(field, displayName, dataType+"CF"+count, module, FieldType.DECIMAL));
				count += 1;
			}
		}

		if(fields.size() != 0) {
			//			fields = addDefaultFields(isLogModule);
			fields.addAll(getDefaultFields(isLogModule));
		}
		return fields;
	}

	private List<FacilioField> getDefaultFields(boolean isLogModule) {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = isLogModule ? ModuleFactory.getMLLogReadingModule() : ModuleFactory.getMLReadingModule();
		fields.add(FieldFactory.getField("mlRunning", "ML_RUNNING", module, FieldType.BOOLEAN));
		fields.add(FieldFactory.getField("errorCode", "ERROR_CODE", module, FieldType.NUMBER));
		if(isLogModule) {
			fields.add(FieldFactory.getField("predictedTime", "PREDICTED_TIME", module, FieldType.NUMBER));
		}
		return fields;
	}

}
