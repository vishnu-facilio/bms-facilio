package com.facilio.energystar.context;

import com.facilio.bmsconsole.context.ReadingContext;

public class EnergyStarMeterDataContext extends ReadingContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String energyStarId;

	public String getEnergyStarId() {
		return energyStarId;
	}

	public void setEnergyStarId(String energyStarId) {
		this.energyStarId = energyStarId;
	}
	
}
