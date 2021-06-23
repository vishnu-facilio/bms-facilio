package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;

public class AddOrUpdateDashboardUserFilterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		DashboardFilterContext dashboardFilterContext=context.get(FacilioConstants.ContextNames.DASHBOARD_FILTER)!=null?(DashboardFilterContext)context.get(FacilioConstants.ContextNames.DASHBOARD_FILTER):null;
		
		if(dashboardFilterContext!=null)
		{			
			//add or update userfilters , Compare old UserFilters with new ones and delete the diff
		
			List<DashboardUserFilterContext> existingfilters=DashboardFilterUtil.getDashboardUserFilters(dashboardFilterContext.getId());
			
			List<Long> existingFilterIds=new ArrayList<>();
			if(existingfilters!=null)
			{
				existingFilterIds= existingfilters.stream().map(existingFilter->existingFilter.getId()).collect(Collectors.toList());	
			}
		
			
			
			List<Long> dashboardUserFilterRelIds=new ArrayList<>();
			
			List<DashboardUserFilterContext> currentDashboardUserFilters=dashboardFilterContext.getDashboardUserFilters();
			if(currentDashboardUserFilters!=null)
			{
				
				for(DashboardUserFilterContext dashboardUserFilterRel:currentDashboardUserFilters)
				{
					dashboardUserFilterRel.setDashboardFilterId(dashboardFilterContext.getId());
					if(dashboardUserFilterRel.getCriteria()!=null)
					{
						long criteriaId=CriteriaAPI.addCriteria(dashboardUserFilterRel.getCriteria());
						dashboardUserFilterRel.setCriteriaId(criteriaId);
					}
					
					if(dashboardUserFilterRel.getId()>0)
					{
						DashboardFilterUtil.updateDashboardUserFilerRel(dashboardUserFilterRel);
					}
					else {
						dashboardUserFilterRel.setId(DashboardFilterUtil.insertDashboardUserFilterRel(dashboardUserFilterRel));
						
					}
					dashboardUserFilterRelIds.add(dashboardUserFilterRel.getId());
					
				}
																
			}
			
			//old user filters no longer present
		
			List<Long> toRemove=new ArrayList<>(existingFilterIds);
			toRemove.removeAll(dashboardUserFilterRelIds);
			if(!toRemove.isEmpty())
			{
				DashboardFilterUtil.deleteDashboardUserFilterRel(toRemove);
			}
			
			
								
		}
		return false;
	}

}
