package com.facilio.controlaction.context;

import java.util.List;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ReadingDataMeta.ControlActionMode;
import com.facilio.modules.fields.FacilioField;

public class ControlGroupContext {
	long id = -1l;
	long orgId = -1l;
	long siteId = -1l;
	String name;
	long assetCategoryId = -1l;
	AssetCategoryContext assetCategoryContext;
	long fieldId = -1;
	FacilioField field;
	List<ControlGroupSpace> controlGroupSpaces;
	List<ControlGroupInclExclContext> controlGroupInclExclContexts;
	List<Long> matchedResources;
	ControlActionMode mode;
	Boolean isDeleted;
	
	
	public boolean isDeleted() {
		if(isDeleted != null) {
			return isDeleted.booleanValue();
		}
		return false;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public int getMode() {
		if(mode != null) {
			return mode.getIntVal();
		}
		return -1;
	}
	public void setMode(int mode) {
		this.mode = ControlActionMode.valueOf(mode);
	}
	public FacilioField getField() {
		return field;
	}
	public void setField(FacilioField field) {
		this.field = field;
	}
	public AssetCategoryContext getAssetCategoryContext() {
		return assetCategoryContext;
	}
	public void setAssetCategoryContext(AssetCategoryContext assetCategoryContext) {
		this.assetCategoryContext = assetCategoryContext;
	}
	public List<Long> getMatchedResources() {
		return matchedResources;
	}
	public void setMatchedResources(List<Long> matchedResources) {
		this.matchedResources = matchedResources;
	}
	public List<ControlGroupSpace> getControlGroupSpaces() {
		return controlGroupSpaces;
	}
	public void setControlGroupSpaces(List<ControlGroupSpace> controlGroupSpaces) {
		this.controlGroupSpaces = controlGroupSpaces;
	}
	public List<ControlGroupInclExclContext> getControlGroupInclExclContexts() {
		return controlGroupInclExclContexts;
	}
	public void setControlGroupInclExclContexts(List<ControlGroupInclExclContext> controlGroupInclExclContexts) {
		this.controlGroupInclExclContexts = controlGroupInclExclContexts;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getAssetCategoryId() {
		return assetCategoryId;
	}
	public void setAssetCategoryId(long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	
}
