package com.facilio.bmsconsole.context;

public class AssetTreeContext {
	public long getParentAsset() {
		return parentAsset;
	}
	public void setParentAsset(long parentAsset) {
		this.parentAsset = parentAsset;
	}
	public long getChildAsset() {
		return childAsset;
	}
	public void setChildAsset(long childAsset) {
		this.childAsset = childAsset;
	}

	private long childAsset;
	private long parentAsset;
}