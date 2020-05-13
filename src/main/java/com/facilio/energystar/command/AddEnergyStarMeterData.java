package com.facilio.energystar.command;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.energystar.context.EnergyStarMeterDataContext;
import com.facilio.energystar.util.EnergyStarUtil;

public class AddEnergyStarMeterData extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<EnergyStarMeterDataContext> meterData = (List<EnergyStarMeterDataContext>)context.get(EnergyStarUtil.ENERGY_STAR_METER_DATA_CONTEXTS);
		
		if(meterData != null && !meterData.isEmpty()) {
			
			FacilioChain addCurrentOccupancy = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			
			FacilioContext newContext = addCurrentOccupancy.getContext();
			newContext.put(FacilioConstants.ContextNames.MODULE_NAME, EnergyStarUtil.ENERGY_STAR_METER_DATA_MODULE_NAME);
			newContext.put(FacilioConstants.ContextNames.READINGS, meterData);
			newContext.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.WEB_ACTION);
			newContext.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
			addCurrentOccupancy.execute();
		}
		return false;
	}

}
