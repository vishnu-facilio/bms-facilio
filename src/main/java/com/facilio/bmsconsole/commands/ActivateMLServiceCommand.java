package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLCustomModuleContext;
import com.facilio.bmsconsole.context.MLResponseContext;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ActivateMLServiceCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(ActivateMLServiceCommand.class.getName());
	private static long maxSamplingPeriod = 7776000000l;
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
					.stream().collect(Collectors.toMap(MLCustomModuleContext::getModelPath, mlCustomModule -> mlCustomModule));


			List<Map<String, Object>> readingFieldsDetails = MLAPI.getReadingFields(assetId, readingVariables);

			for(MLCustomModuleContext moduleContext : mlResponseContext.getModuleInfo()) {
				long mlID = addMLModule(moduleContext, scenario, assetId);

				//				if(moduleContext.getParentModule() == null) {
				//					moduleContext.setRequestFields(requestFields);
				//				}
				addMLVariables(mlID, assetId, moduleContext, readingFieldsDetails, mlCustomModuleMap);
				addMLModelVariables(mlID, mlServiceContext);
				
				ScheduleInfo info = new ScheduleInfo();
				info.setFrequencyType(FrequencyType.DAILY);

				try {
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


	private String convertIntoJson(Map<String, Object> elements) {
		ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(elements);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
		return null;
	}
	
	private void addMLModelVariables(long mlID, MLServiceContext mlServiceContext) throws Exception {
		MLAPI.addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
		MLAPI.addMLModelVariables(mlID,"workflowInfo",convertIntoJson(mlServiceContext.getWorkflowInfo()));
		MLAPI.addMLModelVariables(mlID,"filteringMethod",convertIntoJson(mlServiceContext.getFilteringMethod()));
		MLAPI.addMLModelVariables(mlID,"groupingMethod",convertIntoJson(mlServiceContext.getGroupingMethod()));
		MLAPI.addMLModelVariables(mlID,"mlVariables",convertIntoJson(mlServiceContext.getMlVariables()));
	}

	
	private void addMLVariables(long mlID, long assetId, MLCustomModuleContext moduleContext, List<Map<String, Object>> readingFieldsDetails, Map<String, MLCustomModuleContext> mlCustomModuleMap) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

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
			List<FacilioField> parentModuleFields = prevModuleContext.getRequestFields();

			FacilioField parentField = modBean.getField("parentId", prevModule.getName());
			boolean first = true;
			for(FacilioField variableField : parentModuleFields) {
				MLAPI.addMLVariables(mlID,variableField.getModuleId(),variableField.getFieldId(),parentField.getFieldId(),assetId, maxSamplingPeriod, futureSamplingPeriod, first, "SUM");
				first = false;
			}
		}
		
	}

	private long addMLModule(MLCustomModuleContext moduleContext, String scenario, Long assetId) throws Exception {
		try {
			if(!moduleContext.getModuleNeeded()) {
				return MLAPI.addMLModel(moduleContext.getModelPath(), -1, -1);
			}

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String readingModuleName = (scenario + "_"+ moduleContext.getModuleName());
			FacilioModule readingModule = modBean.getModule(readingModuleName.toLowerCase());
			List<FacilioField> mlFields = readingModule != null ? modBean.getAllFields(readingModule.getName()) : getCustomFields(moduleContext.getFields(), ModuleFactory.getMLReadingModule());
			moduleContext.setRequestFields(mlFields);

			MLAPI.addReading(Collections.singletonList(assetId),readingModuleName,mlFields,ModuleFactory.getMLReadingModule().getTableName(),readingModule);

			String logReadingModuleName = readingModuleName + "_Log";  
			FacilioModule logReadingModule = modBean.getModule(logReadingModuleName.toLowerCase());

			List<FacilioField> mlLogFields = logReadingModule != null ? modBean.getAllFields(logReadingModule.getName()) : getCustomFields(moduleContext.getFields(), ModuleFactory.getMLLogReadingModule());
			mlLogFields = addLogField(mlLogFields, ModuleFactory.getMLLogReadingModule());
			MLAPI.addReading(Collections.singletonList(assetId), logReadingModuleName, mlLogFields,ModuleFactory.getMLLogReadingModule().getTableName(),readingModule);

			moduleContext.setMlReadingModuleId(readingModule.getModuleId());
			return MLAPI.addMLModel(moduleContext.getModelPath(), logReadingModule.getModuleId(), readingModule.getModuleId());
		}
		catch(Exception e) {
			LOGGER.error("Error while adding ml service model Job", e);
			throw e;
		}
	}


	private List<FacilioField> addLogField(List<FacilioField> mlFields, FacilioModule module) {
		mlFields.add(FieldFactory.getField("predictedTime", "PREDICTED_TIME", module, FieldType.NUMBER));
		return mlFields;
	}



	private List<FacilioField> getCustomFields(Map<String, List<String>> map, FacilioModule module) {
		
		List<FacilioField> fields = getDefaultFields(module);
		List<String> booleanFields = map.get("boolean");
		List<String> decimanFields = map.get("decimal");
		String dataType = "NUMBER_";
		int count = 1;
		for(String field : booleanFields) {
			fields.add(FieldFactory.getField(field.toLowerCase(), field.toUpperCase(), dataType+"CF"+count, module, FieldType.BOOLEAN));
			count += 1;
		}
		dataType = "DECIMAL_";
		count = 1;
		for(String field : decimanFields) {
			fields.add(FieldFactory.getField(field.toLowerCase(), field.toUpperCase(), dataType+"CF"+count, module, FieldType.DECIMAL));
			count += 1;
		}

		return fields;
	}


	private List<FacilioField> getDefaultFields(FacilioModule module) {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("mlRunning", "ML_RUNNING", module, FieldType.BOOLEAN));
		fields.add(FieldFactory.getField("errorCode", "ERROR_CODE", module, FieldType.NUMBER));
		return fields;
	}

}
