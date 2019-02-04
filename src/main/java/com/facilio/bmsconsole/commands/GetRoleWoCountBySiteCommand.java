package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMJobsContext;
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
	
		Map<Long, Object> woTotalCount = WorkOrderAPI.getTotalWoCountBySite(startTime, endTime);
		Map<Long, Map<String,Object>> woClosedCount = WorkOrderAPI.getTotalClosedWoCountBySite(startTime, endTime);
		Map<Long, Object> teamCount = WorkOrderAPI.getTeamsCountBySite();
		
		
		List<Map<String,Object>> woClosedCountList = new ArrayList<Map<String,Object>>(woClosedCount.values());
		List<Map<String,Object>> resp = new ArrayList<Map<String,Object>>();
		for(int i=0;i<woClosedCountList.size();i++)
		{
			Map<String,Object> siteCount = new HashMap<String, Object>();
			
			Map<String,Object> eachSite = woClosedCountList.get(i);
			Long siteId = (Long)eachSite.get("siteId");
			Long closedCount = (Long)eachSite.get("count");
			Long totalCount = (Long)woTotalCount.get(siteId);
			Map<String,Object> teamCountBySite = (Map<String, Object>) teamCount.get(siteId);
			if(teamCountBySite!=null) {
			Long technicianCount = (Long)teamCountBySite.get("Technician");
			Long tlCount = (Long)teamCountBySite.get("TL");
			Long executiveCount = (Long)teamCountBySite.get("Executive");
			siteCount.put("technicianCount",technicianCount);
			siteCount.put("tlCount",tlCount);
			siteCount.put("executiveCount",executiveCount);
		
			}
			
			siteCount.put("siteId",siteId);
			siteCount.put("closedCount",closedCount);
			siteCount.put("totalCount",totalCount);
			
			resp.add(siteCount);
					
		}
		
		context.put(FacilioConstants.ContextNames.SITE_ROLE_WO_COUNT, resp);

		return false;
	}

	private void sortPMs(List<PMJobsContext> pmJobs) {
		pmJobs.sort(Comparator.comparing(PMJobsContext::getNextExecutionTime, (s1, s2) -> {
			return Long.compare(s1, s2);
		}));
	}

}
