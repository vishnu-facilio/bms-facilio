package com.facilio.report.customreport;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.modules.FacilioModule;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class CustomReportUtc1 implements CustomReport {

	@Override
	public JSONArray getData(ReportContext report, FacilioModule module, JSONArray dateFilter, JSONObject userFilterValues,
			long baseLineId, long criteriaId) throws Exception {
		
	 	// 7P 
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
		
		JSONArray ticketData = new JSONArray();

		for(TicketCategoryContext category:categories) {
			
			if(!(category.getName().equals("Auditing"))) {
				continue;
			}
			
			List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
			
			if(workorders.isEmpty()) {
				continue;
			}
			
			JSONObject buildingres = new JSONObject();
			for(BuildingContext building : SpaceAPI.getAllBuildings()) {
				
				int buildingScore = 0;
				for(WorkOrderContext workorder:workorders) {
					
//					if( !(workorder.getSubject().contains("Daily Maintenance") || workorder.getSubject().contains("Fortnightly Maintenance") || workorder.getSubject().contains(" Monthly Maintenance"))) {
//						continue;
//					}
					
					if(workorder.getResource().getId() != building.getId()) {
						continue;
					}
					if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
						continue;
					}
					
					Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
					FacilioContext context = new FacilioContext();
					
					context.put(FacilioConstants.ContextNames.ID, workorder.getId());
					context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
					context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
					context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
					context.put("isAsMap", true);
					chain.execute(context);
					
					List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
					
					for(Map<String, Object> task : taskMap) {
						
						if(task.get("inputValue") != null) {
							
							String stringValue = task.get("inputValue").toString();
							
							Integer value = 0;
							if("Met".equals(stringValue) ) {
								value = 5;
							}
							else if ("Not Met".equals(stringValue)) {
								value = 0;
							}
//							else {
//								value = Integer.parseInt(stringValue);
//							}
							
							buildingScore = buildingScore +value;
						}
					}
				}
				
				buildingres = new JSONObject();
				
				buildingres.put("label", building.getName());
				buildingres.put("value", buildingScore);
				
				ticketData.add(buildingres);
			}
		}
		return ticketData;
	
	}

}
