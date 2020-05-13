package com.facilio.energystar.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.energystar.util.EnergyStarUtil;

public class AddEnergyStarHistoricalJobCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long meterId = (long)context.get(EnergyStarUtil.ENERGY_STAR_METER_ID);
		long startTime = (long)context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long)context.get(FacilioConstants.ContextNames.END_TIME);
		
		JSONObject obj = new JSONObject();
		
		obj.put(FacilioConstants.ContextNames.START_TIME, startTime);
		obj.put(FacilioConstants.ContextNames.END_TIME, endTime);
		
		BmsJobUtil.deleteJobWithProps(meterId, "EnergyStarPushHistoricalData");
		BmsJobUtil.scheduleOneTimeJobWithProps(meterId, "EnergyStarPushHistoricalData", 30, "history", obj);
		
		return false;
	}

}
