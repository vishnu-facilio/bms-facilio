package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.jboss.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MlForecastingContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddReadingsForMLCommand implements Command {
	
	private static final Logger LOGGER = Logger.getLogger(AddReadingsForMLCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception 
	{
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		try
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule logModule = modBean.getModule(mlContext.getPredictedLogModuleid());
			FacilioModule predictModule = modBean.getModule(mlContext.getPredictedModuleid());
			
			JSONArray mlArray = (JSONArray) new JSONObject(mlContext.getResult()).get("results");
			List<FacilioField> fields = logModule.getFields();
			
			List<ReadingContext> logReadingList = new ArrayList<>();
			List<ReadingContext> predictReadingList = new ArrayList<>(); 
		 
			 for(int i=0; i<mlArray.length(); i++)
			 {
				 JSONObject readingObj = (JSONObject) mlArray.get(i);
				 
				 ReadingContext newReading = new ReadingContext();
				 newReading.setParentId(mlContext.getAssetContext().getAssetID());
				 newReading.setTtime((long)readingObj.get("ttime"));
				 newReading.addReading("predictedTime", mlContext.getPredictionTime());
				 for(FacilioField field:fields)
				 {
					 if(readingObj.has(field.getName()))
					 {
						 newReading.addReading(field.getName(), readingObj.get(field.getName()));
					 }
				 }
				 logReadingList.add(newReading);
				 predictReadingList.add(newReading);
			 }
			 updateReading(logModule,logReadingList);
			 if(!predictReadingList.isEmpty())
			 {
				 
				 try
				 {
					 updateExistingPredictReading(mlContext.getAssetContext().getAssetID(),predictModule,predictReadingList);


				 }
				 catch(Exception e)
				 {
					 LOGGER.error("Error while updating Predicted Reading", e);
				 }
			 }
			 
			 
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while updating Result in DB", e);
			throw e;
		}
		return false;
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
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.READINGS, readingList);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.ML);
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
