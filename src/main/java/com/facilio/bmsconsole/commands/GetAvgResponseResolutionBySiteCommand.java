package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetAvgResponseResolutionBySiteCommand implements Command{
	private static final Logger LOGGER = Logger.getLogger(GetAvgResponseResolutionBySiteCommand.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		LOGGER.log(Level.SEVERE, "startTime -- " + System.currentTimeMillis());
		long startTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME);
		
		List<Map<String,Object>> workOrderStatusCountList = WorkOrderAPI.getWorkOrderStatusPercentageForWorkflow(null, startTime, endTime);
		Map<Long,Object> technicianCountBySite = WorkOrderAPI.getTechnicianCountBySite();
		Map<Long,Object> avgResponseTimeBySite = WorkOrderAPI.getAvgResponseTimeBySite(startTime, endTime,false);
		List<Map<String,Object>> avgResolutionTimeBySiteList = WorkOrderAPI.getAvgCompletionTimeBySite(startTime, endTime,false);
		Map<Long,Object> avgResponseTimeBySiteTillLastMonth = WorkOrderAPI.getAvgResponseTimeBySite(startTime, endTime,true);
		List<Map<String,Object>> avgResolutionTimeBySiteTillLastMonthList = WorkOrderAPI.getAvgCompletionTimeBySite(startTime, endTime,true);
		Map<Long, Object> siteNameArray = WorkOrderAPI.getLookupFieldPrimary("site");
		
		Map<Long,Object> avgResolutionTimeBySite = new HashMap<Long, Object>();
		
		
		for(int i=0;i<avgResolutionTimeBySiteList.size();i++)
		{
			Map<String, Object> siteTechMap = avgResolutionTimeBySiteList.get(i);
			avgResolutionTimeBySite.put((Long)siteTechMap.get("siteId"),siteTechMap.get("avg_resolution_time"));
			
		}
		
		Map<Long,Object> avgResolutionTimeBySiteTillLastMonth = new HashMap<Long, Object>();
		
		
		for(int i=0;i<avgResolutionTimeBySiteTillLastMonthList.size();i++)
		{
			Map<String, Object> siteTechMap = avgResolutionTimeBySiteTillLastMonthList.get(i);
			avgResolutionTimeBySiteTillLastMonth.put((Long)siteTechMap.get("siteId"),siteTechMap.get("avg_resolution_time"));
			
		}
		
		
		
		List<Map<String,Object>> finalResp = new ArrayList<Map<String,Object>>();
		
	    
		for (int i=0;i<workOrderStatusCountList.size();i++) 
	    	
        { 
			Map<String, Object> map = workOrderStatusCountList.get(i);
			Long siteId = (Long)map.get("siteId");
	     
        	 Map<String,Object> siteInfo = new HashMap<String, Object>();
             siteInfo.put("siteId",siteId);
             siteInfo.put("onTime",map.get("onTime"));
             siteInfo.put("siteName",siteNameArray.get(siteId));
             siteInfo.put("overDue",map.get("overdue"));
             siteInfo.put("open",map.get("open"));
             siteInfo.put("technicianCount",technicianCountBySite.get(siteId)!=null?technicianCountBySite.get(siteId):0);
             
             Double avgResolutionTime=0.0,avgResponseTime=0.0,avgResolutionTimeTillLastMonth=0.0,avgResponseTimeTillLastMonth=0.0;
             if(avgResolutionTimeBySite.get(siteId)!=null) {
         	   avgResolutionTime = ((BigDecimal)avgResolutionTimeBySite.get(siteId)).doubleValue();
         	   avgResolutionTime = Math.round(avgResolutionTime*100.0)/100.0;}
             
             if(avgResponseTimeBySite.get(siteId)!=null) {
            	 avgResponseTime = ((BigDecimal)avgResponseTimeBySite.get(siteId)).doubleValue();
            	 avgResponseTime = Math.round(avgResponseTime*100.0)/100.0;
             }
             
             if(avgResponseTimeBySiteTillLastMonth.get(siteId)!=null) {
            	 avgResponseTimeTillLastMonth = ((BigDecimal)avgResponseTimeBySiteTillLastMonth.get(siteId)).doubleValue();
            	 avgResponseTimeTillLastMonth = Math.round(avgResponseTimeTillLastMonth*100.0)/100.0;
             }
             
             if(avgResolutionTimeBySiteTillLastMonth.get(siteId)!=null) {
            	 avgResolutionTimeTillLastMonth = ((BigDecimal)avgResolutionTimeBySiteTillLastMonth.get(siteId)).doubleValue();
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
