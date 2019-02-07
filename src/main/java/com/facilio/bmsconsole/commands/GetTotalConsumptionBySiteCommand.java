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

public class GetTotalConsumptionBySiteCommand implements Command{
	
	private static final Logger LOGGER = Logger.getLogger(GetTotalConsumptionBySiteCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		LOGGER.log(Level.SEVERE, "startTime -- "+System.currentTimeMillis());
			
		long startTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME);
		
		//String moduleName = (String) context.get(FacilioCosnstants.ContextNames.MODULE_NAME);
		//String fieldName = (String) context.get(FacilioConstants.ContextNames.MODULE_FIELD_NAME);
	    
		Map<String,Object> totalEnergyConsumptionBySites = ConsumptionAPI.getTotalConsumptionBySites(startTime, endTime, "energydata", "totalEnergyConsumptionDelta");
		Map<String,Object> totalWaterConsumptionBySites = ConsumptionAPI.getTotalConsumptionBySites(startTime, endTime, "waterreading", "waterConsumptionDelta");
		
		Map<String,Object> finalResp = new HashMap<String, Object>();
        finalResp.put("energyUnit",totalEnergyConsumptionBySites.get("unit"));
        finalResp.put("waterUnit",totalWaterConsumptionBySites.get("unit"));
        Map<Long,Object> energyData = (Map<Long, Object>) totalEnergyConsumptionBySites.get("totalConsumptionData");
        Map<Long,Object> waterData = (Map<Long, Object>) totalWaterConsumptionBySites.get("totalConsumptionData");
        
        Iterator<Map.Entry<Long, Object>> itr = energyData.entrySet().iterator(); 
    	Map<Long, Map<String, Object>> consumptionData = new HashMap<Long, Map<String,Object>>();
	
        
        while(itr.hasNext()) 
        { 
             Map.Entry<Long, Object> entry = itr.next();
             Map<String,Object> siteData = new HashMap<String, Object>();
             Map<String,Object> siteEnergyData = (Map<String, Object>) entry.getValue();
             
             siteData.put("siteId", entry.getKey());
             siteData.put("siteName", siteEnergyData.get("name"));
             siteData.put("energyConsumption", siteEnergyData.get("total_consumption"));
             Map<String,Object> siteWaterData = (Map<String,Object>)waterData.remove(entry.getKey());
             if(siteWaterData!=null)
             {
                 siteData.put("waterConsumption", siteWaterData.get("total_consumption"));
             }
             else
             {
                 siteData.put("waterConsumption",-1);
             }
             
             consumptionData.put(entry.getKey(), siteData);
             
        } 
    
        Iterator<Map.Entry<Long, Object>> itrWater = waterData.entrySet().iterator(); 
    
        
        while(itrWater.hasNext()) 
        { 
             Map.Entry<Long, Object> entry = itrWater.next();
             Map<String,Object> siteData = new HashMap<String, Object>();
             Map<String,Object> siteWaterData = (Map<String, Object>) entry.getValue();
             
             siteData.put("siteId", entry.getKey());
             siteData.put("siteName", siteWaterData.get("name"));
             siteData.put("energyConsumption", -1);
           
             siteData.put("waterConsumption", siteWaterData.get("total_consumption"));
             
             consumptionData.put(entry.getKey(), siteData);
                       
             
             
        } 
    
        
    	List<Map<String,Object>> listResp = new ArrayList<Map<String,Object>>(consumptionData.values());
    	finalResp.put("total_consumption",listResp);
    	
		context.put(FacilioConstants.ContextNames.TOTAL_CONSUMPTION, finalResp);
		
		
		return false;
	}
	

}
