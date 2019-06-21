package com.facilio.bmsconsole.jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class HistoricalMLJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(HistoricalMLJob.class.getName());
	
	@Override
	public void execute(JobContext jc) throws Exception 
	{
						 
		long time = 1546286400000L;
		long startTime = time ;//+ (12*60*60*24*1000L);//
		long endTime = time + (60 * 60 * 60 * 24 * 1000L);
		//long endTime = 1556654400000L;//
		LOGGER.info("Job started");
		try
		{
			while(startTime<endTime)
			{
				runModel(startTime);
				startTime = startTime + 24 * 3600 * 1000L;
			}
			LOGGER.info("Job completed");
		}
		catch(Exception e)
		{
			LOGGER.error("Error while running Historical ML Job", e);
			throw e;
		}

	}
	
	private void runModel(long startTime) throws Exception
	{
		Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap = new Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>>(5);		
		getReadings(5674,36542,36534,startTime-8640000000L,startTime,mlVariablesDataMap);
		getReadings(5674,168958,36534,startTime-8640000000L,startTime,mlVariablesDataMap);
		getReadings(5639,36311,36313,startTime-8640000000L,startTime+172800000L,mlVariablesDataMap);
		
		generateMLModel(startTime,5674,mlVariablesDataMap,"predictEnergy",48130L);
	}
	
	private String getDate(long time)
    {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return new SimpleDateFormat("YYYY-MM-dd").format(cal.getTime());
    }
	
	private String getDateByTimeZone(long time ,String timeZone)
	{
		final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        cal.setTimeInMillis(time);

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        return sdf.format(cal.getTimeInMillis());
	}
	
	private void generateMLModel(long predictedTime,long meterID,Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap,String modelPath,long logModuleID) throws Exception
	{
		JSONObject postObj = new JSONObject();
		postObj.put("ml_id",100);
		postObj.put("orgid",88);
		LOGGER.info("Time is "+predictedTime+"::"+"no Time Zone date "+getDate(predictedTime)+":: With Time zone"+getDateByTimeZone(predictedTime,"Asia/Dubai"));
		postObj.put("date",getDateByTimeZone(predictedTime,"Asia/Dubai"));
		
		JSONObject modelVariables = new JSONObject();
		modelVariables.put("timezone","Asia/Dubai");
		modelVariables.put("weekend","6,7");
		modelVariables.put("meterinterval","10");
		
		postObj.put("modelvariables",modelVariables);
		
		JSONArray assetVariables = new JSONArray();
		
		JSONObject assetData = new JSONObject();
		
		JSONObject meterData = new JSONObject();
		meterData.put("TYPE", "Energy Meter");
		assetData.put(""+meterID,meterData);
		assetVariables.put(assetData);
		
		JSONObject assetData1 = new JSONObject();
		JSONObject siteData = new JSONObject();
		meterData.put("TYPE", "Site");
		assetData1.put(""+5639,siteData);
		assetVariables.put(assetData1);
		postObj.put("assetdetails", assetVariables);
		
		
		JSONArray ip = new JSONArray();
		ip.put("totalEnergyConsumptionDelta");
		ip.put("marked");
		ip.put("temperature");
		postObj.put("inputmetrics",ip);
		
		JSONArray op = new JSONArray();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(logModuleID);
		if(module!=null)
		{
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			for(FacilioField field:fields)
			{
				op.put(field.getName());
			}
		}
		postObj.put("outputmetrics",op);
		postObj.put("data", constructJSONArray(mlVariablesDataMap));
		
		String postURL=AwsUtil.getAnomalyPredictAPIURL() + "/"+modelPath;
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		LOGGER.info(" Sending request to ML Server "+postURL);
		String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString(),300);
		LOGGER.info("result is"+result);
		addReadingToDB(predictedTime,result,meterID,48130,48129);
	}
	
	public void getReadings(long assetID,long variableFieldID,long parentFieldID,long startTime,long endTime,Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap) throws Exception
	{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		SortedMap<Long,Object> data = new TreeMap<Long,Object>();
        FacilioField variableField = modBean.getField(variableFieldID);
        FacilioField parentField = modBean.getField(parentFieldID);
        
        FacilioField ttimeField = FieldFactory.getField("ttime", "TTIME", variableField.getModule(), FieldType.NUMBER);
        List<FacilioField> fieldList = new ArrayList<FacilioField>(2);
        fieldList.add(variableField);
        fieldList.add(ttimeField);
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																.select(fieldList)
																.module(variableField.getModule())
																.beanClass(ReadingContext.class)
																.orderBy("TTIME ASC")
																.andCustomWhere("TTIME >= ? AND TTIME < ? AND "+parentField.getColumnName()+"=? ",
																		startTime, endTime,assetID);
		
		List<Map<String, Object>> props = selectBuilder.getAsProps();
		for(Map<String,Object> prop : props)
		{
			data.put((long)prop.get(ttimeField.getName()), prop.get(variableField.getName()));
		}
		setMlVariablesDataMap(mlVariablesDataMap,assetID,variableField.getName(), data);
	}
	
	public void setMlVariablesDataMap(Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap,long assetID,String attributeName ,SortedMap<Long,Object> data)
	{
		if(mlVariablesDataMap.containsKey(assetID))
		{
			mlVariablesDataMap.get(assetID).put(attributeName, data);
		}
		else
		{
			Hashtable<String, SortedMap<Long, Object>> assetData = new Hashtable<String,SortedMap<Long,Object>>(5);
			assetData.put(attributeName, data);
			mlVariablesDataMap.put(assetID, assetData);
		}
	}
	
	private JSONArray constructJSONArray(Hashtable<Long, Hashtable<String, SortedMap<Long, Object>>> assetDataMap) throws JSONException
	{
		JSONArray dataObject = new JSONArray();
		Set<Long> assetSet = assetDataMap.keySet();
		for(long assetID: assetSet)
		{
			//JSONArray assetArray = new JSONArray();
			Hashtable<String,SortedMap<Long,Object>> attributeData = assetDataMap.get(assetID);
			Set<String> attributeNameSet = attributeData.keySet();
			for(String attributeName:attributeNameSet)
			{
				JSONArray attributeArray = new JSONArray();
				SortedMap<Long,Object> attributeDataMap = attributeData.get(attributeName);
				Set<Long> timeSet = attributeDataMap.keySet();
				for(long time: timeSet)
				{
					JSONObject object = new JSONObject();
					object.put("ttime", time);
					object.put(attributeName, attributeDataMap.get(time));
					object.put("assetID", assetID);
					
					attributeArray.put(object);
				}
				dataObject.put(attributeArray);
			}
			//dataObject.put(assetArray);
		}
		return dataObject;
		
	}
	
	private void addReadingToDB(long predictedTime,String result,long parentID,long mlLogModuleID, long mlModuleID ) throws Exception
	{
		try
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule logModule = modBean.getModule(mlLogModuleID);
			FacilioModule predictModule = modBean.getModule(mlModuleID);
			
			JSONArray mlArray = (JSONArray) new JSONObject(result).get("data");
			if(mlArray.length()>0)
			{
				List<FacilioField> fields = modBean.getAllFields(logModule.getName());
				
				List<ReadingContext> logReadingList = new ArrayList<>();
				List<ReadingContext> predictReadingList = new ArrayList<>(); 
				
				
			 
				 for(int i=0; i<mlArray.length(); i++)
				 {
					 JSONObject readingObj = (JSONObject) mlArray.get(i);
					 
					 ReadingContext newReading = new ReadingContext();
					 ReadingContext newUpdatedReading = new ReadingContext();
					 
					 newReading.setParentId(parentID);
					 newUpdatedReading.setParentId(parentID);
					 
					 newReading.setTtime((long)readingObj.get("ttime"));
					 newUpdatedReading.setTtime((long)readingObj.get("ttime"));
					 
					 
					 for(FacilioField field:fields)
					 {
						 if(readingObj.has(field.getName()) && !field.getName().equalsIgnoreCase("ttime"))
						 {
							 newReading.addReading(field.getName(), readingObj.get(field.getName()));
							 newUpdatedReading.addReading(field.getName(), readingObj.get(field.getName()));
						 }
					 }
					 newReading.addReading("predictedTime",predictedTime);
					 logReadingList.add(newReading);
					 predictReadingList.add(newUpdatedReading);
					 
					 
					 
				 }
				 
				 if(!predictReadingList.isEmpty())
				 {
					 
					 try
					 {
						 updateExistingPredictReading(parentID,predictModule,predictReadingList);
	
	
					 }
					 catch(Exception e)
					 {
						 LOGGER.error("Error while updating Predicted Reading", e);
					 }
				 }
				 updateReading(logModule,logReadingList);
			}
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while updating Result in DB", e);
			throw e;
		}
	
	}
	
	private void updateReading(FacilioModule module,List<ReadingContext> readings) throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.READINGS, readings);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, com.facilio.bmsconsole.context.ReadingContext.SourceType.ML);
		Chain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
		chain.execute(context);
	}
	
	private void updateExistingPredictReading(long assetID,FacilioModule module,List<ReadingContext> readingList) throws Exception
	{
		Condition parentCondition= CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(assetID), NumberOperators.EQUALS);
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
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.READINGS, readingList);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, com.facilio.bmsconsole.context.ReadingContext.SourceType.ML);
		Chain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
		chain.execute(context);
	}
	
	private static String getTtimeList(List<ReadingContext> readingList) 
	{
		StringJoiner ttimeCriteria = new StringJoiner(",");
		readingList.forEach(reading->ttimeCriteria.add(String.valueOf(reading.getTtime())));
		return ttimeCriteria.toString();
	}


}
