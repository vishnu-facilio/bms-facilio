package com.facilio.bmsconsole.commands;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.MLAnomalyEvent;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.RCAEvent;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;

public class TriggerAlarmForMLCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(TriggerAlarmForMLCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception 
	{
		LOGGER.info("Inside execute");
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		try
		{
			if(mlContext.getModelPath().equals("ratioCheck"))
			{
				
	            Set<Long> assetIDList = mlContext.getMlVariablesDataMap().keySet();
	            String treeHierachy = mlContext.getMLModelVariable("TreeHeirarchy");
	            String[] assetList = treeHierachy.split(",");
	            long parentID = Long.parseLong(assetList[0]);
	            
	            long parentAlarmID = checkAndGenerateEvent(mlContext,parentID);
	            
	            for(long assetID : assetIDList)
            	{
            		if(assetID==parentID)
            		{
            			continue;
            		}
            		generateEvent(mlContext,assetID,parentAlarmID);
            	}
	            
	         }
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while triggering alarm for ML_ID"+mlContext.getId(),e);
		}
         
		return false;
	}
	
	private void generateEvent(MLContext mlContext,long assetID,long parentAlarmID) throws Exception
	{
		LOGGER.info("Inside Generate Event "+parentAlarmID+" AssetId:"+assetID+" Mlid: "+mlContext.getId());
		if(parentAlarmID!=-1)
		{
			checkAndGenerateRCAEvent(mlContext,assetID,parentAlarmID);
		}
		else
		{
			checkAndGenerateEvent(mlContext,assetID);
		}
	}
	
	private long checkAndGenerateEvent(MLContext mlContext, long parentID) throws Exception
	{
		
		Hashtable<String,SortedMap<Long,Object>> variablesData = mlContext.getMlVariablesDataMap().get(parentID);
		
    	SortedMap<Long,Object> actualValueMap = variablesData.get("actualValue");
    	double actualValue = (double) actualValueMap.get(actualValueMap.firstKey());

    	SortedMap<Long,Object> adjustedUpperBoundMap = variablesData.get("adjustedUpperBound");
    	double adjustedUpperBound = (double) adjustedUpperBoundMap.get(adjustedUpperBoundMap.firstKey());
    	 
    	LOGGER.info("Inside check and Generate Event "+parentID+". actual value "+actualValue+" , upperBound "+adjustedUpperBound);
    	if(actualValue > adjustedUpperBound)
    	{
    		return generateAnomalyEvent(actualValue,adjustedUpperBound,parentID,mlContext.getMLVariable().get(0).getFieldID(),mlContext.getPredictionTime(),Long.parseLong(mlContext.getMLModelVariable("energyfieldid")),Long.parseLong(mlContext.getMLModelVariable("adjustedupperboundfieldid")),mlContext.getId());
    	}
    	else
    	{
			generateClearEvent(parentID,mlContext.getMLVariable().get(0).getFieldID(),mlContext.getPredictionTime(),parentID,mlContext.getId());
    	}
    	return -1;
	}
	
	private boolean checkAndGenerateRCAEvent(MLContext mlContext, long assetID,long parentAlarmID) throws Exception
	{
		Set<Long> keys =mlContext.getMlVariablesDataMap().keySet();
		LOGGER.info("Variable Data Map keys are "+keys+":"+assetID+"::"+parentAlarmID);
		Hashtable<String,SortedMap<Long,Object>> variablesData = mlContext.getMlVariablesDataMap().get(assetID);
		
    	SortedMap<Long,Object> actualValueMap = variablesData.get("actualValue");
    	double actualValue = (double) actualValueMap.get(actualValueMap.firstKey());

    	SortedMap<Long,Object> adjustedUpperBoundMap = variablesData.get("adjustedUpperBound");
    	double adjustedUpperBound = (double) adjustedUpperBoundMap.get(adjustedUpperBoundMap.firstKey());

    	if(actualValue > adjustedUpperBound)
    	{
    		generateRCAAnomalyEvent(actualValue,adjustedUpperBound,assetID,mlContext.getMLVariable().get(0).getFieldID(),mlContext.getPredictionTime(),parentAlarmID,Long.parseLong(mlContext.getMLModelVariable("energyfieldid")),Long.parseLong(mlContext.getMLModelVariable("adjustedupperboundfieldid")),mlContext.getId());
    		return true;
    	}
    	else
    	{
    		generateClearEvent(assetID,mlContext.getMLVariable().get(0).getFieldID(),mlContext.getPredictionTime(),parentAlarmID,mlContext.getId());
    	}
    	return false;
	}
	
	private void generateClearEvent(long assetID,long fieldID,long ttime,long parentid,long mlid) {
		try 
		{
			if(NewAlarmAPI.getActiveAlarmOccurance("Anomaly_RCA_" + ResourceAPI.getResource(parentid).getId(),Type.RCA_ALARM) != null)
			{
				generateClearRCAEvent(assetID,fieldID,ttime,parentid,mlid);
			}
			else 
			{
				generateClearMLAnomalyEvent(parentid,fieldID,ttime,mlid);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateClearMLAnomalyEvent(long assetID,long fieldID,long ttime,long mlid)
	{
		try
		{
			AlarmOccurrenceContext alarmOccuranceContext = NewAlarmAPI.getActiveAlarmOccurance("Anomaly_" + ResourceAPI.getResource(assetID).getId(),Type.ML_ANOMALY_ALARM);
			if(alarmOccuranceContext!=null)
			{
				LOGGER.info("Generating clear Event for assetID:"+assetID);
				String message = "Anomaly Cleared";
				MLAnomalyEvent event = new MLAnomalyEvent();
				event.setEventMessage(message);
		        event.setResource(ResourceAPI.getResource(assetID));
		        event.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
		        event.setReadingTime(ttime);
		        event.setmlid(mlid);
		        
		        List<BaseEventContext> eventList = new ArrayList<BaseEventContext>();
		        eventList.add(event);
		        
		        FacilioContext context = new FacilioContext();
				context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
				Chain chain = TransactionChainFactory.getV2AddEventChain();
				chain.execute(context);
			}
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while generating event ",e);
		}
	}
	
	private void generateClearRCAEvent(long assetID,long fieldID,long ttime,long parentid,long mlid)
	{
		try
		{
			AlarmOccurrenceContext alarmOccuranceContext = NewAlarmAPI.getActiveAlarmOccurance("Anomaly_RCA_" + ResourceAPI.getResource(assetID).getId(),Type.RCA_ALARM);
			if(alarmOccuranceContext!=null)
			{
				LOGGER.info("Generating RCAclear Event for assetID:"+assetID+" and PARENTID: "+alarmOccuranceContext.getAlarm().getId()+" Check"+alarmOccuranceContext.getId());
				String message = "Anomaly Cleared";
				RCAEvent event = new RCAEvent();
				event.setEventMessage(message);
		        event.setResource(ResourceAPI.getResource(assetID));
		        event.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
		        event.setReadingTime(ttime);
		        event.setparentid(alarmOccuranceContext.getAlarm().getId());
		        event.setmlid(mlid);
		        
		        List<BaseEventContext> eventList = new ArrayList<BaseEventContext>();
		        eventList.add(event);
		        
		        FacilioContext context = new FacilioContext();
				context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
				Chain chain = TransactionChainFactory.getV2AddEventChain();
				chain.execute(context);
			}
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while generating event ",e);
		}
	}
	
	private long generateAnomalyEvent(double actualValue,double adjustedUpperBound,long assetID,long fieldID,long ttime,long energyDataFieldid,long upperAnomalyFieldid,long mlid) throws Exception
	{
		AssetContext asset = AssetsAPI.getAssetInfo(assetID);
        String assetName = asset.getName();
     
        LOGGER.info("Generating Anomaly Event "+assetID);
		String message = "Anomaly Detected. Actual Consumption :"+actualValue+", Expected Max Consumption :"+adjustedUpperBound;
		
		MLAnomalyEvent event = new MLAnomalyEvent();
		event.setEventMessage(message);
        event.setResource(ResourceAPI.getResource(assetID));
        event.setActualValue(actualValue);
        event.setAdjustedUpperBoundValue(adjustedUpperBound);
        event.setSeverityString("Minor");
        event.setReadingTime(ttime);
        event.setEnergyDataFieldid(energyDataFieldid);
        event.setUpperAnomalyFieldid(upperAnomalyFieldid);
        event.setmlid(mlid);
        
       return addEvent(event); 
	
	}
	
	private void generateRCAAnomalyEvent(double actualValue,double adjustedUpperBound,long assetID,long fieldID,long ttime,long parentid,long energyDataFieldid,long upperAnomalyFieldid,long mlid) throws Exception
	{   
		LOGGER.info("Generating RCAAnomaly Event "+assetID);
		String message = "Anomaly Detected. Actual Consumption :"+actualValue+", Expected Max Consumption :"+adjustedUpperBound;
		
		RCAEvent event = new RCAEvent();
		event.setEventMessage(message);
        event.setResource(ResourceAPI.getResource(assetID));
        event.setActualValue(actualValue);
        event.setAdjustedUpperBoundValue(adjustedUpperBound);
        event.setSeverityString("Minor");
        event.setReadingTime(ttime);
        event.setEnergyDataFieldid(energyDataFieldid);
        event.setUpperAnomalyFieldid(upperAnomalyFieldid);
        event.setparentid(parentid);
        event.setmlid(mlid);
        
        
        addEvent(event);
	
	}
	
	private long addEvent(BaseEventContext event) throws Exception
	{
		List<BaseEventContext> eventList = new ArrayList<BaseEventContext>();
        eventList.add(event);
        
        FacilioContext context = new FacilioContext();
		context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
		Chain chain = TransactionChainFactory.getV2AddEventChain();
		chain.execute(context);
		
		return event.getAlarmOccurrence().getAlarm().getId();
	}
	
	public static void main(String arg[])
	{
		try 
		{
			JSONObject response = new JSONObject(" {'ml_id': 27, 'orgid': 104, 'assetid': 14249, 'data': [{'ttime': 1558442700000, 'actualValue': 13.6979, 'temperature': 50.629999999999995, 'predicted': 9.961061648342277, 'residual': 3.736838351657724, 'predictedResidual': 0.08733544757678538, 'upperGAM': 29.77101612804105, 'lowerGAM': -9.848892831356498, 'upperBound': 28.0272589686494, 'lowerBound': -7.930464776811277, 'upperARMA': 18.066197320307122, 'lowerARMA': -17.891526425153554, 'adjustedUpperBound': 28.0272589686494, 'adjustedLowerBound': 0.0, 'gamAnomaly': 0, 'upperAnomaly': 0, 'lowerAnomaly': 0}], 'OutputMetrics': \"'actualValue','adjustedLowerBound','adjustedUpperBound','gamAnomaly','lowerARMA','lowerAnomaly','lowerBound','lowerGAM','predicted','predictedResidual','residual','temperature','ttime','upperARMA','upperAnomaly','upperBound','upperGAM'\"}");
			long meterId = response.getLong("assetid");
			JSONArray responseData = (JSONArray)response.get("data");
			for(int i=0;i<responseData.length();i++)
			{
				JSONObject data = (JSONObject)responseData.get(i);
			//JSONArray mlArray = new JSONArray(" {'ml_id': 27, 'orgid': 104, 'assetid': 14249, 'data': [{'ttime': 1558442700000, 'actualValue': 13.6979, 'temperature': 50.629999999999995, 'predicted': 9.961061648342277, 'residual': 3.736838351657724, 'predictedResidual': 0.08733544757678538, 'upperGAM': 29.77101612804105, 'lowerGAM': -9.848892831356498, 'upperBound': 28.0272589686494, 'lowerBound': -7.930464776811277, 'upperARMA': 18.066197320307122, 'lowerARMA': -17.891526425153554, 'adjustedUpperBound': 28.0272589686494, 'adjustedLowerBound': 0.0, 'gamAnomaly': 0, 'upperAnomaly': 0, 'lowerAnomaly': 0}], 'OutputMetrics': \"'actualValue','adjustedLowerBound','adjustedUpperBound','gamAnomaly','lowerARMA','lowerAnomaly','lowerBound','lowerGAM','predicted','predictedResidual','residual','temperature','ttime','upperARMA','upperAnomaly','upperBound','upperGAM'\"}");
			
			//AssetContext asset = AssetsAPI.getAssetInfo(meterId);
			//String assetName = asset.getName();

			String message = "Anomaly Detected. Actual Consumption :"+data.getLong("actualValue")+", Expected Max Consumption :"+data.getLong("AdjustedUpperBound");
			JSONObject obj = new JSONObject();
			obj.put("message", message);
			obj.put("source", "test");
			obj.put("condition", "Anomaly Detected in Energy Consumption");
			obj.put("resourceId", meterId);
			obj.put("severity", "Minor");
			obj.put("timestamp", data.get("ttime"));
			obj.put("consumption", data.get("actualValue"));

			obj.put("sourceType", SourceType.ANOMALY_ALARM.getIntVal());
			//obj.put("readingFieldId", consumptionField.getFieldId());
			//obj.put("readingDataId", context.getId());
			//obj.put("startTime", context.getTtime());
			obj.put("readingMessage", message);
			}
			/*FacilioContext addEventContext = new FacilioContext();
			addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, obj);
			Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
			getAddEventChain.execute(addEventContext);*/
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}

}
