package com.facilio.bmsconsoleV3.context;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ServingSitesContext;
import com.facilio.v3.context.V3Context;

public class V3StoreRoomContext extends V3Context {

    private static final long serialVersionUID = 1L;
    
    private String name;
	private String description;
	private LocationContext location;
	private User owner;
	private Long ttime;
	private Long modifiedTime;
	public Long noOfItemTypes;
	public Long noOfToolTypes;
	private Boolean isApprovalNeeded;
	private Long lastPurchasedDate;
	private Boolean isGatePassRequired;
	private V3SiteContext site;
	private List<V3ServingSitesContext> servingsites;
	
	public Long getTtime() {
		return ttime;
	}
	public void setTtime(Long ttime) {
		this.ttime = ttime;
	}
	public Long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(Long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public Long getNoOfItemTypes() {
		return noOfItemTypes;
	}
	public void setNoOfItemTypes(Long noOfItemTypes) {
		this.noOfItemTypes = noOfItemTypes;
	}
	public Long getNoOfToolTypes() {
		return noOfToolTypes;
	}
	public void setNoOfToolTypes(Long noOfToolTypes) {
		this.noOfToolTypes = noOfToolTypes;
	}
	public Long getLastPurchasedDate() {
		return lastPurchasedDate;
	}
	public void setLastPurchasedDate(Long lastPurchasedDate) {
		this.lastPurchasedDate = lastPurchasedDate;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


	public LocationContext getLocation() {
		return location;
	}
	public void setLocation(LocationContext location) {
		this.location = location;
	}
	public long getLocationId() {
		if (location != null) {
			return location.getId();
		}
		return -1;
	}

	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Boolean getIsApprovalNeeded() {
		return isApprovalNeeded;
	}
	public void setIsApprovalNeeded(Boolean isApprovalNeeded) {
		this.isApprovalNeeded = isApprovalNeeded;
	}
	public boolean isApprovalNeeded() {
		if (isApprovalNeeded != null) {
			return isApprovalNeeded.booleanValue();
		}
		return false;
	}
	
	public Boolean getIsGatePassRequired() {
		return isGatePassRequired;
	}
	public void setIsGatePassRequired(Boolean isGatePassRequired) {
		this.isGatePassRequired = isGatePassRequired;
	}
	
	public boolean isGatePassRequired() {
		if (isGatePassRequired != null) {
			return isGatePassRequired.booleanValue();
		}
		return false;
	}
	public V3SiteContext getSite() {
		return site;
	}
	public void setSite(V3SiteContext site) {
		this.site = site;
	}

	public List<V3ServingSitesContext> getServingsites() {
		return servingsites;
	}

	public void setServingsites(List<V3ServingSitesContext> servingsites) {
		this.servingsites = servingsites;
	}
}
