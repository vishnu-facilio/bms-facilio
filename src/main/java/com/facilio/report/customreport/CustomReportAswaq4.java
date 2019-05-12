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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomReportAswaq4 implements CustomReport {

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
			
			int compliance = 0,nonCompliance = 0,repeatFinding = 0;
			
			
			Map<Long,Long> qDateRange = new HashMap<>();
			
			qDateRange.put(1514745000000l, 1522521000000l);
			qDateRange.put(1522521000000l, 1530383400000l);
			qDateRange.put(1530383400000l, 1538332200000l);
			qDateRange.put(1538332200000l, 1546194600000l);
			
			JSONArray ticketData = new JSONArray();
			for(int i=0;i<qDateRange.size();i++) {
				
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
				JSONArray array = new JSONArray(); 
				for(BuildingContext building : SpaceAPI.getAllBuildings()) {
					
					compliance = 0;nonCompliance = 0;repeatFinding = 0;
					for(WorkOrderContext workorder:workorders) {
						
						if(workorder.getResource().getId() != building.getId()) {
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
					buildingres.put("label", building.getName());
					buildingres.put("value", compliance+nonCompliance+repeatFinding);
					
					array.add(buildingres);
				}
				
				JSONObject qres = new JSONObject();
				qres.put("value", array);
				
				
				if(fromTime == 1514745000000l) {
					qres.put("label", "Q1 2018");
				}
				else if(fromTime == 1522521000000l) {
					qres.put("label", "Q2 2018");
				}
				else if(fromTime == 1530383400000l) {
					qres.put("label", "Q3 2018");
				}
				else if(fromTime == 1538332200000l) {
					qres.put("label", "Q4 2018");
				}
				new LinkedHashMap();
				ticketData.add(qres);
				
			}
			
			return ticketData;
		}
		return null;
	}

}
