package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GetBuildingReportCards implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		long buildingId = (long) context.get(FacilioConstants.ContextNames.ID);

		if(buildingId > 0) {
			
			JSONObject reports = new JSONObject();
			reports.put("spaces", SpaceAPI.getSpacesCountForBuilding(buildingId));
			
			JSONObject woCount = new JSONObject();
			woCount.put("type", "count");
			woCount.put("name", "work_orders");
			woCount.put("label", "Work Orders");
			woCount.put("data", SpaceAPI.getWorkOrdersCount(buildingId));
			
			JSONObject faCount = new JSONObject();
			faCount.put("type", "count");
			faCount.put("name", "fire_alarms");
			faCount.put("label", "Alarms");
			faCount.put("data", SpaceAPI.getFireAlarmsCount(buildingId));
			
			JSONObject assetCount = new JSONObject();
			assetCount.put("type", "count");
			assetCount.put("name", "assets");
			assetCount.put("label", "Assets");
			assetCount.put("data", SpaceAPI.getAssetsCount(buildingId));
			
			JSONObject energyUsage= new JSONObject();
			energyUsage.put("type", "count");
			energyUsage.put("name", "energy");
			energyUsage.put("label", "ENERGY CONSUMED");
			energyUsage.put("data", "--");
			
			JSONArray reportCards = new JSONArray();
			reportCards.add(woCount);
			reportCards.add(faCount);
			reportCards.add(assetCount);
			reportCards.add(energyUsage);
			
			context.put(FacilioConstants.ContextNames.REPORTS, reports);
			context.put(FacilioConstants.ContextNames.REPORT_CARDS, reportCards);
		}
		else {
			throw new IllegalArgumentException("Invalid Building ID : "+buildingId);
		}

		return false;
	}
}
