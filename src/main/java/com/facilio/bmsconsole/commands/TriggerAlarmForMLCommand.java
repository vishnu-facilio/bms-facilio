package com.facilio.bmsconsole.commands;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.MLAlarmOccurenceContext;
import com.facilio.bmsconsole.context.MLAnomalyEvent;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.RCAEvent;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.FieldUtil;

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
	            String treeHierachy = mlContext.getMLModelVariable("TreeHierarchy");
	            String[] assetList = treeHierachy.split(",");
	            long parentID = Long.parseLong(assetList[0]);
	            
	            MLAnomalyEvent parentEvent = checkAndGenerateEvent(mlContext,parentID);
	            
	            for(long assetID : assetIDList)
            	{
            		if(assetID==parentID)
            		{
            			continue;
            		}
            		generateEvent(mlContext,assetID,parentEvent);
            	}
	            
	         }
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while triggering alarm for ML_ID"+mlContext.getId(),e);
		}
         
		return false;
	}
	
	private void generateEvent(MLContext mlContext,long assetID,MLAnomalyEvent parentEvent) throws Exception
	{
		LOGGER.info("Inside Generate Event "+FieldUtil.getAsProperties(parentEvent)+" AssetId:"+assetID+" Mlid: "+mlContext.getId());
		if(parentEvent!=null)
		{
			checkAndGenerateRCAEvent(mlContext,assetID,parentEvent);
		}
		else
		{
			checkAndGenerateEvent(mlContext,assetID);
		}
	}
	
	private MLAnomalyEvent checkAndGenerateEvent(MLContext mlContext, long parentID) throws Exception
	{
		try
		{
			Hashtable<String,SortedMap<Long,Object>> variablesData = mlContext.getMlVariablesDataMap().get(parentID);
			
	    	SortedMap<Long,Object> actualValueMap = variablesData.get("actualValue");
	    	double actualValue = (double) actualValueMap.get(actualValueMap.firstKey());
	
	    	SortedMap<Long,Object> adjustedUpperBoundMap = variablesData.get("adjustedUpperBound");
	    	double adjustedUpperBound = (double) adjustedUpperBoundMap.get(adjustedUpperBoundMap.firstKey());
	    	
	    	 
	    	LOGGER.info("Inside check and Generate Event "+parentID+". actual value "+actualValue+" , upperBound "+adjustedUpperBound);
	    	if(actualValue > adjustedUpperBound)
	    	{
	    		return generateAnomalyEvent(actualValue,adjustedUpperBound,parentID,mlContext.getMLVariable().get(0).getFieldID(),getReadingTime(mlContext),Long.parseLong(mlContext.getMLModelVariable("energyfieldid")),Long.parseLong(mlContext.getMLModelVariable("adjustedupperboundfieldid")),mlContext);
	    	}
	    	else
	    	{
				generateClearMLAnomalyEvent(parentID,mlContext.getMLVariable().get(0).getFieldID(),getReadingTime(mlContext),mlContext);
	    	}
		}
		catch(Exception e)
		{
			LOGGER.error("Error while generating event "+parentID+"::"+mlContext.getMlVariablesDataMap().get(parentID), e);
		}
    	return null;
	}
	
	private void checkAndGenerateRCAEvent(MLContext mlContext, long assetID,MLAnomalyEvent parentEvent)
	{
		try{
			Set<Long> keys =mlContext.getMlVariablesDataMap().keySet();
			LOGGER.info("Variable Data Map keys are "+keys+":"+assetID+"::"+parentEvent.getId());
			Hashtable<String,SortedMap<Long,Object>> variablesData = mlContext.getMlVariablesDataMap().get(assetID);
			
	    	SortedMap<Long,Object> actualValueMap = variablesData.get("actualValue");
	    	double actualValue = (double) actualValueMap.get(actualValueMap.firstKey());
	
	    	SortedMap<Long,Object> adjustedUpperBoundMap = variablesData.get("adjustedUpperBound");
	    	double adjustedUpperBound = (double) adjustedUpperBoundMap.get(adjustedUpperBoundMap.firstKey());
	
	    	if(actualValue > adjustedUpperBound)
	    	{
	    		LOGGER.info("Asset Details before generating RCA "+assetID+"::"+mlContext.getAssetDetails());
	    		generateRCAAnomalyEvent(actualValue,adjustedUpperBound,assetID,mlContext.getMLVariable().get(0).getFieldID(),mlContext,parentEvent,Long.parseLong(mlContext.getMLModelVariable("energyfieldid")),Long.parseLong(mlContext.getMLModelVariable("adjustedupperboundfieldid")),mlContext.getId(),mlContext.getAssetDetails());
	    	}
	    	else
	    	{
	    		generateClearMLAnomalyEvent(assetID,mlContext.getMLVariable().get(0).getFieldID(),getReadingTime(mlContext),mlContext);
	    	}
		}catch(Exception e){
			LOGGER.fatal("Error while check And Generating RCAEvent ",e);
		}
	}
	/*
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
	}*/
	
	private void generateClearMLAnomalyEvent(long assetID,long fieldID,long ttime,MLContext mlContext)
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
		        event.setCreatedTime(ttime);
		        event.setmlid(mlContext.getId());
		        
		        addEvent(mlContext,event);
		   
			}
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while generating event ",e);
		}
	}
	
	private void generateClearRCAEvent(long assetID,long fieldID,long ttime,long parentid,MLContext mlContext)
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
		        event.setCreatedTime(ttime);
		        event.setparentid(alarmOccuranceContext.getAlarm().getId());
		        event.setmlid(mlContext.getId());
		        
		        addEvent(mlContext,event);
			}
		}
		catch(Exception e)
		{
			LOGGER.fatal("Error while generating event ",e);
		}
	}
	
	private MLAnomalyEvent generateAnomalyEvent(double actualValue,double adjustedUpperBound,long assetID,long fieldID,long ttime,long energyDataFieldid,long upperAnomalyFieldid,MLContext mlContext) throws Exception
	{
		AssetContext asset = AssetsAPI.getAssetInfo(assetID);
        String assetName = asset.getName();
     
        LOGGER.info("Generating Anomaly Event "+assetID);
		String message = "Anomaly Detected. Actual Consumption :"+actualValue+", Expected Max Consumption :"+adjustedUpperBound;
		
		MLAnomalyEvent event = new MLAnomalyEvent();
		event.setEventMessage("Anomaly Detected");
		event.setDescription(message);
        event.setResource(ResourceAPI.getResource(assetID));
        event.setActualValue(actualValue);
        event.setAdjustedUpperBoundValue(adjustedUpperBound);
        event.setSeverityString("Minor");
        event.setReadingTime(ttime);
        event.setCreatedTime(ttime);
        event.setEnergyDataFieldid(energyDataFieldid);
        event.setUpperAnomalyFieldid(upperAnomalyFieldid);
        event.setmlid(mlContext.getId());
        
       addEvent(mlContext,event);
       return event;
	
	}
	
	private void generateRCAAnomalyEvent(double actualValue,double adjustedUpperBound,long assetID,long fieldID,MLContext mlContext,MLAnomalyEvent parentEvent,long energyDataFieldid,long upperAnomalyFieldid,long mlid,Hashtable<String,Object> assetDetails) throws Exception
	{   
		LOGGER.info("Generating RCAAnomaly Event "+assetID);
		String message = "Anomaly Detected. Actual Consumption :"+actualValue+", Expected Max Consumption :"+adjustedUpperBound;
		
		MLAnomalyEvent event = new MLAnomalyEvent();
		event.setEventMessage("Anomaly Detected");
		event.setDescription(message);
        event.setResource(ResourceAPI.getResource(assetID));
        event.setActualValue(actualValue);
        event.setAdjustedUpperBoundValue(adjustedUpperBound);
        event.setSeverityString("Minor");
        event.setReadingTime(this.getReadingTime(mlContext));
        event.setCreatedTime(this.getReadingTime(mlContext));
        event.setEnergyDataFieldid(energyDataFieldid);
        event.setUpperAnomalyFieldid(upperAnomalyFieldid);
        //event.setParentID(parentid);
        event.setParentEvent(parentEvent);
        event.setType(MLAlarmOccurenceContext.MLAnomalyType.RCA);
        if(assetDetails.containsKey(assetID+"_ratio"))
        {
        	event.setRatio(((Number)assetDetails.get(assetID+"_ratio")).doubleValue());
            event.setUpperAnomaly(((Number) assetDetails.get(assetID+"_upperAnomaly")).doubleValue());
            event.setLowerAnomaly(((Number)assetDetails.get(assetID+"_lowerAnomaly")).doubleValue());
        }
        
        event.setmlid(mlid);
        
        
        addEvent(mlContext,event);
	
	}
	
	private void addEvent(MLContext mlContext,BaseEventContext event) throws Exception
	{
		
        LOGGER.info("mlid: "+((MLAnomalyEvent) event).getmlid()+"::"+mlContext.isHistoric());
        LOGGER.info("map: " + FieldUtil.getAsProperties(event));
        
        if(mlContext.isHistoric())
        {
        	LOGGER.info("Added to eventList");
        	mlContext.addToEventList(event);
        }
        else
        {
        	List<BaseEventContext> eventList = new ArrayList<BaseEventContext>();
            eventList.add(event);
	        FacilioContext context = new FacilioContext();
			context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
			FacilioChain chain = TransactionChainFactory.getV2AddEventChain();
			chain.execute(context);
        }
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
			FacilioChain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
			getAddEventChain.execute(addEventContext);*/
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
	
	private long getReadingTime(MLContext mlContext)
	{
		return mlContext.isHistoric() ? mlContext.getExecutionEndTime() : mlContext.getPredictionTime()*1000;
	}

}
