package com.facilio.bundle.context;

import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BundleChangeSetContext {

	private Long id;
	private Long orgId;
	private BundleComponentsEnum componentType;
	private BundleModeEnum componentMode;
	private Long componentId;
	private String componentDisplayName;
	private Long bundleId;
	private Long componentLastEditedTime;
	
	private Double tempVersion; 
	
	
	public int getComponentType() {
		if(componentType != null) {
			return componentType.getValue();
		}
		return -1;
	}
	public void setComponentType(int componentTypeInt) {
		componentType = BundleComponentsEnum.getAllBundleComponents().get(componentTypeInt);
	}
	
	public void setComponentTypeEnum(BundleComponentsEnum componentType) {
		this.componentType = componentType;
	}
	
	public BundleComponentsEnum getComponentTypeEnum() {
		return componentType;
	}
	
	public int getComponentMode() {
		if(componentMode != null) {
			return componentMode.getValue();
		}
		return -1;
	}
	public void setComponentMode(int modeInt) {
		componentMode = BundleModeEnum.valueOf(modeInt);
	}
	
	public void setModeEnum(BundleModeEnum mode) {
		this.componentMode = mode;
	}
	
	public BundleModeEnum getModeEnum() {
		return componentMode;
	}
}
