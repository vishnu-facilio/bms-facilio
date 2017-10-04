package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.leed.context.UtilityProviderContext;

public class BuildingContext extends ModuleBaseWithCustomFields {
	
	private long baseSpaceId;
	public long getBaseSpaceId() {
		return baseSpaceId;
	}
	public void setBaseSpaceId(long baseSpaceId) {
		this.baseSpaceId = baseSpaceId;
	}
	
	private CampusContext campus;
	public CampusContext getCampus() {
		return campus;
	}
	public void setCampus(CampusContext campus) {
		this.campus = campus;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private int currentOccupancy;
	public int getCurrentOccupancy() {
		return currentOccupancy;
	}
	public void setCurrentOccupancy(int currentOccupancy) {
		this.currentOccupancy = currentOccupancy;
	}
	
	private int maxOccupancy;
	public int getMaxOccupancy() {
		return maxOccupancy;
	}
	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}
	
	private long area;
	public long getArea() {
		return area;
	}
	public void setArea(long area) {
		this.area = area;
	}
	
	private LocationContext location;
	public LocationContext getLocation() {
		return location;
	}
	public void setLocation(LocationContext location) {
		this.location = location;
	}
	
	private int floors;
	public int getFloors() {
		return floors;
	}
	public void setFloors(int floors) {
		this.floors = floors;
	}
	
	private List<UtilityProviderContext> utilityProviders = new ArrayList<>();
	public List<UtilityProviderContext> getUtilityProviders() {
		return this.utilityProviders;
	}
	
	public void setUtilityProviders(List<UtilityProviderContext> utilityProviders) {
		this.utilityProviders = utilityProviders;
	}
	
	private long photoId;
	public long getPhotoId() {
		return photoId;
	}
	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}

	public String getAvatarUrl() throws Exception {
		if (this.photoId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			return fs.getPrivateUrl(this.photoId);
		}
		return null;
	}
}
