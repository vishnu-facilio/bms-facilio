package com.facilio.energystar.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.energystar.util.EnergyStarUtil;

public class EnergyStarHistoricalFetchDataAddJobCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long propertyId = (long)context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_ID);
		long startTime = (long)context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long)context.get(FacilioConstants.ContextNames.END_TIME);
		
		JSONObject obj = new JSONObject();
		
		obj.put(FacilioConstants.ContextNames.START_TIME, startTime);
		obj.put(FacilioConstants.ContextNames.END_TIME, endTime);
		
		BmsJobUtil.deleteJobWithProps(propertyId, "EnergyStarFetchHistoricalData");
		BmsJobUtil.scheduleOneTimeJobWithProps(propertyId, "EnergyStarFetchHistoricalData", 30, "history", obj);
		
		return false;
	}

}
