package com.facilio.bmsconsole.jobs;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class HistoricalAnomalyDetectionJob extends FacilioJob
{
	
	private static final Logger LOGGER = Logger.getLogger(HistoricalAnomalyDetectionJob.class.getName());
	
	private long[] meterList = {14196l,14250l,14247l,14248l,14249l};
	
	private long energyParentFieldID =66308L;
	private long variableFieldID=66317L;
	
	private long siteParentFieldID = 66059L;
	private long weatherFieldID = 66054L;
	
	private long siteID=14152L;
	
	
	@Override
	public void execute(JobContext jc) throws Exception 
	{
        long startTime = 1555736400000L;//April 20
        long endTime = 1556341200000L;//April 27
        //long endTime = 1556600400000L;
        while(startTime<endTime)
        {
        	buildGAMModel(startTime);
        	checkGam1(startTime);
        	startTime = startTime + 86400000;
        }
		
	}
	
	private void checkGam1(long time) throws Exception
	{
		long startTime = time+3600000L;
		long endTime= time + 86400000L;
		
		while(startTime<=endTime)
		{
			for(long meterID:meterList)
			{
				Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap = new Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>>(5);		
				getReadings(meterID,variableFieldID,energyParentFieldID,startTime-3600000,startTime,mlVariablesDataMap);
				getReadings(siteID,weatherFieldID,siteParentFieldID,startTime-(3600000*2),startTime,mlVariablesDataMap);
				
				generateMLModel(startTime,meterID,mlVariablesDataMap,"checkGam1",46455);
			}
			
			doRatioCheck(startTime);
			startTime = startTime + 3600000L;
		}
	}
	
	private void doRatioCheck(long startTime) throws Exception
	{
		Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap = new Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>>(5);
		for(long meterID:meterList)
		{
			getReadings(meterID,612883,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612903,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612901,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612905,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612907,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612923,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612925,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612927,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612929,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612931,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612933,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612935,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612937,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612940,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612942,612891,startTime-3600000,startTime,mlVariablesDataMap);
            getReadings(meterID,612944,612891,startTime-3600000,startTime,mlVariablesDataMap);
		}
		
		generateMLModelForRatioCheck(startTime,14250,mlVariablesDataMap,"ratioCheck",46466);
	}
	
	private void buildGAMModel(long endTime) throws Exception
	{
		for(long meterID:meterList)
		{
			Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap = new Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>>(5);		
			getReadings(meterID,variableFieldID,energyParentFieldID,endTime-7776000000L,endTime,mlVariablesDataMap);
			getReadings(siteID,weatherFieldID,siteParentFieldID,endTime-7776000000L,endTime,mlVariablesDataMap);
			
			generateMLModel(endTime,meterID,mlVariablesDataMap,"buildGamModel",0L);
		}
	}
	
	private String getDate(long time)
    {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return new SimpleDateFormat("YYYY-MM-dd").format(cal.getTime());
    }
	
	private void generateMLModel(long predictedTime,long meterID,Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap,String modelPath,long logModuleID) throws Exception
	{
		JSONObject postObj = new JSONObject();
		postObj.put("ml_id",100);
		postObj.put("orgid", 104);
		postObj.put("date",getDate(predictedTime));
		
		JSONObject modelVariables = new JSONObject();
		modelVariables.put("timezone","US/Central");
		modelVariables.put("dimension1","WEEKDAY");
		modelVariables.put("dimension1Value","[2,3,4,5,6],[1,7]");
		modelVariables.put("tableValue","1.96");
		modelVariables.put("adjustmentPercentage","10");
		modelVariables.put("orderRange","2");
		modelVariables.put("meterInterval","10");
		
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
		assetData1.put(""+14152,siteData);
		assetVariables.put(assetData1);
		postObj.put("assetdetails", assetVariables);
		
		
		JSONArray ip = new JSONArray();
		ip.put("totalEnergyConsumptionDelta");
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
		String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString());
		LOGGER.info("result is"+result);
		addReadingToDB(predictedTime,result,meterID,46455,46454);
	}
	
	private void generateMLModelForRatioCheck(long predictedTime,long meterID,Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap,String modelPath,long logModuleID) throws Exception
	{
		JSONObject postObj = new JSONObject();
		postObj.put("ml_id",100);
		postObj.put("orgid", 104);
		
		JSONObject modelVariables = new JSONObject();
		modelVariables.put("TreeHeirarchy","14250,14247,14248,14249");
		
		postObj.put("modelvariables",modelVariables);
		
		JSONArray assetVariables = new JSONArray();
		
		JSONObject assetData = new JSONObject();
		
		JSONObject meterData = new JSONObject();
		meterData.put("TYPE", "EnergyMeter");
		assetData.put(""+meterID,meterData);
		assetVariables.put(assetData);
		
		postObj.put("assetdetails", assetVariables);
		
		
		JSONArray ip = new JSONArray();
		ip.put("actualValue");
		ip.put("adjustedLowerBound");
		ip.put("adjustedUpperBound");
		ip.put("gamAnomaly");
		ip.put("lowerARMA");
		ip.put("lowerAnomaly");
		ip.put("lowerBound");
		ip.put("lowerGAM");
		ip.put("predicted");
		ip.put("predictedResidual");
		ip.put("residual");
		ip.put("temperature");
		ip.put("upperARMA");
		ip.put("upperAnomaly");
		ip.put("upperBound");
		
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
		String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString());
		LOGGER.info("result is"+result);
		addReadingToDB(predictedTime,result,meterID,46466,46465);
		JSONArray mlArray = (JSONArray) new JSONObject(result).get("data");
        if(mlArray.length()>0)
        {
        	generateAlarm(14250,mlVariablesDataMap,result);
        }
	}
	
	private void generateAlarm(long parentMeterID,Hashtable<Long,Hashtable<String,SortedMap<Long,Object>>> mlVariablesDataMap , String result) throws Exception
	{
		try
		{
			long parentAlertID=0l;
			Set<Long> assetIDList = mlVariablesDataMap.keySet();
			for(long assetID : assetIDList)
			{
				AssetContext asset = AssetsAPI.getAssetInfo(assetID);
				String assetName = asset.getName();
				
				Hashtable<String,SortedMap<Long,Object>> variablesData = mlVariablesDataMap.get(assetID);
				
				
				SortedMap<Long,Object> actualValueMap = variablesData.get("actualValue");
				long ttime = actualValueMap.firstKey();
				double actualValue = (double) actualValueMap.get(actualValueMap.firstKey());
				
				SortedMap<Long,Object> adjustedUpperBoundMap = variablesData.get("adjustedUpperBound"); 
				double adjustedUpperBound = (double) adjustedUpperBoundMap.get(adjustedUpperBoundMap.firstKey());
				
				if(actualValue > adjustedUpperBound)
				{
					String message = "Anomaly Detected. Actual Consumption :"+actualValue+", Expected Max Consumption :"+adjustedUpperBound;
					JSONObject obj = new JSONObject();
					obj.put("message", message);
					obj.put("source", assetName);
					obj.put("condition", "Anomaly Detected in Energy Consumption");
					obj.put("resourceId", assetID);
					obj.put("severity", "Minor");
					obj.put("timestamp", ttime);
					obj.put("consumption", actualValue);
	
					obj.put("sourceType", SourceType.ANOMALY_ALARM.getIntVal());
					//obj.put("readingFieldId", mlVariableContext.getFieldID());
					obj.put("startTime", ttime);
					obj.put("readingMessage", message);
					FacilioContext addEventContext = new FacilioContext();
					addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, obj);
					Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
					getAddEventChain.execute(addEventContext);
					
					if(assetID==parentMeterID)
					{
						parentAlertID = 0L;
					}
					
				}
			}
		}
		catch(Exception e)
		{
			 LOGGER.fatal("Exception while generating Alarm ",e);
		}
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
