package com.facilio.report.customreport;

import java.util.Iterator;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.DashboardAction;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.fw.BeanFactory;

public class CustomReportUTC3 implements CustomReport {	//Score by Criteria

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
		SpaceAPI.getBuildingSpace(buildingId);
		
		DashboardAction d = new DashboardAction();
		d.setReportSpaceFilterContext(report.getReportSpaceFilterContext());
		d.getUTCData();
		
		Iterator ittr = d.reportData.iterator();
		
		ticketData = new JSONArray();
		while(ittr.hasNext()) {
			
			JSONObject json = (JSONObject) ittr.next();
			
			String name = (String) json.get("Criteria");
			
			JSONObject newJSON = new JSONObject();
			
			newJSON.put("label", name);
			newJSON.put("value", json.get("C"));
			
			ticketData.add(newJSON);
		}
		
		return ticketData;
	}
	
}
