package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.MlForecastingContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.criteria.*;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Chain;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class MLForecastingLifetimePredictionJob extends FacilioJob 
{

	private static final Logger LOGGER = Logger.getLogger(MLForecastingLifetimePredictionJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception 
	{
		List<MlForecastingContext> predictionJobs = getPredictionJobs(jc.getOrgId());
		for(MlForecastingContext context: predictionJobs)
		{
			try
			{
				getFields(context);
				getReadings(context);
				generateModel(context);
				addToDB(context,jc.getExecutionTime());
			}
			catch(Exception e)
			{
				LOGGER.fatal("Error while predicting for "+context.getId(),e);
			}
		}
	}
	
	private List<MlForecastingContext> getPredictionJobs(long orgid) throws Exception
	{
		try
		{
			FacilioModule module = ModuleFactory.getMlForecastingLifetimeModule();
			GenericSelectRecordBuilder recordBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(FieldFactory.getMlForecastingLifetimeFields())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
			
			List<Map<String, Object>> predictionListMap = recordBuilder.get();
			List<MlForecastingContext> mlForecastList = getMlForecastFromFromMapList(predictionListMap);
			return mlForecastList;
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while getting resources for OrgID "+orgid, e);
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
	
	private void generateModel(MlForecastingContext pc) throws Exception
	{
		 JSONObject postObj = new JSONObject();
		 postObj.put("predictedFieldID", pc.getPredictedfieldid());
		 postObj.put("maximumDays", 180);
		 if(pc.getPredictedfieldid()==490436)
		 {
			 postObj.put("Threshold", 269);
		 }
		 else if(pc.getPredictedfieldid()==589686)
		 {
			 postObj.put("Threshold", 169);
		 }
		 else if(pc.getPredictedfieldid()==589687)
		 {
			 postObj.put("Threshold", 169);
		 }
		 else if(pc.getPredictedfieldid()==589688)
		 {
			 postObj.put("Threshold", 149);
		 }
		 else if(pc.getPredictedfieldid()==589689)
		 {
			 postObj.put("Threshold", 10);
		 }
		 else if(pc.getPredictedfieldid()==589690)
		 {
			 postObj.put("Threshold", 35);
		 }
		 else if(pc.getPredictedfieldid()==589691)
		 {
			 postObj.put("Threshold", 7);
		 }
		 else if(pc.getPredictedfieldid()==589692)
		 {
			 postObj.put("Threshold", 7);
		 }
		 postObj.put("Timezone", AccountUtil.getCurrentOrg().getTimezone());
		 postObj.put("meterInterval",pc.getDatainterval());
		 postObj.put("AssetDetails", pc.getPyData());
		 
		 String postURL=AwsUtil.getAnomalyPredictAPIURL() + "/predictlifetime";
		 Map<String, String> headers = new HashMap<>();
		 headers.put("Content-Type", "application/json");
		 String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString());
		 pc.setResult(result);
	}
	
	private void addToDB(MlForecastingContext pc, long predictionTime) throws Exception
	{
		try
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField logField = modBean.getField(pc.getPredictedlogfieldid());
			FacilioField predictField = modBean.getField(pc.getPredictedfieldid());
			
			List<ReadingContext> predictedReadingList = new ArrayList<>(1);
			JSONObject resultObj = new JSONObject(pc.getResult());
			int  predictedFailureDays = (int)resultObj.get("result");
			ReadingContext newReading = new ReadingContext();
			newReading.setParentId(pc.getAssetid());
			newReading.addReading(logField.getName(), predictedFailureDays);	   			
			newReading.setTtime(System.currentTimeMillis());
			newReading.addReading("predictedTime", predictionTime);
			predictedReadingList.add(newReading);
			
			updateReading(logField.getModule(),predictedReadingList);
			updateAssetEntry(predictField,pc.getAssetid(),predictedFailureDays);
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
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.ML);
		Chain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
		chain.execute(context);
	}
	
	private void updateAssetEntry(FacilioField field,long assetid,int predictedValue)
	{
		try
		{
			AssetContext asset = AssetsAPI.getAssetInfo(assetid);
			Map<String, Object> assetData = new HashMap<String, Object>();
			assetData.put(field.getName(), predictedValue);
			asset.addData(assetData);
			FacilioContext context = new FacilioContext();
			//context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
			context.put(FacilioConstants.ContextNames.RECORD, asset);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(asset.getId()));
			context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			
			Chain updateAssetChain = TransactionChainFactory.getUpdateAssetChain();
			updateAssetChain.execute(context);
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while updating predicted value for Asset "+assetid, e);
		}
	}

}
