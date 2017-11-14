package com.facilio.bmsconsole.context;

public class EnergyMeterContext extends AssetContext {
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
	
	private Boolean root;
	public Boolean getRoot() {
		return root;
	}
	public void setRoot(Boolean root) {
		this.root = root;
	}
	public boolean isRoot() {
		if(root != null) {
			return root.booleanValue();
		}
		return false;
	}
	
	private BaseSpaceContext purposeSpace;
	public BaseSpaceContext getPurposeSpace() {
		return purposeSpace;
	}
	public void setPurposeSpace(BaseSpaceContext purposeSpace) {
		this.purposeSpace = purposeSpace;
	}
}
