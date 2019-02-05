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

import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;

public class GetRoleWoCountBySiteCommand implements Command{
	
	private static final Logger LOGGER = Logger.getLogger(GetRoleWoCountBySiteCommand.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		LOGGER.log(Level.SEVERE, "startTime -- " + System.currentTimeMillis());
		long startTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME);
	
		List<Map<String, Object>> woClosedCount = WorkOrderAPI.getTotalClosedWoCountBySite(startTime, endTime);
		Map<Long, Object> woTotalCount = WorkOrderAPI.getTotalWoCountBySite(startTime, endTime);
		Map<Long, Object> teamCount = WorkOrderAPI.getTeamsCountBySite();
		Map<Long, Object> screenCountBySite = WorkOrderAPI.getScreensCountBySite();
		
		Map<Long, Object> siteNameMap = WorkOrderAPI.getLookupFieldPrimary("site");
		
		List<Map<String,Object>> resp = new ArrayList<Map<String,Object>>();
		
		
		for(int i=0;i<woClosedCount.size();i++)
		{
			Map<String,Object> siteInfo = woClosedCount.get(i);
			Map<String,Object> siteCount = new HashMap<String, Object>();
		    Long siteId = (Long)siteInfo.get("siteId");
		    String siteName = (String) siteNameMap.remove(siteId);
		    Long closedCount =(Long) siteInfo.get("count");
		    Long screenCount = null;
            if(screenCountBySite!=null) {
              screenCount = screenCountBySite.get(siteId)!=null?(Long)screenCountBySite.get(siteId):0;
              siteCount.put("screenCount",screenCount);
  			
            }
            Object totalCount = woTotalCount.remove(siteId);
            Long totalCountVal = totalCount!=null?(Long)totalCount:0;
            Map<String,Object> teamCountBySite = (Map<String, Object>) teamCount.get(siteId);
			if(teamCountBySite!=null) {
			Long technicianCount = teamCountBySite.get("Technician")!=null?(Long)teamCountBySite.get("Technician"):0;
			Long tlCount = teamCountBySite.get("TL")!=null?(Long)teamCountBySite.get("TL"):0;
			Long executiveCount = teamCountBySite.get("Executive")!=null?(Long)teamCountBySite.get("Executive"):0;
			siteCount.put("technicianCount",technicianCount);
			siteCount.put("tlCount",tlCount);
			siteCount.put("executiveCount",executiveCount);
		
			}
			else
			{
				siteCount.put("technicianCount",0);
				siteCount.put("tlCount",0);
				siteCount.put("executiveCount",0);
			
			}
			siteCount.put("siteId",siteId);
			siteCount.put("siteName",siteName);
			siteCount.put("closedCount",closedCount);
			siteCount.put("totalCount",totalCountVal);
			
			resp.add(siteCount);
			
		}
		
		
		   Iterator<Map.Entry<Long, Object>> itr =woTotalCount.entrySet().iterator(); 
	        
	     while(itr.hasNext()) 
	      { 
	        Map.Entry<Long, Object> entry = itr.next(); 
	       
			Long totalCountVal = (Long)entry.getValue();
			Long siteId = (Long)entry.getKey();
			Map<String,Object> siteCount = new HashMap<String, Object>();
		    String siteName = (String) siteNameMap.remove(siteId);
		    Long screenCount = null;
            if(screenCountBySite!=null) {
              screenCount = screenCountBySite.get(siteId)!=null?(Long)screenCountBySite.get(siteId):0;
              siteCount.put("screenCount",screenCount);
  			
            }
            Object totalCount = woTotalCount.get(siteId);
            siteCount.put("totalCount",(Long)totalCount);
            Map<String,Object> teamCountBySite = (Map<String, Object>) teamCount.get(siteId);
			if(teamCountBySite!=null) {
			Long technicianCount = teamCountBySite.get("Technician")!=null?(Long)teamCountBySite.get("Technician"):0;
			Long tlCount = teamCountBySite.get("TL")!=null?(Long)teamCountBySite.get("TL"):0;
			Long executiveCount = teamCountBySite.get("Executive")!=null?(Long)teamCountBySite.get("Executive"):0;
			siteCount.put("technicianCount",technicianCount);
			siteCount.put("tlCount",tlCount);
			siteCount.put("executiveCount",executiveCount);
		
			}
			else
			{
				siteCount.put("technicianCount",0);
				siteCount.put("tlCount",0);
				siteCount.put("executiveCount",0);
			
			}
			siteCount.put("siteId",siteId);
			siteCount.put("siteName",siteName);
			siteCount.put("closedCount",0);
			
			
			resp.add(siteCount);
			
		}
		
	    Iterator<Map.Entry<Long, Object>> remainingSitesitr = siteNameMap.entrySet().iterator(); 
        
        while(remainingSitesitr.hasNext()) 
        { 
             Map.Entry<Long, Object> entry = remainingSitesitr.next(); 
             Map<String,Object> siteCount = new HashMap<String, Object>();
             Long siteId = entry.getKey();
             String siteName = entry.getValue().toString();
             Long screenCount = null;
             if(screenCountBySite!=null) {
               screenCount = screenCountBySite.get(siteId)!=null?(Long)screenCountBySite.get(siteId):0;
               siteCount.put("screenCount",screenCount);
   			
             }
            
 			Map<String,Object> teamCountBySite = (Map<String, Object>) teamCount.get(siteId);
			if(teamCountBySite!=null) {
			Long technicianCount = teamCountBySite.get("Technician")!=null?(Long)teamCountBySite.get("Technician"):0;
			Long tlCount = teamCountBySite.get("TL")!=null?(Long)teamCountBySite.get("TL"):0;
			Long executiveCount = teamCountBySite.get("Executive")!=null?(Long)teamCountBySite.get("Executive"):0;
			siteCount.put("technicianCount",technicianCount);
			siteCount.put("tlCount",tlCount);
			siteCount.put("executiveCount",executiveCount);
		
			}
			else
			{
				siteCount.put("technicianCount",0);
				siteCount.put("tlCount",0);
				siteCount.put("executiveCount",0);
			
			}
			
			siteCount.put("siteId",siteId);
			siteCount.put("siteName",siteName);
			siteCount.put("closedCount",0);
			siteCount.put("totalCount",0);
			
			resp.add(siteCount);
		
       
        } 

		
		
			context.put(FacilioConstants.ContextNames.SITE_ROLE_WO_COUNT, resp);

		return false;
	}

	

}
