package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ConsumptionAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;

public class GetTotalConsumptionByBuidlingCommand implements Command{
	
	private static final Logger LOGGER = Logger.getLogger(GetTotalConsumptionByBuidlingCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		LOGGER.log(Level.SEVERE, "startTime -- "+System.currentTimeMillis());
			
		long startTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME);
		
		//String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		//String fieldName = (String) context.get(FacilioConstants.ContextNames.MODULE_FIELD_NAME);
	    
		Map<String,Object> totalEnergyConsumptionByBuilding = ConsumptionAPI.getTotalConsumptionByBuildings(startTime, endTime, "energydata", "totalEnergyConsumptionDelta");
		Map<String,Object> totalWaterConsumptionByBuilding = ConsumptionAPI.getTotalConsumptionByBuildings(startTime, endTime, "waterreading", "waterConsumptionDelta");
		
		Map<String,Object> finalResp = new HashMap<String, Object>();
        finalResp.put("energyUnit",totalEnergyConsumptionByBuilding.get("unit"));
        finalResp.put("waterUnit",totalWaterConsumptionByBuilding.get("unit"));
        Map<Long,Object> energyData = (Map<Long, Object>) totalEnergyConsumptionByBuilding.get("totalConsumptionData");
        Map<Long,Object> waterData = (Map<Long, Object>) totalWaterConsumptionByBuilding.get("totalConsumptionData");
        
        Iterator<Map.Entry<Long, Object>> itr = energyData.entrySet().iterator(); 
    	Map<Long, Map<String, Object>> consumptionData = new HashMap<Long, Map<String,Object>>();
	
        
        while(itr.hasNext()) 
        { 
             Map.Entry<Long, Object> entry = itr.next();
             Map<String,Object> buildingData = new HashMap<String, Object>();
             Map<String,Object> buildingEnergyData = (Map<String, Object>) entry.getValue();
             
             buildingData.put("buildingId", entry.getKey());
             buildingData.put("buildingName", buildingEnergyData.get("buildingName"));
             buildingData.put("energyConsumption", buildingEnergyData.get("total_consumption"));
             Map<String,Object> buildingWaterData = (Map<String,Object>)waterData.remove(entry.getKey());
             if(buildingWaterData!=null)
             {
                 buildingData.put("waterConsumption", buildingWaterData.get("total_consumption"));
             }
             else
             {
                 buildingData.put("waterConsumption",-1);
             }
             
             consumptionData.put(entry.getKey(), buildingData);
             
        } 
    
        Iterator<Map.Entry<Long, Object>> itrWater = waterData.entrySet().iterator(); 
    
        
        while(itrWater.hasNext()) 
        { 
             Map.Entry<Long, Object> entry = itrWater.next();
             Map<String,Object> buildingData = new HashMap<String, Object>();
             Map<String,Object> buildingWaterData = (Map<String, Object>) entry.getValue();
             
             buildingData.put("buildingId", entry.getKey());
             buildingData.put("buildingName", buildingWaterData.get("buildingName"));
             buildingData.put("energyConsumption", -1);
           
             buildingData.put("waterConsumption", buildingWaterData.get("total_consumption"));
             
             consumptionData.put(entry.getKey(), buildingData);
                       
             
             
        } 
    
        
    	List<Map<String,Object>> listResp = new ArrayList<Map<String,Object>>(consumptionData.values());
    	finalResp.put("total_consumption",listResp);
    	
		context.put(FacilioConstants.ContextNames.TOTAL_CONSUMPTION, finalResp);
		
		
		return false;
	}
	

}
