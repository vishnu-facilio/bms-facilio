package com.facilio.report.customreport;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomReportUTC2 implements CustomReport {

	private static final Logger LOGGER = Logger.getLogger(CustomReportUTC2.class.getName());
	@Override
	public JSONArray getData(ReportContext report, FacilioModule module, JSONArray dateFilter,
			JSONObject userFilterValues, long baseLineId, long criteriaId) throws Exception {
		
		
		JSONArray ticketData = new JSONArray();
		Long buildingId = null;
		double value = 0;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(report.getReportSpaceFilterContext() != null && report.getReportSpaceFilterContext().getBuildingId() != null) {
			
			buildingId = report.getReportSpaceFilterContext().getBuildingId();
			
		}
		DecimalFormat df = new DecimalFormat(".##");
		
		TicketCategoryContext category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getId(), "Auditing");
			
		List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId(),(Long)dateFilter.get(0), (Long)dateFilter.get(1),buildingId);
		
		if(workorders.isEmpty()) {
			return ticketData;
		}
		for(WorkOrderContext workorder:workorders) {
			
			LOGGER.log(Level.SEVERE, "buildingId --- "+buildingId);
			if(workorder.getResource() != null) {
				LOGGER.log(Level.SEVERE, "workorder.getResource().getId() --- "+workorder.getResource().getId());
			}
			
			LOGGER.log(Level.SEVERE, "dateFilter --- "+dateFilter);
			LOGGER.log(Level.SEVERE, "workorder.getResource().getId() --- "+workorder.getCreatedTime());
			
			
			int criticalCount = 0, tatCount =0, pdCount = 0, fasCountdaily = 0;
			Map<String,Double> daily = new HashMap<>();
			
			Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
			FacilioContext context = new FacilioContext();
			
			context.put(FacilioConstants.ContextNames.ID, workorder.getId());
			context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
			context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
			chain.execute(context);
			
			Map<Long, List<TaskContext>> taksSectionMap = (Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
			
			for(Long sectionid :taksSectionMap.keySet()) {
				
				TaskSectionContext section = TicketAPI.getTaskSection(sectionid);
				
				for(TaskContext task : taksSectionMap.get(sectionid)) {
					
					if(task.getInputValue() != null) {
						
						if(task.getInputValue().equals("Met")) {
							value = 5.0;
						}
						else if(task.getInputValue().equals("Not Met")) {
							value = 0d;
						}
						
						if(workorder.getSubject().contains("Daily")) {
							
							if(section.getName().contains("Critical Service")) {
								
								criticalCount++;
								double value1 = 0;
								if(daily.containsKey("Critical Service")) {
									value1 = daily.get("Critical Service");
								}
								value1 = value1 + value;
								daily.put("Critical Service", value1);
							}
							else if(section.getName().contains("TAT")) {
								
								tatCount++;
								double value1 = 0;
								if(daily.containsKey("TAT")) {
									value1 = daily.get("TAT");
								}
								value1 = value1 + value;
								daily.put("TAT", value1);
							}
							else if(section.getName().contains("PD&PSI")) {
								
								pdCount++;
								double value1 = 0;
								if(daily.containsKey("PD&PSI")) {
									value1 = daily.get("PD&PSI");
								}
								value1 = value1 + value;
								daily.put("PD&PSI", value1);
							}
							else if(section.getName().contains("Financials & Ops")) {
								
								fasCountdaily++;
								double value1 = 0;
								if(daily.containsKey("Financials & Ops")) {
									value1 = daily.get("Financials & Ops");
								}
								value1 = value1 + value;
								daily.put("Financials & Ops", value1);
							}
						}
					}
				}
			}
			
			JSONArray resList = new JSONArray();
				
			LOGGER.log(Level.SEVERE, "daily --- "+daily);
				
			if(daily.containsKey("Critical Service")) {
				value = daily.get("Critical Service");
				if(value > 0) {
					value = value / criticalCount;
				}
				JSONObject json = new JSONObject();
				json.put("label", "Critical Service");
				json.put("value", df.format(value * (40.0d/100.0d)));
				resList.add(json);
			}
			
			if(daily.containsKey("TAT")) {
				value = daily.get("TAT");
				if(value > 0) {
					value = value / tatCount;
				}
				JSONObject json = new JSONObject();
				json.put("label", "TAT");
				json.put("value", df.format(value * (25.0d/100.0d)));
				resList.add(json);
				
			}
			if(daily.containsKey("PD&PSI")) {
				value = daily.get("PD&PSI");
				if(value > 0) {
					value = value / pdCount;
				}
				JSONObject json = new JSONObject();
				json.put("label", "PD&PSI");
				json.put("value", df.format(value * (10.0d/100.0d)));
				resList.add(json);
			}
			
			if(daily.containsKey("Financials & Ops")) {
				value = daily.get("Financials & Ops");
				if(value > 0) {
					value = value / fasCountdaily;
				}
				JSONObject json = new JSONObject();
				json.put("label", "Financials & Ops");
				json.put("value", df.format(value * (25.0d/100.0d)));
				resList.add(json);
			}
				
			JSONObject finalRes1 = new JSONObject();
			
			finalRes1.put("label", workorder.getCreatedTime());
			finalRes1.put("value", resList);
			
			ticketData.add(finalRes1);
		}
		return ticketData;

	}
}
