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

public class CustomReportAswaq3 implements CustomReport {

	public static final int aswaqComp = 1,aswaqnonComp = 1,aswaqrep = 1,aswaqna = 1;
	@Override
	public JSONArray getData(ReportContext report, FacilioModule module, JSONArray dateFilter,
			JSONObject userFilterValues, long baseLineId, long criteriaId) throws Exception {


		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
		
		JSONArray ticketData = new JSONArray();

		for(TicketCategoryContext category:categories) {
			
			List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
			
			if(workorders.isEmpty()) {
				continue;
			}
			
			int compliance = 0,nonCompliance = 0,repeatFinding = 0;
			
			for(BuildingContext building : SpaceAPI.getAllBuildings()) {
				
				compliance = 0;nonCompliance = 0;repeatFinding = 0;
				for(WorkOrderContext workorder:workorders) {
					
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
				nonCompliance = Math.abs(nonCompliance);
				compliance = Math.abs(compliance);
				repeatFinding = Math.abs(repeatFinding);
				
				JSONObject buildingres = new JSONObject();
				buildingres.put("label",building.getName()); 
				if(report.getId() == 2381l) {
					buildingres.put("value", repeatFinding);
				}
				else if (report.getId() == 2380l) {
					buildingres.put("value", nonCompliance);
				}
				else if (report.getId() == 2379l) {
					buildingres.put("value", compliance);
				}
				
				ticketData.add(buildingres);
			}
			return ticketData;
		}
	
		return null;
	}

}
