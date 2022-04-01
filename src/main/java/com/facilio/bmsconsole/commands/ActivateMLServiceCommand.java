package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLCustomModuleContext;
import com.facilio.bmsconsole.context.MLResponseContext;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.v3.exception.ErrorCode;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActivateMLServiceCommand extends FacilioCommand implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(ActivateMLServiceCommand.class.getName());
	private V3MLServiceContext mlServiceContext;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		mlServiceContext = (V3MLServiceContext) context.get(MLServiceUtil.MLSERVICE_CONTEXT);
		if(mlServiceContext.getServiceType().equals("default")) {
			return executeDefaultMLJob();
		}
		return executeCustomMLJob();
	}

	private boolean executeDefaultMLJob() throws Exception {
		try {
			LOGGER.info("Start of ActivateMLServiceCommand Default Model");
			switch (mlServiceContext.getModelName()) {
				case "energyprediction": {
					FacilioChain chain = FacilioChainFactory.addEnergyPredictionchain();
					FacilioContext context = chain.getContext();
					context.put("energyMeterID", mlServiceContext.getParentAssetId());
					context.put("mlModelVariables", mlServiceContext.getMlModelVariables());
					context.put("mlVariables", mlServiceContext.getTrainingSamplingJson());
					context.put("modelPath", mlServiceContext.getMlResponseList().get(0).getModuleInfo().get(0).getModelPath());
					context.put(FacilioConstants.ContextNames.ML_SERVICE_DATA, mlServiceContext);
					chain.execute();
					break;
				}
				case "loadprediction": {
					FacilioChain chain = FacilioChainFactory.addLoadPredictionchain();
					FacilioContext context = chain.getContext();
					context.put("energyMeterID", mlServiceContext.getParentAssetId());
					context.put("mlModelVariables", mlServiceContext.getMlModelVariables());
					context.put("mlVariables", mlServiceContext.getTrainingSamplingJson());
					context.put("modelPath", mlServiceContext.getMlResponseList().get(0).getModuleInfo().get(0).getModelPath());
					context.put(FacilioConstants.ContextNames.ML_SERVICE_DATA, mlServiceContext);
					chain.execute();
					break;
				}
				case "energyanomaly": {
					FacilioChain chain = FacilioChainFactory.enableAnomalyDetectionChain();
					FacilioContext context = chain.getContext();

					List<Long> assetIds = MLServiceUtil.getAllAssetIds(mlServiceContext);
					String assetIdStr = assetIds.stream().map(i -> i.toString()).collect(Collectors.joining(","));
					context.put("TreeHierarchy", assetIdStr);
					context.put("mlModelVariables", mlServiceContext.getMlModelVariables());
					context.put("mlVariables", mlServiceContext.getTrainingSamplingJson());
					context.put("parentHierarchy", "true");
					context.put(FacilioConstants.ContextNames.ML_SERVICE_DATA, mlServiceContext);
					chain.execute();
					break;
				}
				case "ahuoptimization": {
					FacilioChain chain = FacilioChainFactory.addAhuOptimizationchain();
					FacilioContext context = chain.getContext();
					context.put("assetId", mlServiceContext.getParentAssetId());
					context.put("mlModelVariables", mlServiceContext.getMlModelVariables());
					context.put("mlVariables", mlServiceContext.getTrainingSamplingJson());
					context.put(FacilioConstants.ContextNames.ML_SERVICE_DATA, mlServiceContext);
					chain.execute();
					break;
				}
				default: {
					String errMsg = "Given modelname is not available";
					throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, errMsg);
				}
			}
			LOGGER.info("Successfully activated mlservice for Default Model");
			MLServiceUtil.updateMLStatus(mlServiceContext, "Successfully activated mlservice ");
		}catch (Exception e) {
			e.printStackTrace();
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, "Failed to activate Default ML Model");
		}
		return false;
	}

	private boolean executeCustomMLJob() throws Exception {
		try {
			LOGGER.info("Start of ActivateMLServiceCommand Custom Model");
			//			LOGGER.info(mlServiceContext.getMlResponse().getModuleInfo());
			List<MLResponseContext> mlResponseContextList = mlServiceContext.getMlResponseList();
			List<Long> mlIDList  = new ArrayList<>();
			List<Long> assetIdList = MLServiceUtil.getAllAssetIds(mlServiceContext);

			for(int index=1;index<=mlResponseContextList.size();index++) {
				MLResponseContext mlResponseContext = mlResponseContextList.get(index-1);

				long assetId = mlResponseContext.getAssetid();
				String projectName = mlServiceContext.getProjectName();
				String serviceType = mlServiceContext.getServiceType();

				Map<String, MLCustomModuleContext> mlCustomModuleMap = mlResponseContext.getModuleInfo()
						.stream().collect(Collectors.toMap(MLCustomModuleContext::getModuleName, mlCustomModule -> mlCustomModule));

				List<Map<String, Object>> readingFieldsDetails = mlServiceContext.getModelReadings().get(index-1);

				for(MLCustomModuleContext moduleContext : mlResponseContext.getModuleInfo()) {
					//				LOGGER.info("MAPPPSSS:::"+mlCustomModuleMap);
					//				LOGGER.info("MODULEEEEEEEEEEE name :: "+moduleContext.getModelPath());
					//				LOGGER.info("Starting ::"+moduleContext);
					long mlID = addMLModule(moduleContext, projectName, serviceType, assetId, mlServiceContext.getId());
					//				LOGGER.info("addMLModule ::"+moduleContext);
					String moduleName = moduleContext.getModuleName();
					if(moduleName!= null &&moduleName.equals("EnergyAnomalyRatio")) {
						for(long eachId:assetIdList) {
							addMLVariables(mlID, eachId, moduleContext, readingFieldsDetails, mlCustomModuleMap);
						}
					}
					else {
						addMLVariables(mlID, assetId, moduleContext, readingFieldsDetails, mlCustomModuleMap);
					}
					//				LOGGER.info("addMLVariables ::"+moduleContext);
					addMLModelVariables(mlID, moduleContext, mlResponseContext.getAssetid());
					String moduleType = moduleContext.getType();
					String moduleNature = moduleContext.getNature();
					if(moduleType.equals("prediction") && moduleNature.equals("sequence"))
						if (index < mlResponseContextList.size()) {
							mlIDList.add(mlID);
							break;
						}
						else {
							mlIDList.add(mlID);
							MLServiceUtil.updateMLSequence(mlIDList);
						}
					LOGGER.info("addMLModelVariables ::"+moduleContext);
					try {
						ScheduleInfo info = new ScheduleInfo();
						//String moduleType = moduleContext.getType();
						if(moduleType.equals("training")) {
							info = FormulaFieldAPI.getSchedule(FacilioFrequency.DAILY);
						} else if(moduleType.equals("prediction")) {
							info = FormulaFieldAPI.getSchedule(FacilioFrequency.HOURLY);
						} else {
							continue;
						}
						if(moduleType.equals("prediction") && moduleNature.equals("sequence") && (index == mlResponseContextList.size())) {
							MLAPI.addJobs(mlIDList.get(0),"DefaultMLJob",info,"ml");
						} else {
							if( moduleContext.getParentModule() == null) {

								MLAPI.addJobs(mlID,"DefaultMLJob",info,"ml");
							}
						}

					} catch (InterruptedException e) {
						e.printStackTrace();
						Thread.sleep(1000);
					}
					mlServiceContext.setMlID(mlID);
				}
			}
			MLServiceUtil.updateMLStatus(mlServiceContext, "Successfully activated mlservice ");
			LOGGER.info("Successfully activated mlservice for custom Model");
		}catch(Exception e){
			e.printStackTrace();
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, "Failed to activate Custom ML Model");
		}
		return false;
	}

	private void addMLModelVariables(long mlId, String key, JSONObject value) throws Exception {
		if(value!=null) {
			MLAPI.addMLModelVariables(mlId, key, value.toString());
		}
	}

	private void addMLModelVariables(long mlId, String key, JSONArray value) throws Exception {
		if(value!=null) {
			MLAPI.addMLModelVariables(mlId, key, value.toString());
		}
	}

	private void addMLModelVariables(long mlID, MLCustomModuleContext moduleContext, Long assetId) throws Exception {
		//Already available variables
//		MLAPI.addMLModelVariables(mlID,"assetID",String.valueOf(assetId));
		MLAPI.addMLModelVariables(mlID,"timezone",AccountUtil.getCurrentAccount().getTimeZone());
		JSONObject mlModelVariables = mlServiceContext.getMlModelVariables();
		for(Object key : mlModelVariables.keySet()) {
			MLAPI.addMLModelVariables(mlID, (String)key, String.valueOf(mlModelVariables.get(key)));

		}
		MLAPI.addMLModelVariables(mlID, "asset_id", assetId.toString());

		//ML service variables
		addMLModelVariables(mlID,"workflowInfo",mlServiceContext.getWorkflowInfo());
		addMLModelVariables(mlID,"filteringMethod",mlServiceContext.getFilteringMethod());
		addMLModelVariables(mlID,"groupingMethod",mlServiceContext.getGroupingMethod());



//		addMLModelVariables(mlID,"mlVariables",mlServiceContext.getMlVariables());
//		addMLModelVariables(mlID,"readingVariables",mlServiceContext.getReadingVariables());

		//extra variables (duplicates)
//		MLAPI.addMLModelVariables(mlID,"modelName",mlServiceContext.getModelName());
//		MLAPI.addMLModelVariables(mlID,"scenario",mlServiceContext.getScenario());
//		addMLModelVariables(mlID,"assetDetails",mlServiceContext.getAssetDetails());
//		addMLModelVariables(mlID,"orgDetails",mlServiceContext.getOrgDetails());

		MLAPI.addMLModelVariables(mlID,"usecaseId",String.valueOf(mlServiceContext.getId()));
		if(moduleContext.getType().equals("prediction")) {
			MLAPI.addMLModelVariables(mlID,"workflowId",String.valueOf(mlServiceContext.getWorkflowId()));
		}

	}


	private void addMLVariables(long mlID, long assetId, MLCustomModuleContext moduleContext, List<Map<String, Object>> readingFieldsDetails, Map<String, MLCustomModuleContext> mlCustomModuleMap) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		long maxSamplingPeriod = MLServiceUtil.PREDICTION_SAMPLING_PERIOD;
		if(moduleContext.getType().equals("training")) {
			maxSamplingPeriod = mlServiceContext.getTrainingSamplingPeriod();
		}

		String prevModuleName = moduleContext.getParentModule();
		if(prevModuleName == null) {
			boolean first = true;
			for(Map<String, Object> map : readingFieldsDetails) {

				long fieldId = (long)map.get("fieldId");

				FacilioField variableField = modBean.getField(fieldId);
				FacilioModule module = variableField.getModule();
				FacilioField parentField = modBean.getField("parentId", module.getName());

				MLAPI.addMLVariables(mlID,variableField.getModuleId(),variableField.getFieldId(),parentField.getFieldId(),assetId, maxSamplingPeriod, MLServiceUtil.FURUTRE_SAMPLING_PERIOD, first, "SUM");
				first = false;
			}
		} else {
			MLCustomModuleContext prevModuleContext = mlCustomModuleMap.get(prevModuleName);
			FacilioModule prevModule = modBean.getModule(prevModuleContext.getMlReadingModuleId());
//			LOGGER.info("parentModuleSection");
//			LOGGER.info("prevModule - facilio:: "+prevModule);
//			LOGGER.info("prevModule Fields - facilio:: "+prevModule.getFields());
//			LOGGER.info("prevModule - local:: "+prevModuleContext.getMlReadingModuleId());
//			LOGGER.info("prevModule Fields - local:: "+prevModuleContext.getRequestFields());
//			LOGGER.info("prevModule subFields - local:: "+subFields);

			List<String> parentFields = moduleContext.getParentFields();
//			LOGGER.info("parentFields :: "+parentFields);
			List<FacilioField> finalFields = prevModuleContext.getRequestFields().stream()
					.filter( field -> parentFields.contains(field.getDisplayName()))
					.collect(Collectors.toList());

//			LOGGER.info("finalFields :: "+finalFields);

			boolean first = true;
			FacilioField parentField = modBean.getField("parentId", prevModule.getName());
			for(FacilioField variableField : finalFields) {
				MLAPI.addMLVariables(mlID,variableField.getModuleId(),variableField.getFieldId(),parentField.getFieldId(),assetId, maxSamplingPeriod, MLServiceUtil.FURUTRE_SAMPLING_PERIOD, first, "SUM");
				first = false;
			}
			MLAPI.addMLModelVariables(prevModuleContext.getMlId(),"jobid",String.valueOf(mlID));

		}

	}

	private long addMLModule(MLCustomModuleContext moduleContext, String scenario, String serviceType, Long assetId, long mlServiceId) throws Exception {
		try {
			if(!moduleContext.getModuleNeeded()) {
				return MLAPI.addMLModel(moduleContext.getModelPath(), null, -1, -1, mlServiceId);
			}

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String moduleName = moduleContext.getModuleName();
			if(serviceType.equals("custom")) {
				moduleName = scenario + moduleName;
			}
			String mlReadingName =  moduleName + "MLReadings";
			/*			Chiller + MultivariateAnomalyPrediction
			 *			Chiller + MultivariateAnomalyRatio
			 *			Energy + Prediction
			 * 			ChillerMultivariateAnomalyPredictionMLReadings
			 * 			EnergyPredictionMLReadings
			 */
			FacilioModule mlReadingModule = modBean.getModule(mlReadingName.toLowerCase());

//			LOGGER.info(" ---- readingModuleName ---"+mlReadingName);
//			LOGGER.info(" ---- readingModuleName ---"+mlReadingName.toLowerCase());
//			LOGGER.info(" ---- readingModuleName ---"+mlReadingModule);

			LOGGER.info(mlReadingModule);
			List<FacilioField> mlFields = mlReadingModule == null ? getCustomFields(moduleContext.getModuleFields(), false) : modBean.getAllFields(mlReadingModule.getName());
			LOGGER.info(mlFields);
			moduleContext.setRequestFields(mlFields);
//			LOGGER.info(" -- mlFields -- "+mlFields);
			MLAPI.addReading(Collections.singletonList(assetId),mlReadingName,mlFields,ModuleFactory.getMLReadingModule().getTableName(),mlReadingModule);

			String mlLogReadingName = moduleName + "MLLogReadings";

			FacilioModule mlLogReadingModule = modBean.getModule(mlLogReadingName.toLowerCase());

//			LOGGER.info(" ---- logReadingModuleName ---"+mlLogReadingName);
//			LOGGER.info(" ---- logReadingModuleName ---"+mlLogReadingName.toLowerCase());
//			LOGGER.info(" ---- logReadingModuleName ---"+mlLogReadingModule);

			List<FacilioField> mlLogFields = mlLogReadingModule == null ? getCustomFields(moduleContext.getModuleFields(), true) : modBean.getAllFields(mlLogReadingModule.getName());
//			LOGGER.info(" --ml logField -- "+mlLogFields);
			MLAPI.addReading(Collections.singletonList(assetId), mlLogReadingName, mlLogFields,ModuleFactory.getMLLogReadingModule().getTableName(),ModuleType.PREDICTED_READING, mlLogReadingModule);

			mlReadingModule = modBean.getModule(mlReadingName.toLowerCase());
			mlLogReadingModule = modBean.getModule(mlLogReadingName.toLowerCase());

			moduleContext.setMlReadingModuleId(mlReadingModule.getModuleId());
			long mlId = MLAPI.addMLModel(moduleContext.getModelPath(), null, mlLogReadingModule.getModuleId(), mlReadingModule.getModuleId(), mlServiceId);
			moduleContext.setMlId(mlId);
			return mlId;
		}
		catch(Exception e) {
			e.printStackTrace();
			String errMsg = "Error while adding ml service model Job";
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, errMsg);
		}
	}

	private List<FacilioField> getCustomFields(Map<String, List<String>> map, boolean isLogModule) {

		FacilioModule module = isLogModule ? ModuleFactory.getMLLogReadingModule() : ModuleFactory.getMLReadingModule();

		List<FacilioField> fields = new ArrayList<>();
		List<String> booleanFields = map.get("boolean");
		List<String> decimalFields = map.get("decimal");
		String dataType = "NUMBER_";
		int count = 1;

		if(isLogModule) {
			LOGGER.info("I am in mllogreading..");
		} else {
			LOGGER.info("I am in mllreading..");
		}
		if(booleanFields!=null) {
			for(String displayName : booleanFields) {

				String dummyField = displayName.replaceAll(" ", "");
				String field = Character.toLowerCase(dummyField.charAt(0)) + dummyField.substring(1);
				LOGGER.info(isLogModule+" :: "+field +" :: "+displayName);
				displayName = isLogModule ? displayName+" Log" : displayName;

				fields.add(FieldFactory.getField(field, displayName, dataType+"CF"+count, module, FieldType.BOOLEAN));
				count += 1;
			}
		}

		if(decimalFields!=null) {
			dataType = "DECIMAL_";
			count = 1;
			for(String displayName : decimalFields) {

				String dummyField = displayName.replaceAll(" ", "");
				String field = Character.toLowerCase(dummyField.charAt(0)) + dummyField.substring(1);
				displayName = isLogModule ? displayName+" Log" : displayName;


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
