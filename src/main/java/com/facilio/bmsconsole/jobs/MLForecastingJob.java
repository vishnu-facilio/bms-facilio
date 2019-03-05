package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.context.MlForecastingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;


public class MLForecastingJob extends FacilioJob 
{
	private static final Logger LOGGER = Logger.getLogger(MLForecastingJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception 
	{
		LOGGER.info("Job started"+jc.getOrgId());
		
		List<MlForecastingContext> pcList = getPredictionJobs(jc.getOrgId());
		for(MlForecastingContext predictionContext : pcList)
		{
			calculateMSEForPreviousPrediction(predictionContext);
			doPrediction(predictionContext, jc.getExecutionTime() * 1000);
		}
		
	}
	
	private void calculateMSEForPreviousPrediction(MlForecastingContext predictionContext)
	{
		try
		{	
			//get Reading from Readings Table for this time stamp	
			long currentTime = System.currentTimeMillis();
			if( AwsUtil.getConfig("environment").equals("development")) 
			{
				// for dev testing purpose time is moved back 
				currentTime = 1538677800000L + (60 * 60 * 1000);
			}
			long startTime = currentTime - (predictionContext.getPredictioninterval() * 60 * 1000);//prediction interval is in minutes
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module1 = modBean.getModule(predictionContext.getSourcemoduleid());
			SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																		.select(modBean.getAllFields(module1.getName()))
																		.module(module1)
																		.beanClass(ReadingContext.class)
																		.orderBy("TTIME ASC")
																		.andCustomWhere("TTIME >= ? AND TTIME < ? AND PARENT_ID=? ",
																		startTime, currentTime, predictionContext.getAssetid());
			
			List<ReadingContext> props = selectBuilder.get();
			Map<Long,ReadingContext> ttimeVsReading = new HashMap<Long,ReadingContext>();
			props.forEach(readingContext->ttimeVsReading.put(readingContext.getTtime(), readingContext));
			
			
			//get all data from Ml_Forecasting_Data from last predictionInterval
			Condition parentCondition=CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(predictionContext.getAssetid()),NumberOperators.EQUALS);
			Condition ttimeCondition=CriteriaAPI.getCondition("TTIME","ttime", getTtimeList(props),NumberOperators.EQUALS);
			
			FacilioField predictField = modBean.getField(predictionContext.getPredictedfieldid());
			FacilioModule module = modBean.getModule(predictField.getModuleId());
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			
			String tableName=module.getTableName();
			SelectRecordsBuilder<ReadingContext> newSelectBuilder = new SelectRecordsBuilder<ReadingContext>()
																		.select(fields)
																		.table(tableName)
																		.moduleName(module.getName())
																		.maxLevel(0)
																		.beanClass(ReadingContext.class)
																		.andCondition(parentCondition)
																		.andCondition(ttimeCondition);
			List<ReadingContext> newProps = newSelectBuilder.get();
			List<ReadingContext> updatedList = new ArrayList<>();
			
			for(ReadingContext reading : newProps) 
			{
				Long ttime=reading.getTtime();
				ReadingContext actualReading = ttimeVsReading.get(ttime);
			
				long diff = Math.round(Math.abs((double)actualReading.getReadings().get(module1.getName()) - (double)reading.getReadings().get(module.getName())));
				
				ReadingContext newReading = new ReadingContext();
				newReading.setParentId(predictionContext.getAssetid());	   			
				newReading.addReading("predictedactualdiff", diff);
				newReading.setId(reading.getId());
				updatedList.add(newReading);
				
			}
			//update Ml_Forecasting_Data with actual diff
			updateReading(module,updatedList);
			
			
		}
		catch(Exception e)
		{
			LOGGER.error("Error while calculating MSE for "+predictionContext.getAssetid(), e);
		}
	}
	
	private void updateReading(FacilioModule module,List<ReadingContext> readings) throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.READINGS, readings);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.ML);
		Chain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
		chain.execute(context);
	}
	
	private List<MlForecastingContext> getPredictionJobs(long orgID) throws Exception
	{
		try
		{
			FacilioModule module = ModuleFactory.getMlForecastingModule();
			GenericSelectRecordBuilder recordBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(FieldFactory.getMlForecastingFields())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
			
			List<Map<String, Object>> predictionListMap = recordBuilder.get();
			List<MlForecastingContext> mlForecastList = getMlForecastFromFromMapList(predictionListMap);
			return mlForecastList;
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while getting resources for OrgID "+orgID, e);
			throw e;
		}
	}
	
	private List<MlForecastingContext> getMlForecastFromFromMapList(List<Map<String, Object>> predictionListMap)
	{
		if(predictionListMap != null && predictionListMap.size() > 0) 
		{
			List<MlForecastingContext> mlForecastList = new ArrayList<>();
			for(Map<String, Object> prop : predictionListMap) 
			{
				MlForecastingContext mlForecastingContext = FieldUtil.getAsBeanFromMap(prop, MlForecastingContext.class);
				mlForecastList.add(mlForecastingContext);
			}
			return mlForecastList;
		}
		return null;
	}
	
	private void doPrediction(MlForecastingContext pc, long predictionTime)
	{
		try
		{
			getFields(pc);
			getReadings(pc);
			if(!(pc.getPyData()==null || pc.getPyData().length()==0))
			{
				generateModel(pc);
				addToDB(pc, predictionTime);
			}

		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while running Prediction "+pc.toString(),e);
		}
	}
	
	private void addToDB(MlForecastingContext pc, long predictionTime) throws Exception
	{
		try
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField logField = modBean.getField(pc.getPredictedlogfieldid());
			FacilioField predictField = modBean.getField(pc.getPredictedfieldid());
			
			JSONArray mlArray = (JSONArray) new JSONObject(pc.getResult()).get("results");
			
			List<ReadingContext> logReadingList = new ArrayList<>();
			List<ReadingContext> predictReadingList = new ArrayList<>(); 
			if(mlArray.length()==0)
			{
				ReadingContext newReading = new ReadingContext();
				newReading.setParentId(pc.getAssetid());
				Object value = 0;
				newReading.addReading(logField.getName(), value);	   			
				newReading.setTtime(System.currentTimeMillis());
				newReading.addReading("predictedTime", predictionTime);
				logReadingList.add(newReading);
				
			}
		 
			 for(int i=0; i<mlArray.length(); i++)
			 {
				 JSONObject readingObj = (JSONObject) mlArray.get(i);
				 
				 ReadingContext newReading = new ReadingContext();
				 newReading.setParentId(pc.getAssetid());
				 Object value = readingObj.get("predicted");
				 newReading.addReading(logField.getName(), value);	   			
				 newReading.setTtime((long)readingObj.get("ttime"));
				 newReading.addReading("predictedTime", predictionTime);
				 logReadingList.add(newReading);
				 
				 ReadingContext predictReading = new ReadingContext();
				 predictReading.setParentId(pc.getAssetid());
				 predictReading.addReading(predictField.getName(), value);	   			
				 predictReading.setTtime((long)readingObj.get("ttime"));
				 predictReadingList.add(predictReading);
			 }
			 
			 if(!predictReadingList.isEmpty())
			 {
				 
				 try
				 {
					 updateExistingPredictReading(pc,predictField.getModule(),predictReadingList);
					 FacilioContext context = new FacilioContext();
					 context.put(FacilioConstants.ContextNames.MODULE_NAME, predictField.getModule().getName());
					 context.put(FacilioConstants.ContextNames.READINGS, predictReadingList);
					 context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.ML);
					 Chain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
					 chain.execute(context);
					 
					 ReadingContext lastReading = predictReadingList.get(predictReadingList.size());
					 GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
					 deleteRecordBuilder.table(predictField.getModule().getTableName())
		             .andCustomWhere("orgid = ?", pc.getOrgId())
		             .andCustomWhere("parendId = ?",pc.getAssetid())
		             .andCustomWhere("ttime > ?", lastReading.getTtime());

		                deleteRecordBuilder.delete();
				 }
				 catch(Exception e)
				 {
					 LOGGER.error("Error while updating Predicted Reading", e);
				 }
			 }
			 
			 updateReading(logField.getModule(),logReadingList);
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while updating Result in DB", e);
			throw e;
		}
	}
	
	private void updateExistingPredictReading(MlForecastingContext pc,FacilioModule module,List<ReadingContext> readingList) throws Exception
	{
		Condition parentCondition=CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(pc.getAssetid()),NumberOperators.EQUALS);
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
	}
	
	private static String getTtimeList(List<ReadingContext> readingList) 
	{
		StringJoiner ttimeCriteria = new StringJoiner(",");
		readingList.forEach(reading->ttimeCriteria.add(String.valueOf(reading.getTtime())));
		return ttimeCriteria.toString();
	}
	
	
	
	private void generateModel(MlForecastingContext pc) throws Exception
	{
		 JSONObject postObj = new JSONObject();
		 postObj.put("meterInterval",pc.getDatainterval());
		 postObj.put("data", pc.getPyData());
		 
		 String postURL=AwsUtil.getAnomalyPredictAPIURL() + "/timeseriesmodel";
		 Map<String, String> headers = new HashMap<>();
		 headers.put("Content-Type", "application/json");
		 String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString());
		 pc.setResult(result);
	}
	private void getFields(MlForecastingContext pc) throws Exception
	{
		try
		{
			FacilioModule module = ModuleFactory.getMlForecastingFieldsModule();
			GenericSelectRecordBuilder recordBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(FieldFactory.getMlForecastingFieldsFields())
														.andCondition(CriteriaAPI.getIdCondition(pc.getId(), module));
			
			List<Map<String,Object>> predictionFieldMap = recordBuilder.get();
			
			List<FacilioField> columnFields = new ArrayList<>();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			for(Map<String,Object> fieldMap : predictionFieldMap)
			{
				FacilioField field = modBean.getField((Long)fieldMap.get("fieldid"));
				if(columnFields.size()==0)
				{
					FacilioField ttimeField = modBean.getField("ttime", field.getModule().getName());
					columnFields.add(ttimeField);
				}
				columnFields.add(field);
			}
			pc.setFields(columnFields);
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while getting fields for "+pc.getId(), e);
			throw e;
		}
		
	}
	
	private void getReadings(MlForecastingContext pc) throws Exception
	{
		long currentTime = System.currentTimeMillis();
		if( AwsUtil.getConfig("environment").equals("development")) 
		{
			// for dev testing purpose time is moved back 
			currentTime = 1538677800000L;
		}
		long startTime = currentTime - pc.getModelsamplinginterval();
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(pc.getFields())
				.module(pc.getFields().get(0).getModule())
				.beanClass(ReadingContext.class)
				.orderBy("TTIME ASC")
				.andCustomWhere("TTIME >= ? AND TTIME < ? AND PARENT_ID=? ",
						startTime, currentTime, pc.getAssetid());
		
		List<Map<String, Object>> props = selectBuilder.getAsProps();
		
		
		JSONArray array = new JSONArray();
		
		
		Criteria criteria = null;
		
		if(pc.getCriteriaid()!=0L)
		{
			criteria = CriteriaAPI.getCriteria(pc.getOrgId(), pc.getCriteriaid());
		}
		boolean supplyStatus = true;
		long lastTime = -1L;// to remove duplicates while predicting data
		for(Map<String, Object> prop:props)
		{
			if(!isConditional(prop,criteria,supplyStatus))
			{
				if(supplyStatus)
				{
					if((long)prop.get("ttime")>lastTime)
					{
						addDataField(pc,prop,array);
						lastTime=(long) prop.get("ttime");
					}
				}
			}
		}
		if(supplyStatus)
		{
			pc.setPyData(array);
		}
		else
		{
			LOGGER.info("Prediction not called for "+pc.getId()+":"+pc.getAssetid()+" as supply Status is false for current period");
		}
	}
	
	private void addDataField(MlForecastingContext pc,Map<String,Object> prop,JSONArray data) throws JSONException
	{
		List<FacilioField> fields = pc.getFields();
		for(FacilioField field:fields)
		{
			if(prop.containsKey(field.getName()) && !field.getName().equals("ttime"))
			{
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ttime", prop.get("ttime"));
				jsonObject.put("metric", prop.get(field.getName()));//always use metric as this is a generic call
				data.put(jsonObject);
			}
		}
	}
	
	private boolean isConditional(Map<String,Object> prop,Criteria criteria,boolean properData)
	{
		if(criteria.isEmpty())
		{
			return true;
		}
		
		Map<String,Condition> conditions = criteria.getConditions();
		for(Condition condition: conditions.values())
		{
			if(prop.containsKey(condition.getFieldName()))
			{
				Operator<?> operator = condition.getOperator();
				if(operator instanceof BooleanOperators)
				{
					properData = condition.computePredicate().evaluate(prop);
					return true;
				}
			}
		}
		return false;
	}

}
