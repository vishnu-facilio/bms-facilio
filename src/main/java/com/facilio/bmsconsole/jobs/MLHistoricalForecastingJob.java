package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;
import org.json.JSONArray;
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
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class MLHistoricalForecastingJob extends FacilioJob 
{

	private static final Logger LOGGER = Logger.getLogger(MLHistoricalForecastingJob.class.getName());
	
	@Override
	public void execute(JobContext jc) throws Exception 
	{
	
		/*
		  Test Setup - Local
		 
		long startTime=1537637400000l;
		long endTime = 1538677800000l;
		runPrediction(startTime,endTime,15);
		*/
		/*
		long startTime = 1524783600000l;//Apr 27  04:30
		long endTime = 1535482800000l;//Aug 29 00:30 
		
		try
		{
			runPrediction(startTime , endTime,60);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		*/
		long startTime = 1537633600000l;//Sep 22 2018 21:56
		long endTime = 1550000000000l;//Feb 13 2019 01:03:20
		try
		{
			runPrediction(startTime,endTime,10);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void runPrediction(long startTime,long endTime,long dataInterval) throws Exception
	{
		long bagfilterFieldID = 253613;
		long supplyFanStatusFieldID = 253885;
		long ttimeFieldID = 253618;
		
		long predictedLogFieldID=458643l;
		long predictedFieldID=488130l;
		
		long assetId=969283l;
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField predictedField = modBean.getField(predictedFieldID);
		FacilioField predictedLogField = modBean.getField(predictedLogFieldID);
		
		long currentTime = startTime + 259200000;
		Map<Long,ReadingContext> ttimeVsMap = new HashMap<Long,ReadingContext>(25000);
		while(startTime < endTime)
		{
			try
			{
				JSONArray array = getReadings(assetId,bagfilterFieldID,supplyFanStatusFieldID,ttimeFieldID,startTime,currentTime);
				if(array!=null)
				{
					
					JSONObject postObj = new JSONObject();
					postObj.put("meterInterval",dataInterval);
					postObj.put("data", array);
					 
					 String postURL=AwsUtil.getAnomalyPredictAPIURL() + "/timeseriesmodel";
					 Map<String, String> headers = new HashMap<>();
					 headers.put("Content-Type", "application/json");
					 String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString());
					 
					 if(!(result==null || result.isEmpty()))
					 {
						 JSONArray mlArray = (JSONArray) new JSONObject(result).get("results");
						 List<ReadingContext> logReadingList = new ArrayList<>();
						 
						 for(int i=0; i<mlArray.length(); i++)
						 {
							 JSONObject readingObj = (JSONObject) mlArray.get(i);
						
							 ReadingContext newReading = new ReadingContext();
							 newReading.setParentId(assetId);
							 Object value = readingObj.get("predicted");
							 newReading.addReading(predictedLogField.getName(), value);	   			
							 newReading.setTtime((long)readingObj.get("ttime"));
							 newReading.addReading("predictedTime", System.currentTimeMillis());
							 logReadingList.add(newReading);
							 
							 ReadingContext predictReading = new ReadingContext();
							 predictReading.setParentId(assetId);
							 predictReading.addReading(predictedField.getName(), value);	   			
							 predictReading.setTtime((long)readingObj.get("ttime"));
							 ttimeVsMap.put((long)readingObj.get("ttime"), predictReading);
						 }
						 LOGGER.info("Size of TTIME MAP =>"+ttimeVsMap.size());
						 updateReading(predictedLogField.getModule(),logReadingList);
					 }
				}
			}
			catch(Exception e)
			{
				
			}
			startTime = startTime+(3600*1000);
			currentTime = startTime + 259200000;
		}
		if(ttimeVsMap.size()>0)
		{
			try
			{
				updateReading(predictedField.getModule(),new ArrayList<ReadingContext>(ttimeVsMap.values()));
			}
			catch(Exception e)
			{
				
			}
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
	
	private JSONArray getReadings(long assetid,long bagFilterFieldID,long supplyFanStatusFieldID,long ttimeFieldID,long startTime,long endTime) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField bagFilterField = modBean.getField(bagFilterFieldID);
		FacilioField supplyFanStatusField = modBean.getField(supplyFanStatusFieldID);
		FacilioField ttimeField = modBean.getField(ttimeFieldID);
		List<FacilioField> fieldList = new ArrayList<>(3);
		fieldList.add(bagFilterField);
		fieldList.add(supplyFanStatusField);
		fieldList.add(ttimeField);
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(fieldList)
				.module(bagFilterField.getModule())
				.beanClass(ReadingContext.class)
				.orderBy("TTIME ASC")
				.andCustomWhere("TTIME >= ? AND TTIME < ? AND PARENT_ID=? ",
						startTime, endTime, assetid);
		
		List<Map<String, Object>> props = selectBuilder.getAsProps();
		
		
		JSONArray array = new JSONArray();
		Criteria criteria = CriteriaAPI.getCriteria(134, 10363);
		

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
						if(prop.containsKey("bagfilterdifferentialpressure"))
						{
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("ttime", prop.get("ttime"));
							jsonObject.put("metric", prop.get("bagfilterdifferentialpressure"));//always use metric as this is a generic call
							array.put(jsonObject);
						}
						lastTime=(long) prop.get("ttime");
					}
				}
			}
		}
		if(supplyStatus)
		{
			return array;
		}
		else
		{
			return null;
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
