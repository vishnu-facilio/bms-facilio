package com.facilio.report.customreport;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.fw.BeanFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomReportUTC4 implements CustomReport {	//Score Trends

	private static final Logger LOGGER = Logger.getLogger(CustomReportUTC2.class.getName());
	@Override
	public JSONArray getData(ReportContext report, FacilioModule module, JSONArray dateFilter,
			JSONObject userFilterValues, long baseLineId, long criteriaId) throws Exception {
		
		
		JSONArray ticketData = new JSONArray();
		Long buildingId = null;
		BeanFactory.lookup("ModuleBean");
		
		if(report.getReportSpaceFilterContext() != null && report.getReportSpaceFilterContext().getBuildingId() != null) {
			
			buildingId = report.getReportSpaceFilterContext().getBuildingId();
			
		}
		new DecimalFormat(".##");
		
		TicketCategoryContext category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getId(), "Auditing");
			
		List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId(),(Long)dateFilter.get(0), (Long)dateFilter.get(1),buildingId);
		
		if(workorders.isEmpty()) {
			return ticketData;
		}
		JSONObject buildingres = new JSONObject();
		for(WorkOrderContext workorder:workorders) {
			
			int buildingScore = 0;
			if(!(workorder.getSubject().contains("Daily"))) {
				
				continue;
			}

			
			List<Map<String, Object>> taskMap = WorkOrderAPI.getTasks(workorder.getId());			
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
//					else {
//						value = Integer.parseInt(stringValue);
//					}
					
					buildingScore = buildingScore +value;
				}
			}
			buildingres = new JSONObject();
			
			buildingres.put("label", workorder.getCreatedTime());
			buildingres.put("value", buildingScore);
			
			ticketData.add(buildingres);
		}
			
		LOGGER.log(Level.INFO, "23611l buildingres ----"+ticketData);
		return ticketData;

	}
}