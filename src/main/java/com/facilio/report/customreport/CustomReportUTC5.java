package com.facilio.report.customreport;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class CustomReportUTC5 implements CustomReport {	//Score Trends

	private static final Logger LOGGER = Logger.getLogger(CustomReportUTC2.class.getName());
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
		for(WorkOrderContext workorder:workorders) {
			
			int passed = 0,failed = 0;
			JSONObject buildingres = new JSONObject();
			
			if(!(workorder.getSubject().contains("Daily Maintenance"))) {
			
			continue;
			}
			
			List<Map<String, Object>> taskMap = WorkOrderAPI.getTasks(workorder.getId());
			
			for(Map<String, Object> task : taskMap) {
				
				if(task.get("inputValue") != null) {
					
					String stringValue = task.get("inputValue").toString();
					
					Integer value = 0;
					if("Met".equals(stringValue) ) {
						passed = passed + 1;
					}
					else if ("Not Met".equals(stringValue)) {
						failed = failed + 1;
					}
//					else {
//						value = Integer.parseInt(stringValue);
//						if(value <= 2) {
//							failed = failed + 1;
//						}else {
//							passed = passed + 1;
//						}
//					}
				}
			}
			
			JSONArray resArray = new JSONArray();
			
			JSONObject res = new JSONObject();
			res.put("label", "Met");
			res.put("value", passed);
			resArray.add(res);
			
			res = new JSONObject();
			res.put("label", "Not Met");
			res.put("value", failed);
			resArray.add(res);
			
			buildingres.put("label", workorder.getCreatedTime());
			buildingres.put("value", resArray);
			ticketData.add(buildingres);
			
			ticketData.add(buildingres);
		}
			
		
		LOGGER.log(Level.INFO, "23611l buildingres ----"+ticketData);
		return ticketData;

	}
}