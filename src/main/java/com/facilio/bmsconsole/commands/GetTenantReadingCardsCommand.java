package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ConsumptionAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class GetTenantReadingCardsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		List<Long> assetIds = (List<Long>)context.get(FacilioConstants.ContextNames.TENANT_UTILITY_IDS);
		Map<String,Object> energyData = ConsumptionAPI.getEnergyConsumptionByAssetsThisMonth(assetIds);
		Map<String,Object> waterData = ConsumptionAPI.getWaterConsumptionByAssetsThisMonth(assetIds);
		
		
		JSONObject readingCard = new JSONObject();
		readingCard.put("energy", energyData.get("energy"));
		readingCard.put("water", energyData.get("water"));
			
		context.put(FacilioConstants.ContextNames.REPORT, readingCard);
		

		return false;
	}

	
}

