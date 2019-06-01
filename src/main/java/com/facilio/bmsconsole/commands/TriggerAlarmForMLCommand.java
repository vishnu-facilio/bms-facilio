package com.facilio.bmsconsole.commands;


import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.MLVariableContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;

public class TriggerAlarmForMLCommand implements Command {
	
	private static final Logger LOGGER = Logger.getLogger(TriggerAlarmForMLCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception 
	{
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		if(mlContext.getModelPath().equals("checkGam1"))
		{
			JSONObject response = new JSONObject(mlContext.getResult());
			long meterID = response.getLong("assetid");
			AssetContext asset = AssetsAPI.getAssetInfo(meterID);
			String assetName = asset.getName();
			
			MLVariableContext mlVariableContext = mlContext.getMLVariableContext(meterID);
			
			JSONArray responseData = (JSONArray)response.get("data");
			for(int i=0;i<responseData.length();i++)
			{
				JSONObject data = (JSONObject)responseData.get(i);

				String message = "Anomaly Detected. Actual Consumption :"+data.getLong("actualValue")+", Expected Max Consumption :"+data.getLong("AdjustedUpperBound");
				JSONObject obj = new JSONObject();
				obj.put("message", message);
				obj.put("source", assetName);
				obj.put("condition", "Anomaly Detected in Energy Consumption");
				obj.put("resourceId", meterID);
				obj.put("severity", "Minor");
				obj.put("timestamp", data.get("ttime"));
				obj.put("consumption", data.get("actualValue"));

				obj.put("sourceType", SourceType.ANOMALY_ALARM.getIntVal());
				obj.put("readingFieldId", mlVariableContext.getFieldID());
				obj.put("startTime", data.get("ttime"));
				obj.put("readingMessage", message);
				FacilioContext addEventContext = new FacilioContext();
				addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, obj);
				Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
				getAddEventChain.execute(addEventContext);
			}
			
		}
		return false;
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
