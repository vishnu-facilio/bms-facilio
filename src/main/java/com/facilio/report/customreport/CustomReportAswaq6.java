package com.facilio.report.customreport;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.time.DateTimeUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class CustomReportAswaq6 implements CustomReport {

	@Override
	public JSONArray getData(ReportContext report, FacilioModule module, JSONArray dateFilter,
			JSONObject userFilterValues, long baseLineId, long criteriaId) throws Exception {


		BeanFactory.lookup("ModuleBean");
		
		List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
		
		JSONArray ticketData = new JSONArray();

		for(TicketCategoryContext category:categories) {
			
			List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
			
			if(workorders.isEmpty()) {
				continue;
			}
			
			for(BuildingContext building : SpaceAPI.getAllBuildings()) {
				
				int inTime = 0,overdue = 0;
				for(WorkOrderContext workorder:workorders) {
					
					if(workorder.getResource().getId() != building.getId()) {
						continue;
					}
					if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
						continue;
					}
					if(workorder.getStatus() != null && workorder.getStatus().getId() > 0) {
						FacilioStatus status = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getId(), workorder.getStatus().getId());
						workorder.setStatus(status);
					}
					if(workorder.getStatus() != null && workorder.getStatus().getType() != null && workorder.getStatus().getType().equals(StatusType.CLOSED)) {
						
						if(workorder.getEstimatedEnd() != -1 && workorder.getActualWorkEnd() != -1) {
							if(workorder.getEstimatedEnd() < workorder.getActualWorkEnd()) {
								overdue++;
							}
							else {
								inTime++;
							}
						}
					}
					else {
						if(workorder.getEstimatedEnd() != -1) {
							if(workorder.getEstimatedEnd() < DateTimeUtil.getCurrenTime()) {
								overdue++;
							}
							else {
								inTime++;
							}
						}
					}
				}
				
				JSONObject buildingres = new JSONObject();
				
				JSONArray resArray = new JSONArray();
				
				JSONObject res = new JSONObject();
				res.put("label", "On Time");
				res.put("value", inTime);
				resArray.add(res);
				
				res = new JSONObject();
				res.put("label", "Overdue");
				res.put("value", overdue);
				resArray.add(res);
				
				buildingres.put("label", building.getName());
				buildingres.put("value", resArray);
				ticketData.add(buildingres);
			}
			return ticketData;
		}
		return ticketData;
	
	}

}
