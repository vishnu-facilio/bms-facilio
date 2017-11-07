package com.facilio.bmsconsole.context;

public class EntergyMeterContext extends AssetContext {
	public long getControllerId() {
		return super.getParentAssetId();
	}
	public void setControllerId(long controllerId) {
		super.setParentAssetId(controllerId);
	}
	
	private EnergyMeterPurposeContext purpose;
	public EnergyMeterPurposeContext getPurpose() {
		return purpose;
	}
	public void setPurpose(EnergyMeterPurposeContext purpose) {
		this.purpose = purpose;
	}
}
