package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class HazardPrecautionContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PrecautionContext precaution;
	private HazardContext hazard;
	public HazardContext getHazard() {
		return hazard;
	}
	public void setHazard(HazardContext hazard) {
		this.hazard = hazard;
	}
	public PrecautionContext getPrecaution() {
		return precaution;
	}
	public void setPrecaution(PrecautionContext precaution) {
		this.precaution = precaution;
	}
	
}
