package com.facilio.energystar.command;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.energystar.context.EnergyStarMeterDataContext;
import com.facilio.energystar.util.EnergyStarUtil;

public class AddEnergyStarPropertyData extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(AddEnergyStarPropertyData.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		List<ReadingContext> meterData = (List<ReadingContext>)context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_DATA_CONTEXTS);
		
		if(meterData != null && !meterData.isEmpty()) {
			
			FacilioChain addCurrentOccupancy = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			
			FacilioContext newContext = addCurrentOccupancy.getContext();
			newContext.put(FacilioConstants.ContextNames.MODULE_NAME, EnergyStarUtil.ENERGY_STAR_PROPERTY_DATA_MODULE_NAME);
			newContext.put(FacilioConstants.ContextNames.READINGS, meterData);
			newContext.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.WEB_ACTION);
			newContext.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
			addCurrentOccupancy.execute();
		}
		
		return false;
	}

}
