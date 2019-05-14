package com.facilio.leed.commands;

import com.facilio.time.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.ArcContext;
import com.facilio.leed.context.ConsumptionInfoContext;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AddConsumptionForLeed implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long leedId = (long) context.get(FacilioConstants.ContextNames.LEEDID);
		long deviceId = (long) context.get(FacilioConstants.ContextNames.DEVICEID);
		long meterId = LeedAPI.getMeterId(deviceId);
		List<ConsumptionInfoContext> consumptionInfoList = (List<ConsumptionInfoContext>)context.get(FacilioConstants.ContextNames.COMSUMPTIONDATA_LIST);
		Iterator data = consumptionInfoList.iterator();
		List<HashMap> dataMapList = new ArrayList();
		while(data.hasNext())
		{
			HashMap dataMap = new HashMap();
			ConsumptionInfoContext energyData = (ConsumptionInfoContext)data.next();
			long endTime = energyData.getTtime();
			HashMap<String, Object> timeData = DateTimeUtil.getTimeData(endTime); 	
			double consumption = (double) energyData.getReading("totalEnergyConsumptionDelta");
			long startTime  = energyData.getStartTime();
			JSONObject consumptionJSON = new JSONObject();
			String stdate_str = DateTimeUtil.getDateTime(startTime).toString();
			String endate_Str = DateTimeUtil.getDateTime(endTime).toString();
			consumptionJSON.put("start_date", stdate_str);
			consumptionJSON.put("end_date", endate_Str);
			consumptionJSON.put("reading", consumption);
			ArcContext arccontext = LeedAPI.getArcContext();
			LeedIntegrator integ = new LeedIntegrator(arccontext);
			JSONObject response = integ.createConsumption(leedId, meterId, consumptionJSON);
			System.out.println("$$$$$$$$$$$ : "+response);
			long consumptionId = -1;
			if(response != null)
			{
			 consumptionId = (long)((JSONObject)response.get("message")).get("id");
			 
			}
			dataMap.put("deviceId", deviceId);
			dataMap.put("endTime", endTime);
			dataMap.put("consumption", consumption);
			dataMap.put("timeData", timeData);
			dataMap.put("consumptionId", consumptionId);
			dataMap.put("startTime", startTime);
			dataMapList.add(dataMap);
			//LeedAPI.addEnergyData(deviceId, endTime, consumption,timeData);
		}
		LeedAPI.addConsumptionData(dataMapList);
		
		return false;
	}
	
}
