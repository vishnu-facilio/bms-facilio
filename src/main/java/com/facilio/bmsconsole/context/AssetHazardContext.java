package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class AssetHazardContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private AssetContext asset;
	private HazardContext hazard;
	public HazardContext getHazard() {
		return hazard;
	}
	public void setHazard(HazardContext hazard) {
		this.hazard = hazard;
	}
	public AssetContext getAsset() {
		return asset;
	}
	public void setAsset(AssetContext asset) {
		this.asset = asset;
	}
	
}
