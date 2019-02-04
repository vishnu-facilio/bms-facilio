package com.facilio.bmsconsole.commands;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;

public class GetAvgResponseResolutionBySiteCommand implements Command{
	private static final Logger LOGGER = Logger.getLogger(GetAvgResponseResolutionBySiteCommand.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		LOGGER.log(Level.SEVERE, "startTime -- " + System.currentTimeMillis());
		long startTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME);
		
		Map<Long, Map<String, Object>> workOrderStatusCount = WorkOrderAPI.getWorkOrderStatusPercentage(startTime, endTime);
		Map<Long,Object> technicianCountBySite = WorkOrderAPI.getTechnicianCountBySite();
		Map<Long,Object> avgResponseTimeBySite = WorkOrderAPI.getAvgResponseTimeBySite(startTime, endTime,false);
		Map<Long,Object> avgResolutionTimeBySite = WorkOrderAPI.getAvgCompletionTimeBySite(startTime, endTime,false);
		Map<Long,Object> avgResponseTimeBySiteTillLastMonth = WorkOrderAPI.getAvgResponseTimeBySite(startTime, endTime,true);
		Map<Long,Object> avgResolutionTimeBySiteTillLastMonth = WorkOrderAPI.getAvgCompletionTimeBySite(startTime, endTime,true);
		Map<Long, Object> siteNameArray = WorkOrderAPI.getLookupFieldPrimary("site");

		
		List<Map<String,Object>> finalResp = new ArrayList<Map<String,Object>>();
		
		Iterator<Map.Entry<Long, Map<String, Object>>> itr = workOrderStatusCount.entrySet().iterator(); 
        
        while(itr.hasNext()) 
        { 
        	 Map<String,Object> siteInfo = new HashMap<String, Object>();
             Map.Entry<Long, Map<String, Object>> entry = itr.next(); 
             siteInfo.put("siteId",entry.getKey());
             Map<String,Object> statusVal = entry.getValue();
             siteInfo.put("onTime",statusVal.get("onTime"));
             siteInfo.put("siteName",siteNameArray.get(entry.getKey()));
             siteInfo.put("overDue",statusVal.get("overDue"));
             siteInfo.put("open",statusVal.get("open"));
             siteInfo.put("technicianCount",technicianCountBySite.get(entry.getKey())!=null?technicianCountBySite.get(entry.getKey()):0);
             
             Double avgResolutionTime=0.0,avgResponseTime=0.0,avgResolutionTimeTillLastMonth=0.0,avgResponseTimeTillLastMonth=0.0;
             if(avgResolutionTimeBySite.get(entry.getKey())!=null) {
         	   avgResolutionTime = ((BigDecimal)avgResolutionTimeBySite.get(entry.getKey())).doubleValue();
         	   avgResolutionTime = Math.round(avgResolutionTime*100.0)/100.0;}
             
             if(avgResponseTimeBySite.get(entry.getKey())!=null) {
            	 avgResponseTime = ((BigDecimal)avgResponseTimeBySite.get(entry.getKey())).doubleValue();
            	 avgResponseTime = Math.round(avgResponseTime*100.0)/100.0;
             }
             
             if(avgResponseTimeBySiteTillLastMonth.get(entry.getKey())!=null) {
            	 avgResponseTimeTillLastMonth = ((BigDecimal)avgResponseTimeBySiteTillLastMonth.get(entry.getKey())).doubleValue();
            	 avgResponseTimeTillLastMonth = Math.round(avgResponseTimeTillLastMonth*100.0)/100.0;
             }
             
             if(avgResolutionTimeBySiteTillLastMonth.get(entry.getKey())!=null) {
            	 avgResolutionTimeTillLastMonth = ((BigDecimal)avgResolutionTimeBySiteTillLastMonth.get(entry.getKey())).doubleValue();
        	 	avgResolutionTimeTillLastMonth = Math.round(avgResolutionTimeTillLastMonth*100.0)/100.0;
             }
			
         	 siteInfo.put("avgResolutionTime",avgResolutionTime);
             siteInfo.put("avgResponseTime",avgResponseTime);
             siteInfo.put("avgResolutionTimeTillLastMonth",avgResolutionTimeTillLastMonth);
             siteInfo.put("avgResponseTimeTillLastMonth",avgResponseTimeTillLastMonth);
              
             finalResp.add(siteInfo);
             
        } 
		
		
	
		context.put(FacilioConstants.ContextNames.WORKORDER_INFO_BY_SITE,finalResp );
	
		return false;
	}


}
