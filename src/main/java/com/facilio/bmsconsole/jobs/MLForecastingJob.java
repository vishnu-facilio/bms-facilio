package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;


public class MLForecastingJob extends FacilioJob 
{
	private static final Logger LOGGER = Logger.getLogger(MLForecastingJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception 
	{
		LOGGER.info("Job started");
		
		List<MlForecastingContext> pcList = getPredictionJobs(jc.getOrgId());
		for(MlForecastingContext predictionContext : pcList)
		{
			doPrediction(predictionContext, jc.getExecutionTime() * 1000);
		}

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
			generateModel(pc);
			addToDB(pc, predictionTime);
			
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
			FacilioField field = modBean.getField(pc.getPredictedfieldid());
			JSONArray mlArray = (JSONArray) new JSONObject(pc.getResult()).get("results");
			List<ReadingContext> newList = new ArrayList<>();
		 
			 for(int i=0; i<mlArray.length(); i++)
			 {
				 JSONObject readingObj = (JSONObject) mlArray.get(i);
				 
				 ReadingContext newReading = new ReadingContext();
				 newReading.setParentId(pc.getAssetid());
	   			
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
		boolean properData = true;
		long lastTime = -1L;
		for(Map<String, Object> prop:props)
		{
			if(!isConditional(prop,criteria,properData))
			{
				if(properData)
				{
					if((long)prop.get("ttime")>lastTime)
					{
						addDataField(pc,prop,array);
						lastTime=(long) prop.get("ttime");
					}
				}
			}
			/*Map<String,Condition> conditions =criteria.getConditions();
			Map<Condition,Boolean> conditionsResult = new HashMap<Condition,Boolean>(conditions.size());
			conditions.entrySet().stream().forEach( e -> conditionsResult.put(e.getValue(), true));
			if(!isConditional(prop,conditions,conditionsResult,conditionalResult))
			{
				addDataField(pc,prop,array);
			}*/
			
		}
		
		pc.setPyData(array);
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
				jsonObject.put(field.getName(), prop.get(field.getName()));
				data.put(jsonObject);
			}
		}
	}
	
	private boolean isConditional(Map<String,Object> prop,Criteria criteria,boolean properData)
	{
		if(criteria==null)
		{
			return false;
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
