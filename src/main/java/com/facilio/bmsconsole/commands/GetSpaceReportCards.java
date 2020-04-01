package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GetSpaceReportCards extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		long spaceId = (long) context.get(FacilioConstants.ContextNames.ID);

		if(spaceId > 0) {
			
			JSONObject reports = new JSONObject();
			
			JSONObject woCount = new JSONObject();
			woCount.put("type", "count");
			woCount.put("name", "work_orders");
			woCount.put("label", "Work Orders");
			woCount.put("data", SpaceAPI.getWorkOrdersCount(spaceId));
			
			JSONObject faCount = new JSONObject();
			faCount.put("type", "count");
			faCount.put("name", "fire_alarms");
			faCount.put("label", "Alarms");
			faCount.put("data", SpaceAPI.getV2AlarmCount(spaceId));
			
			JSONObject assetCount = new JSONObject();
			assetCount.put("type", "count");
			assetCount.put("name", "assets");
			assetCount.put("label", "Assets");
			assetCount.put("data", SpaceAPI.getAssetsCount(spaceId));

			
			JSONArray reportCards = new JSONArray();
			reportCards.add(woCount);
			reportCards.add(faCount);
			reportCards.add(assetCount);
			
			context.put(FacilioConstants.ContextNames.REPORTS, reports);
			context.put(FacilioConstants.ContextNames.REPORT_CARDS, reportCards);
		}
		else {
			throw new IllegalArgumentException("Invalid Space ID : "+spaceId);
		}

		return false;
	}
}
