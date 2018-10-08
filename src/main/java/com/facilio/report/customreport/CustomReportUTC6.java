package com.facilio.report.customreport;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazonaws.services.cognitoidp.model.StatusType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;

public class CustomReportUTC6 implements CustomReport {	//completed vs pending by task

	private static final Logger LOGGER = Logger.getLogger(CustomReportUTC6.class.getName());
	@Override
	public JSONArray getData(ReportContext report, FacilioModule module, JSONArray dateFilter,
			JSONObject userFilterValues, long baseLineId, long criteriaId) throws Exception {
		
		
		JSONArray ticketData = new JSONArray();
		Long buildingId = null;
		
		if(report.getReportSpaceFilterContext() != null && report.getReportSpaceFilterContext().getBuildingId() != null) {
			
			buildingId = report.getReportSpaceFilterContext().getBuildingId();
			
		}
		
		TicketCategoryContext category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getId(), "Auditing");
			
		List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId(),(Long)dateFilter.get(0), (Long)dateFilter.get(1),buildingId);
		
		if(workorders.isEmpty()) {
			return ticketData;
		}
		int completed = 0,pending = 0;
		for(WorkOrderContext workorder:workorders) {
			
			LOGGER.log(Level.INFO, "dateFilter --- "+dateFilter);
			List<Map<String, Object>> taskMap = WorkOrderAPI.getTasks(workorder.getId());
			
			for(Map<String, Object> task : taskMap) {
				
				Long statusId = null;
				LOGGER.log(Level.SEVERE, "task --- "+task);
				if(task.get("status") != null) {
					statusId = (Long) ((Map<String, Object>)task.get("status")).get("id");
				}
				LOGGER.log(Level.SEVERE, "statusId --- "+statusId);
				
				if(statusId != null) {
					TicketStatusContext status = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getId(), statusId);
					
					if(status.getType().equals(com.facilio.bmsconsole.context.TicketStatusContext.StatusType.CLOSED)) {
						completed ++;
					}
					else {
						pending ++;
					}
				}
			}
		}
		
		JSONObject res = new JSONObject();
		res.put("label", "Completed");
		res.put("value", completed);
		ticketData.add(res);
		
		res = new JSONObject();
		res.put("label", "Pending");
		res.put("value", pending);
		ticketData.add(res);
		
		return ticketData;
	}
}