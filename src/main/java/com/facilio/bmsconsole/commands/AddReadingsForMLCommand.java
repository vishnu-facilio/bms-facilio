package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLCustomModuleContext;
import com.facilio.bmsconsole.context.MLModelVariableContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.util.WorkflowUtil;

public class AddReadingsForMLCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(AddReadingsForMLCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception 
	{
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		try
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule logModule = modBean.getModule(mlContext.getPredictionLogModuleID());
			FacilioModule predictModule = modBean.getModule(mlContext.getPredictionModuleID());
			LOGGER.info("debug info "+mlContext.getResult());

			long parentID=mlContext.getSourceID();

			if(parentID < 0){
				LOGGER.info("Error_JAVA "+ mlContext.getModelPath() + " ML ID : "+mlContext.getId()+" FILE : AddReadingsForMLCommand "+" ERROR MESSAGE : Error parentID is not present : "+ parentID);
			}


			List<ReadingContext> logReadingList = new ArrayList<>();
			List<ReadingContext> predictReadingList = new ArrayList<>(); 
			Boolean mlError = (Boolean) context.get("ML_ERROR");
			if(mlError == null){
				mlError = false;	
			}
			if(mlError){
				ReadingContext newReading = new ReadingContext();
				ReadingContext newUpdatedReading = new ReadingContext();

				newReading.setParentId(parentID);
				newUpdatedReading.setParentId(parentID);

				newReading.setTtime(mlContext.getPredictionTime());
				newUpdatedReading.setTtime(mlContext.getPredictionTime());

				newReading.addReading("mlRunning", false);
				newUpdatedReading.addReading("mlRunning", false);

				newReading.addReading("predictedTime", mlContext.getPredictionTime());

				logReadingList.add(newReading);
				predictReadingList.add(newUpdatedReading);
			}
			else {
				JSONArray mlArray = (JSONArray) new JSONObject(mlContext.getResult()).get("data");
				if(mlArray.length()>0)
				{
					long workflowId = getWorkFlowId(mlContext);
					List<FacilioField> fields = modBean.getAllFields(logModule.getName());
					Set<String> fieldNames = fields.stream().map(FacilioField::getName).collect(Collectors.toSet());
					for(int i=0; i<mlArray.length(); i++)
					{
						JSONObject readingObj = (JSONObject) mlArray.get(i);
						if(workflowId!=0) {
							triggerWorkflow(workflowId, readingObj);
						}
						ReadingContext newReading = new ReadingContext();
						ReadingContext newUpdatedReading = new ReadingContext();

						newReading.setParentId(parentID);
						newUpdatedReading.setParentId(parentID);

						newReading.setTtime((long)readingObj.get("ttime"));
						newUpdatedReading.setTtime((long)readingObj.get("ttime"));

						newReading.addReading("mlRunning", true);
						newUpdatedReading.addReading("mlRunning", true);

						for(String field:fieldNames)
						{
							if(readingObj.has(field) && !field.equalsIgnoreCase("ttime"))
							{
								mlContext.getAssetDetails().put(field,readingObj.get(field));
								newReading.addReading(field, readingObj.get(field));
								newUpdatedReading.addReading(field, readingObj.get(field));
							}else if(!readingObj.has(field) && field.equalsIgnoreCase("errorCode")){
								mlContext.getAssetDetails().put(field,-1);
								newReading.addReading(field, -1);
								newUpdatedReading.addReading(field, -1);
							}
						}


						newReading.addReading("predictedTime", mlContext.getPredictionTime());
						logReadingList.add(newReading);

						boolean readingExist = newUpdatedReading.getReadings().entrySet().stream().anyMatch(f-> !(f.getKey().equals("mlRunning") || 
								(f.getKey().equals("errorCode") && Long.parseLong(f.getValue().toString()) == -1 ) ));

						if(readingExist){
							predictReadingList.add(newUpdatedReading);
						}
					}
				}
			}
			if(!predictReadingList.isEmpty())
			{
				try
				{
					if(predictModule!=null){ // predictionModule null for ratiocheck in demo acc
						updateExistingPredictReading(parentID,predictModule,predictReadingList);
					}
				}
				catch(Exception e)
				{
					LOGGER.error("Error while updating Predicted Reading", e);
				}
			}
			if(!logReadingList.isEmpty())
			{
				try
				{
					updateReading(logModule,logReadingList);
				}
				catch(Exception e)
				{
					LOGGER.error("Error while updating Log Reading", e);
					throw e;				 
				}
			}

		}
		catch(Exception e)
		{
			LOGGER.fatal("Error_JAVA "+ mlContext.getModelPath() + " ML ID : "+mlContext.getId()+" FILE : AddReadingsForMLCommand "+" ERROR MESSAGE : "+e);
			throw e;
		}

		return false;
	}

	private long getWorkFlowId(MLContext mlContext) {
		List<MLModelVariableContext> workflowFilter = mlContext.getMLModelVariable().stream()
				.filter(mlModelVariable -> mlModelVariable.getVariableKey().equals("workflowId"))
				.collect(Collectors.toList());

		if(workflowFilter.size() > 0 ) {
			return Long.parseLong(workflowFilter.get(0).getVariableValue());
		}
		return 0;
	}

	private void triggerWorkflow(long workflowId, JSONObject row) {
		try {
			Map<String, Object> workflowMap = new HashMap<>();
			workflowMap.put("row", row);
			Object resMap;
			resMap = WorkflowUtil.getResult(workflowId, workflowMap);
			LOGGER.info("after script row ::"+resMap);
		} catch (Exception e) {
			LOGGER.error("Error while executing the workflow", e);
		}
	}

	private void updateReading(FacilioModule module,List<ReadingContext> readings) throws Exception
	{
		FacilioChain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.READINGS, readings);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.ML);
		context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS, false);
		chain.execute();
	}

	private void updateExistingPredictReading(long assetID,FacilioModule module,List<ReadingContext> readingList) throws Exception
	{
		Condition parentCondition=CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(assetID),NumberOperators.EQUALS);
		Condition ttimeCondition=CriteriaAPI.getCondition("TTIME","ttime", getTtimeList(readingList),NumberOperators.EQUALS);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		String tableName=module.getTableName();
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(fields)
				.table(tableName)
				.moduleName(module.getName())
				.maxLevel(0)
				.beanClass(ReadingContext.class)
				.andCondition(parentCondition)
				.andCondition(ttimeCondition);
		List<ReadingContext> props = selectBuilder.get();

		Map<Long,ReadingContext> ttimeVsReading = new HashMap<Long,ReadingContext>();
		props.forEach(readingContext->ttimeVsReading.put(readingContext.getTtime(), readingContext));


		if(!ttimeVsReading.isEmpty())
		{
			for (ReadingContext reading: readingList) 
			{
				ReadingContext ttimeReading = ttimeVsReading.get(reading.getTtime());
				reading.setId(ttimeReading!=null ? ttimeReading.getId() : -1);
			}
		}
		FacilioChain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.READINGS, readingList);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.ML);
		chain.execute();
	}

	private static String getTtimeList(List<ReadingContext> readingList) 
	{
		StringJoiner ttimeCriteria = new StringJoiner(",");
		readingList.forEach(reading->ttimeCriteria.add(String.valueOf(reading.getTtime())));
		return ttimeCriteria.toString();
	}


}
