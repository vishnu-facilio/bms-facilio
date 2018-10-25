package com.facilio.bmsconsole.context;

import java.util.List;
import java.util.regex.Pattern;

public class EnergyMeterContext extends AssetContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	public void setRoot(boolean root) {
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
	
	private Boolean isVirtual;
	public Boolean getIsVirtual() {
		return isVirtual;
	}
	public void setIsVirtual(Boolean isVirtual) {
		this.isVirtual = isVirtual;
	}
	public boolean isVirtual() {
		if(isVirtual != null) {
			return isVirtual.booleanValue();
		}
		return false;
	}
	
	private String childMeterExpression;
	public String getChildMeterExpression() {
		return childMeterExpression;
	}
	public void setChildMeterExpression(String childMeterExpression) {
		this.childMeterExpression = childMeterExpression;
	}
	
	private List<Long> childMeterIds;
	public List<Long> getChildMeterIds() {
		return childMeterIds;
	}
	public void setChildMeterIds(List<Long> childMeterIds) {
		this.childMeterIds = childMeterIds;
	}
	
	public static final Pattern EXP_FORMAT = Pattern.compile("([1-9]\\d*)|(\\()|(\\))|(\\+)|(-)");
}
