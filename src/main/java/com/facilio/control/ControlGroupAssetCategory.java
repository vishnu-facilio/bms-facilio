package com.facilio.control;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.v3.context.V3Context;

public class ControlGroupAssetCategory extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	ControlGroupContext controlGroup;
	AssetCategoryContext assetCategory;
	ControlGroupSection controlGroupSection;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ControlGroupContext getControlGroup() {
		return controlGroup;
	}
	public void setControlGroup(ControlGroupContext controlGroup) {
		this.controlGroup = controlGroup;
	}
	public AssetCategoryContext getAssetCategory() {
		return assetCategory;
	}
	public void setAssetCategory(AssetCategoryContext assetCategory) {
		this.assetCategory = assetCategory;
	}
	
	List<ControlGroupAssetContext> controlAssets;

	public List<ControlGroupAssetContext> getControlAssets() {
		return controlAssets;
	}
	public void setControlAssets(List<ControlGroupAssetContext> controlAssets) {
		this.controlAssets = controlAssets;
	}
	
	public void addControlAsset(ControlGroupAssetContext controlAsset) {
		this.controlAssets = this.controlAssets == null ? new ArrayList<ControlGroupAssetContext>() : this.controlAssets; 
		this.controlAssets.add(controlAsset);
	}
	public ControlGroupSection getControlGroupSection() {
		return controlGroupSection;
	}
	public void setControlGroupSection(ControlGroupSection controlGroupSection) {
		this.controlGroupSection = controlGroupSection;
	}
}
