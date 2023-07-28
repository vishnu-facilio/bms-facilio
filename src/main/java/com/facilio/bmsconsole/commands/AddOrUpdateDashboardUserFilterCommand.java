package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsoleV3.actions.dashboard.V3DashboardAPIHandler;
import com.facilio.bmsconsoleV3.context.dashboard.WidgetDashboardFilterContext;
import com.facilio.chain.FacilioChain;
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
						WidgetDashboardFilterContext filter_widget = new WidgetDashboardFilterContext();
						Long widget_id = convertIntoDashboardWidget(filter_widget, dashboardUserFilterRel, dashboardFilterContext);
						if(widget_id != null && widget_id > 0) {
							dashboardUserFilterRel.setWidget_id(widget_id);
							dashboardUserFilterRel.setId(DashboardFilterUtil.insertDashboardUserFilterRel(dashboardUserFilterRel));
						}else{
							dashboardUserFilterRel.setId(DashboardFilterUtil.insertDashboardUserFilterRel(dashboardUserFilterRel));
						}
					}
					dashboardUserFilterRelIds.add(dashboardUserFilterRel.getId());
				}
																
			}
			
			//old user filters no longer present
		
			List<Long> toRemove=new ArrayList<>(existingFilterIds);
			toRemove.removeAll(dashboardUserFilterRelIds);
			if(!toRemove.isEmpty())
			{
				List<Long> deletedWidget_ids=new ArrayList<>();
				for(DashboardUserFilterContext user_filter : existingfilters){
					if(toRemove.contains(user_filter.getId())){
						if(user_filter.getWidget_id() != null) {
							deletedWidget_ids.add(user_filter.getWidget_id());
						}
					}
				}
				DashboardFilterUtil.deleteDashboardUserFilterRel(toRemove, deletedWidget_ids);
			}
			
			
								
		}
		return false;
	}

	public static Long convertIntoDashboardWidget(WidgetDashboardFilterContext filter_widget, DashboardUserFilterContext userFilterContext, DashboardFilterContext dashboard_filter)throws Exception
	{
		filter_widget.setType(DashboardWidgetContext.WidgetType.FILTER.getValue());
		filter_widget.setDashboardId(dashboard_filter.getDashboardId());
		filter_widget.setHeaderText(userFilterContext.getLabel());
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2)){
			filter_widget.setSectionId(userFilterContext.getSectionId());
			filter_widget.setMetaJSONString(userFilterContext.getMetaJSON().toJSONString());
		}
		List<DashboardWidgetContext> widgets = new ArrayList<>();
		widgets.add(filter_widget);
		V3DashboardAPIHandler.checkAndGenerateWidgetLinkName(widgets, dashboard_filter.getDashboardId(), null);
		FacilioChain addWidgetChain = TransactionChainFactory.getAddWidgetChain();
		filter_widget.setDashboardId(filter_widget.getDashboardId());
		Context context = addWidgetChain.getContext();
		context.put(FacilioConstants.ContextNames.WIDGET, filter_widget);
		context.put(FacilioConstants.ContextNames.WIDGET_TYPE, filter_widget.getWidgetType());
		context.put(FacilioConstants.ContextNames.DASHBOARD_ID, filter_widget.getDashboardId());
		addWidgetChain.execute(context);
		return filter_widget.getId();
	}

}
