package com.facilio.bmsconsole.jobs;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.HVACPressurePredictorUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class HVACPressurePredictorJob extends FacilioJob 
{
	 private static final Logger LOGGER = LogManager.getLogger(HVACPressurePredictorJob.class.getName());
	//private static final Logger LOGGER = Logger.getLogger(HVACPressurePredictorJob.class.getName());
	
	@Override
	public void execute(JobContext jc) throws Exception 
	{
		LOGGER.info("Job started");
		long currentTime = System.currentTimeMillis();
		if( AwsUtil.isDevelopment()) 
		{ // for dev testing purpose time is moved back 
			currentTime = 1538677800000L;
		}
		
		long orgID = jc.getOrgId();
		long startTime = currentTime - (3 * 24 * 60 * 60 * 1000);//last 3 days data
		
		
		List<ResourceContext> resources = getResources(orgID);
		LOGGER.info("Running for "+resources.size());
		
		if(resources.size()>0)
		{
			for(ResourceContext resource: resources)
			{
				predictReadings(resource, orgID, startTime, currentTime, jc.getExecutionTime() * 1000);
			}
		}
				            	 //writeJSONArray(jsonArray.toString());
				
		LOGGER.info("Job Completed successfully");

	}
	
	private List<ResourceContext> getResources(long orgID) throws Exception
	{
		try
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
			
			
			SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
																			.select(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE))
																			.module(module)
																			.beanClass(ResourceContext.class)
																			.andCondition(CriteriaAPI.getCondition("RESOURCE_TYPE","resource_type",2+"", NumberOperators.EQUALS));
			return resourceBuilder.get();
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while getting resources for OrgID "+orgID, e);
			throw e;
		}
	}
	
	private void predictReadings(ResourceContext resource,long orgID,long startTime,long endTime, long predictionTime) throws Exception
	{
		try
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
			List<FacilioField> fields = HVACPressurePredictorUtil.getFields();
			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
	                										 .select(fields)
	                										 .table("Prediction_Relation")
	                										 .andCustomWhere("ORGID = ? and asset_id = ?", orgID,resource.getId());
			
			List<Map<String,Object>> predictionRelationMap = selectRecordBuilder.get();
			
			List<FacilioField> columnFields = new ArrayList<>();
			FacilioField field1 = modBean.getField((Long) predictionRelationMap.get(0).get("fieldId"));
			FacilioField predictedField = modBean.getField((Long) predictionRelationMap.get(0).get("predictedFieldId"));
			FacilioField field2 = modBean.getField((Long) predictionRelationMap.get(1).get("fieldId"));
			columnFields.add(field1);
			columnFields.add(field2);
				
			FacilioField ttimeField = modBean.getField("ttime", field1.getModule().getName());
			int dataInterval = field1.getModule().getDataInterval();
			columnFields.add(ttimeField);
				
			SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
					.select(columnFields)
					.module(field1.getModule())
					.beanClass(ReadingContext.class)
					.andCustomWhere("TTIME >= ? AND TTIME < ? AND PARENT_ID=? ",
							startTime, endTime, resource.getId());
	
			 List<Map<String, Object>> props = selectBuilder.getAsProps();
			 
			 TreeMap<Long,Double> bagMap = new TreeMap<Long,Double>();
			 TreeMap<Long,Boolean> supplyMap = new TreeMap<Long,Boolean>();
			 if (props != null && !props.isEmpty()) 
			 {
				 for(Map<String, Object> prop:props) 
				 {
					 long ttime = (long) prop.get("ttime");
					 if(prop.containsKey("bagfilterdifferentialpressure"))
					 {
						 bagMap.put(ttime,(double) prop.get("bagfilterdifferentialpressure"));
	        		 }
					 else
					 {
						 supplyMap.put(ttime, (boolean)prop.get("supplyfanairflowstatus"));
	        		 }
	        	 }
	         }
	         
			 JSONArray jsonArray = new JSONArray();
			 if(bagMap.size()>0)
			 {
				 
				 Set<Long> keySet = bagMap.keySet();
				 boolean lastSupplyFanStatus = true;
				 for(long key: keySet)
				 {
					 if(supplyMap.containsKey(key) && supplyMap.get(key))
					 {
						 JSONObject jsonData = new JSONObject();
						 jsonData.put("ttime", key);
						 jsonData.put("bagfilterdifferentialpressure", bagMap.get(key));
						 jsonArray.put(jsonData);
						 lastSupplyFanStatus = true;
	    		 	 }
	        		 else if(supplyMap.containsKey(key))
	        		 {
	        			 lastSupplyFanStatus = false;
	        		 }
	        		 else
	        		 {
	        			 if(lastSupplyFanStatus)
	        			 {
	        				 JSONObject jsonData = new JSONObject();
	        				 jsonData.put("ttime", key);
	        				 jsonData.put("bagfilterdifferentialpressure", bagMap.get(key));
	        				 jsonArray.put(jsonData);
	        			 }
	    		 	}
	        	 }
			}
			
			 JSONObject postObj = new JSONObject();
			 postObj.put("meterInterval",dataInterval);
			 postObj.put("data", jsonArray);
			 
			 String postURL = AwsUtil.getConfig("anomalyCheckServiceURL") + "/timeseriesmodel";
			 Map<String, String> headers = new HashMap<>();
			 headers.put("Content-Type", "application/json");	
			 String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString());
			 LOGGER.debug("Result is "+result);
			 updateResultinDB(resource.getId(),predictedField,result, predictionTime);

		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while getting Prediction for "+resource.getId(), e) ;
		}
	}
		
	
	private void updateResultinDB(long parentID,FacilioField field , String result, long predictionTime) throws Exception
	{
		try
		{
			JSONArray mlArray = (JSONArray) new JSONObject(result).get("results");
			List<ReadingContext> newList = new ArrayList<>();
		 
			 for(int i=0; i<mlArray.length(); i++)
			 {
				 JSONObject readingObj = (JSONObject) mlArray.get(i);
				 
				 ReadingContext newReading = new ReadingContext();
				 newReading.setParentId(parentID);
	   			
				 Object value = readingObj.get("predicted");
				 newReading.addReading(field.getName(), value);
	   			
				 newReading.setTtime((long)readingObj.get("ttime"));
				 newReading.addReading("predictedTime", predictionTime);
				 newList.add(newReading);
			 }
	   		
			 FacilioContext context = new FacilioContext();
			 context.put(FacilioConstants.ContextNames.MODULE_NAME, field.getModule().getName());
			 context.put(FacilioConstants.ContextNames.READINGS, newList);
			 context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.ML);
			 Chain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
			 chain.execute(context);
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while updating Result in DB", e);
			throw e;
		}
	}
	
	/* Testing code
	private void writeJSONArray(String array)
	{
		try 
		{
			FileWriter writer = new FileWriter("/Users/digitalsuppliers/data/output.data");
			writer.write(array);
			writer.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	*/
	

}
