package com.facilio.report.customreport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class CustomReportAswaq8 implements CustomReport {

	public static final int aswaqComp = 1,aswaqnonComp = 1,aswaqrep = 1,aswaqna = 1;
	@Override
	public JSONArray getData(ReportContext report, FacilioModule module, JSONArray dateFilter,
			JSONObject userFilterValues, long baseLineId, long criteriaId) throws Exception {


		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
		
		for(TicketCategoryContext category:categories) {
			
			List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
			
			if(workorders.isEmpty()) {
				continue;
			}
			
			Map<Long,Long> qDateRange = new HashMap<>();
			
			qDateRange.put(1514745000000l, 1522521000000l);
			qDateRange.put(1522521000000l, 1530383400000l);
			qDateRange.put(1530383400000l, 1538332200000l);
			qDateRange.put(1538332200000l, 1546194600000l);
			
			JSONArray ticketData = new JSONArray();
			for(int i=0;i<qDateRange.size();i++) {
				
				int compliance = 0,nonCompliance = 0,repeatFinding = 0;
				long fromTime = 0l;
				if(i==0) {
					fromTime = 1514745000000l;
				}
				else if(i == 1) {
					fromTime = 1522521000000l;
				}
				else if(i == 2) {
					fromTime = 1530383400000l;				
				}
				else if(i == 3) {
					fromTime = 1538332200000l;
				}
				long toTime = qDateRange.get(fromTime);
					
				for(WorkOrderContext workorder:workorders) {
					
					if(workorder.getResource().getId() != report.getReportSpaceFilterContext().getBuildingId()) {
						continue;
					}
					if(dateFilter != null && !(fromTime < workorder.getCreatedTime() && workorder.getCreatedTime() < toTime)) {
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
							
							String subject = (String) task.get("inputValue");
							
							subject = subject.trim();
							
							if (subject.endsWith("Non Compliance")) {
								nonCompliance += aswaqnonComp;
							}
							else if(subject.endsWith("Compliance")) {
								compliance += aswaqComp;
							}
							else if (subject.endsWith("Repeat Findings")) {
								repeatFinding += aswaqrep;
							}
						}
					}
				}
				
				JSONObject buildingres = new JSONObject();
				buildingres.put("value", compliance+nonCompliance+repeatFinding);
				
				if(fromTime == 1514745000000l) {
					buildingres.put("label", "Q1 2018");
				}
				else if(fromTime == 1522521000000l) {
					buildingres.put("label", "Q2 2018");
				}
				else if(fromTime == 1530383400000l) {
					buildingres.put("label", "Q3 2018");
				}
				else if(fromTime == 1538332200000l) {
					buildingres.put("label", "Q4 2018");
				}
				ticketData.add(buildingres);
			}
			
			return ticketData;
		}
		return dateFilter;
	
	}

}
