package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;

public class AddAdditionalPropsForEnergyMeterCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EnergyMeterContext energyMeter = (EnergyMeterContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(energyMeter != null) {
			energyMeter.setCategory(AssetsAPI.getCategory("Energy Meter"));
		}
		else {
			throw new IllegalArgumentException("Record cannot be null during addition");
		}
		return false;
	}

}
