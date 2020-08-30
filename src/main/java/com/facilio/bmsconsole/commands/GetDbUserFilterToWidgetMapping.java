package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext.WidgetType;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetListViewContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.cards.util.CardLayout;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.util.ReportUtil;

public class GetDbUserFilterToWidgetMapping extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
			Map<Long, List<Long>> widgetUserFiltersMap=new HashMap<>();
			
		DashboardFilterContext dbFilter=(DashboardFilterContext) context.get(FacilioConstants.ContextNames.DASHBOARD_FILTER);
		if(dbFilter!=null) {
			
			List<DashboardUserFilterContext> userFilters=dbFilter.getDashboardUserFilters();
			
			 DashboardContext dashboard=(DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
			 
			 DashboardTabContext dashboardTab=(DashboardTabContext) context.get(FacilioConstants.ContextNames.DASHBOARD_TAB);
			 
			 List<DashboardWidgetContext> widgets=new ArrayList<>();
			 if(dashboard!=null)
			 {
				 	widgets=dashboard.getDashboardWidgets();
			 }
			 else if(dashboardTab!=null)
			 {
				 widgets=dashboardTab.getDashboardWidgets();
			 }
			 else {
				 throw new Exception("NO dashboard or dashboard tab");
			 }
			 
			//for each  filter , check which widgets have the same module as the filter.
			 
				 
				 
				 
				
				 			
				 
				 if(widgets!=null&&userFilters!=null)
				 {
					 
					 for (DashboardWidgetContext widget : widgets) {
					
						 long widgetId=widget.getId();
						 
						 long widgetModuleId=DashboardFilterUtil.getModuleIdFromWidget(widget);

						 
						 
						 
						 for(DashboardUserFilterContext filter:userFilters)
						 {
							 
							 
							 							 
							 FacilioModule  filterModule=filter.getField().getModule();
							 						
							 ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
							 
							 List<FacilioModule> filterChildModules=modBean.getChildModules(filterModule);
							 
							 List<Long> filterChildModuleIds=new ArrayList<Long>();
							 
							 if(filterChildModules!=null)
							 {
								  filterChildModuleIds=filterChildModules.stream().map((FacilioModule module)-> {return module.getModuleId();}).collect(Collectors.toList());
							 }
							 
							 
							 //must check if widget module is either same as filter module or one of its children
							 //Ex , ticketCategory field has module='ticket' but report_chart corresponding to workorders has module='workorder'
							 
							 JSONObject widgetSettings=widget.getWidgetSettings();
							 boolean isFilterExclude=(boolean)widgetSettings.get("excludeDbFilters");
							 Map<Long,FacilioField> widgetFilterFieldMap=filter.getWidgetFieldMap();
							 boolean isFilterFieldMappedToWidgetField=false;
							 
							 if(widgetFilterFieldMap!=null&&widgetFilterFieldMap.containsKey(widgetId))
							 {
								 isFilterFieldMappedToWidgetField=true;
							 }
							 
							 if(!isFilterExclude &&(widgetModuleId==filterModule.getModuleId()||filterChildModuleIds.contains(widgetModuleId)||isFilterFieldMappedToWidgetField))
							 	
							 	{
							 	
								 
								 if(!widgetUserFiltersMap.containsKey(widgetId))
								 {
									 widgetUserFiltersMap.put(widgetId,new ArrayList<Long>());
								 }
								 widgetUserFiltersMap.get(widgetId).add(filter.getId());
								 
							 		
							 	}
						 }
						 
						 
					}
				 }
				 
				 
				 
				 
				 
				 dbFilter.setWidgetUserFiltersMap(widgetUserFiltersMap);
			
			
			 
			
			
		}

		return false;
	}


}
