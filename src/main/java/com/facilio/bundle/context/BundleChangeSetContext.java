package com.facilio.bundle.context;

import com.facilio.bundle.enums.BundleCommitStatusEnum;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BundleChangeSetContext {

	private Long id;
	private Long orgId;
	private BundleComponentsEnum componentType;
	private BundleModeEnum mode;
	private Long componentId;
	private String name;
	private BundleCommitStatusEnum commitStatus;
	private Long parentId;
	private Long lastEditedTime;
	
	
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
	
	public int getMode() {
		if(mode != null) {
			return mode.getValue();
		}
		return -1;
	}
	public void setMode(int modeInt) {
		mode = BundleModeEnum.valueOf(modeInt);
	}
	
	public void setModeEnum(BundleModeEnum mode) {
		this.mode = mode;
	}
	
	public BundleModeEnum getModeEnum() {
		return mode;
	}
	
	public int getCommitStatus() {
		if(commitStatus != null) {
			return commitStatus.getValue();
		}
		return -1;
	}
	public void setCommitStatus(int commitStatusInt) {
		commitStatus = BundleCommitStatusEnum.valueOf(commitStatusInt);
	}

	public void setCommitStatusEnum(BundleCommitStatusEnum commitStatus) {
		this.commitStatus = commitStatus;
	}
	
	public BundleCommitStatusEnum getCommitStatusEnum() {
		return commitStatus;
	}
}
