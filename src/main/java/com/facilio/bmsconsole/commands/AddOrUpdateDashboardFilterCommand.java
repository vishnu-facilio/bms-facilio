package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddOrUpdateDashboardFilterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		DashboardFilterContext dashboardFilterContext=context.get(FacilioConstants.ContextNames.DASHBOARD_FILTER)!=null?(DashboardFilterContext)context.get(FacilioConstants.ContextNames.DASHBOARD_FILTER):null;
		
		
		
		//Only when timeline enabled or atleast one dashboard user filter is added the filter is saved 
		if(dashboardFilterContext!=null)
		{
		//no need as dashboardFilterContext needed dashboardId or dashboardTabId to be filled
//		long dashboardId=context.get(FacilioConstants.ContextNames.DASHBOARD)!=null?((DashboardContext)context.get(FacilioConstants.ContextNames.DASHBOARD)).getId():-1;
//
//		long dashboardTabId=context.get(FacilioConstants.ContextNames.DASHBOARD_TAB)!=null?((DashboardTabContext)context.get(FacilioConstants.ContextNames.DASHBOARD_TAB)).getId():-1;
//
//			
//		dashboardFilterContext.setDashboardId(dashboardId);
//		dashboardFilterContext.setDashboardTabId(dashboardTabId);
		Long dashboardFilterId=dashboardFilterContext.getId();
		
		
	
		
			if(dashboardFilterId!=null&&dashboardFilterId>0)
			{
				this.updateDashboardFilter(dashboardFilterContext);
			}
			else {
				dashboardFilterContext.setId(this.addDashboardFilter(dashboardFilterContext));
			}
			
		}

		
		return false;
	}
	
	public Long addDashboardFilter(DashboardFilterContext dashboardFilterContext) throws Exception{
		
		
		GenericInsertRecordBuilder builder=new GenericInsertRecordBuilder()
				.table(ModuleFactory.getDashboardFilterModule().getTableName())
				.fields(FieldFactory.getDashboardFilterFields());
		
		
		return builder.insert(FieldUtil.getAsProperties(dashboardFilterContext)); 
		
	}
	public void updateDashboardFilter(DashboardFilterContext dashboardFilterContext) throws Exception
	{
		FacilioModule module=ModuleFactory.getDashboardFilterModule();
		GenericUpdateRecordBuilder builder=new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getDashboardFilterFields())
				.andCondition(CriteriaAPI.getIdCondition(dashboardFilterContext.getId(), module));
		builder.update(FieldUtil.getAsProperties(dashboardFilterContext));
			
	}

}
