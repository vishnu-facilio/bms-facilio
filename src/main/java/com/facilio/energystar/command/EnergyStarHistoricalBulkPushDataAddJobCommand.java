package com.facilio.energystar.command;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.energystar.util.EnergyStarUtil;

public class EnergyStarHistoricalBulkPushDataAddJobCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		JSONArray pushMeterData = (JSONArray)context.get(EnergyStarUtil.ENERGY_STAR_PUSH_METER_DATA);
		
		for(int i=0;i<pushMeterData.size();i++) {
			
			Map<String,Object> meterData = (Map<String,Object>)pushMeterData.get(i);
			
			long meterId = (long) meterData.get("id");
			
			List<Object> dateRanges = (List) meterData.get("dateRanges");
			
			for(int j=0;j<dateRanges.size();j++) {
				
				Map<String,Object> dateRange = (Map<String,Object>) dateRanges.get(j);
				long startTime = (long) dateRange.get("startTime");
				long endTime = (long) dateRange.get("endTime");
				
				FacilioChain chain = TransactionChainFactory.addPushESHistoricalDataJobChain();
				
				FacilioContext context1 = chain.getContext();
				
				context1.put(EnergyStarUtil.ENERGY_STAR_METER_ID, meterId);
				context1.put(FacilioConstants.ContextNames.START_TIME, startTime);
				context1.put(FacilioConstants.ContextNames.END_TIME, endTime);
				
				chain.execute();
			}
		}
		
		return false;
	}

}
