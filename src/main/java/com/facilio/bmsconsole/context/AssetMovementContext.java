package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class AssetMovementContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private User requestedBy;
	private long fromSite;
	private long toSite;
	private long fromSpace;
	private long toSpace;
	private String fromGeoLocation;
	private String toGeoLocation;

	public Boolean approvalNeeded;

	public Boolean getApprovalNeeded() {
		return approvalNeeded;
	}

	public void setApprovalNeeded(Boolean approvalNeeded) {
		this.approvalNeeded = approvalNeeded;
	}

	public boolean isApprovalNeeded() {
		if (approvalNeeded != null) {
			return approvalNeeded.booleanValue();
		}
		return false;
	}
	
	private long assetId;
	public User getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	public long getFromSite() {
		return fromSite;
	}
	public void setFromSite(long fromSite) {
		this.fromSite = fromSite;
	}
	public long getToSite() {
		return toSite;
	}
	public void setToSite(long toSite) {
		this.toSite = toSite;
	}
	public long getFromSpace() {
		return fromSpace;
	}
	public void setFromSpace(long fromSpace) {
		this.fromSpace = fromSpace;
	}
	public long getToSpace() {
		return toSpace;
	}
	public void setToSpace(long toSpace) {
		this.toSpace = toSpace;
	}
	public String getFromGeoLocation() {
		return fromGeoLocation;
	}
	public void setFromGeoLocation(String fromGeoLocation) {
		this.fromGeoLocation = fromGeoLocation;
	}
	public String getToGeoLocation() {
		return toGeoLocation;
	}
	public void setToGeoLocation(String toGeoLocation) {
		this.toGeoLocation = toGeoLocation;
	}
	
	public Boolean isMovementNeeded;

	public Boolean getIsMovementNeeded() {
		return isMovementNeeded;
	}

	public void setIsMovementNeeded(Boolean isMovementNeeded) {
		this.isMovementNeeded = isMovementNeeded;
	}

	public boolean isMovementNeeded() {
		if (isMovementNeeded != null) {
			return isMovementNeeded.booleanValue();
		}
		return false;
	}
}
