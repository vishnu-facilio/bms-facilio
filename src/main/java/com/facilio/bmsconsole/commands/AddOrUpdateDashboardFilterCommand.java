package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.util.DashboardUtil;


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
	
	public static Long addDashboardFilter(DashboardFilterContext dashboardFilterContext) throws Exception{

		addFilterLinkName(dashboardFilterContext);
		GenericInsertRecordBuilder builder=new GenericInsertRecordBuilder()
				.table(ModuleFactory.getDashboardFilterModule().getTableName())
				.fields(FieldFactory.getDashboardFilterFields());
		
		Map<String,Object> props = FieldUtil.getAsProperties(dashboardFilterContext);
		if(!props.isEmpty()){
			props.put("isTimelineFilterEnabled",dashboardFilterContext.getIsTimelineFilterEnabled());
			props.put("hideFilterInsideWidgets",dashboardFilterContext.isHideFilterInsideWidgets());
		}
		return builder.insert(props);
		
	}
	public static void updateDashboardFilter(DashboardFilterContext dashboardFilterContext) throws Exception
	{
		FacilioModule module=ModuleFactory.getDashboardFilterModule();
		GenericUpdateRecordBuilder builder=new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getDashboardFilterFields())
				.andCondition(CriteriaAPI.getIdCondition(dashboardFilterContext.getId(), module));
		builder.update(FieldUtil.getAsProperties(dashboardFilterContext,true));
			
	}
	public static void addFilterLinkName(DashboardFilterContext filterContext) throws Exception {
		Map<String, FacilioField> dashboardFilterFields = FieldFactory.getAsMap(FieldFactory.getDashboardFilterFields());
		FacilioField filterLinkName = dashboardFilterFields.get(FacilioConstants.ContextNames.LINK_NAME);
		FacilioModule module = ModuleFactory.getDashboardFilterModule();
		List<String> linkNames = DashboardUtil.getExistingLinkNames(module.getTableName(),filterLinkName);
		int i = 1;
		while(i<1000){
			String linkName = "filter";
			linkName = String.valueOf(new StringBuilder(linkName).append(i));
			if(linkNames.contains(linkName)){
				i++;
			}
			else{
				filterContext.setLinkName(linkName);
				break;
			}
		}
	}

}
