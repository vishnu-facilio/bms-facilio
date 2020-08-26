package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext.WidgetType;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.util.ReportUtil;

public class FetchModulesInDashboardCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long dashboardId=(Long)context.get(FacilioConstants.ContextNames.DASHBOARD_ID);
		Long dashboardTabId=(Long)context.get(FacilioConstants.ContextNames.DASHBOARD_TAB_ID);
		
		 List<DashboardWidgetContext> dashboardWidgets=DashboardUtil.getDashboardWidgetsFormDashboardIdOrTabId(dashboardId, dashboardTabId);
		 
		 List<String> moduleNames=new ArrayList<>();
		 
		 Set<Long> moduleIds=new HashSet<>();
		
		 for(DashboardWidgetContext widget : dashboardWidgets)
		 {
			 
				 	long widgetModuleId=DashboardFilterUtil.getModuleIdFromWidget(widget);
				 	if(widgetModuleId!=-1)
				 	{
					 moduleIds.add(widgetModuleId);
					 
				 	}
			
			
		 }
		 
		 ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		 	for(Long moduleId:moduleIds)
		 	{
		 		moduleNames.add(modBean.getModule(moduleId).getName());
		 		
		 	}
		 
			context.put(FacilioConstants.ContextNames.MODULE_LIST, moduleNames);			

		
		
		return false;
	
	}
	

}
