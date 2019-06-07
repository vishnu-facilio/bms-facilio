package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class DuplicateBaseSpaceForDashboard implements Command {

	public boolean execute(Context context) throws Exception {
		
		String moduleName = (String)context.get(FacilioConstants.ContextNames.MODULE);
		
		List<DashboardContext> dashboards = (List<DashboardContext>)context.get(FacilioConstants.ContextNames.DASHBOARDS);
			
	    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(moduleName);
		
		Long buildingId = getBuildingId(module.getModuleId());
		
		Long siteId = getSiteId(module.getModuleId());
		
	
		DashboardContext buildingDashboard = DashboardUtil.getDashboardWithWidgets(buildingId);
		DashboardContext siteDashboard = DashboardUtil.getDashboardWithWidgets(siteId);
		
		for(DashboardContext dashboard :dashboards) {
			if(dashboard.getId() == -1 && dashboard.getBaseSpaceId() != -1) {
				
				if(dashboard.getLinkName().equals( "buildingdashboard") && buildingId != -1l) {
					dashboard.setDashboardWidgets(buildingDashboard.getDashboardWidgets());
					dashboard.setLinkName(buildingDashboard.getLinkName());
		  
					context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
					context.put(FacilioConstants.ContextNames.BUILDING_ID, dashboard.getBaseSpaceId());
				
					Chain updateDashboardChain = TransactionChainFactory.getUpdateDashboardChain();
					updateDashboardChain.execute(context);
				}
				else if(dashboard.getLinkName().equals("sitedashboard") && siteId != -1l) {
					dashboard.setDashboardWidgets(siteDashboard.getDashboardWidgets());
					dashboard.setLinkName(siteDashboard.getLinkName());
			  
					context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
					context.put(FacilioConstants.ContextNames.BUILDING_ID, dashboard.getBaseSpaceId());
					
					Chain updateDashboardChain = TransactionChainFactory.getUpdateDashboardChain();
					updateDashboardChain.execute(context);
				}
		   }
		}
		return false;
		}
	
	private Long getBuildingId(Long moduleId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("MODULEID = ?",moduleId)
		        .andCustomWhere("LINK_NAME = ?","buildingdashboard")
		        .andCustomWhere("BASE_SPACE_ID IS NULL");
		
		List<Map<String, Object>> props = selectBuilder.get();
		
	    Long id = -1l; 
		if(props != null) {
			System.out.print(props.get(0).get("id"));
			id =(Long) props.get(0).get("id");
		}
		return id;
		
	}
	
private Long getSiteId( Long moduleId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDashboardFields())
				.table(ModuleFactory.getDashboardModule().getTableName())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("MODULEID = ?",moduleId)
		        .andCustomWhere("LINK_NAME = ?","sitedashboard")
		        .andCustomWhere("BASE_SPACE_ID IS NULL");
		
		List<Map<String, Object>> props = selectBuilder.get();
		
	    Long id = -1l; 
		if(props != null) {
			System.out.print(props.get(0).get("id"));
			id =(Long) props.get(0).get("id");
		}
		return id;
		
	}
	
}
